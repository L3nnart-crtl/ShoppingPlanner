package backend.service;


import backend.model.Ingredient;
import backend.model.MealPlan;
import backend.model.Recipe;
import backend.service.MealPlanService;
import backend.service.RecipeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeService recipeService;
    private final MealPlanService mealPlanService;

    public DataLoader(RecipeService recipeService, MealPlanService mealPlanService) {
        this.recipeService = recipeService;
        this.mealPlanService = mealPlanService;
    }

    @Override
    public void run(String... args) throws Exception {
        Random rand = new Random();

        // Testrezepte und Zutaten anlegen
        List<Recipe> recipes = generateTestRecipes(rand);

        // Rezepte in die Datenbank speichern
        for (Recipe recipe : recipes) {
            recipeService.saveRecipe(recipe);
        }

        // MealPlans anlegen (z.B. für 30 Tage)
        generateAndSaveMealPlans(rand, recipes);

        System.out.println("Testdaten wurden erfolgreich hinzugefügt.");
    }

    private List<Recipe> generateTestRecipes(Random rand) {
        List<Recipe> recipes = new ArrayList<>();

        // Erzeuge eine Vielzahl an Zutaten
        List<Ingredient> ingredients1 = Arrays.asList(
                new Ingredient("Eier", "2", null),
                new Ingredient("Tomaten", "3", null),
                new Ingredient("Käse", "100g", null)
        );

        List<Ingredient> ingredients2 = Arrays.asList(
                new Ingredient("Hähnchenbrust", "200g", null),
                new Ingredient("Salat", "1 Kopf", null),
                new Ingredient("Olivenöl", "50ml", null)
        );

        List<Ingredient> ingredients3 = Arrays.asList(
                new Ingredient("Nudeln", "150g", null),
                new Ingredient("Tomatensoße", "200ml", null),
                new Ingredient("Basilikum", "frisch", null)
        );

        // Rezepte erstellen
        Recipe breakfastRecipe = new Recipe("Rührei mit Tomaten", "Ein einfaches Rührei mit Tomaten und Käse", ingredients1);
        Recipe lunchRecipe = new Recipe("Hähnchensalat", "Frischer Hähnchensalat mit Olivenöl-Dressing", ingredients2);
        Recipe dinnerRecipe = new Recipe("Pasta mit Tomatensoße", "Nudeln mit einer leckeren Tomatensoße", ingredients3);

        recipes.add(breakfastRecipe);
        recipes.add(lunchRecipe);
        recipes.add(dinnerRecipe);

        // Weitere zufällige Rezepte generieren
        for (int i = 0; i < 10; i++) {
            List<Ingredient> randomIngredients = generateRandomIngredients(rand);
            recipes.add(new Recipe("Rezept " + (i + 4), "Zufälliges Rezept " + (i + 4), randomIngredients));
        }

        return recipes;
    }

    private List<Ingredient> generateRandomIngredients(Random rand) {
        List<String> possibleIngredients = Arrays.asList(
                "Hähnchen", "Rindfleisch", "Kartoffeln", "Spinat", "Brokkoli", "Reis", "Nudeln", "Tomaten", "Zwiebeln", "Knoblauch"
        );
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < 3 + rand.nextInt(4); i++) { // Zufällig 3 bis 6 Zutaten pro Rezept
            String ingredientName = possibleIngredients.get(rand.nextInt(possibleIngredients.size()));
            String quantity = rand.nextInt(10) + "g";
            ingredients.add(new Ingredient(ingredientName, quantity, null));
        }

        return ingredients;
    }

    private void generateAndSaveMealPlans(Random rand, List<Recipe> recipes) {
        LocalDate startDate = LocalDate.now();

        // Erzeuge MealPlans für 30 Tage (1 Monat)
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);
            Recipe breakfastRecipe = recipes.get(rand.nextInt(recipes.size()));
            Recipe lunchRecipe = recipes.get(rand.nextInt(recipes.size()));
            Recipe dinnerRecipe = recipes.get(rand.nextInt(recipes.size()));

            // Sicherstellen, dass die Rezepte gespeichert sind und IDs existieren
            if (breakfastRecipe.getId() == null) {
                breakfastRecipe = recipeService.saveRecipe(breakfastRecipe); // Speichern und ID setzen
            }
            if (lunchRecipe.getId() == null) {
                lunchRecipe = recipeService.saveRecipe(lunchRecipe); // Speichern und ID setzen
            }
            if (dinnerRecipe.getId() == null) {
                dinnerRecipe = recipeService.saveRecipe(dinnerRecipe); // Speichern und ID setzen
            }

            // MealPlan erstellen und speichern
            MealPlan mealPlan = new MealPlan();
            mealPlan.setDate(date);
            mealPlan.setBreakfastRecipe(breakfastRecipe);
            mealPlan.setLunchRecipe(lunchRecipe);
            mealPlan.setDinnerRecipe(dinnerRecipe);

            // MealPlan in die Datenbank speichern
            mealPlanService.saveMealPlan(mealPlan.getDate(), mealPlan.getBreakfastRecipe().getId(),
                    mealPlan.getLunchRecipe().getId(), mealPlan.getDinnerRecipe().getId());
        }
    }

}
