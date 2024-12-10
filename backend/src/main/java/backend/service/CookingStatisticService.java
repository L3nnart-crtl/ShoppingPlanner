package backend.service;

import backend.model.CookingStatistic;
import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.MealPlanRepository;
import backend.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CookingStatisticService {

    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public CookingStatistic calculateStatistics() {
        CookingStatistic statistic = new CookingStatistic();

        // Anzahl der gekochten Gerichte
        statistic.setAmountOfCookedRecipes(mealPlanRepository.count());

        // Durchschnittliche Portionsgröße
        statistic.setAveragePortions(mealPlanRepository.calculateAveragePortionSize());

        // Durchschnittliche Kochzeit
        statistic.setAverageCookingTime(mealPlanRepository.calculateAverageCookingTime());

        // Meist verwendete Tags
        Map<Tag, Long> mostUsedTags = mealPlanRepository.calculateTagUsage();
        statistic.setAttributes(mostUsedTags);

        // Beliebte Rezepte
        Map<Recipe, Long> popularRecipes = mealPlanRepository.calculatePopularRecipes();
        statistic.setFavouriteRecipes(popularRecipes);

        // Meistgenutzte Zutaten
        Map<String, Integer> topIngredients = mealPlanRepository.calculateTopIngredients();
        Map<String, Integer> mappedIngredients = new HashMap<>(topIngredients);
        statistic.setFavouriteIngredients(mappedIngredients);

        return statistic;
    }
}
