package backend.controller;

import backend.model.Recipe.Ingredient;
import backend.service.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing ingredient-related operations.
 * <p>
 * Provides endpoints to search for ingredients using external APIs.
 * </p>
 */
@RestController
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    /**
     * Searches for ingredients based on a query string.
     * <p>
     * This endpoint allows users to search for ingredients by providing a query.
     * It delegates the search operation to the {@link IngredientService}.
     * </p>
     *
     * @param query the search query for ingredients
     * @return a list of {@link Ingredient} objects matching the search query
     */
    @GetMapping("/api/ingredients/search")
    public List<Ingredient> searchIngredients(@RequestParam String query) {
        return ingredientService.searchIngredientsFromAPI(query);
    }
}
