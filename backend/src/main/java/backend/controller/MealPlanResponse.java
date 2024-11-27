package backend.controller;

import backend.model.MealPlan;

public class MealPlanResponse {

    private String date;
    private String breakfastRecipeName;
    private int breakfastPortionSize; // Portionenanzahl für Frühstück
    private String lunchRecipeName;
    private int lunchPortionSize; // Portionenanzahl für Mittagessen
    private String dinnerRecipeName;
    private int dinnerPortionSize; // Portionenanzahl für Abendessen

    public MealPlanResponse(final MealPlan mealPlan) {
        this.date = mealPlan.getDate().toString();
        this.breakfastRecipeName = mealPlan.getBreakfastRecipe() != null ? mealPlan.getBreakfastRecipe().getName() : null;
        this.breakfastPortionSize = mealPlan.getBreakfastPortionSize();
        this.lunchRecipeName = mealPlan.getLunchRecipe() != null ? mealPlan.getLunchRecipe().getName() : null;
        this.lunchPortionSize = mealPlan.getLunchPortionSize();
        this.dinnerRecipeName = mealPlan.getDinnerRecipe() != null ? mealPlan.getDinnerRecipe().getName() : null;
        this.dinnerPortionSize = mealPlan.getDinnerPortionSize();
    }

    // Getter und Setter
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBreakfastRecipeName() {
        return breakfastRecipeName;
    }

    public void setBreakfastRecipeName(String breakfastRecipeName) {
        this.breakfastRecipeName = breakfastRecipeName;
    }

    public int getBreakfastPortionSize() {
        return breakfastPortionSize;
    }

    public void setBreakfastPortionSize(int breakfastPortionSize) {
        this.breakfastPortionSize = breakfastPortionSize;
    }

    public String getLunchRecipeName() {
        return lunchRecipeName;
    }

    public void setLunchRecipeName(String lunchRecipeName) {
        this.lunchRecipeName = lunchRecipeName;
    }

    public int getLunchPortionSize() {
        return lunchPortionSize;
    }

    public void setLunchPortionSize(int lunchPortionSize) {
        this.lunchPortionSize = lunchPortionSize;
    }

    public String getDinnerRecipeName() {
        return dinnerRecipeName;
    }

    public void setDinnerRecipeName(String dinnerRecipeName) {
        this.dinnerRecipeName = dinnerRecipeName;
    }

    public int getDinnerPortionSize() {
        return dinnerPortionSize;
    }

    public void setDinnerPortionSize(int dinnerPortionSize) {
        this.dinnerPortionSize = dinnerPortionSize;
    }
}
