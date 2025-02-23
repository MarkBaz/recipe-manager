package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.CategoryDTO;
import com.recipeapp.recipemanager.model.Category;
import com.recipeapp.recipemanager.repository.CategoryRepository;
import com.recipeapp.recipemanager.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category mockCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCategory = new Category(1L, "Italian");
    }

    @Test
    void testCreateCategory_Success() {
        CategoryDTO categoryDTO = new CategoryDTO(null, "Mexican");

        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(2L, "Mexican"));

        CategoryDTO createdCategory = categoryService.createCategory(categoryDTO);

        assertNotNull(createdCategory);
        assertEquals("Mexican", createdCategory.getName());
    }

    @Test
    void testGetAllCategories_Success() {
        List<Category> categories = Arrays.asList(mockCategory, new Category(2L, "Mexican"));
        when(categoryRepository.findAll()).thenReturn(categories);

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Italian", result.get(0).getName());
        assertEquals("Mexican", result.get(1).getName());
    }
}
