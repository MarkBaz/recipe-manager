package com.recipeapp.recipemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Mock
    private Principal mockPrincipal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        when(mockPrincipal.getName()).thenReturn("mark@mail.com");
    }

    @Test
    void testCreateUser_Success() throws Exception {
        UserDTO userDTO = new UserDTO(null, "Mark", "Baz", "mark@mail.com", "securepassword");

        when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Mark"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Mark", "Baz", "mark@mail.com", "securepassword");

        when(userService.getUserById(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Mark"));
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        List<UserDTO> users = Arrays.asList(
                new UserDTO(1L, "Mark", "Baz", "mark@mail.com", "securepassword"),
                new UserDTO(2L, "Tilemachos", "Bazakas", "tile@mail.com", "securepassword")
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        User existingUser = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");
        UserDTO userDTO = new UserDTO(1L, "Markos", "Baz", "mark@mail.com", "newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser)); // ensure user exists
        when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userDTO))
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Markos"));
    }

    @Test
    void testSearchUsers_Endpoint() throws Exception {
        List<UserDTO> users = Arrays.asList(
                new UserDTO(1L, "Mark", "Baz", "mark@mail.com", "securepassword"),
                new UserDTO(2L, "Markus", "Taz", "markus@mail.com", "securepassword")
        );

        when(userService.searchUsers("Mark")).thenReturn(users);

        mockMvc.perform(get("/api/users/search?keyword=Mark"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstname").value("Mark"));
    }


    @Test
    void testDeleteUser_Success() throws Exception {
        User existingUser = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        mockMvc.perform(delete("/api/users/1")
                        .principal(mockPrincipal))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_Forbidden() throws Exception {
        User existingUser = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(mockPrincipal.getName()).thenReturn("otheruser@mail.com");

        mockMvc.perform(delete("/api/users/1")
                        .principal(mockPrincipal))
                .andExpect(status().isForbidden());
    }
}
