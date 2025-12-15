package com.ethiopianbank.services;

import com.ethiopianbank.dao.UserDAO;
import com.ethiopianbank.models.User;

public class AuthService {
    private UserDAO userDAO;
    
    public AuthService() {
        this.userDAO = new UserDAO();
    }
    
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        
        return userDAO.authenticate(username, password);
    }
    
    public boolean createUser(User user) {
        validateUser(user);
        return userDAO.createUser(user);
    }
    
    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
    }
}