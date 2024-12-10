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

        List<String> recipeNames = Arrays.asList(
                "Spaghetti Bolognese", "Caesar Salad", "Vegetable Stir Fry", "Grilled Chicken Breast",
                "Tomato Soup", "Beef Stroganoff", "Quinoa Bowl", "Pancakes", "Avocado Toast",
                "Chicken Curry", "Lentil Soup", "Shrimp Tacos", "Greek Salad", "Fried Rice",
                "Smoothie Bowl", "Margherita Pizza", "Sushi Rolls", "Chocolate Cake", "Pumpkin Soup"
        );

        List<String> ingredientNames = Arrays.asList(
                "Tomato", "Cheese", "Lettuce", "Bread", "Cucumber", "Chicken", "Beef", "Rice",
                "Olive Oil", "Garlic", "Onion", "Spinach", "Zucchini", "Eggplant", "Mushrooms",
                "Avocado", "Bacon", "Fish", "Shrimp", "Tuna", "Lemon", "Herbs", "Chili", "Pepper",
                "Pineapple", "Broccoli", "Carrot", "Potato", "Egg", "Yogurt", "Butter", "Milk"
        );

        int numberOfRecipes = random.nextInt(11) + 40; // 40 bis 50 Rezepte

        for (int i = 0; i < numberOfRecipes; i++) {
            String recipeName = recipeNames.get(random.nextInt(recipeNames.size()));

            // Überprüfe, ob ein Rezept mit diesem Namen bereits existiert
            if (!recipeRepository.existsByName(recipeName)) {
                Recipe recipe = new Recipe();
                recipe.setName(recipeName);

                // Setze eine zufällige Kochzeit zwischen 15 und 120 Minuten
                int cookingTime = random.nextInt(106) + 15; // 15 bis 120 Minuten
                recipe.setCookingTime(cookingTime);

                Set<Tag> tags = getRandomTags(random);
                recipe.setTags(tags);

                recipeRepository.save(recipe);

                int numberOfIngredients = random.nextInt(4) + 4; // 4 bis 8 Zutaten
                for (int j = 0; j < numberOfIngredients; j++) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(ingredientNames.get(random.nextInt(ingredientNames.size())));

                    QuantityUnit unit = QuantityUnit.values()[random.nextInt(QuantityUnit.values().length)];
                    ingredient.setUnit(unit);

                    String quantity = generateQuantity(random, unit);
                    ingredient.setQuantity(quantity);

                    ingredient.setRecipe(recipe);
                    ingredientRepository.save(ingredient);
                }
            }
        }

        List<Recipe> allRecipes = recipeRepository.findAll();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            int dailyMealPlans = random.nextInt(3) + 1; // 1 bis 3 Mahlzeiten pro Tag

            for (int i = 0; i < dailyMealPlans; i++) {
                Recipe breakfast = getRandomRecipeByTag(allRecipes, Tag.BREAKFAST, random);
                Recipe lunch = getRandomRecipeByTag(allRecipes, Tag.LUNCH, random);
                Recipe dinner = getRandomRecipeByTag(allRecipes, Tag.DINNER, random);

                if (breakfast != null && lunch != null && dinner != null) {
                    MealPlan mealPlan = new MealPlan(date, breakfast, 2, lunch, 3, dinner, 4);

                    if (!mealPlanRepository.existsByDate(date)) {
                        mealPlanRepository.save(mealPlan);
                    }
                }
            }
        }
    }

    private Set<Tag> getRandomTags(Random random) {
        Set<Tag> tags = new HashSet<>();
        int numberOfTags = random.nextInt(3) + 1; // 1 bis 3 Tags
        List<Tag> allTags = Arrays.asList(Tag.values());

        for (int i = 0; i < numberOfTags; i++) {
            tags.add(allTags.get(random.nextInt(allTags.size())));
        }
        return tags;
    }

    private String generateQuantity(Random random, QuantityUnit unit) {
        switch (unit) {
            case GRAM:
            case MILLILITER:
                return String.valueOf(random.nextInt(500) + 50); // 50-550 g/ml
            case KILOGRAM:
            case LITER:
                return String.format("%.2f", (random.nextDouble() * 2) + 0.1); // 0.1-2.1 kg/l
            case TABLESPOON:
                return String.valueOf(random.nextInt(10) + 1); // 1-10 EL
            case TEASPOON:
                return String.valueOf(random.nextInt(5) + 1); // 1-5 TL
            case CUP:
                return String.valueOf(random.nextInt(4) + 1); // 1-4 Tassen
            case PIECE:
                return String.valueOf(random.nextInt(10) + 1); // 1-10 Stück
            case DOZEN:
                return String.valueOf(random.nextInt(2) + 1); // 1-2 Dutzend
            default:
                return "1";
        }
    }

    private Recipe getRandomRecipeByTag(List<Recipe> recipes, Tag tag, Random random) {
        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.getTags().contains(tag)) {
                filteredRecipes.add(recipe);
            }
        }
        return filteredRecipes.isEmpty() ? null : filteredRecipes.get(random.nextInt(filteredRecipes.size()));
    }
}
