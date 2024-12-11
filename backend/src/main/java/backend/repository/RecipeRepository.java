package backend.repository;

import backend.model.Recipe.Recipe;
import backend.model.Recipe.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);



    @Query("""
        SELECT r 
        FROM Recipe r 
        JOIN r.tags t 
        WHERE t IN :tags
    """)
    List<Recipe> findByTagsIn(@Param("tags") List<Tag> tags);

    boolean existsByName(String recipeName);

    @Query("""
    SELECT r 
    FROM Recipe r
    JOIN r.tags t
    WHERE r.name LIKE %:name% 
    AND t IN :tags
    GROUP BY r
    HAVING COUNT(t) = :tagsSize
""")
    List<Recipe> findByNameContainingIgnoreCaseAndTagsIn(@Param("name") String name,
                                                         @Param("tags") List<Tag> tags,
                                                         @Param("tagsSize") int tagsSize);
}
