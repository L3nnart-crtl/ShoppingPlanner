package backend.model.Recipe;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

/**
 * Represents a recipe that includes a list of ingredients, nutritional values, and tags.
 * Each recipe is associated with a tenant for multitenancy purposes.
 */
@Entity
@Table(name = "Recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Lob
    private String description;
    private Integer cookingTime;
    private boolean isFavorite;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Tag> tags;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ingredient> ingredients;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    // Nutritional values
    private Double totalCalories = 0.0;
    private Double totalProteins = 0.0;
    private Double totalFats = 0.0;
    private Double totalCarbohydrates = 0.0;

    // Relative contributions
    private Double proteinPercentage = 0.0;
    private Double fatPercentage = 0.0;
    private Double carbohydratePercentage = 0.0;

    /**
     * Default constructor.
     */
    public Recipe() {}

    /**
     * Constructor to create a Recipe with specific details.
     *
     * @param name The name of the recipe.
     * @param description A description of the recipe.
     * @param cookingTime The cooking time in minutes.
     * @param ingredients A list of ingredients used in the recipe.
     * @param tags A set of tags associated with the recipe.
     * @param tenantId The tenant ID for multitenancy.
     */
    public Recipe(String name, String description, Integer cookingTime, List<Ingredient> ingredients, Set<Tag> tags, String tenantId) {
        this.name = name;
        this.description = description;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.tags = tags;
        this.isFavorite = false; // Default value
        this.tenantId = tenantId;
    }

    // Getter and Setter methods

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(Integer cookingTime) {
        this.cookingTime = cookingTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        calculateNutritionalValues(); // Recalculate when ingredients are set
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public Double getTotalProteins() {
        return totalProteins;
    }

    public Double getTotalFats() {
        return totalFats;
    }

    public Double getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public Double getProteinPercentage() {
        return proteinPercentage;
    }

    public Double getFatPercentage() {
        return fatPercentage;
    }

    public Double getCarbohydratePercentage() {
        return carbohydratePercentage;
    }

    /**
     * Converts the quantity of an ingredient to grams based on its unit.
     *
     * @param quantity Quantity as a string (assumed to be numeric).
     * @param unit     Unit of the quantity (e.g., gram, kilogram).
     * @return Quantity in grams.
     */
    public double convertToGrams(String quantity, QuantityUnit unit) {
        double value = Double.parseDouble(quantity);
        switch (unit) {
            case KILOGRAM:
                return value * 1000;
            case MILLIGRAM:
                return value / 1000;
            case GRAM:
            default:
                return value;
        }
    }

    /**
     * Calculates total and relative nutritional values based on the ingredients.
     */
    public void calculateNutritionalValues() {
        double calories = 0.0;
        double proteins = 0.0;
        double fats = 0.0;
        double carbohydrates = 0.0;

        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                double quantityInGrams = convertToGrams(ingredient.getQuantity(), ingredient.getUnit());
                proteins += ingredient.getProteins() * quantityInGrams / 100;
                fats += ingredient.getFats() * quantityInGrams / 100;
                carbohydrates += ingredient.getCarbohydrates() * quantityInGrams / 100;
            }
        }

        this.totalCalories = roundToOneDecimalPlace(proteins * 4 + fats * 9 + carbohydrates * 4);
        this.totalProteins = roundToOneDecimalPlace(proteins);
        this.totalFats = roundToOneDecimalPlace(fats);
        this.totalCarbohydrates = roundToOneDecimalPlace(carbohydrates);

        if (this.totalCalories > 0) {
            this.proteinPercentage = roundToOneDecimalPlace((proteins * 4 / this.totalCalories) * 100);
            this.fatPercentage = roundToOneDecimalPlace((fats * 9 / this.totalCalories) * 100);
            this.carbohydratePercentage = roundToOneDecimalPlace((carbohydrates * 4 / this.totalCalories) * 100);
        } else {
            this.proteinPercentage = 0.0;
            this.fatPercentage = 0.0;
            this.carbohydratePercentage = 0.0;
        }
    }

    /**
     * Rounds a given value to one decimal place.
     *
     * @param value The value to round.
     * @return The rounded value.
     */
    private Double roundToOneDecimalPlace(Double value) {
        if (value == null) return null;
        return Math.round(value * 10.0) / 10.0;
    }
}
