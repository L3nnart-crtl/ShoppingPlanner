package backend.controller;

import backend.model.MealPlan.MealPlan;
import backend.model.ShoppingList.ShoppingItem;
import backend.model.ShoppingList.ShoppingList;
import backend.repository.MealPlanRepository;
import backend.multitenant.tenantId.TenantContext; // Importiere TenantContext
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    @Autowired
    private MealPlanRepository mealPlanRepository;  // Repository für MealPlans

    // Endpoint, um eine Einkaufsliste zu erstellen
    @PostMapping("/generate")
    public List<ShoppingItem> generateShoppingList(@RequestParam("startDate") LocalDate startDate,
                                                   @RequestParam("endDate") LocalDate endDate) {
        // Hole die Tenant-ID aus dem aktuellen Kontext
        String tenantId = TenantContext.getCurrentTenant();

        // Prüfen, ob die Tenant-ID gesetzt ist, falls nicht, gebe einen Fehler zurück
        if (tenantId == null) {
            throw new IllegalStateException("Tenant ID is not set in the context.");
        }

        // Finde alle MealPlans für den aktuellen Tenant
        List<MealPlan> allMealPlans = mealPlanRepository.findByTenantId(tenantId);

        // Erstelle eine neue ShoppingList und führe die Methode zur Generierung aus
        ShoppingList shoppingList = new ShoppingList(startDate, endDate);  // Konstruktor mit Start- und Enddatum
        shoppingList.generateShoppingList(allMealPlans); // Diese Methode wird die Einkaufsliste generieren

        // Rückgabe der generierten Einkaufsliste (Items)
        return shoppingList.getItems();
    }
}
