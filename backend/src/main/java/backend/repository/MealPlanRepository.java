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

}
