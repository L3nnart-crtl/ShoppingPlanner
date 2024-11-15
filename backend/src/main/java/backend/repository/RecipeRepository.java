package backend.repository;

import backend.model.Ingredient;
import backend.model.Recipe;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class RecipeRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecipeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Recipe> findById(Long id) {
        String sql = "SELECT * FROM Recipe WHERE id = ?";
        List<Recipe> recipes = jdbcTemplate.query(sql, new RecipeRowMapper(), id);

        if (recipes.isEmpty()) {
            return Optional.empty(); // Return an empty Optional if no recipe was found
        } else {
            return Optional.of(recipes.get(0)); // Return the found recipe wrapped in an Optional
        }
    }

    public void saveRecipe(Recipe recipe) {
        String insertRecipeSql = "INSERT INTO Recipe (name, description) VALUES (?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(insertRecipeSql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, recipe.getName());
                    ps.setString(2, recipe.getDescription());
                    return ps;
                },
                keyHolder
        );

        Long recipeId = keyHolder.getKey().longValue();

        for (Ingredient ingredient : recipe.getIngredients()) {
            String insertIngredientSql = "INSERT INTO Ingredient (name, quantity, recipe_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertIngredientSql, ingredient.getName(), ingredient.getQuantity(), recipeId);
        }
    }

    public List<Recipe> getAllRecipes() {
        String sql = "SELECT * FROM Recipe";
        List<Recipe> recipes = jdbcTemplate.query(sql, new RecipeRowMapper());

        for (Recipe recipe : recipes) {
            String ingredientSql = "SELECT * FROM Ingredient WHERE recipe_id = ?";
            List<Ingredient> ingredients = jdbcTemplate.query(ingredientSql, new IngredientRowMapper(), recipe.getId());
            recipe.setIngredients(ingredients);
        }

        return recipes;
    }

    public boolean deleteRecipe(Long recipeId) {
        String deleteIngredientsSql = "DELETE FROM Ingredient WHERE recipe_id = ?";
        int ingredientsDeleted = jdbcTemplate.update(deleteIngredientsSql, recipeId);

        String deleteRecipeSql = "DELETE FROM Recipe WHERE id = ?";
        int recipeDeleted = jdbcTemplate.update(deleteRecipeSql, recipeId);

        return ingredientsDeleted > 0 && recipeDeleted > 0;
    }

    private static class RecipeRowMapper implements RowMapper<Recipe> {
        @Override
        public Recipe mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Recipe recipe = new Recipe();
            recipe.setId(rs.getLong("id"));
            recipe.setName(rs.getString("name"));
            recipe.setDescription(rs.getString("description"));
            return recipe;
        }
    }

    private static class IngredientRowMapper implements RowMapper<Ingredient> {
        @Override
        public Ingredient mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getLong("id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setQuantity(rs.getString("quantity"));
            ingredient.setRecipeId(rs.getLong("recipe_id"));
            return ingredient;
        }
    }
}
