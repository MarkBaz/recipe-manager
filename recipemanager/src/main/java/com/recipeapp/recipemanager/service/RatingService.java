package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.RatingDTO;
import java.util.List;

public interface RatingService {
    RatingDTO addOrUpdateRating(RatingDTO ratingDTO);
    List<RatingDTO> getRatingsByRecipeId(Long recipeId);
    RatingDTO getUserRatingForRecipe(Long userId, Long recipeId);
    double getAverageRating(Long recipeId);
    void deleteRating(Long ratingId);
}
