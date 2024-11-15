package backend.service;

import backend.model.MealPlan;
import backend.model.Recipe;
import backend.repository.MealPlanRepository;
import backend.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository, RecipeRepository recipeRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
    }

    // Mahlzeitenplan für einen bestimmten Tag erstellen
    public MealPlan saveMealPlan(LocalDate date, Long breakfastRecipeId, Long lunchRecipeId, Long dinnerRecipeId) {
        // Überprüfen, ob bereits ein MealPlan für dieses Datum existiert
        Optional<MealPlan> existingMealPlan = mealPlanRepository.findByDate(date);
        if (existingMealPlan.isPresent()) {
            throw new IllegalArgumentException("Für das Datum " + date + " existiert bereits ein MealPlan.");
        }

        // Rezepte für Frühstück, Mittagessen und Abendessen abrufen
        Optional<Recipe> breakfastRecipe = recipeRepository.findById(breakfastRecipeId);
        Optional<Recipe> lunchRecipe = recipeRepository.findById(lunchRecipeId);
        Optional<Recipe> dinnerRecipe = recipeRepository.findById(dinnerRecipeId);

        if (breakfastRecipe.isPresent() && lunchRecipe.isPresent() && dinnerRecipe.isPresent()) {
            MealPlan mealPlan = new MealPlan(date, breakfastRecipe.get(), lunchRecipe.get(), dinnerRecipe.get());
            return mealPlanRepository.save(mealPlan);
        }
        return null; // Rückgabe von null, wenn eines der Rezepte nicht gefunden wurde
    }

    // Mahlzeitenplan für ein bestimmtes Datum abfragen
    public MealPlan getMealPlanByDate(LocalDate date) {
        return mealPlanRepository.findByDate(date).orElse(null);
    }

    // Alle Mahlzeitenpläne abfragen
    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public boolean deleteMealPlanByDate(String date) {
        // Deine Logik hier, um den MealPlan anhand des Datums zu finden und zu löschen.
        Optional<MealPlan> mealPlan = mealPlanRepository.findByDate(LocalDate.parse(date));
        if (mealPlan.isPresent()) {
            mealPlanRepository.delete(mealPlan.get());
            return true;
        }
        return false;
    }

}
