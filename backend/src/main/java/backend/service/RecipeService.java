package backend.service;

import backend.model.Ingredient;
import backend.model.Recipe;
import backend.model.Tag;
import backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe saveRecipe(Recipe recipeBody) {
        Recipe recipe = new Recipe();
        recipe.setName(recipeBody.getName());
        recipe.setDescription(recipeBody.getDescription());
        recipe.setTags(recipeBody.getTags());  // Tags setzen
        recipe.setIngredients(recipeBody.getIngredients().stream()
                .map(ingredient -> new Ingredient(
                        ingredient.getName(),
                        ingredient.getQuantity(),
                        ingredient.getUnit(),
                        recipe))
                .collect(Collectors.toList()));
        return recipeRepository.save(recipe);  // Rezept speichern
    }

    // Alle Rezepte abrufen
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // Rezepte suchen basierend auf Name und Tags
    public List<Recipe> searchRecipes(String name, List<String> tags) {
        if (name != null && !name.isEmpty() && (tags == null || tags.isEmpty())) {
            return recipeRepository.findByNameContainingIgnoreCase(name);
        } else if (tags != null && !tags.isEmpty() && (name == null || name.isEmpty())) {
            return recipeRepository.findByTagsIn(tags);
        } else if (name != null && !name.isEmpty() && tags != null && !tags.isEmpty()) {
            return recipeRepository.findByNameContainingIgnoreCaseAndTagsIn(name, tags);
        } else {
            return recipeRepository.findAll();
        }
    }

    // Rezept löschen
    public boolean deleteRecipe(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
