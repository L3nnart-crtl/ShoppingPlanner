package backend.model.MealPlan;

import backend.model.Recipe.Recipe;

public class MealPlanDTO {

    private String date;
    private Long breakfastRecipeId;
    private int breakfastPortionSize;
    private Long lunchRecipeId;
    private int lunchPortionSize;
    private Long dinnerRecipeId;
    private int dinnerPortionSize;
    private Recipe breakfastRecipe;
    private Recipe lunchRecipe;
    private Recipe dinnerRecipe;
    // Zusätzliche Felder, die nur für die Antwort benötigt werden
    private String breakfastRecipeName;
    private String lunchRecipeName;
    private String dinnerRecipeName;

    // Neues Feld für tenantId
    private String tenantId;

    public MealPlanDTO() {}

    // Konstruktor für Response
    public MealPlanDTO(MealPlan mealPlan) {
        this.date = mealPlan.getDate().toString();
        this.breakfastRecipeName = mealPlan.getBreakfastRecipe() != null ? mealPlan.getBreakfastRecipe().getName() : null;
        this.breakfastPortionSize = mealPlan.getBreakfastPortionSize();
        this.lunchRecipeName = mealPlan.getLunchRecipe() != null ? mealPlan.getLunchRecipe().getName() : null;
        this.lunchPortionSize = mealPlan.getLunchPortionSize();
        this.dinnerRecipeName = mealPlan.getDinnerRecipe() != null ? mealPlan.getDinnerRecipe().getName() : null;
        this.dinnerPortionSize = mealPlan.getDinnerPortionSize();
        this.breakfastRecipeId = mealPlan.getBreakfastRecipe() != null ? mealPlan.getBreakfastRecipe().getId() : null;
        this.lunchRecipeId = mealPlan.getLunchRecipe() != null ? mealPlan.getLunchRecipe().getId() : null;
        this.dinnerRecipeId = mealPlan.getDinnerRecipe() != null ? mealPlan.getDinnerRecipe().getId() : null;
        this.breakfastRecipe = mealPlan.getBreakfastRecipe();
        this.lunchRecipe = mealPlan.getLunchRecipe();
        this.dinnerRecipe = mealPlan.getDinnerRecipe();
        // tenantId setzen
        this.tenantId = mealPlan.getTenantId();
    }

    // Getter und Setter für tenantId
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    // Getter und Setter für andere Felder
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public Recipe getLunchRecipe() {
        return lunchRecipe;
    }

    public Recipe getBreakfastRecipe() {
        return breakfastRecipe;
    }

    public Recipe getDinnerRecipe() {
        return dinnerRecipe;
    }

    public void setBreakfastRecipe(final Recipe breakfastRecipe) {
        this.breakfastRecipe = breakfastRecipe;
    }

    public void setLunchRecipe(final Recipe lunchRecipe) {
        this.lunchRecipe = lunchRecipe;
    }

    public void setDinnerRecipe(final Recipe dinnerRecipe) {
        this.dinnerRecipe = dinnerRecipe;
    }
}
