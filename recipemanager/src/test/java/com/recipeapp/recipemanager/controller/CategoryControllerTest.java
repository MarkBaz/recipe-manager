package com.recipeapp.recipemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.recipemanager.dto.CategoryDTO;
import com.recipeapp.recipemanager.service.CategoryService;
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


public class CategoryControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void testCreateCategory_Success() throws Exception {
        CategoryDTO categoryDTO = new CategoryDTO(null, "Mexican");

        when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(new CategoryDTO(2L, "Mexican"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mexican"));
    }

    @Test
    void testGetAllCategories_Success() throws Exception {
        List<CategoryDTO> categories = Arrays.asList(
                new CategoryDTO(1L, "Italian"),
                new CategoryDTO(2L, "Mexican")
        );

        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Italian"))
                .andExpect(jsonPath("$[1].name").value("Mexican"));
    }
}
