package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.AuthRequest;
import com.agroo.agroo.dto.response.AuthResponse;
import com.agroo.agroo.model.User;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(AuthRequest request) {
        // Check if user exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return new AuthResponse(null, null, null, null, "Username already exists", false);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(null, null, null, null, "Email already exists", false);
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDistrict(request.getDistrict());
        user.setRole(request.getRole() != null ? request.getRole() : "ROLE_USER");

        userRepository.save(user);

        return new AuthResponse(
                null,
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                "Registration successful",
                true
        );
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // TODO: Implement JWT authentication
        return new AuthResponse(
                "temp_token",
                request.getUsername(),
                null,
                null,
                "Login successful (JWT coming soon)",
                true
        );
    }
}