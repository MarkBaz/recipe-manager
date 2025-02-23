package com.recipeapp.recipemanager.service.impl;

import com.recipeapp.recipemanager.dto.CommentDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.Comment;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.CommentRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.CommentService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepository, RecipeRepository recipeRepository, UserRepository userRepository){
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentDTO createComment(CommentDTO commentDTO){
        Recipe recipe = recipeRepository.findById(commentDTO.getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        User user = userRepository.findById(commentDTO.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setRecipe(recipe);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);
        return new CommentDTO(
                savedComment.getCommentId(),
                savedComment.getContent(),
                new UserDTO(comment.getUser().getUserId(), comment.getUser().getFirstname(), comment.getUser().getLastname(), comment.getUser().getEmail()), // Full UserDTO
                savedComment.getRecipe().getRecipeId());
    }

    @Override
    public List<CommentDTO> getCommentsByRecipeId(Long recipeId){
        return commentRepository.findByRecipeRecipeId(recipeId).stream()
                .map(comment -> new CommentDTO(
                        comment.getCommentId(),
                        comment.getContent(),
                        new UserDTO(comment.getUser().getUserId(), comment.getUser().getFirstname(), comment.getUser().getLastname(), comment.getUser().getEmail()), // Full UserDTO
                        comment.getRecipe().getRecipeId()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId){
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        commentRepository.delete(comment);
    }

}
