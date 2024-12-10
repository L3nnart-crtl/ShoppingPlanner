package backend.repository;

import backend.model.MealPlan.MealPlan;
import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    // Suche nach MealPlan für ein bestimmtes Datum
    Optional<MealPlan> findByDate(LocalDate date);
    boolean existsByDate(LocalDate date);

    @Query("SELECT AVG(mp.breakfastPortionSize + mp.lunchPortionSize + mp.dinnerPortionSize) FROM MealPlan mp")
    Integer calculateAveragePortionSize();

    @Query("SELECT AVG(r.cookingTime) FROM MealPlan mp " +
            "JOIN Recipe r ON (mp.breakfastRecipe = r OR mp.lunchRecipe = r OR mp.dinnerRecipe = r)")
    Long calculateAverageCookingTime();

    @Query("SELECT tag, COUNT(tag) FROM MealPlan mp JOIN mp.breakfastRecipe.tags tag GROUP BY tag ORDER BY COUNT(tag) DESC")
    Map<Tag, Long> calculateTagUsage();


    @Query("SELECT r, COUNT(r) FROM MealPlan mp " +
            "JOIN Recipe r ON (mp.breakfastRecipe = r OR mp.lunchRecipe = r OR mp.dinnerRecipe = r) " +
            "GROUP BY r ORDER BY COUNT(r) DESC")
    Map<Recipe, Long> calculatePopularRecipes();

    @Query("SELECT i.name, COUNT(i) FROM MealPlan mp " +
            "JOIN Recipe r ON (mp.breakfastRecipe = r OR mp.lunchRecipe = r OR mp.dinnerRecipe = r) " +
            "JOIN r.ingredients i " +
            "GROUP BY i.name ORDER BY COUNT(i) DESC")
    Map<String, Integer> calculateTopIngredients();
}
