package com.recipeapp.recipemanager.controller;

import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.javapoet.ClassName;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController{
    private static final Logger logger = LoggerFactory.getLogger(ClassName.class);

    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserService userService, UserRepository userRepository){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO, Principal principal){
        logger.info(" PUT Request to update user: {}", userId);

        String loggedInEmail = principal.getName();
        logger.info("Logged-in user: {}", loggedInEmail);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!loggedInEmail.equals(existingUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile.");
        }

        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword){
        List<UserDTO> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, Principal principal) {

        String loggedInEmail = principal.getName();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!loggedInEmail.equals(existingUser.getEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own profile.");
        }

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

