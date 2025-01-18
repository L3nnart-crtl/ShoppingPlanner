package backend.repository;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Rezepte basierend auf dem TenantId und Name suchen
    List<Recipe> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);

    // Rezepte basierend auf dem TenantId und maximaler Kochzeit suchen
    List<Recipe> findByTenantIdAndCookingTimeLessThanEqual(String tenantId, Integer maxCookingTime);

    // Neue Abfrage für Rezepte basierend auf Tags und TenantId
    @Query("""
        SELECT r 
        FROM Recipe r 
        JOIN r.tags t 
        WHERE t IN :tags
        AND r.tenantId = :tenantId
    """)
    List<Recipe> findByTenantIdAndTagsIn(@Param("tenantId") String tenantId, @Param("tags") List<Tag> tags);

    boolean existsByTenantIdAndName(String tenantId, String recipeName);

    // Suche nach Name und Tags basierend auf TenantId
    @Query("""
    SELECT r 
    FROM Recipe r
    JOIN r.tags t
    WHERE r.name LIKE %:name% 
    AND t IN :tags
    AND r.tenantId = :tenantId
    """)
    List<Recipe> findByTenantIdAndNameContainingIgnoreCaseAndTagsIn(@Param("tenantId") String tenantId,
                                                                    @Param("name") String name,
                                                                    @Param("tags") List<Tag> tags);

    // Finde Rezepte anhand des TenantId
    List<Recipe> findByTenantId(String tenantId);

    // Finde Rezept anhand des TenantId und der Rezept-ID
    @Query("""
        SELECT r 
        FROM Recipe r
        WHERE r.id = :id 
        AND r.tenantId = :tenantId
    """)
    Optional<Recipe> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") Long id);
}
