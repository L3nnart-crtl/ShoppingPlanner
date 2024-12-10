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

    public CookingStatistic calculateStatistics() {
        CookingStatistic statistic = new CookingStatistic();

        // Durchschnittliche Portionsgröße
        statistic.setAveragePortions(this.calculateAveragePortionSize());

        // Durchschnittliche Kochzeit
        statistic.setAverageCookingTime(this.calculateAverageCookingTime());

        // Meist verwendete Tags
        Map<Tag, Long> mostUsedTags = this.calculateTagUsage();
        statistic.setAttributes(mostUsedTags);

        // Beliebte Rezepte
        Map<String, Long> popularRecipes = this.calculatePopularRecipes();
        statistic.setFavouriteRecipes(popularRecipes);

        // Meistgenutzte Zutaten
        Map<String, Double> topIngredients = this.calculateTopIngredients();
        statistic.setFavouriteIngredients(topIngredients);

        statistic.setAmountOfCookedRecipes((long) statistic.getFavouriteRecipes().keySet().size());
        return statistic;
    }

    private Map<String, Double> calculateTopIngredients() {
        Map<String, Double> topIngredients = new HashMap<>();

        // Durchlaufe alle MealPlans
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            // Die Rezepte aus dem MealPlan holen
            List<Recipe> recipes = new ArrayList<>();
            recipes.add(mealPlan.getBreakfastRecipe());
            recipes.add(mealPlan.getLunchRecipe());
            recipes.add(mealPlan.getDinnerRecipe());

            // Verarbeite jedes Rezept
            for (Recipe recipe : recipes) {
                List<Ingredient> ingredients = recipe.getIngredients();
                int portionSize = 0;

                // Bestimme die Portionsgröße des Rezepts
                if (recipe.equals(mealPlan.getBreakfastRecipe())) {
                    portionSize = mealPlan.getBreakfastPortionSize();
                } else if (recipe.equals(mealPlan.getLunchRecipe())) {
                    portionSize = mealPlan.getLunchPortionSize();
                } else if (recipe.equals(mealPlan.getDinnerRecipe())) {
                    portionSize = mealPlan.getDinnerPortionSize();
                }

                // Gehe durch alle Zutaten des Rezepts
                for (Ingredient ingredient : ingredients) {
                    // Konvertiere die Menge in Double für die Berechnung
                    double amount = parseDoubleWithCommaHandling(ingredient.getQuantity());

                    // Skaliere die Menge basierend auf der Portionsgröße
                    double scaledAmount = amount * portionSize;

                    // Erstelle den Schlüssel für die Map, der den Zutatennamen und die Einheit kombiniert
                    String key = ingredient.getName() + " (" + ingredient.getUnit().toString() + ")";

                    // Addiere die skalierte Menge zur entsprechenden Zutat
                    topIngredients.put(key, topIngredients.getOrDefault(key, 0.0) + scaledAmount);
                }
            }
        }

        return topIngredients;
    }

    // Hilfsmethode zum sicheren Parsen von Zahlen mit Komma
    private double parseDoubleWithCommaHandling(String input) {
        input = input.replace(",", "."); // Komma durch Punkt ersetzen
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            // Fehlerbehandlung, falls die Zahl immer noch ungültig ist
            System.err.println("Ungültiges Zahlformat: " + input);
            throw new IllegalArgumentException("Ungültiges Zahlformat: " + input, e);
        }
    }

    private Map<Tag, Long> calculateTagUsage() {
        Map<Tag, Long> mostUsedTags = new HashMap<>();

        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            List<Recipe> recipes = new ArrayList<>();
            recipes.add(mealPlan.getLunchRecipe());
            recipes.add(mealPlan.getDinnerRecipe());
            recipes.add(mealPlan.getBreakfastRecipe());

            for (Recipe recipe : recipes) {
                for (Tag tag : recipe.getTags()) {
                    mostUsedTags.put(tag, mostUsedTags.getOrDefault(tag, 0L) + 1);
                }
            }
        }
        return mostUsedTags;
    }

    private Long calculateAveragePortionSize() {
        long portionsOfAllMealplans = 0L;
        long count = 0;
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            portionsOfAllMealplans += mealPlan.getLunchPortionSize();
            portionsOfAllMealplans += mealPlan.getBreakfastPortionSize();
            portionsOfAllMealplans += mealPlan.getDinnerPortionSize();
            count++;
        }
        return portionsOfAllMealplans / (count * 3);
    }

    private Long calculateAverageCookingTime() {
        long cookingTimeOfAllMealplans = 0L;
        long count = 0;
        for (MealPlan mealPlan : mealPlanRepository.findAll()) {
            cookingTimeOfAllMealplans += mealPlan.getBreakfastRecipe().getCookingTime();
            cookingTimeOfAllMealplans += mealPlan.getLunchRecipe().getCookingTime();
            cookingTimeOfAllMealplans += mealPlan.getDinnerRecipe().getCookingTime();
            count++;
        }
        return cookingTimeOfAllMealplans / (count * 3);
    }

    private Map<String, Long> calculatePopularRecipes() {
        Map<String, Long> popularRecipes = new HashMap<>();

        for (MealPlan mealPlan : mealPlanRepository.findAll()) {

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
