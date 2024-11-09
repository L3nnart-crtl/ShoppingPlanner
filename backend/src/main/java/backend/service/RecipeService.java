package backend.service;

import backend.model.Recipe;
import backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public void saveRecipe(Recipe recipe) {
        recipeRepository.saveRecipe(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.getAllRecipes();
    }
}
