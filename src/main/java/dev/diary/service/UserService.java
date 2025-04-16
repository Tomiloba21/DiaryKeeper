package dev.sec3.se3.service;

import dev.sec3.se3.dao.UserDAO;
import dev.sec3.se3.model.User;

import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public void registerUser(String username, String password, String email) throws Exception {
        String passwordHash = PasswordUtils.hashPassword(password);
        User user = new User(username, passwordHash, email);
        userDAO.save(user);
    }

    public Optional<User> authenticateUser(String username, String password) throws Exception {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
