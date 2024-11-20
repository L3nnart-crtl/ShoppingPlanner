package backend.controller;

import backend.model.MealPlan;
import backend.model.MealPlanRequest;
import backend.service.MealPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
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
        List<MealPlanResponse> mealPlansResponseList = new ArrayList<>();
        MealPlanResponse mealPlanResponse;
        for (MealPlan mealPlan : mealPlans) {
            mealPlanResponse = new MealPlanResponse(mealPlan.getBreakfastRecipe().getId(),mealPlan.getLunchRecipe().getId(),mealPlan.getDinnerRecipe().getId());
            mealPlansResponseList.add(mealPlanResponse);
        }
        return mealPlansResponseList;
    }

    @PostMapping
    public ResponseEntity<?> addMealPlan(@RequestBody MealPlanRequest mealPlan) {
        try {
            MealPlan mealPlanToAdd = mealPlanService.saveMealPlan(
                    mealPlan.getDate(),
                    mealPlan.getBreakfastRecipeId(),
                    mealPlan.getLunchRecipeId(),
                    mealPlan.getDinnerRecipeId()
            );
            URI location = URI.create("/api/mealplans/" + mealPlanToAdd.getId());
            return ResponseEntity.created(location).body(mealPlanToAdd);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ein unerwarteter Fehler ist aufgetreten.");
        }
    }

    @GetMapping("/{date}")
    public ResponseEntity<MealPlanResponse> getMealPlanByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            MealPlan mealPlan = mealPlanService.getMealPlanByDate(parsedDate);
            if (mealPlan != null) {
                MealPlanResponse mealPlanResponse = new MealPlanResponse(mealPlan.getBreakfastRecipe().getId(),mealPlan.getLunchRecipe().getId(),mealPlan.getDinnerRecipe().getId());
                return ResponseEntity.ok(mealPlanResponse);
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
