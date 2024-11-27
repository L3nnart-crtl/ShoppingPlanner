package backend.service;

import backend.model.Recipe;
import backend.model.Tag;
import backend.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;

    @PersistenceContext
    private EntityManager em;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public List<Recipe> searchRecipes(String name, List<Tag> tags) {
        logger.info("Suche nach Rezepten mit Name: {} und Tags: {}", name, tags);

        if (name != null && tags != null && !tags.isEmpty()) {
            logger.info("Suche nach Name und Tags");

            // Hole alle Rezepte, die den Namen und Tags matchen
            List<Recipe> recipes = recipeRepository.findByNameAndTagsIn(name, tags);

            // Filtere nur die Rezepte, die alle Tags enthalten
            return recipes.stream()
                    .filter(recipe -> recipe.getTags().containsAll(tags))
                    .toList();
        } else if (name != null) {
            logger.info("Suche nur nach Name");
            return recipeRepository.findByNameContainingIgnoreCase(name);
        } else if (tags != null && !tags.isEmpty()) {
            logger.info("Suche nur nach Tags");

            // Suche Rezepte basierend auf Tags und filtere für vollständige Übereinstimmung
            List<Recipe> recipes = recipeRepository.findByTagsIn(tags);
            return recipes.stream()
                    .filter(recipe -> recipe.getTags().containsAll(tags))
                    .toList();
        } else {
            logger.info("Keine Suchkriterien angegeben, alle Rezepte werden zurückgegeben");
            return recipeRepository.findAll();
        }
    }

    public Recipe saveRecipe(Recipe recipe) {
        logger.info("Speichere Rezept: {}", recipe);
        return recipeRepository.save(recipe);
    }

    @Transactional
    public boolean deleteRecipe(Long id) {
        logger.info("Lösche Rezept mit ID: {}", id);
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            // Prüfen, ob das Rezept von einem MealPlan referenziert wird
            Query query = em.createQuery("SELECT COUNT(mp) FROM MealPlan mp WHERE mp.dinnerRecipe.id = :recipeId");
            query.setParameter("recipeId", id);
            long count = (long) query.getSingleResult();

            if (count > 0) {
                logger.warn("Rezept mit ID {} kann nicht gelöscht werden, da es noch einem MealPlan zugeordnet ist", id);
                return false;
            }

            // Lösche recipe Eintrag
            recipeRepository.delete(recipe.get());
            logger.info("Rezept gelöscht");
            return true;
        }
        logger.warn("Rezept mit ID {} nicht gefunden", id);
        return false;
    }
}