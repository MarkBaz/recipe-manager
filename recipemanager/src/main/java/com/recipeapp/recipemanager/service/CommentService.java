package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.CommentDTO;
import com.recipeapp.recipemanager.model.Comment;
import java.util.List;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    List<CommentDTO> getCommentsByRecipeId(Long recipeId);
    void deleteComment(Long commentId);
}
