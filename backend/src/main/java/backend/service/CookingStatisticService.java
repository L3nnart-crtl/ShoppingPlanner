package backend.service;

import backend.model.CookingStatistic.CookingStatistic;
import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.MealPlanRepository;
import backend.multitenant.tenantId.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CookingStatisticService {

    @Autowired
    private MealPlanRepository mealPlanRepository;

    /**
     * Calculates cooking statistics for a specified date range.
     *
     * @param startDate the start date of the range
     * @param endDate the end date of the range
     * @return CookingStatistic containing various cooking statistics
     */
    public CookingStatistic calculateStatisticsForDateRange(LocalDate startDate, LocalDate endDate) {
        CookingStatistic statistic = new CookingStatistic();
        String tenantId = TenantContext.getCurrentTenant(); // Tenant ID from context

        // Fetch meal plans within the specified date range
        List<MealPlan> mealPlans = mealPlanRepository.findByTenantIdAndDateBetween(tenantId, startDate, endDate);

        if (mealPlans.isEmpty()) {
            // Return an empty statistic if no meal plans are found
            return statistic;
        }

        // Calculate average portion size for the period
        statistic.setAveragePortions(this.calculateAveragePortionSize(mealPlans));

        // Calculate average cooking time for the period
        statistic.setAverageCookingTime(this.calculateAverageCookingTime(mealPlans));

        // Calculate most used tags for the period
        Map<Tag, Long> mostUsedTags = this.calculateTagUsage(mealPlans);
        statistic.setAttributes(mostUsedTags);

        // Calculate popular recipes for the period
        Map<String, Long> popularRecipes = this.calculatePopularRecipes(mealPlans);
        statistic.setFavouriteRecipes(popularRecipes);

        // Calculate most used ingredients for the period
        Map<String, Integer> topIngredients = this.getTop20MostUsedIngredients(mealPlans);
        statistic.setFavouriteIngredients(topIngredients);

        // Set the amount of cooked recipes for the period
        statistic.setAmountOfCookedRecipes((long) statistic.getFavouriteRecipes().keySet().size());

        // Calculate average calories per recipe for the period
        statistic.setAverageCaloriesPerRecipe(this.calculateAverageCaloriesPerRecipeStatistic(mealPlans));

        // Calculate average calories per day for the period
        statistic.setAverageCaloriesPerDay(this.calculateAverageCaloriesPerDay(mealPlans));

        // Calculate average nutritional distribution per recipe for the period
        statistic.setAverageNutrientDistributionPerRecipe(this.calculateAverageNutritionalDistributionPerPortion(mealPlans));

        // Calculate average nutritional distribution per day for the period
        statistic.setAverageNutrientDistributionPerDay(this.calculateAverageNutritionalDistributionPerDay(mealPlans));

        return statistic;
    }

    /**
     * Calculates the average calories per recipe from a list of meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return the average calories per recipe
     */
    private Double calculateAverageCaloriesPerRecipeStatistic(List<MealPlan> mealPlans) {
        double totalCalories = 0.0;
        List<String> uniqueRecipeNames = new ArrayList<>();

        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getBreakfastRecipe() != null) {
                totalCalories += mealPlan.getBreakfastRecipe().getTotalCalories();
                uniqueRecipeNames.add(mealPlan.getBreakfastRecipe().getName());
            }
            if (mealPlan.getLunchRecipe() != null) {
                totalCalories += mealPlan.getLunchRecipe().getTotalCalories();
                uniqueRecipeNames.add(mealPlan.getLunchRecipe().getName());
            }
            if (mealPlan.getDinnerRecipe() != null) {
                totalCalories += mealPlan.getDinnerRecipe().getTotalCalories();
                uniqueRecipeNames.add(mealPlan.getDinnerRecipe().getName());
            }
        }

        if (uniqueRecipeNames.size() == 0) return 0.0;
        return totalCalories / uniqueRecipeNames.size();
    }

    /**
     * Calculates the average calories consumed per day from a list of meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return the average calories per day
     */
    private Double calculateAverageCaloriesPerDay(List<MealPlan> mealPlans) {
        double totalCaloriesPerDay = 0.0;
        long count = 0;

        for (MealPlan mealPlan : mealPlans) {
            double dailyCalories = 0.0;

            if (mealPlan.getBreakfastRecipe() != null) {
                dailyCalories += mealPlan.getBreakfastRecipe().getTotalCalories() * mealPlan.getBreakfastPortionSize();
            }
            if (mealPlan.getLunchRecipe() != null) {
                dailyCalories += mealPlan.getLunchRecipe().getTotalCalories() * mealPlan.getLunchPortionSize();
            }
            if (mealPlan.getDinnerRecipe() != null) {
                dailyCalories += mealPlan.getDinnerRecipe().getTotalCalories() * mealPlan.getDinnerPortionSize();
            }

            totalCaloriesPerDay += dailyCalories;
            count++;
        }

        if (count == 0) return 0.0;
        return totalCaloriesPerDay / count;
    }

    /**
     * Calculates the average nutritional distribution per portion across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return a map containing the average nutritional distribution
     */
    private Map<String, Double> calculateAverageNutritionalDistributionPerPortion(List<MealPlan> mealPlans) {
        Map<String, Double> averageDistribution = new HashMap<>();
        double totalProteinPercentage = 0.0;
        double totalFatPercentage = 0.0;
        double totalCarbohydratePercentage = 0.0;
        double totalPortions = 0;
        long count = 0;

        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getBreakfastRecipe() != null) {
                totalProteinPercentage += mealPlan.getBreakfastRecipe().getProteinPercentage() * mealPlan.getBreakfastPortionSize();
                totalFatPercentage += mealPlan.getBreakfastRecipe().getFatPercentage() * mealPlan.getBreakfastPortionSize();
                totalCarbohydratePercentage += mealPlan.getBreakfastRecipe().getCarbohydratePercentage() * mealPlan.getBreakfastPortionSize();
            }
            if (mealPlan.getLunchRecipe() != null) {
                totalProteinPercentage += mealPlan.getLunchRecipe().getProteinPercentage() * mealPlan.getLunchPortionSize();
                totalFatPercentage += mealPlan.getLunchRecipe().getFatPercentage() * mealPlan.getLunchPortionSize();
                totalCarbohydratePercentage += mealPlan.getLunchRecipe().getCarbohydratePercentage() * mealPlan.getLunchPortionSize();
            }
            if (mealPlan.getDinnerRecipe() != null) {
                totalProteinPercentage += mealPlan.getDinnerRecipe().getProteinPercentage();
                totalFatPercentage += mealPlan.getDinnerRecipe().getFatPercentage();
                totalCarbohydratePercentage += mealPlan.getDinnerRecipe().getCarbohydratePercentage();
            }
            totalPortions += mealPlan.getBreakfastPortionSize() + mealPlan.getLunchPortionSize() + mealPlan.getDinnerPortionSize();
        }

        if (totalPortions > 0) {
            averageDistribution.put("protein", totalProteinPercentage / totalPortions);
            averageDistribution.put("fat", totalFatPercentage / totalPortions);
            averageDistribution.put("carbohydrate", totalCarbohydratePercentage / totalPortions);
        }

        return averageDistribution;
    }

    /**
     * Calculates the average nutritional distribution per day across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return a map containing the average nutritional distribution per day
     */
    private Map<String, Double> calculateAverageNutritionalDistributionPerDay(List<MealPlan> mealPlans) {
        Map<String, Double> averageDistribution = new HashMap<>();
        double totalProteinPercentage = 0.0;
        double totalFatPercentage = 0.0;
        double totalCarbohydratePercentage = 0.0;
        long count = 0;

        for (MealPlan mealPlan : mealPlans) {
            double dailyProtein = 0.0;
            double dailyFat = 0.0;
            double dailyCarbohydrate = 0.0;

            if (mealPlan.getBreakfastRecipe() != null) {
                dailyProtein += mealPlan.getBreakfastRecipe().getProteinPercentage() * mealPlan.getBreakfastPortionSize();
                dailyFat += mealPlan.getBreakfastRecipe().getFatPercentage() * mealPlan.getBreakfastPortionSize();
                dailyCarbohydrate += mealPlan.getBreakfastRecipe().getCarbohydratePercentage() * mealPlan.getBreakfastPortionSize();
            }
            if (mealPlan.getLunchRecipe() != null) {
                dailyProtein += mealPlan.getLunchRecipe().getProteinPercentage() * mealPlan.getLunchPortionSize();
                dailyFat += mealPlan.getLunchRecipe().getFatPercentage() * mealPlan.getLunchPortionSize();
                dailyCarbohydrate += mealPlan.getLunchRecipe().getCarbohydratePercentage() * mealPlan.getLunchPortionSize();
            }
            if (mealPlan.getDinnerRecipe() != null) {
                dailyProtein += mealPlan.getDinnerRecipe().getProteinPercentage() * mealPlan.getDinnerPortionSize();
                dailyFat += mealPlan.getDinnerRecipe().getFatPercentage() * mealPlan.getDinnerPortionSize();
                dailyCarbohydrate += mealPlan.getDinnerRecipe().getCarbohydratePercentage() * mealPlan.getDinnerPortionSize();
            }

            totalProteinPercentage += dailyProtein/(mealPlan.getBreakfastPortionSize() + mealPlan.getLunchPortionSize() + mealPlan.getDinnerPortionSize());
            totalFatPercentage += dailyFat/(mealPlan.getBreakfastPortionSize() + mealPlan.getLunchPortionSize() + mealPlan.getDinnerPortionSize());
            totalCarbohydratePercentage += dailyCarbohydrate/(mealPlan.getBreakfastPortionSize() + mealPlan.getLunchPortionSize() + mealPlan.getDinnerPortionSize());
            count++;
        }

        if (count > 0) {
            averageDistribution.put("protein", totalProteinPercentage / count);
            averageDistribution.put("fat", totalFatPercentage / count);
            averageDistribution.put("carbohydrate", totalCarbohydratePercentage / count);
        }

        return averageDistribution;
    }

    /**
     * Retrieves the top 20 most used ingredients across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return a map containing the top 20 most used ingredients with their usage frequencies
     */
    private Map<String, Integer> getTop20MostUsedIngredients(List<MealPlan> mealPlans) {
        Map<String, Integer> ingredientUsageFrequency = new HashMap<>();

        // Filter MealPlans nach tenantId
        mealPlans.forEach(mealPlan -> {
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

    /**
     * Calculates the usage of tags across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return a map containing the tags and their usage frequencies
     */
    private Map<Tag, Long> calculateTagUsage(List<MealPlan> mealPlans) {
        Map<Tag, Long> mostUsedTags = new HashMap<>();

        // Iterate over all meal plans for the given tenant
        for (MealPlan mealPlan : mealPlans) {
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

    /**
     * Calculates the average portion size across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return the average portion size
     */
    private Long calculateAveragePortionSize(List<MealPlan> mealPlans) {
        long portionsOfAllMealplans = 0L;
        long count = 0;

        // Iterate over all meal plans to calculate the total portion sizes for the given tenant
        for (MealPlan mealPlan : mealPlans) {
            portionsOfAllMealplans += mealPlan.getLunchPortionSize();
            portionsOfAllMealplans += mealPlan.getBreakfastPortionSize();
            portionsOfAllMealplans += mealPlan.getDinnerPortionSize();
            count++;
        }

        // Calculate and return the average portion size
        if (count == 0) return 0L;
        return portionsOfAllMealplans / (count * 3);
    }

    /**
     * Calculates the average cooking time across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return the average cooking time
     */
    private Long calculateAverageCookingTime(List<MealPlan> mealPlans) {
        long totalCookingTime = 0L;
        long count = 0;

        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getBreakfastRecipe() != null) {
                totalCookingTime += mealPlan.getBreakfastRecipe().getCookingTime();
            }
            if (mealPlan.getLunchRecipe() != null) {
                totalCookingTime += mealPlan.getLunchRecipe().getCookingTime();
            }
            if (mealPlan.getDinnerRecipe() != null) {
                totalCookingTime += mealPlan.getDinnerRecipe().getCookingTime();
            }
            count++;
        }

        if (count == 0) return 0L;
        return totalCookingTime / (count*3);
    }

    /**
     * Calculates the most popular recipes across all meal plans.
     *
     * @param mealPlans the list of meal plans
     * @return a map containing the popular recipes and their frequencies
     */
    private Map<String, Long> calculatePopularRecipes(List<MealPlan> mealPlans) {
        Map<String, Long> recipeUsage = new HashMap<>();

        // Iterate over all meal plans for the given tenant
        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getBreakfastRecipe() != null) {
                recipeUsage.put(mealPlan.getBreakfastRecipe().getName(), recipeUsage.getOrDefault(mealPlan.getBreakfastRecipe().getName(), 0L) + 1);
            }
            if (mealPlan.getLunchRecipe() != null) {
                recipeUsage.put(mealPlan.getLunchRecipe().getName(), recipeUsage.getOrDefault(mealPlan.getLunchRecipe().getName(), 0L) + 1);
            }
            if (mealPlan.getDinnerRecipe() != null) {
                recipeUsage.put(mealPlan.getDinnerRecipe().getName(), recipeUsage.getOrDefault(mealPlan.getDinnerRecipe().getName(), 0L) + 1);
            }
        }

        return recipeUsage.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sort by usage frequency
                .limit(10) // Take the top 10 recipes
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // In case of key conflict (should not happen), keep the first entry
                        LinkedHashMap::new // Preserve sorted order
                ));
    }
}
