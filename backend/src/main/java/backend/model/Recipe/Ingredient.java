package backend.model.Recipe;

import backend.multitenant.tenantId.TenantContext;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Ingredient")
public class Ingredient {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String quantity;

    @Enumerated(EnumType.STRING)
    private QuantityUnit unit;  // Geändert zu QuantityUnit

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    // Standard Constructor
    public Ingredient() {}

    // Constructor with parameters
    public Ingredient(String name, String quantity, QuantityUnit unit, Recipe recipe) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.recipe = recipe;

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
