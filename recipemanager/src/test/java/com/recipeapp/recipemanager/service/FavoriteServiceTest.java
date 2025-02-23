package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.FavoriteDTO;
import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.model.Category;
import com.recipeapp.recipemanager.model.Favorite;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.FavoriteRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FavoriteServiceTest {
    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User mockUser;
    private Recipe mockRecipe;
    private Favorite mockFavorite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User(1L, "John", "Doe", "john@example.com", "hashedPassword");

        Category mockCategory = new Category(1L, "Italian"); //create a category to assign on a recipe

        mockRecipe = new Recipe(1L, "Spaghetti", "Classic Italian pasta", "Ingredients", "Instructions", mockUser, mockCategory);
        mockFavorite = new Favorite(1L, mockUser, mockRecipe);
    }

    @Test
    void testAddFavorite_Success() {
        FavoriteDTO favoriteDTO = new FavoriteDTO(null, 1L, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(mockFavorite);

        FavoriteDTO createdFavorite = favoriteService.addFavorite(favoriteDTO);

        assertNotNull(createdFavorite);
        assertEquals(1L, createdFavorite.getUserId());
    }

    @Test
    void testGetFavoritesByUserId_Success() {
        when(favoriteRepository.findByUserUserId(1L)).thenReturn(Arrays.asList(mockFavorite));

        List<RecipeDTO> result = favoriteService.getFavoritesByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRecipeId());
    }

    @Test
    void testRemoveFavorite_Success() {
        List<Favorite> mockFavorites = List.of(mockFavorite);

        when(favoriteRepository.findByUserUserIdAndRecipeRecipeId(1L, 1L)).thenReturn(mockFavorites);
        doNothing().when(favoriteRepository).delete(mockFavorite);

        assertDoesNotThrow(() -> favoriteService.removeFavorite(1L, 1L));
        verify(favoriteRepository, times(1)).delete(mockFavorite);
    }
}
