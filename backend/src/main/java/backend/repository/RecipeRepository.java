package backend.repository;


import backend.model.Recipe;

import org.springframework.stereotype.Repository;



import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

}
