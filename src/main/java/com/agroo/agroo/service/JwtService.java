package com.agroo.agroo.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String extractUsername(String token);
    String generateToken(String username, String email, String role);
    String generateRefreshToken(String username);
    Boolean validateToken(String token, UserDetails userDetails);
    Boolean isTokenValid(String token);
}