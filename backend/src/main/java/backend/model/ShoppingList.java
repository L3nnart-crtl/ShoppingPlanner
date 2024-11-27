package backend.model;

import java.time.LocalDate;
import java.util.*;

public class ShoppingList {

    private List<ShoppingItem> items; // Liste der Einkaufslisten-Elemente (Zutaten)
    private LocalDate startDate;
    private LocalDate endDate;

    public ShoppingList(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.items = new ArrayList<>();
    }

    public void generateShoppingList(List<MealPlan> mealPlans) {
        // Map für Zutaten, die alle gleichen Zutaten zusammenführt und skaliert
        Map<String, Double> ingredientQuantities = new HashMap<>();

        // Iteriere über alle MealPlans im angegebenen Zeitraum
        for (MealPlan mealPlan : mealPlans) {
            // Überprüfe, ob das Datum des MealPlans im angegebenen Zeitraum liegt
            if (mealPlan.getDate().isAfter(startDate.minusDays(1)) && mealPlan.getDate().isBefore(endDate.plusDays(1))) {
                // Füge die Zutaten für jedes Rezept hinzu und skaliere sie mit den Portionen
                addIngredientsFromRecipe(mealPlan.getBreakfastRecipe(), mealPlan.getBreakfastPortionSize(), ingredientQuantities);
                addIngredientsFromRecipe(mealPlan.getLunchRecipe(), mealPlan.getLunchPortionSize(), ingredientQuantities);
                addIngredientsFromRecipe(mealPlan.getDinnerRecipe(), mealPlan.getDinnerPortionSize(), ingredientQuantities);
            }
        }

        // Füge die Zutaten zur Einkaufsliste hinzu
        for (Map.Entry<String, Double> entry : ingredientQuantities.entrySet()) {
            items.add(new ShoppingItem(entry.getKey(), entry.getValue()));
        }
    }

    private void addIngredientsFromRecipe(Recipe recipe, int portionSize, Map<String, Double> ingredientQuantities) {
        if (recipe != null && recipe.getIngredients() != null) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                // Umrechnung und Skalierung der Menge
                double scaledQuantity = convertAndScaleQuantity(ingredient.getQuantity(), ingredient.getUnit(), portionSize);
                ingredientQuantities.put(ingredient.getName(), ingredientQuantities.getOrDefault(ingredient.getName(), 0.0) + scaledQuantity);
            }
        }
    }

    // Getter und Setter
    public List<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingItem> items) {
        this.items = items;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    private double convertAndScaleQuantity(String quantity, QuantityUnit unit, int portionSize) {
        double baseQuantity = parseQuantity(quantity, unit);
        return baseQuantity * portionSize;
    }

    private double convertToBaseUnit(double quantity, QuantityUnit unit) {
        switch (unit) {
            case GRAM:
                return quantity; // Gramm bleibt unverändert
            case KILOGRAM:
                return quantity * 1000; // Kilogramm in Gramm umrechnen
            case MILLIGRAM:
                return quantity / 1000; // Milligramm in Gramm umrechnen
            case MILLILITER:
                return quantity; // Milliliter bleibt unverändert
            case LITER:
                return quantity * 1000; // Liter in Milliliter umrechnen
            case CUP:
                return quantity * 240; // Tasse in Milliliter umrechnen (1 Tasse = 240 ml)
            case TABLESPOON:
                return quantity * 15; // Esslöffel in Milliliter umrechnen (1 Esslöffel = 15 ml)
            case TEASPOON:
                return quantity * 5; // Teelöffel in Milliliter umrechnen (1 Teelöffel = 5 ml)
            case PIECE:
                return quantity; // Stück bleibt unverändert (falls keine Einheit erforderlich)
            case DOZEN:
                return quantity * 12; // Dutzend in Stück umrechnen
            default:
                return quantity; // Fallback: keine Umrechnung
        }
    }

    private double parseQuantity(String quantity, QuantityUnit unit) {
        try {
            double parsedQuantity = Double.parseDouble(quantity);
            // Umrechnung in die Basiseinheit (z.B. Gramm oder Milliliter)
            return convertToBaseUnit(parsedQuantity, unit);
        } catch (NumberFormatException e) {
            // Falls das Parsen fehlschlägt, zurück 0.0
            return 0.0;
        }
    }
}
