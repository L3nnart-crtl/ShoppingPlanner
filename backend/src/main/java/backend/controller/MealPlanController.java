package backend.controller;

import backend.model.MealPlan;
import backend.service.MealPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mealplans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    // Alle Mahlzeitenpläne abfragen
    @GetMapping
    public List<MealPlan> getAllMealPlans() {
        return mealPlanService.getAllMealPlans();
    }

    // Mahlzeitenplan für ein bestimmtes Datum hinzufügen
    @PostMapping
    public ResponseEntity<MealPlan> addMealPlan(@RequestParam String date,
                                                @RequestParam Long breakfastRecipeId,
                                                @RequestParam Long lunchRecipeId,
                                                @RequestParam Long dinnerRecipeId) {
        try {
            LocalDate parsedDate = LocalDate.parse(date); // Das Datum im Format YYYY-MM-DD
            MealPlan mealPlan = mealPlanService.saveMealPlan(parsedDate, breakfastRecipeId, lunchRecipeId, dinnerRecipeId);
            return ResponseEntity.ok(mealPlan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);  // Rückgabe einer schlechten Anfrage, wenn der Plan bereits existiert
        }
    }

    // Mahlzeitenplan für einen bestimmten Tag abfragen
    @GetMapping("/{date}")
    public ResponseEntity<MealPlan> getMealPlanByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);  // Das Datum im Format YYYY-MM-DD
            MealPlan mealPlan = mealPlanService.getMealPlanByDate(parsedDate);
            return mealPlan != null ? ResponseEntity.ok(mealPlan) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();  // Fehlerbehandlung bei ungültigem Datum
        }
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable String date) {
        // Hier sollte eine Methode in deinem Service existieren, die den MealPlan nach Datum löscht.
        boolean isDeleted = mealPlanService.deleteMealPlanByDate(date);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
