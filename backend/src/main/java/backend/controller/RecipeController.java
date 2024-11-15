package backend.controller;

import backend.model.Recipe;
import backend.service.RecipeService;
import org.springframework.http.HttpStatus;
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

    // POST: Rezept hinzufügen
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        return recipeService.saveRecipe(recipe);
    }

    // GET: Alle Rezepte abrufen
    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    // DELETE: Rezept löschen
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        boolean deleted = recipeService.deleteRecipe(id);
        if (deleted) {
            return ResponseEntity.ok().build();  // Erfolgreiche Löschung
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Rezept nicht gefunden!");  // Fehler, wenn Rezept nicht gefunden
        }
    }
}
