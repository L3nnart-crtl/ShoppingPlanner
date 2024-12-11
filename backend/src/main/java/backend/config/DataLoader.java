package backend.config;

import backend.model.Recipe.Ingredient;
import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.QuantityUnit;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.IngredientRepository;
import backend.repository.MealPlanRepository;
import backend.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    // Repositories for accessing data related to recipes, ingredients, and meal plans
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MealPlanRepository mealPlanRepository;

    // Constructor to inject the repositories
    public DataLoader(RecipeRepository recipeRepository, IngredientRepository ingredientRepository, MealPlanRepository mealPlanRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.mealPlanRepository = mealPlanRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize random number generator
        Random random = new Random();

        // List of predefined recipe names
        List<String> recipeNames = Arrays.asList(
                "Spaghetti Bolognese", "Caesar Salad", "Vegetable Stir Fry", "Grilled Chicken Breast",
                "Tomato Soup", "Beef Stroganoff", "Quinoa Bowl", "Pancakes", "Avocado Toast",
                "Chicken Curry", "Lentil Soup", "Shrimp Tacos", "Greek Salad", "Fried Rice",
                "Smoothie Bowl", "Margherita Pizza", "Sushi Rolls", "Chocolate Cake", "Pumpkin Soup"
        );

        // List of predefined ingredient names
        List<String> ingredientNames = Arrays.asList(
                "Tomato", "Cheese", "Lettuce", "Bread", "Cucumber", "Chicken", "Beef", "Rice",
                "Olive Oil", "Garlic", "Onion", "Spinach", "Zucchini", "Eggplant", "Mushrooms",
                "Avocado", "Bacon", "Fish", "Shrimp", "Tuna", "Lemon", "Herbs", "Chili", "Pepper",
                "Pineapple", "Broccoli", "Carrot", "Potato", "Egg", "Yogurt", "Butter", "Milk"
        );

        // Generate a random number of recipes (between 40 and 50)
        int numberOfRecipes = random.nextInt(11) + 40;

        // Loop to create the recipes
        for (int i = 0; i < numberOfRecipes; i++) {
            String recipeName = recipeNames.get(random.nextInt(recipeNames.size()));

            // Check if the recipe already exists
            if (!recipeRepository.existsByName(recipeName)) {
                Recipe recipe = new Recipe();
                recipe.setName(recipeName);
                recipe.setCookingTime(random.nextInt(106) + 15); // Random cooking time between 15 and 120 minutes

                // Assign random tags to the recipe
                Set<Tag> tags = getRandomTags(random);
                recipe.setTags(tags);

                // Save the recipe to the database
                Recipe savedRecipe = recipeRepository.save(recipe);

                // Create a random number of ingredients (between 4 and 8)
                int numberOfIngredients = random.nextInt(4) + 4;
                for (int j = 0; j < numberOfIngredients; j++) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(ingredientNames.get(random.nextInt(ingredientNames.size())));

                    // Assign a random unit and quantity to the ingredient
                    QuantityUnit unit = QuantityUnit.values()[random.nextInt(QuantityUnit.values().length)];
                    ingredient.setUnit(unit);
                    ingredient.setQuantity(generateQuantity(random, unit));
                    ingredient.setRecipe(savedRecipe);

                    // Save the ingredient to the database
                    ingredientRepository.save(ingredient);
                }
            }
        }

        // Retrieve all saved recipes
        List<Recipe> allRecipes = recipeRepository.findAll();

        // If no recipes exist, print an error and return
        if (allRecipes.isEmpty()) {
            System.err.println("No recipes found! MealPlans cannot be created.");
            return;
        }

        // Define the start and end date for meal plans (3 months from now)
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        // Loop to create meal plans for each day in the date range
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            // If there is no meal plan for the current date, create one
            if (!mealPlanRepository.existsByDate(date)) {
                // Select random recipes for breakfast, lunch, and dinner
                Recipe breakfast = allRecipes.get(random.nextInt(allRecipes.size()));
                Recipe lunch = allRecipes.get(random.nextInt(allRecipes.size()));
                Recipe dinner = allRecipes.get(random.nextInt(allRecipes.size()));

                // Create a new meal plan for the day and save it
                MealPlan mealPlan = new MealPlan(date, breakfast, 2, lunch, 3, dinner, 4);
                mealPlanRepository.save(mealPlan);
            }
        }
    }

    // Helper method to get a random set of tags for a recipe
    private Set<Tag> getRandomTags(Random random) {
        Set<Tag> tags = new HashSet<>();
        List<Tag> allTags = Arrays.asList(Tag.values());
        int numberOfTags = random.nextInt(3) + 1; // Randomly select 1 to 3 tags

        for (int i = 0; i < numberOfTags; i++) {
            tags.add(allTags.get(random.nextInt(allTags.size())));
        }
        return tags;
    }

    // Helper method to generate a random quantity based on the unit type
    private String generateQuantity(Random random, QuantityUnit unit) {
        switch (unit) {
            case GRAM:
            case MILLILITER:
                return String.valueOf(random.nextInt(500) + 50); // Between 50 and 550
            case KILOGRAM:
            case LITER:
                return String.format("%.2f", (random.nextDouble() * 2) + 0.1); // Between 0.1 and 2.1
            case TABLESPOON:
                return String.valueOf(random.nextInt(10) + 1); // Between 1 and 10
            case TEASPOON:
                return String.valueOf(random.nextInt(5) + 1); // Between 1 and 5
            case CUP:
                return String.valueOf(random.nextInt(4) + 1); // Between 1 and 4
            case PIECE:
                return String.valueOf(random.nextInt(10) + 1); // Between 1 and 10
            case DOZEN:
                return String.valueOf(random.nextInt(2) + 1); // Between 1 and 2
            default:
                return "1"; // Default quantity
        }
    }

    // Helper method to get a random recipe by tag
    private Recipe getRandomRecipeByTag(List<Recipe> recipes, Tag tag, Random random) {
        List<Recipe> filteredRecipes = recipes.stream()
                .filter(recipe -> recipe.getTags().contains(tag))
                .toList();
        return filteredRecipes.isEmpty() ? null : filteredRecipes.get(random.nextInt(filteredRecipes.size()));
    }
}
