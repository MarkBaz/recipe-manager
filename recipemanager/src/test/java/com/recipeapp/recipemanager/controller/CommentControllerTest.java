package com.recipeapp.recipemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.recipemanager.dto.CommentDTO;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.service.CommentService;
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

public class CommentControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void testCreateComment_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        CommentDTO commentDTO = new CommentDTO(null, "Great recipe!", mockUserDTO, 1L);

        when(commentService.createComment(any(CommentDTO.class))).thenReturn(commentDTO);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Great recipe!"));
    }

    @Test
    void testGetCommentsByRecipeId_Success() throws Exception {
        UserDTO mockUserDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com");

        List<CommentDTO> comments = Arrays.asList(new CommentDTO(1L, "Looks delicious!", mockUserDTO, 1L));

        when(commentService.getCommentsByRecipeId(1L)).thenReturn(comments);

        mockMvc.perform(get("/api/comments/recipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].content").value("Looks delicious!"));
    }

    @Test
    void testDeleteComment_Success() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
    }
}
