package com.recipeapp.recipemanager.repository;

import com.recipeapp.recipemanager.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserUserId(Long userId);
    List<Favorite> findByRecipeRecipeId(Long recipeId);
    List<Favorite> findByUserUserIdAndRecipeRecipeId(Long userId, Long recipeId);

}
