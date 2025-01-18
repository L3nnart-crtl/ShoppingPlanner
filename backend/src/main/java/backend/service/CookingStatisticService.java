package backend.service;

import backend.model.CookingStatistic;
import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.MealPlanRepository;
import backend.multitenant.tenantId.TenantContext;  // Fügen Sie diesen Import hinzu
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CookingStatisticService {

    @Autowired
    private MealPlanRepository mealPlanRepository;

    // Method to calculate the cooking statistics
    public CookingStatistic calculateStatistics() {
        CookingStatistic statistic = new CookingStatistic();
        String tenantId = TenantContext.getCurrentTenant();  // Tenant-ID aus dem Context holen

        // Calculate and set the average portion size
        statistic.setAveragePortions(this.calculateAveragePortionSize(tenantId));

        // Calculate and set the average cooking time
        statistic.setAverageCookingTime(this.calculateAverageCookingTime(tenantId));

        // Calculate and set the most used tags
        Map<Tag, Long> mostUsedTags = this.calculateTagUsage(tenantId);
        statistic.setAttributes(mostUsedTags);

        // Calculate and set the popular recipes
        Map<String, Long> popularRecipes = this.calculatePopularRecipes(tenantId);
        statistic.setFavouriteRecipes(popularRecipes);

        // Calculate and set the most used ingredients
        Map<String, Integer> topIngredients = this.getTop20MostUsedIngredients(tenantId);
        statistic.setFavouriteIngredients(topIngredients);

        // Set the amount of cooked recipes
        statistic.setAmountOfCookedRecipes((long) statistic.getFavouriteRecipes().keySet().size());

        return statistic;
    }

    private Map<String, Integer> getTop20MostUsedIngredients(String tenantId) {
        Map<String, Integer> ingredientUsageFrequency = new HashMap<>();

        // Filter MealPlans nach tenantId
        mealPlanRepository.findByTenantId(tenantId).forEach(mealPlan -> {
            Stream.of(
                            mealPlan.getBreakfastRecipe(),
                            mealPlan.getLunchRecipe(),
                            mealPlan.getDinnerRecipe()
                    )
                    .filter(Objects::nonNull) // Skip null recipes
                    .flatMap(recipe -> recipe.getIngredients().stream()) // Get ingredients from each recipe
                    .forEach(ingredient ->
                            ingredientUsageFrequency.merge(ingredient.getName(), 1, Integer::sum)
                    );
        });

        // Sort and limit to top 20 ingredients
        return ingredientUsageFrequency.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Sort by usage frequency
                .limit(20) // Take the top 20
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // In case of key conflict (should not happen), keep the first entry
                        LinkedHashMap::new // Preserve sorted order
                ));
    }

    // Method to calculate the usage of tags across all meal plans
    private Map<Tag, Long> calculateTagUsage(String tenantId) {
        Map<Tag, Long> mostUsedTags = new HashMap<>();

        // Iterate over all meal plans for the given tenant
        for (MealPlan mealPlan : mealPlanRepository.findByTenantId(tenantId)) {
            List<Recipe> recipes = new ArrayList<>();
            if (mealPlan.getBreakfastRecipe() != null) recipes.add(mealPlan.getBreakfastRecipe());
            if (mealPlan.getLunchRecipe() != null) recipes.add(mealPlan.getLunchRecipe());
            if (mealPlan.getDinnerRecipe() != null) recipes.add(mealPlan.getDinnerRecipe());

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
    private Long calculateAveragePortionSize(String tenantId) {
        long portionsOfAllMealplans = 0L;
        long count = 0;

        // Iterate over all meal plans to calculate the total portion sizes for the given tenant
        for (MealPlan mealPlan : mealPlanRepository.findByTenantId(tenantId)) {
            portionsOfAllMealplans += mealPlan.getLunchPortionSize();
            portionsOfAllMealplans += mealPlan.getBreakfastPortionSize();
            portionsOfAllMealplans += mealPlan.getDinnerPortionSize();
            count++;
        }

        // Calculate and return the average portion size
        return portionsOfAllMealplans / (count * 3);
    }

    // Method to calculate the average cooking time across all meal plans
    private Long calculateAverageCookingTime(String tenantId) {
        long cookingTimeOfAllMealplans = 0L;
        long count = 0;

        // Iterate over all meal plans to calculate the total cooking times for the given tenant
        for (MealPlan mealPlan : mealPlanRepository.findByTenantId(tenantId)) {
            if (mealPlan.getBreakfastRecipe() != null) {
                cookingTimeOfAllMealplans += mealPlan.getBreakfastRecipe().getCookingTime();
            }
            if (mealPlan.getLunchRecipe() != null) {
                cookingTimeOfAllMealplans += mealPlan.getLunchRecipe().getCookingTime();
            }
            if (mealPlan.getDinnerRecipe() != null) {
                cookingTimeOfAllMealplans += mealPlan.getDinnerRecipe().getCookingTime();
            }
            count++;
        }

        // Calculate and return the average cooking time
        return cookingTimeOfAllMealplans / (count * 3);
    }

    // Method to calculate the popularity of recipes across all meal plans
    private Map<String, Long> calculatePopularRecipes(String tenantId) {
        Map<String, Long> popularRecipes = new HashMap<>();

        // Iterate over all meal plans for the given tenant
        for (MealPlan mealPlan : mealPlanRepository.findByTenantId(tenantId)) {
            if (mealPlan.getBreakfastRecipe() != null) {
                popularRecipes.put(mealPlan.getBreakfastRecipe().getName(),
                        popularRecipes.getOrDefault(mealPlan.getBreakfastRecipe().getName(), 0L) + mealPlan.getBreakfastPortionSize());
            }
            if (mealPlan.getLunchRecipe() != null) {
                popularRecipes.put(mealPlan.getLunchRecipe().getName(),
                        popularRecipes.getOrDefault(mealPlan.getLunchRecipe().getName(), 0L) + mealPlan.getLunchPortionSize());
            }
            if (mealPlan.getDinnerRecipe() != null) {
                popularRecipes.put(mealPlan.getDinnerRecipe().getName(),
                        popularRecipes.getOrDefault(mealPlan.getDinnerRecipe().getName(), 0L) + mealPlan.getDinnerPortionSize());
            }
        }
        return popularRecipes;
    }
}
