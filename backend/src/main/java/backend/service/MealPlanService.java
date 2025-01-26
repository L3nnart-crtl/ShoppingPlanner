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

    /**
     * Saves a new meal plan for a specific tenant and date.
     *
     * @param tenantId the tenant's ID
     * @param date the date for the meal plan
     * @param breakfastRecipeId the ID of the breakfast recipe
     * @param breakfastPortionSize the portion size for breakfast
     * @param lunchRecipeId the ID of the lunch recipe
     * @param lunchPortionSize the portion size for lunch
     * @param dinnerRecipeId the ID of the dinner recipe
     * @param dinnerPortionSize the portion size for dinner
     * @return the saved MealPlan object
     * @throws IllegalArgumentException if a meal plan already exists for the given date and tenantId
     */
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

    /**
     * Updates an existing meal plan for a specific tenant and date.
     *
     * @param tenantId the tenant's ID
     * @param date the date for the meal plan
     * @param breakfastRecipeId the ID of the breakfast recipe
     * @param breakfastPortionSize the portion size for breakfast
     * @param lunchRecipeId the ID of the lunch recipe
     * @param lunchPortionSize the portion size for lunch
     * @param dinnerRecipeId the ID of the dinner recipe
     * @param dinnerPortionSize the portion size for dinner
     * @return the updated MealPlan object
     * @throws IllegalArgumentException if no meal plan exists for the given date and tenantId
     */
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

    /**
     * Retrieves a meal plan by its date and tenantId.
     *
     * @param tenantId the tenant's ID
     * @param date the date for the meal plan
     * @return the MealPlan object, or null if no meal plan exists for the given date and tenantId
     */
    public MealPlan getMealPlanByDate(String tenantId, LocalDate date) {
        return mealPlanRepository.findByTenantIdAndDate(tenantId, date).orElse(null);
    }

    /**
     * Retrieves all meal plans for a specific tenantId.
     *
     * @param tenantId the tenant's ID
     * @return a list of MealPlan objects for the given tenantId
     */
    public List<MealPlan> getAllMealPlans(String tenantId) {
        return mealPlanRepository.findByTenantId(tenantId);
    }

    /**
     * Deletes a meal plan by its date and tenantId.
     *
     * @param tenantId the tenant's ID
     * @param date the date for the meal plan
     * @return true if the meal plan was deleted, false otherwise
     */
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
