package backend.controller;

import backend.model.MealPlan.MealPlan;
import backend.model.MealPlan.MealPlanRequest;
import backend.service.MealPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mealplans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @GetMapping
    public List<MealPlanResponse> getAllMealPlans() {
        List<MealPlan> mealPlans = mealPlanService.getAllMealPlans();
        return mealPlans.stream()
                .map(mealPlan -> new MealPlanResponse(mealPlan))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> addMealPlan(@RequestBody MealPlanRequest mealPlanRequest) {
        try {
            MealPlan mealPlanToAdd = mealPlanService.saveMealPlan(
                    mealPlanRequest.getDate(),
                    mealPlanRequest.getBreakfastRecipeId(),
                    mealPlanRequest.getBreakfastPortionSize(),
                    mealPlanRequest.getLunchRecipeId(),
                    mealPlanRequest.getLunchPortionSize(),
                    mealPlanRequest.getDinnerRecipeId(),
                    mealPlanRequest.getDinnerPortionSize()
            );
            URI location = URI.create("/api/mealplans/" + mealPlanToAdd.getId());
            return ResponseEntity.created(location).body(new MealPlanResponse(mealPlanToAdd));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten.");
        }
    }

    @GetMapping("/{date}")
    public ResponseEntity<?> getMealPlanByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            MealPlan mealPlan = mealPlanService.getMealPlanByDate(parsedDate);
            if (mealPlan != null) {
                return ResponseEntity.ok(new MealPlanResponse(mealPlan));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable String date) {
        boolean isDeleted = mealPlanService.deleteMealPlanByDate(date);
        if (isDeleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
