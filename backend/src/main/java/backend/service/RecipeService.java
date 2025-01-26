package backend.service;

import backend.model.Recipe.Ingredient;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.IngredientRepository;
import backend.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class to manage recipes.
 * Provides methods to create, update, delete, search, and manage recipes.
 */
@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    @PersistenceContext
    private EntityManager em;

    /**
     * Constructs a RecipeService instance with the provided repositories.
     *
     * @param recipeRepository   The repository for managing Recipe entities.
     * @param ingredientRepository The repository for managing Ingredient entities.
     */
    public RecipeService(RecipeRepository recipeRepository, final IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Retrieves all recipes for a given tenant.
     *
     * @param tenantId The ID of the tenant.
     * @return A list of all recipes for the tenant.
     */
    public List<Recipe> getAllRecipes(String tenantId) {
        return recipeRepository.findByTenantId(tenantId);
    }

    /**
     * Searches for recipes based on various criteria such as name, tags, cooking time, and favorite status.
     *
     * @param tenantId        The ID of the tenant.
     * @param name            The name of the recipe (partial match).
     * @param tags            The tags associated with the recipe.
     * @param maxCookingTime  The maximum cooking time for the recipe.
     * @param favorite        The favorite status of the recipe.
     * @return A list of recipes matching the search criteria.
     */
    public List<Recipe> searchRecipes(String tenantId, String name, List<Tag> tags, Integer maxCookingTime, Boolean favorite) {
        logger.info("Searching recipes for tenantId: {}, name: {}, tags: {}, max cooking time: {}, favorite: {}", tenantId, name, tags, maxCookingTime, favorite);

        List<Recipe> recipes = new ArrayList<>();

        if (name != null && tags != null && !tags.isEmpty()) {
            recipes = recipeRepository.findByTenantIdAndNameStartingWithIgnoreCaseAndTagsIn(tenantId, name, tags);
            recipes = recipes.stream().filter(recipe -> recipe.getTags().containsAll(tags)).collect(Collectors.toList());
        } else if (name != null) {
            recipes = recipeRepository.findByTenantIdAndNameStartingWithIgnoreCase(tenantId, name);
        }

        else if (tags != null && !tags.isEmpty()) {
            recipes = recipeRepository.findByTenantIdAndTagsIn(tenantId, tags);
            recipes = recipes.stream().filter(recipe -> recipe.getTags().containsAll(tags)).collect(Collectors.toList());
        } else {
            recipes = recipeRepository.findByTenantId(tenantId);
        }

        if (maxCookingTime != null) {
            recipes = recipes.stream().filter(recipe -> recipe.getCookingTime() != null && recipe.getCookingTime() <= maxCookingTime).toList();
        }

        // Filter by favorite status
        if (favorite != null && favorite) {
            recipes = recipes.stream().filter(Recipe::isFavorite).collect(Collectors.toList());
        }

        return recipes;
    }

    /**
     * Saves a new recipe for the specified tenant.
     *
     * @param tenantId The ID of the tenant.
     * @param recipe   The recipe to be saved.
     * @return The saved recipe.
     */
    public Recipe saveRecipe(String tenantId, Recipe recipe) {
        logger.info("Saving recipe for tenantId: {}", tenantId);
        recipe.setTenantId(tenantId);  // Set the tenantId
        recipe.calculateNutritionalValues();
        return recipeRepository.save(recipe);
    }

    /**
     * Updates an existing recipe for the specified tenant.
     *
     * @param tenantId     The ID of the tenant.
     * @param recipeId     The ID of the recipe to be updated.
     * @param updatedRecipe The updated recipe data.
     * @return The updated recipe.
     */
    @Transactional
    public Recipe updateRecipe(String tenantId, Long recipeId, Recipe updatedRecipe) {
        // 1. Load the existing recipe from the database for the correct tenantId
        Recipe existingRecipe = recipeRepository.findByTenantIdAndId(tenantId, recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));

        // 2. Update the recipe data
        existingRecipe.setName(updatedRecipe.getName());
        existingRecipe.setDescription(updatedRecipe.getDescription());
        existingRecipe.setCookingTime(updatedRecipe.getCookingTime());
        existingRecipe.setTags(updatedRecipe.getTags());
        existingRecipe.setFavorite(updatedRecipe.isFavorite());

        // 3. Update the ingredients
        List<Ingredient> updatedIngredients = updatedRecipe.getIngredients();

        // 4. Ensure all ingredients have the correct recipe reference
        for (Ingredient updatedIngredient : updatedIngredients) {
            if (updatedIngredient.getRecipe() == null) {
                updatedIngredient.setRecipe(existingRecipe);  // Set the recipe reference
            }
        }

        // 5. Remove ingredients no longer present in the updated recipe
        existingRecipe.getIngredients().clear();
        existingRecipe.getIngredients().addAll(updatedIngredients);

        // 6. Save the updated recipe
        existingRecipe.calculateNutritionalValues();
        return recipeRepository.save(existingRecipe);
    }

    /**
     * Deletes a recipe for the specified tenant.
     *
     * @param tenantId The ID of the tenant.
     * @param id       The ID of the recipe to be deleted.
     * @return true if the recipe was deleted, false otherwise.
     */
    @Transactional
    public boolean deleteRecipe(String tenantId, Long id) {
        logger.info("Deleting recipe with ID: {} for tenantId: {}", id, tenantId);
        Optional<Recipe> recipe = recipeRepository.findByTenantIdAndId(tenantId, id);
        if (recipe.isPresent()) {
            // Remove references to the recipe in MealPlans
            Query updateMealPlanQuery = em.createQuery("UPDATE MealPlan mp SET mp.breakfastRecipe = NULL WHERE mp.breakfastRecipe.id = :recipeId AND mp.tenantId = :tenantId");
            updateMealPlanQuery.setParameter("recipeId", id);
            updateMealPlanQuery.setParameter("tenantId", tenantId);
            int updatedBreakfastMealPlans = updateMealPlanQuery.executeUpdate();
            logger.info("{} MealPlans updated to remove breakfast reference.", updatedBreakfastMealPlans);

            updateMealPlanQuery = em.createQuery("UPDATE MealPlan mp SET mp.lunchRecipe = NULL WHERE mp.lunchRecipe.id = :recipeId AND mp.tenantId = :tenantId");
            updateMealPlanQuery.setParameter("recipeId", id);
            updateMealPlanQuery.setParameter("tenantId", tenantId);
            int updatedLunchMealPlans = updateMealPlanQuery.executeUpdate();
            logger.info("{} MealPlans updated to remove lunch reference.", updatedLunchMealPlans);

            updateMealPlanQuery = em.createQuery("UPDATE MealPlan mp SET mp.dinnerRecipe = NULL WHERE mp.dinnerRecipe.id = :recipeId AND mp.tenantId = :tenantId");
            updateMealPlanQuery.setParameter("recipeId", id);
            updateMealPlanQuery.setParameter("tenantId", tenantId);
            int updatedDinnerMealPlans = updateMealPlanQuery.executeUpdate();
            logger.info("{} MealPlans updated to remove dinner reference.", updatedDinnerMealPlans);

            // Delete the recipe
            recipeRepository.delete(recipe.get());
            logger.info("Recipe deleted");
            return true;
        }
        logger.warn("Recipe with ID {} not found", id);
        return false;
    }

    /**
     * Removes an ingredient from a recipe.
     *
     * @param tenantId    The ID of the tenant.
     * @param recipeId    The ID of the recipe.
     * @param ingredientId The ID of the ingredient to be removed.
     */
    @Transactional
    public void removeIngredientFromRecipe(String tenantId, Long recipeId, Long ingredientId) {
        // Fetch the recipe
        Recipe recipe = recipeRepository.findByTenantIdAndId(tenantId, recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Fetch the ingredient
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));

        // Remove the ingredient from the recipe
        recipe.getIngredients().remove(ingredient);
        ingredientRepository.delete(ingredient);

        // Save the recipe after the ingredient has been removed
        recipeRepository.save(recipe);
    }

    /**
     * Toggles the favorite status of a recipe.
     *
     * @param tenantId The ID of the tenant.
     * @param id       The ID of the recipe.
     * @return The updated recipe.
     */
    public Recipe toggleFavorite(String tenantId, Long id) {
        Recipe recipe = recipeRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Toggle the favorite status
        recipe.setFavorite(!recipe.isFavorite());
        recipe.calculateNutritionalValues();
        return recipeRepository.save(recipe);
    }
}
