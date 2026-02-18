package com.thbs.user.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thbs.user.Exceptions.UserNotFoundException;
import com.thbs.user.Repository.UserRepository;
import com.thbs.user.entity.User;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        // Always store BCrypt hash
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public User getUserById(Long id) {
        return userRepo.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found with the id: " + id));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);

        existingUser.setUserName(user.getUserName());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepo.save(existingUser);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepo.delete(user);
    }
}
