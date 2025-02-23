package com.recipeapp.recipemanager.service;

import com.recipeapp.recipemanager.dto.UserDTO;

public interface AuthService {
    String register(UserDTO userDTO);
    String login(String email, String password);
}
