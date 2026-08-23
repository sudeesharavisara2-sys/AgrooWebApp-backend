package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.AuthRequest;
import com.agroo.agroo.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse login(AuthRequest request);
}