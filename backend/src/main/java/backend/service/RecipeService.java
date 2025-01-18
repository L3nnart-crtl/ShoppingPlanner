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

@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    @PersistenceContext
    private EntityManager em;

    public RecipeService(RecipeRepository recipeRepository, final IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Recipe> getAllRecipes(String tenantId) {
        return recipeRepository.findByTenantId(tenantId);
    }

    public List<Recipe> searchRecipes(String tenantId, String name, List<Tag> tags, Integer maxCookingTime, Boolean favorite) {
        logger.info("Searching recipes for tenantId: {}, name: {}, tags: {}, max cooking time: {}, favorite: {}", tenantId, name, tags, maxCookingTime, favorite);

        List<Recipe> recipes = new ArrayList<>();

        if (name != null && tags != null && !tags.isEmpty()) {
            recipes = recipeRepository.findByTenantIdAndNameContainingIgnoreCaseAndTagsIn(tenantId, name, tags);
        } else if (name != null) {
            recipes = recipeRepository.findByTenantIdAndNameContainingIgnoreCase(tenantId, name);
        } else if (tags != null && !tags.isEmpty()) {
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

    public Recipe saveRecipe(String tenantId, Recipe recipe) {
        logger.info("Speichere Rezept für tenantId: {}", tenantId);
        recipe.setTenantId(tenantId);  // Setze den tenantId
        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(String tenantId, Long recipeId, Recipe updatedRecipe) {
        // 1. Rezept aus der Datenbank laden, nur für den richtigen tenantId
        Recipe existingRecipe = recipeRepository.findByTenantIdAndId(tenantId, recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));

        // 2. Rezept-Daten aktualisieren
        existingRecipe.setName(updatedRecipe.getName());
        existingRecipe.setDescription(updatedRecipe.getDescription());
        existingRecipe.setCookingTime(updatedRecipe.getCookingTime());
        existingRecipe.setTags(updatedRecipe.getTags());
        existingRecipe.setFavorite(updatedRecipe.isFavorite());

        // 3. Zutaten (Ingredients) aktualisieren
        List<Ingredient> updatedIngredients = updatedRecipe.getIngredients();

        // 4. Sicherstellen, dass alle Zutaten die Rezept-Referenz korrekt gesetzt haben
        for (Ingredient updatedIngredient : updatedIngredients) {
            if (updatedIngredient.getRecipe() == null) {
                updatedIngredient.setRecipe(existingRecipe);  // Recipe-Referenz setzen
            }
        }

        // 5. Zutaten aus dem bestehenden Rezept entfernen, die nicht mehr vorhanden sind
        existingRecipe.getIngredients().removeIf(existingIngredient ->
                !updatedIngredients.contains(existingIngredient));

        // 6. Neue Zutaten zum Rezept hinzufügen
        for (Ingredient updatedIngredient : updatedIngredients) {
            if (!existingRecipe.getIngredients().contains(updatedIngredient)) {
                existingRecipe.getIngredients().add(updatedIngredient);
            }
        }

        // 7. Speichern des aktualisierten Rezeptes
        return recipeRepository.save(existingRecipe);
    }

    @Transactional
    public boolean deleteRecipe(String tenantId, Long id) {
        logger.info("Lösche Rezept mit ID: {} für tenantId: {}", id, tenantId);
        Optional<Recipe> recipe = recipeRepository.findByTenantIdAndId(tenantId, id);
        if (recipe.isPresent()) {
            // Entferne alle Verweise auf das Rezept in MealPlans
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

            // Lösche das Rezept
            recipeRepository.delete(recipe.get());
            logger.info("Rezept gelöscht");
            return true;
        }
        logger.warn("Rezept mit ID {} nicht gefunden", id);
        return false;
    }


    @Transactional
    public void removeIngredientFromRecipe(String tenantId, Long recipeId, Long ingredientId) {
        // Rezept abrufen
        Recipe recipe = recipeRepository.findByTenantIdAndId(tenantId, recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Zutat abrufen
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));

        // Zutat vom Rezept entfernen
        recipe.getIngredients().remove(ingredient);
        ingredientRepository.delete(ingredient);

        // Rezept speichern, nachdem die Zutat entfernt wurde
        recipeRepository.save(recipe);
    }

    // Favoritenstatus umschalten (markieren oder nicht markieren)
    public Recipe toggleFavorite(String tenantId, Long id) {
        Recipe recipe = recipeRepository.findByTenantIdAndId(tenantId, id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Favoritenstatus umschalten
        recipe.setFavorite(!recipe.isFavorite());
        return recipeRepository.save(recipe);
    }
}
