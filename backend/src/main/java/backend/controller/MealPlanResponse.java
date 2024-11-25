package backend.controller;

import backend.model.MealPlan;

public class MealPlanResponse {


    private String date;
    private String breakfastRecipeName;
    private String lunchRecipeName;
    private String dinnerRecipeName;

    public MealPlanResponse(final MealPlan mealPlan) {

        this.date = mealPlan.getDate().toString();
        this.breakfastRecipeName = mealPlan.getBreakfastRecipe() != null ? mealPlan.getBreakfastRecipe().getName() : null;
        this.lunchRecipeName = mealPlan.getLunchRecipe() != null ? mealPlan.getLunchRecipe().getName() : null;
        this.dinnerRecipeName = mealPlan.getDinnerRecipe() != null ? mealPlan.getDinnerRecipe().getName() : null;
    }





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

    public String getLunchRecipeName() {
        return lunchRecipeName;
    }

    public void setLunchRecipeName(String lunchRecipeName) {
        this.lunchRecipeName = lunchRecipeName;
    }

    public String getDinnerRecipeName() {
        return dinnerRecipeName;
    }

    public void setDinnerRecipeName(String dinnerRecipeName) {
        this.dinnerRecipeName = dinnerRecipeName;
    }
}
