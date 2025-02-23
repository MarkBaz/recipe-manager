package com.recipeapp.recipemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class RecipeControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();
    }

    @Test
    void testCreateRecipe_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        RecipeDTO recipeDTO = new RecipeDTO(null, "Pasta", "Delicious", "Pasta, Sauce", "Cook", mockUserDTO, 1L);

        when(recipeService.createRecipe(any(RecipeDTO.class))).thenReturn(recipeDTO);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(recipeDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetRecipeById_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        RecipeDTO recipeDTO = new RecipeDTO(1L, "Pasta", "Delicious", "Ingredients", "Instructions", mockUserDTO, 1L);

        when(recipeService.getRecipeById(1L)).thenReturn(recipeDTO);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Pasta"));
    }

    @Test
    void testGetAllRecipes_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        List<RecipeDTO> recipes = Arrays.asList(new RecipeDTO(1L, "Pasta", "Delicious", "Ingredients", "Instructions", mockUserDTO, 1L));

        when(recipeService.getAllRecipes()).thenReturn(recipes);

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGetRecipesByCategory_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        List<RecipeDTO> recipes = Arrays.asList(new RecipeDTO(1L, "Pizza", "Tasty", "Dough, Cheese", "Bake", mockUserDTO, 1L));

        when(recipeService.getRecipesByCategory(1L)).thenReturn(recipes);

        mockMvc.perform(get("/api/recipes/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGetRecipesByUser_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        List<RecipeDTO> recipes = Arrays.asList(new RecipeDTO(1L, "Salad", "Healthy", "Lettuce, Tomato", "Mix", mockUserDTO, 1L));

        when(recipeService.getRecipesByUser(1L)).thenReturn(recipes);

        mockMvc.perform(get("/api/recipes/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testUpdateRecipe_Success() throws Exception {
        Long recipeId = 1L;
        String loggedInEmail = "user@mail.com";
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        RecipeDTO updatedRecipeDTO = new RecipeDTO(recipeId, "Updated Pasta", "Better taste", "New Ingredients", "New Instructions", mockUserDTO, 1L);

        when(recipeService.updateRecipeIfOwner(eq(recipeId), any(RecipeDTO.class), eq(loggedInEmail))).thenReturn(updatedRecipeDTO);

        mockMvc.perform(put("/api/recipes/{recipeId}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedRecipeDTO))
                        .principal(() -> loggedInEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Pasta"));

        verify(recipeService, times(1)).updateRecipeIfOwner(eq(recipeId), any(RecipeDTO.class), eq(loggedInEmail));
    }

    @Test
    void testUpdateRecipe_Forbidden() throws Exception {
        Long recipeId = 1L;
        String loggedInEmail = "wronguser@mail.com"; // Wrong user
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        RecipeDTO updatedRecipeDTO = new RecipeDTO(recipeId, "Updated Title", "Updated Description", "Updated Ingredients", "Updated Instructions", mockUserDTO, 1L);

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own recipes"))
                .when(recipeService).updateRecipeIfOwner(eq(recipeId), any(RecipeDTO.class), eq(loggedInEmail));

        mockMvc.perform(put("/api/recipes/{recipeId}", recipeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedRecipeDTO))
                        .principal(() -> loggedInEmail))
                .andExpect(status().isForbidden());

        verify(recipeService, times(1)).updateRecipeIfOwner(eq(recipeId), any(RecipeDTO.class), eq(loggedInEmail));
    }

    @Test
    void testSearchRecipes_Endpoint() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        List<RecipeDTO> recipes = Arrays.asList(
                new RecipeDTO(1L, "Pasta Primavera", "Fresh pasta with vegetables", "Ingredients", "Instructions", mockUserDTO, 1L),
                new RecipeDTO(2L, "Pasta Carbonara", "Classic pasta dish with eggs and pancetta", "Ingredients", "Instructions", mockUserDTO, 1L)
        );

        when(recipeService.searchRecipes("Pasta")).thenReturn(recipes);

        mockMvc.perform(get("/api/recipes/search?keyword=Pasta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Pasta Primavera"));
    }



    @Test
    void testDeleteRecipe_Success() throws Exception {
        Long recipeId = 1L;
        String loggedInEmail = "user@mail.com";

        doNothing().when(recipeService).deleteRecipeIfOwner(recipeId, loggedInEmail);

        mockMvc.perform(delete("/api/recipes/{recipeId}", recipeId)
                        .principal(() -> loggedInEmail))
                .andExpect(status().isNoContent());

        verify(recipeService, times(1)).deleteRecipeIfOwner(recipeId, loggedInEmail);
    }

    @Test
    void testDeleteRecipe_Forbidden() throws Exception {
        Long recipeId = 1L;
        String loggedInEmail = "wronguser@mail.com"; // Wrong user

        doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own recipes"))
                .when(recipeService).deleteRecipeIfOwner(recipeId, loggedInEmail);

        mockMvc.perform(delete("/api/recipes/{recipeId}", recipeId)
                        .principal(() -> loggedInEmail))
                .andExpect(status().isForbidden());

        verify(recipeService, times(1)).deleteRecipeIfOwner(recipeId, loggedInEmail);
    }

}
