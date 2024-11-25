package backend.model;

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
    private String unit;  // Hinzugefügt für Einheit der Zutaten

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    // Standard Constructor
    public Ingredient() {}

    // Constructor with parameters
    public Ingredient(String name, String quantity, String unit, Recipe recipe) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.recipe = recipe;
    }

    // Getter und Setter
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }
}