package backend.config;

import backend.model.Ingredient;
import backend.model.MealPlan;
import backend.model.Recipe;
import backend.model.Tag;
import backend.repository.IngredientRepository;
import backend.repository.MealPlanRepository;
import backend.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MealPlanRepository mealPlanRepository;

    public DataLoader(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, MealPlanRepository mealPlanRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        // Erweiterte Rezeptnamen und Zutaten
        List<String> recipeNames = Arrays.asList(
                "Pasta", "Pizza", "Salad", "Burger", "Soup", "Sushi",
                "Tacos", "Steak", "Risotto", "Fried Rice", "Curry",
                "Pancakes", "Omelette", "Sushi Rolls", "Burritos", "Lasagna"
        );

        List<String> ingredientNames = Arrays.asList(
                "Tomato", "Cheese", "Lettuce", "Bread", "Cucumber", "Chicken",
                "Beef", "Rice", "Olive Oil", "Garlic", "Onion", "Spinach",
                "Zucchini", "Eggplant", "Mushrooms", "Avocado", "Bacon",
                "Fish", "Shrimp", "Tuna", "Lemon", "Herbs", "Chili", "Pepper",
                "Pineapple", "Broccoli", "Carrot", "Potato", "Egg", "Yogurt",
                "Butter", "Milk"
        );

        // Erstelle viele Rezepte (zwischen 20 und 50)
        int numberOfRecipes = random.nextInt(31) + 20;

        for (int i = 0; i < numberOfRecipes; i++) {
            // Erstelle ein Rezept
            Recipe recipe = new Recipe();
            recipe.setName(recipeNames.get(random.nextInt(recipeNames.size())));

            // Zufällig Tags zuweisen
            Set<Tag> tags = getRandomTags(random);
            recipe.setTags(tags);

            // Speichere das Rezept
            recipeRepository.save(recipe);

            // Erstelle eine zufällige Anzahl an Zutaten (zwischen 5 und 10)
            int numberOfIngredients = random.nextInt(6) + 5;
            for (int j = 0; j < numberOfIngredients; j++) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingredientNames.get(random.nextInt(ingredientNames.size())));
                ingredient.setRecipe(recipe);  // Verknüpfe das Rezept mit der Zutat
                ingredientRepository.save(ingredient);
            }
        }

        // Hole alle Rezepte, um sie zufällig MealPlans zuzuordnen
        List<Recipe> allRecipes = recipeRepository.findAll();

        // Erstelle viele MealPlans (zwischen 30 und 70)
        int numberOfMealPlans = random.nextInt(41) + 30;

        // Generiere MealPlans für einen Zeitraum von 3 Monaten
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            // Generiere zufällig viele MealPlans pro Tag (zwischen 1 und 3)
            int dailyMealPlans = random.nextInt(3) + 1;
            for (int i = 0; i < dailyMealPlans; i++) {
                // Wähle zufällige Rezepte für Frühstück, Mittagessen und Abendessen
                Recipe breakfast = allRecipes.get(random.nextInt(allRecipes.size()));
                Recipe lunch = allRecipes.get(random.nextInt(allRecipes.size()));
                Recipe dinner = allRecipes.get(random.nextInt(allRecipes.size()));

                // Erstelle einen MealPlan und setze die Rezepte zu
                MealPlan mealPlan = new MealPlan(date, breakfast, lunch, dinner);

                // Überprüfe, ob bereits ein MealPlan für das Datum existiert
                if (!mealPlanRepository.existsByDate(date)) {
                    mealPlanRepository.save(mealPlan);
                }
            }
        }
    }

    // Hilfsmethode, um zufällig Tags zu wählen
    private Set<Tag> getRandomTags(Random random) {
        Set<Tag> tags = new HashSet<>();
        int numberOfTags = random.nextInt(3) + 1;  // Zufällig 1 bis 3 Tags
        List<Tag> allTags = Arrays.asList(Tag.values());
        for (int i = 0; i < numberOfTags; i++) {
            tags.add(allTags.get(random.nextInt(allTags.size())));
        }
        return tags;
    }
}
