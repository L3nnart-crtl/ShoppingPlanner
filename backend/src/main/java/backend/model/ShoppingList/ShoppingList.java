package backend.model.ShoppingList;

import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Ingredient;
import backend.model.Recipe.QuantityUnit;
import backend.model.Recipe.Recipe;
import backend.multitenant.tenantId.TenantContext;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a shopping list generated from meal plans over a specified period.
 * The list contains the ingredients, quantities, and units required for meals.
 */
public class ShoppingList {

    private List<ShoppingItem> items; // List of shopping items (ingredients)
    private LocalDate startDate;      // Start date of the shopping list
    private LocalDate endDate;        // End date of the shopping list

    /**
     * Constructor to create a shopping list for a given period.
     *
     * @param startDate The start date of the shopping list.
     * @param endDate   The end date of the shopping list.
     */
    public ShoppingList(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.items = new ArrayList<>();
    }

    /**
     * Generates the shopping list based on the provided meal plans within the specified period.
     *
     * @param mealPlans The list of meal plans that contain the recipes for the shopping list.
     */
    public void generateShoppingList(List<MealPlan> mealPlans) {
        String tenantId = TenantContext.getCurrentTenant();  // Get tenant ID
        // Map for storing ingredients, their names, quantities, and units
        Map<String, List<ShoppingItem>> ingredientMap = new HashMap<>();

        // Iterate over all meal plans within the specified period
        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getDate().isAfter(startDate.minusDays(1)) && mealPlan.getDate().isBefore(endDate.plusDays(1))) {
                addIngredientsFromRecipe(mealPlan.getBreakfastRecipe(), mealPlan.getBreakfastPortionSize(), ingredientMap, tenantId);
                addIngredientsFromRecipe(mealPlan.getLunchRecipe(), mealPlan.getLunchPortionSize(), ingredientMap, tenantId);
                addIngredientsFromRecipe(mealPlan.getDinnerRecipe(), mealPlan.getDinnerPortionSize(), ingredientMap, tenantId);
            }
        }

        // Add ingredients from the map to the shopping list
        items = new ArrayList<>();
        for (Map.Entry<String, List<ShoppingItem>> entry : ingredientMap.entrySet()) {
            ShoppingItem combinedItem = combineItems(entry.getValue());
            items.add(combinedItem);
        }
    }

    /**
     * Adds ingredients from a recipe to the ingredient map, scaling the quantities based on the portion size.
     *
     * @param recipe         The recipe from which ingredients are extracted.
     * @param portionSize    The number of servings (portion size) for the recipe.
     * @param ingredientMap  The map where ingredients will be stored.
     * @param tenantId       The tenant ID for multitenancy.
     */
    private void addIngredientsFromRecipe(Recipe recipe, int portionSize, Map<String, List<ShoppingItem>> ingredientMap, String tenantId) {
        if (recipe != null && recipe.getIngredients() != null) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getQuantity() != null && !ingredient.getQuantity().trim().isEmpty()) {
                    // Convert and scale the quantity based on the portion size
                    double scaledQuantity = convertAndScaleQuantity(ingredient.getQuantity(), ingredient.getUnit(), portionSize);

                    // Ingredients are only combined if they have the same unit
                    String unit = ingredient.getUnit().toString();

                    // Add ingredient to the map under its name
                    ingredientMap.computeIfAbsent(ingredient.getName(), k -> new ArrayList<>())
                            .add(new ShoppingItem(ingredient.getName(), Math.round(scaledQuantity), unit));
                } else {
                    System.err.println("Warning: Missing or empty quantity for ingredient: " + ingredient.getName());
                }
            }
        }
    }

    /**
     * Combines a list of shopping items with the same name, summing the amounts and grouping by unit.
     *
     * @param items The list of shopping items to be combined.
     * @return A single combined shopping item with the total amount and unit(s).
     */
    private ShoppingItem combineItems(List<ShoppingItem> items) {
        String ingredientName = items.get(0).getIngredientName();
        Map<String, Double> unitAmounts = new HashMap<>();
        StringBuilder combinedUnits = new StringBuilder();
        double totalAmount = 0;

        // Sum amounts grouped by unit
        for (ShoppingItem item : items) {
            totalAmount += item.getAmount();  // Calculate total amount for the ingredient
            unitAmounts.put(item.getUnit(), unitAmounts.getOrDefault(item.getUnit(), 0.0) + item.getAmount());
        }

        // Combine amounts and units correctly
        for (Map.Entry<String, Double> entry : unitAmounts.entrySet()) {
            combinedUnits.append(entry.getValue()).append(" ").append(entry.getKey()).append(", ");
        }

        // Remove the last comma and space if present
        if (combinedUnits.length() > 0) {
            combinedUnits.setLength(combinedUnits.length() - 2);
        }

        return new ShoppingItem(ingredientName, totalAmount, combinedUnits.toString());
    }

    // Getter and Setter methods

    /**
     * Gets the list of shopping items.
     *
     * @return The list of shopping items.
     */
    public List<ShoppingItem> getItems() {
        return items;
    }

    /**
     * Sets the list of shopping items.
     *
     * @param items The list of shopping items to set.
     */
    public void setItems(List<ShoppingItem> items) {
        this.items = items;
    }

    /**
     * Gets the start date of the shopping list.
     *
     * @return The start date of the shopping list.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the shopping list.
     *
     * @param startDate The start date to set.
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the shopping list.
     *
     * @return The end date of the shopping list.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the shopping list.
     *
     * @param endDate The end date to set.
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Converts and scales the quantity of an ingredient based on its unit and the portion size.
     *
     * @param quantity The quantity as a string.
     * @param unit     The unit of the quantity.
     * @param portionSize The portion size for scaling the quantity.
     * @return The scaled quantity.
     */
    private double convertAndScaleQuantity(String quantity, QuantityUnit unit, int portionSize) {
        double baseQuantity = parseQuantity(quantity, unit);
        return baseQuantity * portionSize;
    }

    /**
     * Parses the quantity string into a numeric value.
     *
     * @param quantity The quantity as a string.
     * @param unit     The unit of the quantity (not used here, but passed for potential future handling).
     * @return The parsed quantity as a double.
     */
    private double parseQuantity(String quantity, QuantityUnit unit) {
        try {
            // Replace comma with dot to correct format
            quantity = quantity.replace(',', '.');
            return Double.parseDouble(quantity);
        } catch (NumberFormatException e) {
            return 0.0;  // Return 0 if the quantity cannot be parsed
        }
    }
}
