package com.recipeapp.recipemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.recipemanager.dto.RatingDTO;
import com.recipeapp.recipemanager.service.RatingService;
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

public class RatingControllerTest {
    private MockMvc mockMvc;

    @Mock
    private RatingService ratingService;

    @InjectMocks
    private RatingController ratingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();
    }

    @Test
    void testAddOrUpdateRating_CreatesNewRating() throws Exception {
        RatingDTO ratingDTO = new RatingDTO(null, 5, 1L, 1L);

        when(ratingService.addOrUpdateRating(any(RatingDTO.class))).thenReturn(ratingDTO);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(ratingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stars").value(5));
    }

    @Test
    void testAddOrUpdateRating_UpdatesExistingRating() throws Exception {
        RatingDTO ratingDTO = new RatingDTO(1L, 4, 1L, 1L); // Existing rating ID present

        when(ratingService.addOrUpdateRating(any(RatingDTO.class))).thenReturn(ratingDTO);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(ratingDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stars").value(4)); // Ensure it's updated
    }

    @Test
    void testGetRatingsByRecipeId_Success() throws Exception {
        List<RatingDTO> ratings = Arrays.asList(new RatingDTO(1L, 5, 1L, 1L));

        when(ratingService.getRatingsByRecipeId(1L)).thenReturn(ratings);

        mockMvc.perform(get("/api/ratings/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].stars").value(5));
    }

    @Test
    void testGetAverageRating_Success() throws Exception {
        when(ratingService.getAverageRating(1L)).thenReturn(4.5);

        mockMvc.perform(get("/api/ratings/recipe/1/average"))
                .andExpect(status().isOk())
                .andExpect(content().string("4.5"));
    }

    @Test
    void testDeleteRating_Success() throws Exception {
        doNothing().when(ratingService).deleteRating(1L);

        mockMvc.perform(delete("/api/ratings/1"))
                .andExpect(status().isNoContent());
    }
}
