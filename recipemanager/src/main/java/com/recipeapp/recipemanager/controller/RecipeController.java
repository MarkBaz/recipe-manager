package com.recipeapp.recipemanager.controller;

import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.createRecipe(recipeDTO));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long recipeId) {

        RecipeDTO recipeDTO = recipeService.getRecipeById(recipeId);

        if (recipeDTO == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        return ResponseEntity.ok(recipeDTO);
    }

    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(recipeService.getRecipesByCategory(categoryId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByUser(@PathVariable Long userId){
        return ResponseEntity.ok(recipeService.getRecipesByUser(userId));
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<RecipeDTO> updateRecipeIfOwner(@PathVariable Long recipeId, @RequestBody RecipeDTO recipeDTO, Principal principal){
        String loggedInEmail = principal.getName();
        RecipeDTO updatedRecipe = recipeService.updateRecipeIfOwner(recipeId, recipeDTO, loggedInEmail);
        return ResponseEntity.ok(updatedRecipe);
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeDTO>> searchRecipes(@RequestParam String keyword){
        List<RecipeDTO> recipes = recipeService.searchRecipes(keyword);
        return ResponseEntity.ok(recipes);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Map<String, String>> deleteRecipeIfOwner(@PathVariable Long recipeId, Principal principal) {
        String loggedInEmail = principal.getName();

        recipeService.deleteRecipeIfOwner(recipeId, loggedInEmail);

        return ResponseEntity.noContent().build();
    }
}
