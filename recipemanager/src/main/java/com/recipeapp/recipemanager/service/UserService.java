package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserById(Long userId);
    List<UserDTO> getAllUsers();
    UserDTO updateUser(Long userId, UserDTO userDTO);
    List<UserDTO> searchUsers(String keyword);
    void deleteUser(Long userId);
}
