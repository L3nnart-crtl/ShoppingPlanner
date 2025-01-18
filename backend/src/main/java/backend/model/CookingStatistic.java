package backend.model;

import backend.model.Recipe.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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

    // Getter und Setter für tenantId
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
