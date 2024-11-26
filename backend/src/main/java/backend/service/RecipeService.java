package backend.service;

import backend.model.Recipe;
import backend.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public List<Recipe> searchRecipes(String name, List<String> tags) {
        logger.info("Suche nach Rezepten mit Name: {} und Tags: {}", name, tags);

        // Name und Tags zusammen
        if (name != null && tags != null && !tags.isEmpty()) {
            logger.info("Suche nach Name und Tags");
            return recipeRepository.findByNameAndTagsIn(name, tags);
        }
        // Nur Name
        else if (name != null) {
            logger.info("Suche nur nach Name");
            return recipeRepository.findByNameContainingIgnoreCase(name);
        }
        // Nur Tags
        else if (tags != null && !tags.isEmpty()) {
            logger.info("Suche nur nach Tags");
            return recipeRepository.findByTagsIn(tags);
        }
        // Keine Suchkriterien, alle Rezepte zurückgeben
        else {
            logger.info("Keine Suchkriterien angegeben, alle Rezepte werden zurückgegeben");
            return recipeRepository.findAll();
        }
    }

    public Recipe saveRecipe(Recipe recipe) {
        logger.info("Speichere Rezept: {}", recipe);
        return recipeRepository.save(recipe);
    }

    public boolean deleteRecipe(Long id) {
        logger.info("Lösche Rezept mit ID: {}", id);
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            recipeRepository.delete(recipe.get());
            logger.info("Rezept gelöscht");
            return true;
        }
        logger.warn("Rezept mit ID {} nicht gefunden", id);
        return false;
    }
}