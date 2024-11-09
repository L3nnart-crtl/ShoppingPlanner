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

@Repository
public class RecipeRepository {

    // JdbcTemplate helps to execute SQL queries without worrying about managing database connections manually.
    private final JdbcTemplate jdbcTemplate;

    public RecipeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Saves a new recipe to the database.
     * A recipe includes a name, a description, and a list of ingredients.
     */
    public void saveRecipe(Recipe recipe) {
        // SQL query to insert a new recipe into the database.
        // We are inserting the name and description of the recipe.
        String insertRecipeSql = "INSERT INTO Recipe (name, description) VALUES (?, ?)";

        // KeyHolder helps to retrieve the auto-generated ID (e.g., recipeId) after inserting a recipe.
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        // Execute the SQL query and store the generated recipe ID.
        jdbcTemplate.update(
                connection -> {
                    // Prepare the SQL statement and set the placeholders (?) with actual values.
                    PreparedStatement ps = connection.prepareStatement(insertRecipeSql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, recipe.getName()); // Sets the recipe name.
                    ps.setString(2, recipe.getDescription()); // Sets the recipe description.
                    return ps;
                },
                keyHolder
        );

        // Retrieve the auto-generated ID of the new recipe.
        Long recipeId = keyHolder.getKey().longValue();

        // Now insert the ingredients for the recipe into the Ingredient table.
        for (Ingredient ingredient : recipe.getIngredients()) {
            String insertIngredientSql = "INSERT INTO Ingredient (name, quantity, recipe_id) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertIngredientSql, ingredient.getName(), ingredient.getQuantity(), recipeId);
        }
    }

    /**
     * Retrieves all recipes from the database, including their ingredients.
     */
    public List<Recipe> getAllRecipes() {
        // SQL query to fetch all recipes from the Recipe table.
        String sql = "SELECT * FROM Recipe";
        List<Recipe> recipes = jdbcTemplate.query(sql, new RecipeRowMapper());

        // For each recipe, fetch the corresponding ingredients from the Ingredient table.
        for (Recipe recipe : recipes) {
            String ingredientSql = "SELECT * FROM Ingredient WHERE recipe_id = ?";
            List<Ingredient> ingredients = jdbcTemplate.query(ingredientSql, new IngredientRowMapper(), recipe.getId());
            recipe.setIngredients(ingredients);
        }

        return recipes;
    }

    /**
     * Helper class to map a row from the Recipe table to a Recipe object.
     */
    private static class RecipeRowMapper implements RowMapper<Recipe> {
        @Override
        public Recipe mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Recipe recipe = new Recipe();
            recipe.setId(rs.getLong("id")); // Reads the recipe ID from the database.
            recipe.setName(rs.getString("name")); // Reads the recipe name.
            recipe.setDescription(rs.getString("description")); // Reads the recipe description.
            return recipe;
        }
    }

    /**
     * Helper class to map a row from the Ingredient table to an Ingredient object.
     */
    private static class IngredientRowMapper implements RowMapper<Ingredient> {
        @Override
        public Ingredient mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getLong("id")); // Reads the ingredient ID.
            ingredient.setName(rs.getString("name")); // Reads the ingredient name.
            ingredient.setQuantity(rs.getString("quantity")); // Reads the ingredient quantity.
            ingredient.setRecipeId(rs.getLong("recipe_id")); // Reads the associated recipe ID.
            return ingredient;
        }
    }
}
