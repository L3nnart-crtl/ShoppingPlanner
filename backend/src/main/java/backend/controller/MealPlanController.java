package backend.controller;

import backend.model.MealPlan.MealPlan;
import backend.model.MealPlan.MealPlanDTO;
import backend.service.MealPlanService;
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

    // MealPlanService is injected to handle the business logic
    private final MealPlanService mealPlanService;

    // Constructor to initialize the mealPlanService
    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    // Endpoint to get all meal plans
    @GetMapping
    public List<MealPlanDTO> getAllMealPlans() {
        // Fetch all meal plans from the service
        List<MealPlan> mealPlans = mealPlanService.getAllMealPlans();

        // Convert the list of MealPlan objects to MealPlanDTO objects and return
        return mealPlans.stream()
                .map(MealPlanDTO::new)  // Convert each MealPlan to MealPlanDTO
                .toList();
    }

    // Endpoint to create a new meal plan
    @PostMapping
    public ResponseEntity<?> addMealPlan(@RequestBody MealPlanDTO mealPlanDTO) {
        try {
            // Save the meal plan using the service, converting the date string to LocalDate
            MealPlan mealPlanToAdd = mealPlanService.saveMealPlan(
                    LocalDate.parse(mealPlanDTO.getDate()),  // Parse the date
                    mealPlanDTO.getBreakfastRecipeId(),
                    mealPlanDTO.getBreakfastPortionSize(),
                    mealPlanDTO.getLunchRecipeId(),
                    mealPlanDTO.getLunchPortionSize(),
                    mealPlanDTO.getDinnerRecipeId(),
                    mealPlanDTO.getDinnerPortionSize()
            );

            // Create a URI for the new meal plan
            URI location = URI.create("/api/mealplans/" + mealPlanToAdd.getId());

            // Return a response with the created meal plan and the HTTP status code 201 (Created)
            return ResponseEntity.created(location).body(new MealPlanDTO(mealPlanToAdd));
        } catch (IllegalArgumentException e) {
            // If the input is invalid, return a bad request response
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // For any unexpected errors, return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Endpoint to update an existing meal plan by date
    @PutMapping("/{date}")
    public ResponseEntity<?> updateMealPlan(@PathVariable String date, @RequestBody MealPlanDTO mealPlanDTO) {
        try {
            // Parse the date from the path variable
            LocalDate parsedDate = LocalDate.parse(date);

            // Fetch the existing meal plan for the given date
            MealPlan existingMealPlan = mealPlanService.getMealPlanByDate(parsedDate);

            // If the meal plan doesn't exist, return a 404 Not Found response
            if (existingMealPlan == null) {
                return ResponseEntity.notFound().build();
            }

            // Update the meal plan with the provided data
            MealPlan updatedMealPlan = mealPlanService.updateMealPlan(
                    parsedDate,
                    mealPlanDTO.getBreakfastRecipeId(),
                    mealPlanDTO.getBreakfastPortionSize(),
                    mealPlanDTO.getLunchRecipeId(),
                    mealPlanDTO.getLunchPortionSize(),
                    mealPlanDTO.getDinnerRecipeId(),
                    mealPlanDTO.getDinnerPortionSize()
            );

            // Return the updated meal plan as a response
            return ResponseEntity.ok(new MealPlanDTO(updatedMealPlan));
        } catch (DateTimeParseException e) {
            // If the date format is invalid, return a bad request response
            return ResponseEntity.badRequest().body("Invalid date format.");
        } catch (IllegalArgumentException e) {
            // Handle invalid argument errors and return a bad request response
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // For any unexpected errors, return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Endpoint to delete a meal plan by date
    @DeleteMapping("/{date}")
    public ResponseEntity<Void> deleteMealPlan(@PathVariable String date) {
        try {
            // Parse the date from the path variable
            LocalDate providedDate = LocalDate.parse(date);
            LocalDate today = LocalDate.now();

            // Attempt to delete the meal plan for the given date
            boolean isDeleted = mealPlanService.deleteMealPlanByDate(date);

            // If the deletion was successful, return a 204 No Content response
            if (isDeleted) {
                return ResponseEntity.noContent().build();
            }
            // If no meal plan was found for the provided date, return a 404 Not Found response
            return ResponseEntity.notFound().build();

        } catch (DateTimeParseException e) {
            // If the date format is invalid, return a bad request response
            return ResponseEntity.badRequest().build();
        }
    }
}
