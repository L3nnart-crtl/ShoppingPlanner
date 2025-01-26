package backend.repository;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("""
        SELECT r 
        FROM Recipe r 
        JOIN r.tags t 
        WHERE t IN :tags
        AND r.tenantId = :tenantId
    """)
    List<Recipe> findByTenantIdAndTagsIn(@Param("tenantId") String tenantId, @Param("tags") List<Tag> tags);

    boolean existsByTenantIdAndName(String tenantId, String recipeName);

    List<Recipe> findByTenantId(String tenantId);

    @Query("""
        SELECT r 
        FROM Recipe r
        WHERE r.id = :id 
        AND r.tenantId = :tenantId
    """)
    Optional<Recipe> findByTenantIdAndId(@Param("tenantId") String tenantId, @Param("id") Long id);

    @Query("""
    SELECT r 
    FROM Recipe r
    JOIN r.tags t
    WHERE r.name LIKE %:name% 
    AND t IN :tags
    AND r.tenantId = :tenantId
    """)
    List<Recipe> findByTenantIdAndNameStartingWithIgnoreCaseAndTagsIn(@Param("tenantId") final String tenantId,@Param("name") final String name,@Param("tags") final List<Tag> tags);

    List<Recipe> findByTenantIdAndNameStartingWithIgnoreCase(String tenantId, String name);
}
