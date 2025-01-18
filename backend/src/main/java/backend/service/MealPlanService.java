package backend.service;

import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Recipe;
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

    // Constructor to inject the MealPlanRepository and RecipeRepository dependencies
    public MealPlanService(MealPlanRepository mealPlanRepository, RecipeRepository recipeRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
    }

    // Method to save a new meal plan for a specific date
    public MealPlan saveMealPlan(String tenantId, LocalDate date, Long breakfastRecipeId, int breakfastPortionSize,
                                 Long lunchRecipeId, int lunchPortionSize,
                                 Long dinnerRecipeId, int dinnerPortionSize) {

        // Check if a meal plan already exists for the specified date and tenantId
        if (mealPlanRepository.findByTenantIdAndDate(tenantId, date).isPresent()) {
            throw new IllegalArgumentException("A MealPlan already exists for the date " + date + " and tenantId " + tenantId);
        }

        // Retrieve the recipes for breakfast, lunch, and dinner by their IDs if provided
        Recipe breakfastRecipe = (breakfastRecipeId != null) ?
                recipeRepository.findByTenantIdAndId(tenantId, breakfastRecipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Breakfast recipe not found: " + breakfastRecipeId)) : null;

        Recipe lunchRecipe = (lunchRecipeId != null) ?
                recipeRepository.findByTenantIdAndId(tenantId, lunchRecipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Lunch recipe not found: " + lunchRecipeId)) : null;

        Recipe dinnerRecipe = (dinnerRecipeId != null) ?
                recipeRepository.findByTenantIdAndId(tenantId, dinnerRecipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Dinner recipe not found: " + dinnerRecipeId)) : null;

        // Create a new MealPlan object with the provided details
        MealPlan mealPlan = new MealPlan(tenantId, date, breakfastRecipe, breakfastPortionSize,
                lunchRecipe, lunchPortionSize, dinnerRecipe, dinnerPortionSize);

        // Save the new meal plan in the repository
        return mealPlanRepository.save(mealPlan);
    }

    // Method to update an existing meal plan for a specific date and tenantId
    public MealPlan updateMealPlan(String tenantId, LocalDate date, Long breakfastRecipeId, int breakfastPortionSize,
                                   Long lunchRecipeId, int lunchPortionSize,
                                   Long dinnerRecipeId, int dinnerPortionSize) {

        // Retrieve the existing meal plan for the given date and tenantId
        MealPlan existingMealPlan = mealPlanRepository.findByTenantIdAndDate(tenantId, date)
                .orElseThrow(() -> new IllegalArgumentException("No MealPlan found for the date " + date + " and tenantId " + tenantId));

        // Retrieve the updated recipes by their IDs if provided
        Recipe breakfastRecipe = (breakfastRecipeId != null) ?
                recipeRepository.findByTenantIdAndId(tenantId, breakfastRecipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Breakfast recipe not found: " + breakfastRecipeId)) : null;

        Recipe lunchRecipe = (lunchRecipeId != null) ?
                recipeRepository.findByTenantIdAndId(tenantId, lunchRecipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Lunch recipe not found: " + lunchRecipeId)) : null;

        Recipe dinnerRecipe = (dinnerRecipeId != null) ?
                recipeRepository.findByTenantIdAndId(tenantId, dinnerRecipeId)
                        .orElseThrow(() -> new IllegalArgumentException("Dinner recipe not found: " + dinnerRecipeId)) : null;

        // Update the existing meal plan with the new recipes and portion sizes
        existingMealPlan.setBreakfastRecipe(breakfastRecipe);
        existingMealPlan.setBreakfastPortionSize(breakfastPortionSize);
        existingMealPlan.setLunchRecipe(lunchRecipe);
        existingMealPlan.setLunchPortionSize(lunchPortionSize);
        existingMealPlan.setDinnerRecipe(dinnerRecipe);
        existingMealPlan.setDinnerPortionSize(dinnerPortionSize);

        // Save the updated meal plan in the repository
        return mealPlanRepository.save(existingMealPlan);
    }

    // Method to retrieve a meal plan by its date and tenantId
    public MealPlan getMealPlanByDate(String tenantId, LocalDate date) {
        return mealPlanRepository.findByTenantIdAndDate(tenantId, date).orElse(null);
    }

    // Method to retrieve all meal plans for a specific tenantId
    public List<MealPlan> getAllMealPlans(String tenantId) {
        return mealPlanRepository.findByTenantId(tenantId);
    }

    // Method to delete a meal plan by its date and tenantId
    public boolean deleteMealPlanByDate(String tenantId, String date) {
        try {
            // Attempt to find the meal plan for the specified date and tenantId
            Optional<MealPlan> mealPlan = mealPlanRepository.findByTenantIdAndDate(tenantId, LocalDate.parse(date));

            // If the meal plan exists, delete it
            mealPlan.ifPresent(mealPlanRepository::delete);
            return mealPlan.isPresent();
        } catch (Exception e) {
            // Return false if an error occurs (e.g., invalid date format)
            return false;
        }
    }
}
