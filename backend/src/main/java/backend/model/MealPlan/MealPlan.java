package backend.model.MealPlan;

import backend.model.Recipe.Recipe;
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

    private int breakfastPortionSize; // Portionenanzahl für das Frühstück

    @ManyToOne
    @JoinColumn(name = "lunch_recipe_id")
    private Recipe lunchRecipe; // Mittagessen-Rezept

    private int lunchPortionSize; // Portionenanzahl für das Mittagessen

    @ManyToOne
    @JoinColumn(name = "dinner_recipe_id")
    private Recipe dinnerRecipe; // Abendessen-Rezept

    private int dinnerPortionSize; // Portionenanzahl für das Abendessen

    // Standard Constructor
    public MealPlan() {}

    // Constructor mit Parametern
    public MealPlan(LocalDate date, Recipe breakfastRecipe, int breakfastPortionSize,
                    Recipe lunchRecipe, int lunchPortionSize, Recipe dinnerRecipe, int dinnerPortionSize) {
        this.date = date;
        this.breakfastRecipe = breakfastRecipe;
        this.breakfastPortionSize = breakfastPortionSize;
        this.lunchRecipe = lunchRecipe;
        this.lunchPortionSize = lunchPortionSize;
        this.dinnerRecipe = dinnerRecipe;
        this.dinnerPortionSize = dinnerPortionSize;
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

    public int getBreakfastPortionSize() {
        return breakfastPortionSize;
    }

    public void setBreakfastPortionSize(int breakfastPortionSize) {
        this.breakfastPortionSize = breakfastPortionSize;
    }

    public Recipe getLunchRecipe() {
        return lunchRecipe;
    }

    public void setLunchRecipe(Recipe lunchRecipe) {
        this.lunchRecipe = lunchRecipe;
    }

    public int getLunchPortionSize() {
        return lunchPortionSize;
    }

    public void setLunchPortionSize(int lunchPortionSize) {
        this.lunchPortionSize = lunchPortionSize;
    }

    public Recipe getDinnerRecipe() {
        return dinnerRecipe;
    }

    public void setDinnerRecipe(Recipe dinnerRecipe) {
        this.dinnerRecipe = dinnerRecipe;
    }

    public int getDinnerPortionSize() {
        return dinnerPortionSize;
    }

    public void setDinnerPortionSize(int dinnerPortionSize) {
        this.dinnerPortionSize = dinnerPortionSize;
    }
}
