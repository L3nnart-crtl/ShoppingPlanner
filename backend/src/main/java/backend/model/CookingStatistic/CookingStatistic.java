package backend.model.CookingStatistic;

import backend.model.Recipe.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
/**
 * Represents statistics related to cooking activities, such as the number of cooked recipes,
 * average portions, cooking time, and nutrient distribution.
 * This class is persisted in the database with relevant cooking statistics.
 */
@Entity
public class CookingStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Getters und Setters
    @Setter
    @Getter
    private Long amountOfCookedRecipes;

    @Setter
    @Getter
    private Long averagePortions;
    private Double cookingTime;

    @Setter
    @Getter
    private Long averageCookingTime;

    @Setter
    @Getter
    @ElementCollection
    @MapKeyColumn(name = "tag")
    @Column(name = "amount")
    private Map<Tag, Long> attributes = new HashMap<>();

    @Setter
    @Getter
    @ElementCollection
    @MapKeyColumn(name = "recipe")
    @Column(name = "amount")
    private Map<String, Long> favouriteRecipes = new HashMap<>();

    @Getter
    @Setter
    @ElementCollection
    @MapKeyColumn(name = "ingredient")
    @Column(name = "amount")
    private Map<String, Integer> favouriteIngredients = new HashMap<>();

    @Setter
    @Getter
    private Double averageCaloriesPerRecipe;

    @Setter
    @Getter
    private Double averageCaloriesPerDay;

    @Setter
    @Getter
    @ElementCollection
    @MapKeyColumn(name = "nutrientRecipe")
    @Column(name = "amount")
    private Map<String, Double> averageNutrientDistributionPerRecipe = new HashMap<>();

    @Setter
    @Getter
    @ElementCollection
    @MapKeyColumn(name = "nutrientDay")
    @Column(name = "amount")
    private Map<String, Double> averageNutrientDistributionPerDay = new HashMap<>();


    // Neue Spalte für Tenant-ID
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    // Standard Constructor
    public CookingStatistic() {}

    // Constructor with tenantId
    public CookingStatistic(Long amountOfCookedRecipes, Long averagePortions, Double cookingTime, Long averageCookingTime, String tenantId) {
        this.amountOfCookedRecipes = amountOfCookedRecipes;
        this.averagePortions = averagePortions;
        this.cookingTime = cookingTime;
        this.averageCookingTime = averageCookingTime;
        this.tenantId = tenantId;
    }

    public void setAverageNutrientDistributionPerDay(final Map<String, Double> averageNutrientDistributionPerDay) {
        this.averageNutrientDistributionPerDay = averageNutrientDistributionPerDay;
    }

    public void setAverageNutrientDistributionPerRecipe(final Map<String, Double> averageNutrientDistributionPerRecipe) {
        this.averageNutrientDistributionPerRecipe = averageNutrientDistributionPerRecipe;
    }

    public void setAverageCaloriesPerDay(final Double averageCaloriesPerDay) {
        this.averageCaloriesPerDay = averageCaloriesPerDay;
    }

    public void setAverageCaloriesPerRecipe(final Double averageCaloriesPerRecipe) {
        this.averageCaloriesPerRecipe = averageCaloriesPerRecipe;
    }

    // Getter und Setter für tenantId
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
