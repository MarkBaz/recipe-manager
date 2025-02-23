package com.recipeapp.recipemanager.service.impl;

import com.recipeapp.recipemanager.dto.CategoryDTO;
import com.recipeapp.recipemanager.model.Category;
import com.recipeapp.recipemanager.repository.CategoryRepository;
import com.recipeapp.recipemanager.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());

        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory.getCategoryId(), savedCategory.getName());
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDTO(category.getCategoryId(), category.getName()))
                .collect(Collectors.toList());
    }
}
