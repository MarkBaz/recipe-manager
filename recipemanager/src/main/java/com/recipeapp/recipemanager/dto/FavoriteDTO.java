package com.recipeapp.recipemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Connects a user to a recipe through favorite(many to many)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteDTO {
    private Long favoriteId;
    private Long userId;
    private Long recipeId;
}
