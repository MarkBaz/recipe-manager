package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success(){
        UserDTO userDTO = new UserDTO(null, "Mark", "Baz", "mark@mail.com", "securepassword");
        User user = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");

        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals("Mark", createdUser.getFirstname());
        assertEquals("Baz", createdUser.getLastname());
    }

    @Test
    void testGetUserById_Success(){
        User user = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.getUserById(1L);

        assertNotNull(userDTO);
        assertEquals(1L, userDTO.getUserId());
        assertEquals("Mark", userDTO.getFirstname());
    }

    @Test
    void testGetUserById_NotFound(){
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
        assertEquals("User not found with id99", exception.getMessage());
    }

    @Test
    void testGetAllUsers_Success(){
        List<User> users = Arrays.asList(
                new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword"),
                new User(2L, "Tilemachos", "Bazakas", "tile@mail.com", "hashedPassword")
        );

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("Mark", result.get(0).getFirstname());
    }

    @Test
    void testUpdateUser_Success(){
        User existingUser = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");
        UserDTO updateData = new UserDTO(1L, "Markos", "Baz", "mark@mail.com", "newPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updateData.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        UserDTO updateUser = userService.updateUser(1L,  updateData);

        assertNotNull(updateUser);
        assertEquals("Markos", updateUser.getFirstname());
    }

    @Test
    void testSearchUsers_Success() {
        String keyword = "Mark";

        List<User> users = Arrays.asList(
                new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword"),
                new User(2L, "Markus", "Taz", "markus@mail.com", "hashedPassword")
        );

        when(userRepository.searchUsers(keyword)).thenReturn(users);

        List<UserDTO> result = userService.searchUsers(keyword);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Mark", result.get(0).getFirstname());
    }


    @Test
    void testDeleteUser_Success(){
        User user = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedPassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(any(User.class));

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository,  times(1)).delete(any(User.class));
    }
}
