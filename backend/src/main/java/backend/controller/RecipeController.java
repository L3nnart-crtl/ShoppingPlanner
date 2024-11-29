package backend.controller;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.service.RecipeService;
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

    // GET: Rezepte suchen basierend auf Name und Tags
    @GetMapping("/search")
    public List<Recipe> searchRecipes(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<Tag> tags) {
        return recipeService.searchRecipes(name, tags);
    }

    // POST: Neues Rezept speichern
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
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
}