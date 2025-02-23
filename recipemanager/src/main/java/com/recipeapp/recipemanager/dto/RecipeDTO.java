package com.recipeapp.recipemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDTO {
    private Long recipeId;
    private String title;
    private String description;
    private String ingredients;
    private String instructions;
    private UserDTO user; // full User object
    private Long categoryId; //Only returning categoryId
}
