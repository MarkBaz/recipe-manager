package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.FavoriteDTO;
import com.recipeapp.recipemanager.dto.RecipeDTO;

import java.util.List;

public interface FavoriteService {
    FavoriteDTO addFavorite(FavoriteDTO favoriteDTO);
    List<RecipeDTO> getFavoritesByUserId(Long userId);
    void removeFavorite(Long userId, Long recipeId);
}
