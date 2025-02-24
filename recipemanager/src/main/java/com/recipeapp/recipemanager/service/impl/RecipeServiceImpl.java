package com.recipeapp.recipemanager.service.impl;

import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.Category;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.CategoryRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService{

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public RecipeDTO createRecipe(RecipeDTO recipeDTO) {
        User user = userRepository.findById(recipeDTO.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + recipeDTO.getUser().getUserId()));
        Category category = categoryRepository.findById(recipeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + recipeDTO.getCategoryId()));
        Recipe recipe = new Recipe();
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setUser(user);
        recipe.setCategory(category);

        Recipe savedRecipe = recipeRepository.save(recipe);
        return mapToDTO(savedRecipe);
    }

    @Override
    public RecipeDTO getRecipeById(Long recipeId){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));
        return mapToDTO(recipe);
    }

    @Override
    public List<RecipeDTO> getAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();
        return recipes.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public RecipeDTO updateRecipeIfOwner(Long recipeId, RecipeDTO recipeDTO, String loggedInEmail){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "You can only edit your own recipes"));

        if (!recipe.getUser().getEmail().equals(loggedInEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own recipes");
        }

        // perform update
        recipe.setTitle(recipeDTO.getTitle());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setIngredients(recipeDTO.getIngredients());
        recipe.setInstructions(recipeDTO.getInstructions());

        Recipe updatedRecipe = recipeRepository.save(recipe);
        return mapToDTO(updatedRecipe);
    }

    @Override
    public List<RecipeDTO> getRecipesByCategory(Long categoryId){
        List<Recipe> recipes = recipeRepository.findByCategoryCategoryId(categoryId);
        return recipes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> getRecipesByUser(Long userId){
        List<Recipe> recipes = recipeRepository.findByUserUserId(userId);
        return recipes.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecipeDTO> searchRecipes(String keyword) {
        List<Recipe> recipes = recipeRepository.searchRecipes(keyword);

        return recipes.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteRecipeIfOwner(Long recipeId, String loggedInEmail){
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        if (!recipe.getUser().getEmail().equals(loggedInEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own recipes");
        }

        recipeRepository.delete(recipe);
    }

    private RecipeDTO mapToDTO(Recipe recipe){
        return new RecipeDTO(
                recipe.getRecipeId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getIngredients(),
                recipe.getInstructions(),
                new UserDTO(
                        recipe.getUser().getUserId(),
                        recipe.getUser().getFirstname(),
                        recipe.getUser().getLastname(),
                        recipe.getUser().getEmail()
                ),
                recipe.getCategory().getCategoryId());
    }

}
