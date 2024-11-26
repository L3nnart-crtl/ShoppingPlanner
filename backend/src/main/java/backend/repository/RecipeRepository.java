package backend.repository;

import backend.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String name);
    List<Recipe> findByTagsIn(List<String> tags);
    List<Recipe> findByNameAndTagsIn(String name, List<String> tags);
}