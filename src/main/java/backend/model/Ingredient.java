package backend.model;

public class Ingredient {
    private Long id;
    private String name;
    private String quantity;
    private Long recipeId;

    // Standard Constructor
    public Ingredient() {}

    // Constructor with parameters
    public Ingredient(String name, String quantity, Long recipeId) {
        this.name = name;
        this.quantity = quantity;
        this.recipeId = recipeId;
    }

    // Getters, Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }
}
