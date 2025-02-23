package com.recipeapp.recipemanager.service.impl;

import com.recipeapp.recipemanager.dto.FavoriteDTO;
import com.recipeapp.recipemanager.dto.RecipeDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.Favorite;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.FavoriteRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService{
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public FavoriteDTO addFavorite(FavoriteDTO favoriteDTO) {
        User user = userRepository.findById(favoriteDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Recipe recipe = recipeRepository.findById(favoriteDTO.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setRecipe(recipe);

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return new FavoriteDTO(savedFavorite.getFavoriteId(), savedFavorite.getUser().getUserId(), savedFavorite.getRecipe().getRecipeId());
    }

    @Override
    public List<RecipeDTO> getFavoritesByUserId(Long userId) {
        return favoriteRepository.findByUserUserId(userId).stream()
                .map(favorite -> {
                        Recipe recipe = favorite.getRecipe();
                        User recipeUser = recipe.getUser();
                        UserDTO recipeUserDTO = new UserDTO(
                                recipeUser.getUserId(),
                                recipeUser.getFirstname(),
                                recipeUser.getLastname(),
                                recipeUser.getEmail()
                                );

                        return new RecipeDTO(
                                recipe.getRecipeId(),
                                recipe.getTitle(),
                                recipe.getDescription(),
                                recipe.getIngredients(),
                                recipe.getInstructions(),
                                recipeUserDTO,
                                recipe.getCategory() != null ? recipe.getCategory().getCategoryId() : null // handle null category
                        );
                })
                .collect(Collectors.toList());

    }

    @Override
    public void removeFavorite(Long userId, Long recipeId){
        List<Favorite> favorites = favoriteRepository.findByUserUserIdAndRecipeRecipeId(userId, recipeId);

        if (favorites.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found");
        }

        // Assuming there's only one favorite per user per recipe
        favoriteRepository.delete(favorites.getFirst());
    }
}
