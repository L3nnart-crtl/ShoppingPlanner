package backend.controller;

import backend.model.MealPlan.MealPlan;
import backend.model.MealPlan.MealPlanDTO;
import backend.service.MealPlanService;
import backend.multitenant.tenantId.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/mealplans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    // Endpoint to get all meal plans
    @GetMapping
    public List<MealPlanDTO> getAllMealPlans() {
        String tenantId = TenantContext.getCurrentTenant();
        List<MealPlan> mealPlans = mealPlanService.getAllMealPlans(tenantId);
        return mealPlans.stream()
                .map(MealPlanDTO::new)
                .toList();
    }

    // Endpoint to create a new meal plan
    @PostMapping
    public ResponseEntity<?> addMealPlan(@RequestBody MealPlanDTO mealPlanDTO) {
        String tenantId = TenantContext.getCurrentTenant();
        try {
            // Check for null values and handle accordingly
            Long breakfastRecipeId = mealPlanDTO.getBreakfastRecipeId() != null ? mealPlanDTO.getBreakfastRecipeId() : null;
            Long lunchRecipeId = mealPlanDTO.getLunchRecipeId() != null ? mealPlanDTO.getLunchRecipeId() : null;
            Long dinnerRecipeId = mealPlanDTO.getDinnerRecipeId() != null ? mealPlanDTO.getDinnerRecipeId() : null;

            MealPlan mealPlanToAdd = mealPlanService.saveMealPlan(
                    tenantId,
                    LocalDate.parse(mealPlanDTO.getDate()),
                    breakfastRecipeId,
                    mealPlanDTO.getBreakfastPortionSize(),
                    lunchRecipeId,
                    mealPlanDTO.getLunchPortionSize(),
                    dinnerRecipeId,
                    mealPlanDTO.getDinnerPortionSize()
            );

            URI location = URI.create("/api/mealplans/" + mealPlanToAdd.getId());
            return ResponseEntity.created(location).body(new MealPlanDTO(mealPlanToAdd));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Endpoint to update an existing meal plan by date
    @PutMapping("/{date}")
    public ResponseEntity<?> updateMealPlan(@PathVariable String date, @RequestBody MealPlanDTO mealPlanDTO) {
        String tenantId = TenantContext.getCurrentTenant();
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            MealPlan existingMealPlan = mealPlanService.getMealPlanByDate(tenantId, parsedDate);

            if (existingMealPlan == null) {
                return ResponseEntity.notFound().build();
            }

            // Check for null values and handle accordingly
            Long breakfastRecipeId = mealPlanDTO.getBreakfastRecipeId() != null ? mealPlanDTO.getBreakfastRecipeId() : null;
            Long lunchRecipeId = mealPlanDTO.getLunchRecipeId() != null ? mealPlanDTO.getLunchRecipeId() : null;
            Long dinnerRecipeId = mealPlanDTO.getDinnerRecipeId() != null ? mealPlanDTO.getDinnerRecipeId() : null;

            MealPlan updatedMealPlan = mealPlanService.updateMealPlan(
                    tenantId,
                    parsedDate,
                    breakfastRecipeId,
                    mealPlanDTO.getBreakfastPortionSize(),
                    lunchRecipeId,
                    mealPlanDTO.getLunchPortionSize(),
                    dinnerRecipeId,
                    mealPlanDTO.getDinnerPortionSize()
            );

            return ResponseEntity.ok(new MealPlanDTO(updatedMealPlan));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Endpoint to delete a meal plan by date
    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable String date) {
        String tenantId = TenantContext.getCurrentTenant();
        try {
            LocalDate providedDate = LocalDate.parse(date);
            boolean isDeleted = mealPlanService.deleteMealPlanByDate(tenantId, date);

            if (isDeleted) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
