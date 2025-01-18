package backend.repository;

import backend.model.Recipe.Ingredient;
import backend.model.Recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    // Beispiel-Query, um alle Zutaten eines bestimmten Rezepts für den aktuellen Mandanten zu finden
    @Query("SELECT i FROM Ingredient i WHERE i.recipe.id = :recipeId AND i.recipe.tenantId = :tenantId")
    List<Ingredient> findIngredientsByRecipeIdAndTenantId(Long recipeId, String tenantId);

}
