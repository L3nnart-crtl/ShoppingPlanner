package backend.model.Recipe;

import backend.repository.IngredientRepository;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer cookingTime; // Kochzeit in Minuten
    private boolean isFavorite;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Tag> tags;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Ingredient> ingredients;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;  // Neue Spalte für die Mandanten-ID

    // Standard Constructor
    public Recipe() {}

    // Constructor with parameters
    public Recipe(String name, String description, Integer cookingTime, List<Ingredient> ingredients, Set<Tag> tags, String tenantId) {
        this.name = name;
        this.description = description;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.tags = tags;
        this.isFavorite = false;  // Standardwert
        this.tenantId = tenantId; // Tenant ID setzen
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
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(final boolean favorite) {
        isFavorite = favorite;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
