package main.java.services;

import main.java.models.User;
import main.java.utils.FileHandler;
import main.java.utils.Validation;
import java.util.HashMap;
import java.util.Map;


/**
 * Handles user authentication and authorization
 */

public class UserManager {
    private Map<String, User> users;
    private User currentUser;
    private static final String USERS_FILE = "data/users.dat";

    public UserManager() {
        this.users = FileHandler.loadUsers(USERS_FILE);
        if (users == null) {
            users = new HashMap<>();
            // Create default admin if no users exist
            register("admin", "admin123", "admin");
        }
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && Validation.verifyPassword(password, user.getPasswordHash())) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean register(String username, String password, String role) {
        if (users.containsKey(username)) return false;

        users.put(username, new User(username, Validation.hashPassword(password), role));
        FileHandler.saveUsers(USERS_FILE, users);
        return true;
    }

    // Add these getter methods
    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAdminLoggedIn() {
        return currentUser != null && "admin".equals(currentUser.getRole());
    }
}