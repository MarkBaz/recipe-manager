package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.RatingDTO;
import com.recipeapp.recipemanager.model.Rating;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.RatingRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private User mockUser;
    private Recipe mockRecipe;
    private Rating mockRating;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User(1L, "John", "Doe", "john@example.com", "hashedPassword");
        mockRecipe = new Recipe(1L, "Spaghetti", "Classic Italian pasta", "Ingredients", "Instructions", mockUser, null);
        mockRating = new Rating(1L, 5, mockUser, mockRecipe);
    }

    @Test
    void testAddOrUpdateRating_CreatesNewRating_WhenNoneExists() {
        RatingDTO ratingDTO = new RatingDTO(null, 5, 1L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));
        when(ratingRepository.findByUserUserIdAndRecipeRecipeId(1L, 1L)).thenReturn(Collections.emptyList()); // No existing rating
        when(ratingRepository.save(any(Rating.class))).thenReturn(mockRating);

        RatingDTO createdRating = ratingService.addOrUpdateRating(ratingDTO);

        assertNotNull(createdRating);
        assertEquals(5, createdRating.getStars());
        verify(ratingRepository, times(1)).save(any(Rating.class)); // Ensuring rating is saved once
    }

    @Test
    void testAddOrUpdateRating_UpdatesExistingRating() {
        RatingDTO ratingDTO = new RatingDTO(null, 4, 1L, 1L);

        Rating existingRating = new Rating();
        existingRating.setRatingId(1L);
        existingRating.setStars(3); // Previous rating
        existingRating.setUser(mockUser);
        existingRating.setRecipe(mockRecipe);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));
        when(ratingRepository.findByUserUserIdAndRecipeRecipeId(1L, 1L)).thenReturn(Collections.singletonList(existingRating)); // Existing rating
        when(ratingRepository.save(any(Rating.class))).thenReturn(existingRating);

        RatingDTO updatedRating = ratingService.addOrUpdateRating(ratingDTO);

        assertNotNull(updatedRating);
        assertEquals(4, updatedRating.getStars()); // Ensure rating is updated
        verify(ratingRepository, times(1)).save(any(Rating.class)); // Ensure save is called once
    }

    @Test
    void testGetRatingsByRecipeId_Success() {
        when(ratingRepository.findByRecipeRecipeId(1L)).thenReturn(Arrays.asList(mockRating));

        List<RatingDTO> result = ratingService.getRatingsByRecipeId(1L);

        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getStars());
    }

    @Test
    void testGetAverageRating_Success() {
        when(ratingRepository.findByRecipeRecipeId(1L)).thenReturn(Arrays.asList(
                new Rating(1L, 5, mockUser, mockRecipe),
                new Rating(2L, 4, mockUser, mockRecipe)
        ));

        double avgRating = ratingService.getAverageRating(1L);

        assertEquals(4.5, avgRating);
    }

    @Test
    void testDeleteRating_Success() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(mockRating));
        doNothing().when(ratingRepository).delete(mockRating);

        assertDoesNotThrow(() -> ratingService.deleteRating(1L));
        verify(ratingRepository, times(1)).delete(mockRating);
    }
}
