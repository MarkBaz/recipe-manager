package com.recipeapp.recipemanager.controller;

import com.recipeapp.recipemanager.dto.RatingDTO;
import com.recipeapp.recipemanager.model.Rating;
import com.recipeapp.recipemanager.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingDTO> addOrUpdateRating(@RequestBody RatingDTO ratingDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingService.addOrUpdateRating(ratingDTO));
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<RatingDTO>> getRatingsByRecipeId(@PathVariable Long recipeId) {
        return ResponseEntity.ok(ratingService.getRatingsByRecipeId(recipeId));
    }

    @GetMapping("/user/{userId}/recipe/{recipeId}")
    public ResponseEntity<RatingDTO> getUserRatingForRecipe(@PathVariable Long userId, @PathVariable Long recipeId) {
        RatingDTO ratingDTO = ratingService.getUserRatingForRecipe(userId, recipeId);
        return ratingDTO != null ? ResponseEntity.ok(ratingDTO) : ResponseEntity.noContent().build();
    }


    @GetMapping("/recipe/{recipeId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long recipeId){
        return ResponseEntity.ok(ratingService.getAverageRating(recipeId));
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.noContent().build();
    }
}
