package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.CommentDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.Comment;
import com.recipeapp.recipemanager.model.Recipe;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.CommentRepository;
import com.recipeapp.recipemanager.repository.RecipeRepository;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User mockUser;
    private UserDTO mockUserDTO;
    private Recipe mockRecipe;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");
        mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com", "securePassword");
        mockRecipe = new Recipe(1L, "Spaghetti", "Classic Italian pasta", "Ingredients", "Instructions", mockUser, null);
        mockComment = new Comment(1L, "This is a great recipe!", mockUser, mockRecipe);
    }

    @Test
    void testCreateComment_Success() {
        CommentDTO commentDTO = new CommentDTO(null, "Great recipe!", mockUserDTO, 1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(mockRecipe));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentDTO createdComment = commentService.createComment(commentDTO);

        assertNotNull(createdComment);
        assertEquals("This is a great recipe!", createdComment.getContent());
    }

    @Test
    void testGetCommentsByRecipeId_Success() {
        when(commentRepository.findByRecipeRecipeId(1L)).thenReturn(Arrays.asList(mockComment));

        List<CommentDTO> result = commentService.getCommentsByRecipeId(1L);

        assertEquals(1, result.size());
        assertEquals("This is a great recipe!", result.get(0).getContent());
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(mockComment));
        doNothing().when(commentRepository).delete(mockComment);

        assertDoesNotThrow(() -> commentService.deleteComment(1L));
        verify(commentRepository, times(1)).delete(mockComment);
    }
}
