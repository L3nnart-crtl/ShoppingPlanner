package backend.service;

import backend.model.CookingStatistic;
import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Ingredient;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.MealPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CookingStatisticService {

    @Autowired
    private MealPlanRepository mealPlanRepository;

    // Method to calculate the cooking statistics
    public CookingStatistic calculateStatistics() {
        CookingStatistic statistic = new CookingStatistic();

        // Calculate and set the average portion size
        statistic.setAveragePortions(this.calculateAveragePortionSize());

        // Calculate and set the average cooking time
        statistic.setAverageCookingTime(this.calculateAverageCookingTime());

        // Calculate and set the most used tags
        Map<Tag, Long> mostUsedTags = this.calculateTagUsage();
        statistic.setAttributes(mostUsedTags);

        // Calculate and set the popular recipes
        Map<String, Long> popularRecipes = this.calculatePopularRecipes();
        statistic.setFavouriteRecipes(popularRecipes);

        // Calculate and set the most used ingredients
        Map<String, Double> topIngredients = this.calculateTopIngredients();
        statistic.setFavouriteIngredients(topIngredients);

        // Set the amount of cooked recipes
        statistic.setAmountOfCookedRecipes((long) statistic.getFavouriteRecipes().keySet().size());

        return statistic;
    }

    // Method to calculate the most used ingredients across all meal plans
    private Map<String, Double> calculateTopIngredients() {
        Map<String, Double> topIngredients = new HashMap<>();

        // Iterate over all meal plans
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            // Get the recipes from the meal plan
            List<Recipe> recipes = new ArrayList<>();
            recipes.add(mealPlan.getBreakfastRecipe());
            recipes.add(mealPlan.getLunchRecipe());
            recipes.add(mealPlan.getDinnerRecipe());

            // Process each recipe
            for (Recipe recipe : recipes) {
                List<Ingredient> ingredients = recipe.getIngredients();
                int portionSize = 0;

                // Determine the portion size of the recipe
                if (recipe.equals(mealPlan.getBreakfastRecipe())) {
                    portionSize = mealPlan.getBreakfastPortionSize();
                } else if (recipe.equals(mealPlan.getLunchRecipe())) {
                    portionSize = mealPlan.getLunchPortionSize();
                } else if (recipe.equals(mealPlan.getDinnerRecipe())) {
                    portionSize = mealPlan.getDinnerPortionSize();
                }

                // Iterate through all ingredients in the recipe
                for (Ingredient ingredient : ingredients) {
                    // Convert the quantity to double for calculation
                    double amount = parseDoubleWithCommaHandling(ingredient.getQuantity());

                    // Scale the amount based on the portion size
                    double scaledAmount = amount * portionSize;

                    // Create the key for the map, combining the ingredient name and unit
                    String key = ingredient.getName() + " (" + ingredient.getUnit().toString() + ")";

                    // Add the scaled amount to the corresponding ingredient
                    topIngredients.put(key, topIngredients.getOrDefault(key, 0.0) + scaledAmount);
                }
            }
        }

        return topIngredients;
    }

    // Helper method to safely parse numbers with commas as decimal separators
    private double parseDoubleWithCommaHandling(String input) {
        input = input.replace(",", "."); // Replace comma with a dot
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            // Handle error if the number is still invalid
            System.err.println("Invalid number format: " + input);
            throw new IllegalArgumentException("Invalid number format: " + input, e);
        }
    }

    // Method to calculate the usage of tags across all meal plans
    private Map<Tag, Long> calculateTagUsage() {
        Map<Tag, Long> mostUsedTags = new HashMap<>();

        // Iterate over all meal plans
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            List<Recipe> recipes = new ArrayList<>();
            recipes.add(mealPlan.getLunchRecipe());
            recipes.add(mealPlan.getDinnerRecipe());
            recipes.add(mealPlan.getBreakfastRecipe());

            // Process each recipe's tags
            for (Recipe recipe : recipes) {
                for (Tag tag : recipe.getTags()) {
                    mostUsedTags.put(tag, mostUsedTags.getOrDefault(tag, 0L) + 1);
                }
            }
        }
        return mostUsedTags;
    }

    // Method to calculate the average portion size across all meal plans
    private Long calculateAveragePortionSize() {
        long portionsOfAllMealplans = 0L;
        long count = 0;

        // Iterate over all meal plans to calculate the total portion sizes
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            portionsOfAllMealplans += mealPlan.getLunchPortionSize();
            portionsOfAllMealplans += mealPlan.getBreakfastPortionSize();
            portionsOfAllMealplans += mealPlan.getDinnerPortionSize();
            count++;
        }

        // Calculate and return the average portion size
        return portionsOfAllMealplans / (count * 3);
    }

    // Method to calculate the average cooking time across all meal plans
    private Long calculateAverageCookingTime() {
        long cookingTimeOfAllMealplans = 0L;
        long count = 0;

        // Iterate over all meal plans to calculate the total cooking times
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            cookingTimeOfAllMealplans += mealPlan.getBreakfastRecipe().getCookingTime();
            cookingTimeOfAllMealplans += mealPlan.getLunchRecipe().getCookingTime();
            cookingTimeOfAllMealplans += mealPlan.getDinnerRecipe().getCookingTime();
            count++;
        }

        // Calculate and return the average cooking time
        return cookingTimeOfAllMealplans / (count * 3);
    }

    // Method to calculate the popularity of recipes across all meal plans
    private Map<String, Long> calculatePopularRecipes() {
        Map<String, Long> popularRecipes = new HashMap<>();

        // Iterate over all meal plans to calculate the popularity of recipes
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {

            // Update the popularity of breakfast, lunch, and dinner recipes
            popularRecipes.put(mealPlan.getBreakfastRecipe().getName(),
                    popularRecipes.getOrDefault(mealPlan.getBreakfastRecipe().getName(), 0L) + mealPlan.getBreakfastPortionSize());

            popularRecipes.put(mealPlan.getLunchRecipe().getName(),
                    popularRecipes.getOrDefault(mealPlan.getLunchRecipe().getName(), 0L) + mealPlan.getLunchPortionSize());

            popularRecipes.put(mealPlan.getDinnerRecipe().getName(),
                    popularRecipes.getOrDefault(mealPlan.getDinnerRecipe().getName(), 0L) + mealPlan.getDinnerPortionSize());
        }
        return popularRecipes;
    }
}
