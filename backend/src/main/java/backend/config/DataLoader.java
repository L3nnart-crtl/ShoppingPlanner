package backend.config;

import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Ingredient;
import backend.model.Recipe.QuantityUnit;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import backend.repository.IngredientRepository;
import backend.repository.MealPlanRepository;
import backend.repository.RecipeRepository;
import backend.multitenant.authentication.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MealPlanRepository mealPlanRepository;
    private final AuthService authService;

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

        // Load data for tenants
        loadDataForTenant("1");
        loadDataForTenant("2");
    }

    private void loadDataForTenant(String tenantId) {
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is not set in the context.");
        }

        // Define the recipes with their ingredients, tags, and nutritional values
        List<Recipe> recipes = Arrays.asList(
                createRecipe("Haferflocken mit Beeren", Arrays.asList(
                                createIngredient("Haferflocken", QuantityUnit.GRAM, 50, 372, 13.5, 7, 58.7, null),
                                createIngredient("Gemischte Beeren", QuantityUnit.GRAM, 100, 40, 1.1, 0.2, 5.8, null),
                                createIngredient("Milch", QuantityUnit.MILLIGRAM, 200, 64, 3.3, 3.6, 4.8, null)
                        ), "Ein nährstoffreiches und sättigendes Frühstück, das perfekt für einen schnellen und gesunden Start in den Tag ist. Die Haferflocken werden mit Milch gekocht und mit frischen Beeren garniert, um zusätzlichen Geschmack und Antioxidantien zu bieten.",
                        Arrays.asList(Tag.BREAKFAST, Tag.LOW_CALORIE, Tag.QUICK)),

                createRecipe("Vollkornbrot mit Avocado und Ei", Arrays.asList(
                                createIngredient("Vollkornbrot", QuantityUnit.GRAM, 45, 247, 8.5, 3.5, 41.5, null),
                                createIngredient("Avocado", QuantityUnit.GRAM, 100, 160, 2, 15, 9, null),
                                createIngredient("Ei", QuantityUnit.GRAM, 50, 143, 12.6, 9.5, 0.7, null)
                        ), "Ein ausgewogenes Frühstück oder Snack, das reich an Proteinen ist. Das Vollkornbrot wird getoastet, die Avocado darauf zerdrückt und ein weich gekochtes Ei für zusätzliches Protein hinzugefügt. Eine perfekte Kombination aus gesunden Fetten und Ballaststoffen.",
                        Arrays.asList(Tag.BREAKFAST, Tag.LOW_CALORIE, Tag.HIGH_PROTEIN)),

                createRecipe("Gegrilltes Hähnchen mit Quinoa und Gemüse", Arrays.asList(
                                createIngredient("Hähnchenbrust", QuantityUnit.GRAM, 150, 165, 31, 3.6, 0, null),
                                createIngredient("Quinoa", QuantityUnit.GRAM, 100, 120, 4.1, 1.9, 21.3, null),
                                createIngredient("Gemischtes Gemüse", QuantityUnit.GRAM, 200, 50, 2, 0.5, 10, null)
                        ), "Ein eiweißreiches Abendessen mit gegrilltem Hähnchen, Quinoa und verschiedenen Gemüsesorten. Die Quinoa liefert Ballaststoffe und Kohlenhydrate, während das Hähnchen eine hervorragende Proteinquelle ist.",
                        Arrays.asList(Tag.DINNER, Tag.HIGH_PROTEIN, Tag.LOW_CALORIE)),

                createRecipe("Linsensuppe mit Karotten und Sellerie", Arrays.asList(
                                createIngredient("Linsen", QuantityUnit.GRAM, 100, 116, 9, 0.4, 20, null),
                                createIngredient("Karotten", QuantityUnit.GRAM, 100, 41, 0.9, 0.2, 9.6, null),
                                createIngredient("Sellerie", QuantityUnit.GRAM, 100, 16, 0.7, 0.2, 3.5, null)
                        ), "Eine herzhafte und nährstoffreiche vegetarische Suppe. Linsen, Karotten und Sellerie werden zusammen gekocht, um eine geschmackvolle und sättigende Mahlzeit zu bieten. Linsen liefern pflanzliches Protein und die Gemüse sorgen für Vitamine und Mineralien.",
                        Arrays.asList(Tag.LUNCH, Tag.VEGETARIAN, Tag.ONE_POT)),

                createRecipe("Gegrillter Lachs mit Süßkartoffeln und Brokkoli", Arrays.asList(
                                createIngredient("Lachsfilet", QuantityUnit.GRAM, 150, 280, 22, 20, 0, null),
                                createIngredient("Süßkartoffeln", QuantityUnit.GRAM, 200, 86, 1.6, 0.1, 20, null),
                                createIngredient("Brokkoli", QuantityUnit.GRAM, 100, 35, 2.4, 0.4, 7, null)
                        ), "Ein leichtes, aber sättigendes Abendessen mit gegrilltem Lachs, gerösteten Süßkartoffeln und gedämpftem Brokkoli. Der omega-3-reiche Lachs harmoniert hervorragend mit den süßen und herzhaften Süßkartoffeln und dem nährstoffreichen Brokkoli.",
                        Arrays.asList(Tag.DINNER, Tag.FISH, Tag.LOW_CALORIE)),

                createRecipe("Gemüseomelett mit Tomaten und Spinat", Arrays.asList(
                                createIngredient("Eier", QuantityUnit.GRAM, 100, 143, 12.6, 9.5, 0.7, null),
                                createIngredient("Tomaten", QuantityUnit.GRAM, 100, 18, 1, 0.2, 3.9, null),
                                createIngredient("Spinat", QuantityUnit.GRAM, 100, 23, 3, 0.4, 1.3, null)
                        ), "Ein leichtes und gesundes Gericht, das sich hervorragend als Frühstück oder leichtes Mittagessen eignet. Das Omelett wird mit frischen Tomaten und Spinat zubereitet, die dem Gericht zusätzliches Gemüse und Nährstoffe verleihen. Die Eier liefern hochwertiges Eiweiß und gesunde Fette.",
                        Arrays.asList(Tag.BREAKFAST, Tag.VEGETARIAN, Tag.LOW_CALORIE))
        );

        // Save the recipes to the repository
        for (Recipe recipe : recipes) {
            if (!recipeRepository.existsByTenantIdAndName(tenantId, recipe.getName())) {
                recipe.setTenantId(tenantId);
                recipeRepository.save(recipe); // Save the recipe first

                // Now that the recipe is saved, set the recipe reference for each ingredient
                for (Ingredient ingredient : recipe.getIngredients()) {
                    ingredient.setRecipe(recipe); // Set the recipe reference here
                    ingredientRepository.save(ingredient); // Save the ingredient after the recipe reference is set
                }
            }
        }

        // Create meal plans
        createMealPlans(tenantId, recipes);
    }

    private Recipe createRecipe(String name, List<Ingredient> ingredients, String description, List<Tag> tags) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setDescription(description);
        recipe.setCookingTime(30); // Beispielwert
        recipe.setIngredients(ingredients);
        Set<Tag> tagsSet = new HashSet<>(tags);
        recipe.setTags(tagsSet);  // Set the tags here
        recipe.calculateNutritionalValues();
        return recipe;
    }

    private Ingredient createIngredient(String name, QuantityUnit unit, double quantity, double calories, double protein, double fat, double carbs, Recipe recipe) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setUnit(unit);
        ingredient.setQuantity(String.valueOf(quantity));
        ingredient.setCalories(calories);
        ingredient.setProteins(protein);
        ingredient.setFats(fat);
        ingredient.setCarbohydrates(carbs);
        if (recipe != null) {
            ingredient.setRecipe(recipe); // Set the recipe reference here
        }
        return ingredient;
    }

    private void createMealPlans(String tenantId, List<Recipe> recipes) {
        Random random = new Random();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        // Separate recipes by tags for meal types
        List<Recipe> breakfastRecipes = new ArrayList<>();
        List<Recipe> lunchRecipes = new ArrayList<>();
        List<Recipe> dinnerRecipes = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (recipe.getTags().contains(Tag.BREAKFAST)) {
                breakfastRecipes.add(recipe);
            }
            if (recipe.getTags().contains(Tag.LUNCH)) {
                lunchRecipes.add(recipe);
            }
            if (recipe.getTags().contains(Tag.DINNER)) {
                dinnerRecipes.add(recipe);
            }
        }

        // Now create meal plans
        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1)) {
            if (!mealPlanRepository.existsByTenantIdAndDate(tenantId, date)) {
                // Distribute recipes across meal plans
                Recipe breakfast = breakfastRecipes.get(random.nextInt(breakfastRecipes.size()));
                Recipe lunch = lunchRecipes.get(random.nextInt(lunchRecipes.size()));
                Recipe dinner = dinnerRecipes.get(random.nextInt(dinnerRecipes.size()));

                // Create a meal plan with the selected recipes
                MealPlan mealPlan = new MealPlan(tenantId, date, breakfast, 2, lunch, 3, dinner, 4);
                mealPlanRepository.save(mealPlan);
            }
        }
    }

}
