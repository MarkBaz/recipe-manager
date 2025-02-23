package com.recipeapp.recipemanager.controller;

import com.recipeapp.recipemanager.dto.FavoriteDTO;
import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.model.Favorite;
import com.recipeapp.recipemanager.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestBody FavoriteDTO favoriteDTO) {
        favoriteService.addFavorite(favoriteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/user/{userId}/recipe/{recipeId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long userId, @PathVariable Long recipeId){
        favoriteService.removeFavorite(userId, recipeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDTO>> getFavoritesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }
}
