package backend.controller;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.service.RecipeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import backend.multitenant.tenantId.TenantContext;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // GET: Alle Rezepte abrufen
    @GetMapping
    public List<Recipe> getAllRecipes() {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        return recipeService.getAllRecipes(tenantId);
    }

    // GET: Suche nach Rezepten (Name, Tags, Kochzeit, Favoriten)
    @GetMapping("/search")
    public List<Recipe> searchRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Tag> tags,
            @RequestParam(required = false) Integer cookingTime,
            @RequestParam(required = false) boolean favorite) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        return recipeService.searchRecipes(tenantId, name, tags, cookingTime, favorite);
    }

    // POST: Neues Rezept speichern
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        return recipeService.saveRecipe(tenantId, recipe);
    }

    // PUT: Rezept aktualisieren (einschließlich Kochzeit)
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeDetails) {
        String tenantId = TenantContext.getCurrentTenant(); // tenantId holen
        Recipe updatedRecipe = recipeService.updateRecipe(tenantId, id, recipeDetails);
        return ResponseEntity.ok(updatedRecipe);
    }

    // PUT: Favoritenstatus umschalten (markieren oder nicht markieren)
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

    // DELETE: Rezept löschen
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

    // DELETE: Zutat aus einem Rezept entfernen
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
