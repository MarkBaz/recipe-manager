package com.recipeapp.recipemanager.repository;

import com.recipeapp.recipemanager.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRecipeRecipeId(Long recipeId);
    List<Rating> findByUserUserIdAndRecipeRecipeId(Long userId, Long recipeId);
}
