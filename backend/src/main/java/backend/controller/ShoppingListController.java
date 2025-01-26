package backend.controller;

import backend.model.MealPlan.MealPlan;
import backend.model.ShoppingList.ShoppingItem;
import backend.model.ShoppingList.ShoppingList;
import backend.repository.MealPlanRepository;
import backend.multitenant.tenantId.TenantContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing the shopping list. Provides an endpoint to generate a shopping list
 * based on MealPlans for a specific tenant.
 */
@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    @Autowired
    private MealPlanRepository mealPlanRepository;  // Repository for MealPlans

    /**
     * Generates a shopping list for a given date range based on the tenant's meal plans.
     *
     * @param startDate The start date for the shopping list.
     * @param endDate The end date for the shopping list.
     * @return A list of shopping items required for the specified date range.
     * @throws IllegalStateException if the tenant ID is not set in the context.
     */
    @PostMapping("/generate")
    public List<ShoppingItem> generateShoppingList(@RequestParam("startDate") LocalDate startDate,
                                                   @RequestParam("endDate") LocalDate endDate) {
        // Retrieve the Tenant ID from the current context
        String tenantId = TenantContext.getCurrentTenant();

        // Check if the Tenant ID is set, if not, return an error
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is not set in the context.");
        }

        // Find all meal plans for the current tenant
        List<MealPlan> allMealPlans = mealPlanRepository.findByTenantId(tenantId);

        // Create a new shopping list and generate the items
        ShoppingList shoppingList = new ShoppingList(startDate, endDate);  // Constructor with start and end date
        shoppingList.generateShoppingList(allMealPlans); // Generate the shopping list based on meal plans

        // Return the generated shopping list items
        return shoppingList.getItems();
    }
}
