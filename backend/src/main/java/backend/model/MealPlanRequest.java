package backend.model;
import java.time.LocalDate;

public class MealPlanRequest {
    private LocalDate date;
    private Long breakfastRecipeId;
    private Long lunchRecipeId;
    private Long dinnerRecipeId;

    // Getter und Setter
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getBreakfastRecipeId() {
        return breakfastRecipeId;
    }

    public void setBreakfastRecipeId(Long breakfastRecipeId) {
        this.breakfastRecipeId = breakfastRecipeId;
    }

    public Long getLunchRecipeId() {
        return lunchRecipeId;
    }

    public void setLunchRecipeId(Long lunchRecipeId) {
        this.lunchRecipeId = lunchRecipeId;
    }

    public Long getDinnerRecipeId() {
        return dinnerRecipeId;
    }

    public void setDinnerRecipeId(Long dinnerRecipeId) {
        this.dinnerRecipeId = dinnerRecipeId;
    }
}
