package com.recipeapp.recipemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingDTO {
    private Long ratingId;
    private int stars; // stars from 1 to 5
    private Long userId;
    private Long recipeId;
}
