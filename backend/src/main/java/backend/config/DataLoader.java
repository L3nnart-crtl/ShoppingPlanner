package backend.config;

import backend.model.Recipe.Ingredient;
import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.QuantityUnit;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.IngredientRepository;
import backend.repository.MealPlanRepository;
import backend.repository.RecipeRepository;
import backend.multitenant.authentication.AuthService; // Import the AuthService
import backend.multitenant.tenantId.TenantContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MealPlanRepository mealPlanRepository;
    private final AuthService authService; // Inject AuthService

    public DataLoader(RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                      MealPlanRepository mealPlanRepository, AuthService authService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.mealPlanRepository = mealPlanRepository;
        this.authService = authService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Register two test users
        authService.register("user1", "user1", "1");
        authService.register("user2", "user2", "2");


        // Load data for user1
          // Set tenantId for user1
        loadDataForTenant("1");

        // Load data for user2
         // Set tenantId for user2
        loadDataForTenant("2");
    }

    private void loadDataForTenant(String tenantId) {
        Random random = new Random();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is not set in the context.");
        }

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

        int numberOfRecipes = random.nextInt(11) + 40;

        for (int i = 0; i < numberOfRecipes; i++) {
            String recipeName = recipeNames.get(random.nextInt(recipeNames.size()));

            if (!recipeRepository.existsByTenantIdAndName(tenantId, recipeName)) {
                Recipe recipe = new Recipe();
                recipe.setName(recipeName);
                recipe.setCookingTime(random.nextInt(106) + 15);
                recipe.setTenantId(tenantId);

                Set<Tag> tags = getRandomTags(random);
                recipe.setTags(tags);

                Recipe savedRecipe = recipeRepository.save(recipe);

                int numberOfIngredients = random.nextInt(4) + 4;
                for (int j = 0; j < numberOfIngredients; j++) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(ingredientNames.get(random.nextInt(ingredientNames.size())));

                    QuantityUnit unit = QuantityUnit.values()[random.nextInt(QuantityUnit.values().length)];
                    ingredient.setUnit(unit);
                    ingredient.setQuantity(generateQuantity(random, unit));
                    ingredient.setRecipe(savedRecipe);

                    ingredientRepository.save(ingredient);
                }
            }
        }

        List<Recipe> allRecipes = recipeRepository.findByTenantId(tenantId);
        if (allRecipes.isEmpty()) {
            System.err.println("No recipes found! MealPlans cannot be created.");
            return;
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (!mealPlanRepository.existsByTenantIdAndDate(tenantId, date)) {
                Recipe breakfast = allRecipes.get(random.nextInt(allRecipes.size()));
                Recipe lunch = allRecipes.get(random.nextInt(allRecipes.size()));
                Recipe dinner = allRecipes.get(random.nextInt(allRecipes.size()));

                MealPlan mealPlan = new MealPlan(tenantId, date, breakfast, 2, lunch, 3, dinner, 4);
                mealPlan.setTenantId(tenantId);
                mealPlanRepository.save(mealPlan);
            }
        }
    }

    private Set<Tag> getRandomTags(Random random) {
        Set<Tag> tags = new HashSet<>();
        List<Tag> allTags = Arrays.asList(Tag.values());
        int numberOfTags = random.nextInt(3) + 1;

        for (int i = 0; i < numberOfTags; i++) {
            tags.add(allTags.get(random.nextInt(allTags.size())));
        }
        return tags;
    }

    private String generateQuantity(Random random, QuantityUnit unit) {
        switch (unit) {
            case GRAM:
            case MILLILITER:
                return String.valueOf(random.nextInt(500) + 50);
            case KILOGRAM:
            case LITER:
                return String.format("%.2f", (random.nextDouble() * 2) + 0.1);
            case TABLESPOON:
                return String.valueOf(random.nextInt(10) + 1);
            case TEASPOON:
                return String.valueOf(random.nextInt(5) + 1);
            case CUP:
                return String.valueOf(random.nextInt(4) + 1);
            case PIECE:
                return String.valueOf(random.nextInt(10) + 1);
            case DOZEN:
                return String.valueOf(random.nextInt(2) + 1);
            default:
                return "1";
        }
    }
}
