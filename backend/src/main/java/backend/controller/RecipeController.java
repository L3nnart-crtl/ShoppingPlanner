package backend.controller;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.service.RecipeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.multitenant.tenantId.TenantContext;
import java.util.List;

/**
 * Controller for managing recipes. Provides endpoints to get, search, add, update, and delete recipes.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * Constructor to initialize the RecipeController with a RecipeService.
     *
     * @param recipeService The service used for managing recipes.
     */
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Retrieves all recipes for the current tenant.
     *
     * @return A list of all recipes.
     */
    @GetMapping
    public List<Recipe> getAllRecipes() {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        return recipeService.getAllRecipes(tenantId);
    }

    /**
     * Searches for recipes based on provided parameters (name, tags, cooking time, favorite).
     *
     * @param name Optional name of the recipe to search for.
     * @param tags Optional list of tags to filter the recipes.
     * @param cookingTime Optional cooking time to filter the recipes.
     * @param favorite Optional flag to search for favorite recipes.
     * @return A list of recipes matching the search criteria.
     */
    @GetMapping("/search")
    public List<Recipe> searchRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Tag> tags,
            @RequestParam(required = false) Integer cookingTime,
            @RequestParam(required = false) boolean favorite) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        return recipeService.searchRecipes(tenantId, name, tags, cookingTime, favorite);
    }

    /**
     * Adds a new recipe for the current tenant.
     *
     * @param recipe The recipe object to be added.
     * @return The added recipe.
     */
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        return recipeService.saveRecipe(tenantId, recipe);
    }

    /**
     * Updates an existing recipe with new details.
     *
     * @param id The ID of the recipe to be updated.
     * @param recipeDetails The updated recipe details.
     * @return The updated recipe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeDetails) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        Recipe updatedRecipe = recipeService.updateRecipe(tenantId, id, recipeDetails);
        return ResponseEntity.ok(updatedRecipe);
    }

    /**
     * Toggles the favorite status of a recipe.
     *
     * @param id The ID of the recipe whose favorite status is to be toggled.
     * @return The updated recipe with the new favorite status.
     */
    @PutMapping("/{id}/favorite")
    public ResponseEntity<Recipe> toggleFavorite(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        try {
            Recipe updatedRecipe = recipeService.toggleFavorite(tenantId, id);
            return ResponseEntity.ok(updatedRecipe);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null); // Rezept nicht gefunden
        }
    }

    /**
     * Deletes a recipe by its ID.
     *
     * @param id The ID of the recipe to be deleted.
     * @return A response indicating whether the deletion was successful or if the recipe could not be deleted.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        boolean isDeleted = recipeService.deleteRecipe(tenantId, id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(409).body("Das Rezept kann nicht gelöscht werden, da es noch einem MealPlan zugeordnet ist.");
        }
    }

    /**
     * Removes an ingredient from a recipe.
     *
     * @param recipeId The ID of the recipe from which the ingredient should be removed.
     * @param ingredientId The ID of the ingredient to be removed.
     * @return A response indicating whether the removal was successful or if the recipe or ingredient was not found.
     */
    @DeleteMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<String> removeIngredientFromRecipe(@PathVariable Long recipeId, @PathVariable Long ingredientId) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        try {
            recipeService.removeIngredientFromRecipe(tenantId, recipeId, ingredientId);
            return ResponseEntity.noContent().build(); // Erfolgreiches Entfernen
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Zutat oder Rezept nicht gefunden
        }
    }
}
