package backend.model.Recipe;

import backend.multitenant.tenantId.TenantContext;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

/**
 * Represents an ingredient used in a recipe. Contains information about the ingredient's name,
 * quantity, nutritional values (calories, proteins, fats, carbohydrates), and its associated recipe.
 */
@Entity
@Table(name = "Ingredient")
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String quantity;

    @Enumerated(EnumType.STRING)
    private QuantityUnit unit;

    private Double calories;       // Calories per 100g of the ingredient
    private Double proteins;       // Proteins per 100g of the ingredient
    private Double fats;           // Fats per 100g of the ingredient
    private Double carbohydrates;  // Carbohydrates per 100g of the ingredient

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = true)
    @JsonBackReference
    private Recipe recipe;

    /**
     * Default constructor.
     */
    public Ingredient() {}

    /**
     * Constructor to create an Ingredient with specific details.
     *
     * @param name The name of the ingredient.
     * @param quantity The quantity of the ingredient.
     * @param unit The unit of measurement for the ingredient.
     * @param recipe The recipe that this ingredient is part of.
     */
    public Ingredient(String name, String quantity, QuantityUnit unit, Recipe recipe) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.recipe = recipe;
    }

    // Getter and Setter methods

    public Double getCalories() {
        return calories;
    }

    public void setCalories(final Double calories) {
        this.calories = calories;
    }

    public Double getProteins() {
        return proteins;
    }

    public void setProteins(final Double proteins) {
        this.proteins = proteins;
    }

    public Double getFats() {
        return fats;
    }

    public void setFats(final Double fats) {
        this.fats = fats;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(final Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(final String quantity) {
        this.quantity = quantity;
    }

    public QuantityUnit getUnit() {
        return unit;
    }

    public void setUnit(final QuantityUnit unit) {
        this.unit = unit;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(final Recipe recipe) {
        this.recipe = recipe;
    }
}
