package com.agroo.agroo.controller;

import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", userRepository.count());
        response.put("message", "Welcome to Admin Dashboard");
        response.put("status", "Admin access granted");
        return response;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/users/{id}/activate")
    public Map<String, String> activateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User activated successfully");
        response.put("userId", id.toString());
        return response;
    }

    @PutMapping("/users/{id}/deactivate")
    public Map<String, String> deactivateUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deactivated successfully");
        response.put("userId", id.toString());
        return response;
    }
}