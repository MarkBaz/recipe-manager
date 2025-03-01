package com.recipeapp.recipemanager.service.impl;

import com.recipeapp.recipemanager.dto.UserDTO;
import com.recipeapp.recipemanager.model.User;
import com.recipeapp.recipemanager.repository.UserRepository;
import com.recipeapp.recipemanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.javapoet.ClassName;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(ClassName.class);

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO getUserById(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id" + userId));
        return mapToDTO(user, true);
    }

    @Override
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream().map(user -> mapToDTO(user, true)).collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO userDTO){
        logger.info("Received update request for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        logger.info("Found user: {} with email: {}", user.getUserId(), user.getEmail());

        user.setFirstname(userDTO.getFirstname());
        user.setLastname(userDTO.getLastname());
        userDTO.setEmail(user.getEmail());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToDTO(updatedUser, true);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    @Override
    public List<UserDTO> searchUsers(String keyword){
        List<User> users = userRepository.searchUsers(keyword);

        return users.stream().map(user -> mapToDTO(user, false)).collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user, boolean includeSensitive) {
        if (includeSensitive) {
            return new UserDTO(user.getUserId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getPassword());
        } else {
            return new UserDTO(user.getUserId(), user.getFirstname(), user.getLastname(), user.getEmail(), null); // Hide password
        }
    }


}
