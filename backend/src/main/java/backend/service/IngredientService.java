package backend.service;

import backend.model.Recipe.Ingredient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class IngredientService {

    // Base URL for the Open Food Facts API
    private static final String OPEN_FOOD_FACTS_API_URL = "https://world.openfoodfacts.org/cgi/search.pl";

    /**
     * Searches for ingredients from the Open Food Facts API based on a query string.
     *
     * @param query the search term used to find ingredients
     * @return a list of ingredients matching the search query
     */
    public List<Ingredient> searchIngredientsFromAPI(String query) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = OPEN_FOOD_FACTS_API_URL +
                "?search_terms=" + query +
                "&search_simple=1&action=process&json=1&page_size=5";

        try {
            // Make a GET request to the Open Food Facts API
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            List<Map<String, Object>> products = (List<Map<String, Object>>) response.get("products");

            List<Ingredient> ingredients = new ArrayList<>();
            // Process each product in the response
            for (Map<String, Object> product : products) {
                String productName = (String) product.get("product_name");
                Map<String, Object> nutriments = (Map<String, Object>) product.get("nutriments");

                if (productName != null && !productName.isEmpty() && nutriments != null) {
                    // Create a new Ingredient object
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(productName);

                    // Extract nutritional values with type safety
                    ingredient.setCalories(getNutrientValue(nutriments, "energy-kcal_100g"));
                    ingredient.setProteins(getNutrientValue(nutriments, "proteins_100g"));
                    ingredient.setFats(getNutrientValue(nutriments, "fat_100g"));
                    ingredient.setCarbohydrates(getNutrientValue(nutriments, "carbohydrates_100g"));

                    ingredients.add(ingredient);
                }
            }
            return ingredients;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();  // Return an empty list in case of error
        }
    }

    /**
     * Helper method to extract nutrient values from the nutriments map with type safety.
     *
     * @param nutriments the map containing nutritional data
     * @param nutrientKey the key for the nutrient value to retrieve
     * @return the nutrient value as a Double, or 0.0 if the nutrient is not found
     */
    private Double getNutrientValue(Map<String, Object> nutriments, String nutrientKey) {
        // Retrieve the nutrient value using the provided key, default to 0 if not found
        Object value = nutriments.getOrDefault(nutrientKey, 0);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();  // Converts Integer or Double to Double
        }
        return 0.0;  // Default value if the nutrient is not present or is of an unexpected type
    }
}
