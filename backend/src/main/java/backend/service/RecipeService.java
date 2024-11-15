package backend.service;

import backend.model.Ingredient;
import backend.model.Recipe;
import backend.repository.IngredientRepository;
import backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository, IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Recipe saveRecipe(Recipe recipeBody) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeBody.getName());
        recipe.setDescription(recipeBody.getDescription());
        List<Ingredient> ingredientsOfRecipe = new ArrayList<>();
        recipeRepository.save(recipe);
        // Erstelle das Rezept, aber speichere es noch nicht
        for (Ingredient ingredient : recipeBody.getIngredients()) {
            // Zutaten mit dem gespeicherten Rezept verknüpfen
            Ingredient ingredientToAdd = new Ingredient(ingredient.getName(), ingredient.getQuantity(), recipe);
            ingredientRepository.save(ingredientToAdd);
            ingredientsOfRecipe.add(ingredientToAdd);
        }
        recipe.setIngredients(ingredientsOfRecipe);
        recipeRepository.save(recipe);
        // Speichere das Rezept und erhalte das gespeicherte Rezept mit der ID

        return recipe; // Gebe das gespeicherte Rezept zurück
    }

    // Alle Rezepte abrufen
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Rezept löschen
    public boolean deleteRecipe(Long id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            // Löscht alle Zutaten des Rezepts
            List<Ingredient> ingredients = recipe.get().getIngredients();
            for (Ingredient ingredient : ingredients) {
                ingredientRepository.delete(ingredient); // Löscht jede Zutat
            }

            // Löscht das Rezept
            recipeRepository.delete(recipe.get());
            return true;
        }
        return false;
    }
}
