package com.recipeapp.recipemanager.service.impl;

import com.recipeapp.recipemanager.dto.RatingDTO;
import com.recipeapp.recipemanager.model.Rating;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.RatingRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.RatingService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RatingServiceImpl(RatingRepository ratingRepository, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RatingDTO addOrUpdateRating(RatingDTO ratingDTO){
        Recipe recipe = recipeRepository.findById(ratingDTO.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        User user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // get existing ratings by this user for this recipe
        List<Rating> existingRatings = ratingRepository.findByUserUserIdAndRecipeRecipeId(user.getUserId(), recipe.getRecipeId());

        Rating rating;
        if (!existingRatings.isEmpty()) {
            // Update existing rating (assuming one rating per user per recipe)
            rating = existingRatings.getFirst();
            rating.setStars(ratingDTO.getStars());
        } else {
            // Create new rating
            rating = new Rating();
            rating.setStars(ratingDTO.getStars());
            rating.setRecipe(recipe);
            rating.setUser(user);
        }

        Rating savedRating = ratingRepository.save(rating);

        return new RatingDTO(
                savedRating.getRatingId(),
                savedRating.getStars(),
                savedRating.getUser().getUserId(),
                savedRating.getRecipe().getRecipeId());
    }

    @Override
    public List<RatingDTO> getRatingsByRecipeId(Long recipeId){
        return ratingRepository.findByRecipeRecipeId(recipeId).stream()
                .map(rating -> new RatingDTO(rating.getRatingId(), rating.getStars(), rating.getUser().getUserId(), rating.getRecipe().getRecipeId()))
                .collect(Collectors.toList());
    }

    @Override
    public RatingDTO getUserRatingForRecipe(Long userId, Long recipeId) {
        List<Rating> ratings = ratingRepository.findByUserUserIdAndRecipeRecipeId(userId, recipeId);

        if (ratings.isEmpty()) {
            return null; // No rating found
        }

        Rating rating = ratings.getFirst(); // Get the first rating (since a user can only have one)
        return new RatingDTO(rating.getRatingId(), rating.getStars(), rating.getUser().getUserId(), rating.getRecipe().getRecipeId());
    }


    @Override
    public double getAverageRating(Long recipeId){
        List<Rating> ratings = ratingRepository.findByRecipeRecipeId(recipeId);
        if(ratings.isEmpty()){
            return 0.0;
        }
        double total = ratings.stream().mapToInt(Rating::getStars).sum();
        return total / ratings.size();
    }

    @Override
    public void deleteRating(Long ratingId){
        Rating rating = ratingRepository.findById(ratingId)
                        .orElseThrow(() -> new RuntimeException("Rating not found with id: " + ratingId));

        ratingRepository.delete(rating);
    }

}
