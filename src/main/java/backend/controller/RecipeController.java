package backend.controller;

import backend.model.Recipe;
import backend.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = {
        "http://localhost",
        "http://[2001:7c0:2320:1:f816:3eff:fe50:6f6d]"
})
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<String> addRecipe(@RequestBody Recipe recipe) {
        recipeService.saveRecipe(recipe);
        return new ResponseEntity<>("Rezept hinzugefügt!", HttpStatus.CREATED);
    }

    @GetMapping
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
}
