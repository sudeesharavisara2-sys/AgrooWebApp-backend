package com.agroo.agroo.controller;

import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    public Map<String, Object> getProfile(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("fullName", user.getFullName());
        response.put("phoneNumber", user.getPhoneNumber());
        response.put("address", user.getAddress());
        response.put("district", user.getDistrict());
        response.put("userType", user.getUserType());
        response.put("role", user.getRole().name());
        response.put("isVerified", user.getIsVerified());
        response.put("profileImageUrl", user.getProfileImageUrl());
        response.put("bio", user.getBio());
        return response;
    }
}