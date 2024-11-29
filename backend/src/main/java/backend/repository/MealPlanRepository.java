package backend.repository;

import backend.model.MealPlan.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    // Suche nach MealPlan für ein bestimmtes Datum
    Optional<MealPlan> findByDate(LocalDate date);
    boolean existsByDate(LocalDate date);
}
