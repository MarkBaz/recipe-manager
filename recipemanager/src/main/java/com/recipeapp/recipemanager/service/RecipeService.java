package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.model.Recipe;

import java.util.List;

public interface RecipeService {
    RecipeDTO createRecipe(RecipeDTO recipeDTO);
    RecipeDTO getRecipeById(Long recipeId);
    List<RecipeDTO> getAllRecipes();
    RecipeDTO updateRecipeIfOwner(Long recipeId, RecipeDTO recipeDTO, String loggedInEmail);
    List<RecipeDTO> getRecipesByCategory(Long categoryId);
    List<RecipeDTO> getRecipesByUser(Long userId);
    List<RecipeDTO> searchRecipes(String keyword);
    void deleteRecipeIfOwner(Long recipeId, String loggedInEmail);
}
