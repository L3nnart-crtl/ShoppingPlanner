package backend.repository;

import backend.model.MealPlan.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    Optional<MealPlan> findByTenantIdAndDate(String tenantId, LocalDate date);

    boolean existsByTenantIdAndDate(String tenantId, LocalDate date);

    List<MealPlan> findByTenantId(String tenantId);

    List<MealPlan> findByTenantIdAndDateBetween(String tenantId, LocalDate startDate, LocalDate endDate);
}
