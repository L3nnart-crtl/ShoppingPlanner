package backend.service;

import backend.model.Recipe.Ingredient;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.IngredientRepository;
import backend.repository.RecipeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    @PersistenceContext
    private EntityManager em;

    public RecipeService(RecipeRepository recipeRepository, final IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public List<Recipe> searchRecipes(String name, List<Tag> tags, Integer maxCookingTime) {
        logger.info("Suche nach Rezepten mit Name: {}, Tags: {}, und maximaler Kochzeit: {}", name, tags, maxCookingTime);

        List<Recipe> recipes;

        if (name != null && tags != null && !tags.isEmpty()) {
            logger.info("Suche nach Name und allen Tags");
            // Hier wird auch die Anzahl der Tags übergeben
            recipes = recipeRepository.findByNameContainingIgnoreCaseAndTagsIn(name, tags, tags.size());
        } else if (name != null) {
            logger.info("Suche nur nach Name");
            recipes = recipeRepository.findByNameContainingIgnoreCase(name);
        } else if (tags != null && !tags.isEmpty()) {
            logger.info("Suche nur nach Tags");
            // Hier wird nach allen Tags gefiltert
            recipes = recipeRepository.findByTagsIn(tags);

            // Filtere nach Rezepten, die alle Tags enthalten
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getTags().containsAll(tags))
                    .collect(Collectors.toList());
        } else {
            logger.info("Keine Suchkriterien angegeben, alle Rezepte werden zurückgegeben");
            recipes = recipeRepository.findAll();
        }

        // Kochzeit filtern, falls angegeben
        if (maxCookingTime != null) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getCookingTime() != null && recipe.getCookingTime() <= maxCookingTime)
                    .toList();
        }

        return recipes;
    }



    public Recipe saveRecipe(Recipe recipe) {
        logger.info("Speichere Rezept: {}", recipe);
        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipe) {
        // 1. Rezept aus der Datenbank laden
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: " + recipeId));

        // 2. Rezept-Daten aktualisieren
        existingRecipe.setName(updatedRecipe.getName());
        existingRecipe.setDescription(updatedRecipe.getDescription());
        existingRecipe.setCookingTime(updatedRecipe.getCookingTime());
        existingRecipe.setTags(updatedRecipe.getTags());

        // 3. Zutaten (Ingredients) aktualisieren
        List<Ingredient> updatedIngredients = updatedRecipe.getIngredients();

        // 4. Sicherstellen, dass alle Zutaten die Rezept-Referenz korrekt gesetzt haben
        for (Ingredient updatedIngredient : updatedIngredients) {
            if (updatedIngredient.getRecipe() == null) {
                updatedIngredient.setRecipe(existingRecipe);  // Recipe-Referenz setzen
            }
        }

        // 5. Zutaten aus dem bestehenden Rezept entfernen, die nicht mehr vorhanden sind
        existingRecipe.getIngredients().removeIf(existingIngredient ->
                !updatedIngredients.contains(existingIngredient));

        // 6. Neue Zutaten zum Rezept hinzufügen
        for (Ingredient updatedIngredient : updatedIngredients) {
            if (!existingRecipe.getIngredients().contains(updatedIngredient)) {
                existingRecipe.getIngredients().add(updatedIngredient);
            }
        }

        // 7. Speichern des aktualisierten Rezeptes
        return recipeRepository.save(existingRecipe);
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

    @Transactional
    public void removeIngredientFromRecipe(Long recipeId, Long ingredientId) {
        // Rezept abrufen
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        // Zutat abrufen
        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));

        // Zutat vom Rezept entfernen
        recipe.getIngredients().remove(ingredient);
        ingredientRepository.delete(ingredient);

        // Rezept speichern, nachdem die Zutat entfernt wurde
        recipeRepository.save(recipe);
    }
}
