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

    public MealPlan saveMealPlan(LocalDate date, Long breakfastRecipeId, Long lunchRecipeId, Long dinnerRecipeId) {
        if (mealPlanRepository.findByDate(date).isPresent()) {
            throw new IllegalArgumentException("Für das Datum " + date + " existiert bereits ein MealPlan.");
        }

        Recipe breakfastRecipe = recipeRepository.findById(breakfastRecipeId)
                .orElseThrow(() -> new IllegalArgumentException("Frühstücksrezept nicht gefunden: " + breakfastRecipeId));
        Recipe lunchRecipe = recipeRepository.findById(lunchRecipeId)
                .orElseThrow(() -> new IllegalArgumentException("Mittagsrezept nicht gefunden: " + lunchRecipeId));
        Recipe dinnerRecipe = recipeRepository.findById(dinnerRecipeId)
                .orElseThrow(() -> new IllegalArgumentException("Abendrezept nicht gefunden: " + dinnerRecipeId));

        MealPlan mealPlan = new MealPlan(date, breakfastRecipe, lunchRecipe, dinnerRecipe);
        return mealPlanRepository.save(mealPlan);
    }

    public MealPlan getMealPlanByDate(LocalDate date) {
        return mealPlanRepository.findByDate(date).orElse(null);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public boolean deleteMealPlanByDate(String date) {
        try {
            Optional<MealPlan> mealPlan = mealPlanRepository.findByDate(LocalDate.parse(date));
            mealPlan.ifPresent(mealPlanRepository::delete);
            return mealPlan.isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}
