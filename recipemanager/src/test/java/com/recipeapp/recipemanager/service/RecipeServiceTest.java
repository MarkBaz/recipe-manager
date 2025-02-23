package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.Category;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.CategoryRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.impl.RecipeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {
    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private User mockUser;
    private UserDTO mockUserDTO;
    private Category mockCategory;
    private Recipe mockRecipe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");
        mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com", "securePassword");
        mockCategory = new Category(1L, "Italian");
        mockRecipe = new Recipe(1L, "Spaghetti", "Classic Italian pasta", "Ingredients", "Instructions", mockUser, mockCategory);
    }

    @Test
    void testCreateRecipe_Success() {
        RecipeDTO recipeDTO = new RecipeDTO(null, "Pasta", "Delicious pasta", "Pasta, Sauce", "Cook and serve", mockUserDTO, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(mockRecipe);

        RecipeDTO createdRecipe = recipeService.createRecipe(recipeDTO);

        assertNotNull(createdRecipe);
        assertEquals("Spaghetti", createdRecipe.getTitle());
    }

    @Test
    void testGetRecipeById_Success() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));

        RecipeDTO result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getRecipeId());
    }

    @Test
    void testGetRecipeById_NotFound() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> recipeService.getRecipeById(99L));
        assertEquals("Recipe not found with id: 99", exception.getMessage());
    }

    @Test
    void testGetAllRecipes_Success() {
        List<Recipe> recipes = Arrays.asList(mockRecipe);
        when(recipeRepository.findAll()).thenReturn(recipes);

        List<RecipeDTO> result = recipeService.getAllRecipes();

        assertEquals(1, result.size());
        assertEquals("Spaghetti", result.get(0).getTitle());
    }

    @Test
    void testUpdateRecipe_Success() {
        Recipe recipe = new Recipe(1L, "Spaghetti", "Description","Ingredients", "Instructions", mockUser, mockCategory);
        RecipeDTO updatedRecipeDTO = new RecipeDTO(1L, "Updated Spaghetti", "New description", "New ingredients", "New instructions", mockUserDTO, 1L);


        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

        RecipeDTO updatedRecipe = recipeService.updateRecipeIfOwner(1L, updatedRecipeDTO, mockUser.getEmail());

        assertNotNull(updatedRecipe);
        assertEquals("Updated Spaghetti", updatedRecipe.getTitle());
    }

    @Test
    void testUpdateRecipe_Unauthorized() {
        Recipe recipe = new Recipe(1L, "Pasta", "Yummy pasta", "Ingredients", "Instructions", mockUser, mockCategory);
        RecipeDTO updatedRecipeDTO = new RecipeDTO(1L, "Updated Pasta", "New Description", "New Ingredients", "New Instructions", mockUserDTO, 1L);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        assertThrows(ResponseStatusException.class, () -> {
            recipeService.updateRecipeIfOwner(1L, updatedRecipeDTO, "unauthorizedUser@mail.com");
        });
    }

    @Test
    void testGetRecipesByCategory_Success() {
        when(recipeRepository.findByCategoryCategoryId(1L)).thenReturn(Arrays.asList(mockRecipe));

        List<RecipeDTO> result = recipeService.getRecipesByCategory(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetRecipesByUser_Success() {
        when(recipeRepository.findByUserUserId(1L)).thenReturn(Arrays.asList(mockRecipe));

        List<RecipeDTO> result = recipeService.getRecipesByUser(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testSearchRecipes_Success() {
        String keyword = "Pasta";

        List<Recipe> recipes = Arrays.asList(
                new Recipe(1L, "Pasta Primavera", "Fresh pasta with vegetables", "Ingredients", "Instructions", mockUser, mockCategory),
                new Recipe(2L, "Pasta Carbonara", "Classic pasta dish with eggs and pancetta", "Ingredients", "Instructions", mockUser, mockCategory)
        );

        when(recipeRepository.searchRecipes(keyword)).thenReturn(recipes);

        List<RecipeDTO> result = recipeService.searchRecipes(keyword);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getTitle().contains("Pasta"));
    }


    @Test
    void testDeleteRecipe_Success() {
        String loggedInEmail = "user@mail.com";

        // Create a real User and set the expected email
        User mockUser = new User();
        mockUser.setEmail(loggedInEmail);

        // Create a mock Recipe and ensure it has the correct user
        Recipe recipe = new Recipe(1L, "Pasta", "Yummy pasta", "Ingredients", "Instructions", mockUser, mockCategory);

        // Mock repository to return this recipe
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        doNothing().when(recipeRepository).delete(recipe);

        assertDoesNotThrow(() -> recipeService.deleteRecipeIfOwner(1L, loggedInEmail));

        verify(recipeRepository, times(1)).delete(recipe);
    }



    @Test
    void testDeleteRecipe_Unauthorized() {
        String loggedInEmail = "user@example.com";
        String differentUserEmail = "wronguser@mail.com";

        // Create a user who owns the recipe
        User recipeOwner = new User();
        recipeOwner.setEmail(loggedInEmail);

        // Create a recipe owned by the correct user
        Recipe recipe = new Recipe(1L, "Pasta", "Yummy pasta", "Ingredients", "Instructions", recipeOwner, mockCategory);

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        // Attempt to delete with a different user's email
        assertThrows(ResponseStatusException.class, () -> {
            recipeService.deleteRecipeIfOwner(1L, differentUserEmail);
        });

        // Ensure that delete is never called since the user is unauthorized
        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

}
