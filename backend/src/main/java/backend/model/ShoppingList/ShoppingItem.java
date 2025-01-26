package backend.model.ShoppingList;
/**
 * Represents an item in a shopping list, which consists of an ingredient, its amount, and its unit.
 */
public class ShoppingItem {

    private String ingredientName;
    private double amount;
    private String unit; // Einheit der Zutat

    public ShoppingItem(String ingredientName, double amount, String unit) {
        this.ingredientName = ingredientName;
        this.amount = amount;
        this.unit = unit;
    }

    // Getter und Setter
    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return ingredientName + ": " + amount + " " + unit;
    }
}
