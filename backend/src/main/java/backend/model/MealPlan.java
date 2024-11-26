package backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "MealPlan", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date"})  // Stellt sicher, dass es nur einen MealPlan pro Tag gibt
})
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; // Das genaue Datum für den MealPlan

    @ManyToOne
    @JoinColumn(name = "breakfast_recipe_id")
    private Recipe breakfastRecipe; // Frühstücks-Rezept

    @ManyToOne
    @JoinColumn(name = "lunch_recipe_id")
    private Recipe lunchRecipe; // Mittagessen-Rezept

    @ManyToOne
    @JoinColumn(name = "dinner_recipe_id")
    private Recipe dinnerRecipe; // Abendessen-Rezept

    // Standard Constructor
    public MealPlan() {}

    // Constructor mit Parametern
    public MealPlan(LocalDate date, Recipe breakfastRecipe, Recipe lunchRecipe, Recipe dinnerRecipe) {
        this.date = date;
        this.breakfastRecipe = breakfastRecipe;
        this.lunchRecipe = lunchRecipe;
        this.dinnerRecipe = dinnerRecipe;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Recipe getBreakfastRecipe() {
        return breakfastRecipe;
    }

    public void setBreakfastRecipe(Recipe breakfastRecipe) {
        this.breakfastRecipe = breakfastRecipe;
    }

    public Recipe getLunchRecipe() {
        return lunchRecipe;
    }

    public void setLunchRecipe(Recipe lunchRecipe) {
        this.lunchRecipe = lunchRecipe;
    }

    public Recipe getDinnerRecipe() {
        return dinnerRecipe;
    }

    public void setDinnerRecipe(Recipe dinnerRecipe) {
        this.dinnerRecipe = dinnerRecipe;
    }

}
