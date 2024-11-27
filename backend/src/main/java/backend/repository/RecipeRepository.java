package backend.repository;

import backend.model.Recipe;
import backend.model.Tag;
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
        WHERE r.name LIKE %:name% AND t IN :tags
    """)
    List<Recipe> findByNameAndTagsIn(@Param("name") String name, @Param("tags") List<Tag> tags);

    @Query("""
        SELECT r 
        FROM Recipe r 
        JOIN r.tags t 
        WHERE t IN :tags
    """)
    List<Recipe> findByTagsIn(@Param("tags") List<Tag> tags);
}
