package backend.controller;

import backend.model.Recipe;
import backend.service.RecipeService;
import backend.model.Tag;
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
    public List<Recipe> searchRecipes(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) List<String> tags) {
        return recipeService.searchRecipes(name, tags);
    }

    // POST: Rezept hinzufügen
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    // DELETE: Rezept löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        boolean deleted = recipeService.deleteRecipe(id);
        if (deleted) {
            return ResponseEntity.ok().build();  // Erfolgreiche Löschung
        } else {
            return ResponseEntity.status(404)
                    .body("Rezept nicht gefunden!");  // Fehler, wenn Rezept nicht gefunden
        }
    }
}
