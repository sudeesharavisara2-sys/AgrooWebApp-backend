package com.agroo.agroo.service.impl;

import com.agroo.agroo.dto.request.*;
import com.agroo.agroo.dto.response.ApiResponse;
import com.agroo.agroo.dto.response.AuthResponse;
import com.agroo.agroo.model.User;
import com.agroo.agroo.model.enums.OtpType;
import com.agroo.agroo.model.enums.Role;
import com.agroo.agroo.repository.UserRepository;
import com.agroo.agroo.service.AuthService;
import com.agroo.agroo.service.JwtService;
import com.agroo.agroo.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;

    @Override
    @Transactional
    public AuthResponse register(AuthRequest request) {
        // Validate username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Validate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDistrict(request.getDistrict());
        user.setAddress(request.getAddress());

        // Set role - REGISTERED_USER by default
        try {
            user.setRole(Role.valueOf(request.getRole()));
        } catch (Exception e) {
            user.setRole(Role.REGISTERED_USER);
        }

        // Set user type
        user.setUserType(request.getUserType() != null ? request.getUserType() : "BUYER");

        user.setIsVerified(false);
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setFailedAttempts(0);

        userRepository.save(user);

        // Generate and send OTP
        String otpCode = otpService.generateAndSendOtp(user.getEmail(), OtpType.REGISTRATION);

        return new AuthResponse(
                null,
                null,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getUserType(),
                false,
                "Registration successful. Please verify your email with OTP: " + otpCode,
                true
        );
    }

    @Override
    @Transactional
    public AuthResponse verifyOtp(OtpVerificationRequest request) {
        // Verify OTP
        Boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtpCode(), OtpType.REGISTRATION);

        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Update user verification status
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsVerified(true);
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new AuthResponse(
                token,
                refreshToken,
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getUserType(),
                true,
                "Email verified successfully",
                true
        );
    }

    @Override
    @Transactional
    public ApiResponse resendOtp(ResendOtpRequest request) {
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getIsVerified()) {
            throw new RuntimeException("Email already verified");
        }

        // Generate and send new OTP
        String otpCode = otpService.generateAndSendOtp(user.getEmail(), OtpType.REGISTRATION);

        return new ApiResponse(true, "OTP resent successfully. New OTP: " + otpCode);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .or(() -> userRepository.findByEmail(userDetails.getUsername()))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if user is verified
            if (!user.getIsVerified()) {
                throw new RuntimeException("Please verify your email before logging in");
            }

            // Check if user is active
            if (!user.getIsActive()) {
                throw new RuntimeException("Account is deactivated. Please contact admin.");
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            user.setFailedAttempts(0);
            userRepository.save(user);

            // Generate tokens
            String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getRole().name());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            return new AuthResponse(
                    token,
                    refreshToken,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getUserType(),
                    user.getIsVerified(),
                    "Login successful",
                    true
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public ApiResponse changePassword(ChangePasswordRequest request, String token) {
        // Extract username from token (remove "Bearer " prefix)
        String jwt = token.substring(7);
        String username = jwtService.extractUsername(jwt);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse(true, "Password changed successfully");
    }

    @Override
    @Transactional
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        // Check if user exists
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate and send OTP
        String otpCode = otpService.generateAndSendOtp(user.getEmail(), OtpType.FORGOT_PASSWORD);

        return new ApiResponse(true, "OTP sent to your email. OTP: " + otpCode);
    }

    @Override
    @Transactional
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        // Validate passwords match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Verify OTP
        Boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtpCode(), OtpType.FORGOT_PASSWORD);

        if (!isValid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Update password
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse(true, "Password reset successfully");
    }

    @Override
    public ApiResponse logout(String token) {
        // JWT is stateless, so we can't invalidate the token on the server
        // However, we can do the following:
        // 1. Clear the security context
        SecurityContextHolder.clearContext();

        // 2. Optionally: Add token to a blacklist (if you have a token blacklist service)
        // blacklistService.addToBlacklist(token);

        // 3. Client should discard the token on their side
        return new ApiResponse(true, "Logged out successfully. Please discard your token on client side.");
    }
}