package backend.controller;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.service.RecipeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return recipeService.getAllRecipes();
    }

    // GET: Rezepte suchen basierend auf Name, Tags und Kochzeit
    @GetMapping("/search")
    public List<Recipe> searchRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Tag> tags,
            @RequestParam(required = false) Integer maxCookingTime) {
        return recipeService.searchRecipes(name, tags, maxCookingTime);
    }

    // POST: Neues Rezept speichern
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    // PUT: Rezept aktualisieren (einschließlich Kochzeit)
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @RequestBody Recipe recipeDetails) {
        Recipe updatedRecipe = recipeService.updateRecipe(id, recipeDetails);
        return ResponseEntity.ok(updatedRecipe);
    }

    // DELETE: Rezept löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        boolean isDeleted = recipeService.deleteRecipe(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(409).body("Das Rezept kann nicht gelöscht werden, da es noch einem MealPlan zugeordnet ist.");
        }
    }

    // DELETE: Zutat aus einem Rezept entfernen
    @DeleteMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<String> removeIngredientFromRecipe(@PathVariable Long recipeId, @PathVariable Long ingredientId) {
        try {
            recipeService.removeIngredientFromRecipe(recipeId, ingredientId);
            return ResponseEntity.noContent().build(); // Erfolgreiches Entfernen
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // Zutat oder Rezept nicht gefunden
        }
    }
}
