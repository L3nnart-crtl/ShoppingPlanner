package backend.controller;

import backend.model.MealPlan.MealPlan;

import backend.model.ShoppingList.ShoppingItem;
import backend.model.ShoppingList.ShoppingList;
import backend.repository.MealPlanRepository;
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
        List<MealPlan> allMealPlans = mealPlanRepository.findAll();
        // Erstelle eine neue ShoppingList und führe die Methode zur Generierung aus
        ShoppingList shoppingList = new ShoppingList(startDate, endDate);  // Konstruktor mit Start- und Enddatum
        shoppingList.generateShoppingList(allMealPlans); // Diese Methode wird die Einkaufsliste generieren

        // Rückgabe der generierten Einkaufsliste (Items)
        return shoppingList.getItems();
    }
}
