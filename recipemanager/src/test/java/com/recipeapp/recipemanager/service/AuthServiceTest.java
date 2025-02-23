package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.security.JwtTokenProvider;
import com.recipeapp.recipemanager.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success(){
        UserDTO userDTO = new UserDTO(null, "Mark", "Baz", "mark@mail.com", "mypassword");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("hashedPassword");

        String result = authService.register(userDTO);

        assertEquals("User registered successfully!", result);
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void testRegister_emailAlreadyExists(){
        UserDTO userDTO = new UserDTO(null, "Mark", "Baz", "mark@mail.com", "mypassword");

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> authService.register(userDTO));
        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void testLogin_Success(){
        User user = new User(1L, "Mark", "Baz", "mark@mail.com", "hashedpassword");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("securepassword", user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn("mocked-jwt-token");

        String token = authService.login(user.getEmail(), "securepassword");

        assertNotNull(token);
        assertEquals("mocked-jwt-token", token);
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userRepository.findByEmail("wrong@mail.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login("wrong@mail.com", "wrongpassword"));
        assertEquals("Invalid email or password", exception.getMessage());
    }
}
