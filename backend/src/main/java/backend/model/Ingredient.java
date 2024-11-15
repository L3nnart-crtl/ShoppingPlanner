package backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Markiert die Klasse als JPA-Entität
@Table(name = "Ingredient")
public class Ingredient {

    @Id // Markiert das 'id'-Feld als Primärschlüssel
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Definiert die automatische Generierung des Schlüssels
    private Long id;

    private String name;
    private String quantity;

    // Beziehung zur Recipe-Tabelle (optional, wenn du die Beziehung modellieren möchtest)
    private Long recipeId;

    // Standard Constructor
    public Ingredient() {}

    // Constructor mit Parametern
    public Ingredient(String name, String quantity, Long recipeId) {
        this.name = name;
        this.quantity = quantity;
        this.recipeId = recipeId;
    }

    // Getter und Setter
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
