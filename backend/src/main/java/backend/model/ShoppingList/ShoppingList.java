package backend.model.ShoppingList;

import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Ingredient;
import backend.model.Recipe.QuantityUnit;
import backend.model.Recipe.Recipe;

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
        // Map für Zutaten, die Namen, Mengen und Einheiten speichert
        Map<String, List<ShoppingItem>> ingredientMap = new HashMap<>();

        // Iteriere über alle MealPlans im angegebenen Zeitraum
        for (MealPlan mealPlan : mealPlans) {
            if (mealPlan.getDate().isAfter(startDate.minusDays(1)) && mealPlan.getDate().isBefore(endDate.plusDays(1))) {
                addIngredientsFromRecipe(mealPlan.getBreakfastRecipe(), mealPlan.getBreakfastPortionSize(), ingredientMap);
                addIngredientsFromRecipe(mealPlan.getLunchRecipe(), mealPlan.getLunchPortionSize(), ingredientMap);
                addIngredientsFromRecipe(mealPlan.getDinnerRecipe(), mealPlan.getDinnerPortionSize(), ingredientMap);
            }
        }

        // Zutaten aus der Map zur Einkaufsliste hinzufügen
        items = new ArrayList<>();
        for (Map.Entry<String, List<ShoppingItem>> entry : ingredientMap.entrySet()) {
            ShoppingItem combinedItem = combineItems(entry.getValue());
            items.add(combinedItem);
        }
    }

    private void addIngredientsFromRecipe(Recipe recipe, int portionSize, Map<String, List<ShoppingItem>> ingredientMap) {
        if (recipe != null && recipe.getIngredients() != null) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.getQuantity() != null && !ingredient.getQuantity().trim().isEmpty()) {
                    // Umrechnung und Skalierung der Menge basierend auf der Portion
                    double scaledQuantity = convertAndScaleQuantity(ingredient.getQuantity(), ingredient.getUnit(), portionSize);

                    // Zutaten nur zusammenfassen, wenn sie die gleiche Einheit haben
                    String unit = ingredient.getUnit().toString();

                    // Zutaten zu einer Liste hinzufügen, die denselben Namen haben
                    ingredientMap.computeIfAbsent(ingredient.getName(), k -> new ArrayList<>())
                            .add(new ShoppingItem(ingredient.getName(), scaledQuantity, unit));
                } else {
                    System.err.println("Warnung: Leere oder fehlende Menge für Zutat: " + ingredient.getName());
                }
            }
        }
    }

    private ShoppingItem combineItems(List<ShoppingItem> items) {
        String ingredientName = items.get(0).getIngredientName();
        Map<String, Double> unitAmounts = new HashMap<>();
        StringBuilder combinedUnits = new StringBuilder();
        double totalAmount = 0;

        // Mengen nach Einheiten gruppieren und Gesamtmenge berechnen
        for (ShoppingItem item : items) {
            totalAmount += item.getAmount();  // Gesamtmenge für die Zutat berechnen
            if (unitAmounts.containsKey(item.getUnit())) {
                unitAmounts.put(item.getUnit(), unitAmounts.get(item.getUnit()) + item.getAmount());
            } else {
                unitAmounts.put(item.getUnit(), item.getAmount());
            }
        }

        // Mengen und Einheiten korrekt zusammenführen
        for (Map.Entry<String, Double> entry : unitAmounts.entrySet()) {
            combinedUnits.append(entry.getValue()).append(" ").append(entry.getKey()).append(", ");
        }

        // Entferne das letzte Komma und Leerzeichen, falls vorhanden
        if (combinedUnits.length() > 0) {
            combinedUnits.setLength(combinedUnits.length() - 2); // Entfernt das letzte Komma
        }

        return new ShoppingItem(ingredientName, totalAmount, combinedUnits.toString());
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

    private double parseQuantity(String quantity, QuantityUnit unit) {
        try {
            // Ersetze Komma durch Punkt, um das Format zu korrigieren
            quantity = quantity.replace(',', '.');
            return Double.parseDouble(quantity);
        } catch (NumberFormatException e) {
            return 0.0;  // Falls die Menge nicht geparst werden kann, geben wir 0 zurück
        }
    }
}
