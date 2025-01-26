package backend.model.MealPlan;

import backend.model.Recipe.Recipe;
import jakarta.persistence.*;
import java.time.LocalDate;
/**
 * Entity class representing a meal plan for a specific day. It contains information about the
 * recipes for breakfast, lunch, and dinner, as well as the portion sizes for each meal.
 */
@Entity
@Table(name = "MealPlan")
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

    // Neue Spalte für Tenant-ID
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    // Standard Constructor
    public MealPlan() {}

    public MealPlan(final String tenantId, final LocalDate date, final Recipe breakfastRecipe, final int breakfastPortionSize, final Recipe lunchRecipe, final int lunchPortionSize, final Recipe dinnerRecipe, final int dinnerPortionSize) {
        this.date = date;
        this.breakfastRecipe = breakfastRecipe;
        this.breakfastPortionSize = breakfastPortionSize;
        this.lunchRecipe = lunchRecipe;
        this.lunchPortionSize = lunchPortionSize;
        this.dinnerRecipe = dinnerRecipe;
        this.dinnerPortionSize = dinnerPortionSize;
        this.tenantId = tenantId;
    }

    // Getter und Setter für tenantId
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    // Getter und Setter für andere Felder
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
