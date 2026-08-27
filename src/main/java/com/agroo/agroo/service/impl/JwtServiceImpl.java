package com.agroo.agroo.service.impl;

import com.agroo.agroo.service.JwtService;
import com.agroo.agroo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtUtil jwtUtil;

    @Override
    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }

    @Override
    public String generateToken(String username, String email, String role) {
        return jwtUtil.generateToken(username, email, role);
    }

    @Override
    public String generateRefreshToken(String username) {
        return jwtUtil.generateRefreshToken(username);
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        return jwtUtil.validateToken(token, userDetails);
    }

    @Override
    public Boolean isTokenValid(String token) {
        try {
            return !jwtUtil.extractExpiration(token).before(new java.util.Date());
        } catch (Exception e) {
            return false;
        }
    }
}