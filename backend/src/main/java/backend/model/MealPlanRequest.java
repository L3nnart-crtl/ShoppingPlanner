package backend.model;

import java.time.LocalDate;

public class MealPlanRequest {

    private LocalDate date;
    private Long breakfastRecipeId;
    private int breakfastPortionSize;
    private Long lunchRecipeId;
    private int lunchPortionSize;
    private Long dinnerRecipeId;
    private int dinnerPortionSize;

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

    public int getBreakfastPortionSize() {
        return breakfastPortionSize;
    }

    public void setBreakfastPortionSize(int breakfastPortionSize) {
        this.breakfastPortionSize = breakfastPortionSize;
    }

    public Long getLunchRecipeId() {
        return lunchRecipeId;
    }

    public void setLunchRecipeId(Long lunchRecipeId) {
        this.lunchRecipeId = lunchRecipeId;
    }

    public int getLunchPortionSize() {
        return lunchPortionSize;
    }

    public void setLunchPortionSize(int lunchPortionSize) {
        this.lunchPortionSize = lunchPortionSize;
    }

    public Long getDinnerRecipeId() {
        return dinnerRecipeId;
    }

    public void setDinnerRecipeId(Long dinnerRecipeId) {
        this.dinnerRecipeId = dinnerRecipeId;
    }

    public int getDinnerPortionSize() {
        return dinnerPortionSize;
    }

    public void setDinnerPortionSize(int dinnerPortionSize) {
        this.dinnerPortionSize = dinnerPortionSize;
    }
}
