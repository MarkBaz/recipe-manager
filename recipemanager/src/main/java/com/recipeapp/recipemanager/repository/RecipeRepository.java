package com.recipeapp.recipemanager.repository;

import com.recipeapp.recipemanager.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByCategoryCategoryId(Long categoryId);
    List<Recipe> findByUserUserId(Long userId);

    @Query(value = """
    SELECT * FROM recipes r
    WHERE LOWER(r.title) LIKE LOWER(CONCAT(:keyword, '%'))
    OR LOWER(r.title) LIKE LOWER(CONCAT('% ', :keyword, '%'))
    """, nativeQuery = true)
    List<Recipe> searchRecipes(@Param("keyword") String keyword);

}
