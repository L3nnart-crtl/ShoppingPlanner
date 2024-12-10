package backend.model;

import backend.model.Recipe.Ingredient;
import backend.model.Recipe.Recipe;
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
    private Integer averagePortions;
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
    private Map<Recipe, Long> favouriteRecipes = new HashMap<>();

    @Getter
    @Setter
    @ElementCollection
    @MapKeyColumn(name = "ingredient")
    @Column(name = "amount")
    private Map<String, Integer> favouriteIngredients = new HashMap<>();

}
