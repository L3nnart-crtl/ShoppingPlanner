package backend.model;

public class ShoppingItem {

    private String ingredientName;
    private double amount;

    public ShoppingItem(String ingredientName, double amount) {
        this.ingredientName = ingredientName;
        this.amount = amount;
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
}
