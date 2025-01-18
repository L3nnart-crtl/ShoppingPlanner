package backend.repository;

import backend.model.MealPlan.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    // Suche nach MealPlan für ein bestimmtes Datum und tenantId
    Optional<MealPlan> findByTenantIdAndDate(String tenantId, LocalDate date);

    // Überprüfen, ob ein MealPlan für ein bestimmtes Datum und tenantId existiert
    boolean existsByTenantIdAndDate(String tenantId, LocalDate date);

    // Alle MealPlans für einen bestimmten tenantId
    List<MealPlan> findByTenantId(String tenantId);
}
