package com.recipeapp.recipemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.recipemanager.dto.FavoriteDTO;
import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.service.FavoriteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FavoriteControllerTest {
    private MockMvc mockMvc;

    @Mock
    private FavoriteService favoriteService;

    @InjectMocks
    private FavoriteController favoriteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(favoriteController).build();
    }

    @Test
    void testAddFavorite_Success() throws Exception {
        FavoriteDTO favoriteDTO = new FavoriteDTO(null, 1L, 1L);

        when(favoriteService.addFavorite(any(FavoriteDTO.class))).thenReturn(favoriteDTO);

        mockMvc.perform(post("/api/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(favoriteDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetFavoritesByUserId_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        List<RecipeDTO> recipes = Arrays.asList(new RecipeDTO(1L, "Pasta", "Delicious", "Ingredients", "Instructions", mockUserDTO, 1L));

        when(favoriteService.getFavoritesByUserId(1L)).thenReturn(recipes);

        mockMvc.perform(get("/api/favorites/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Pasta"));
    }

    @Test
    void testRemoveFavorite_Success() throws Exception {
        doNothing().when(favoriteService).removeFavorite(1L, 1L);

        mockMvc.perform(delete("/api/favorites/user/1/recipe/1"))
                .andExpect(status().isNoContent());
    }
}
