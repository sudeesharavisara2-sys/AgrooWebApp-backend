package com.agroo.agroo.service;

import com.agroo.agroo.dto.request.*;
import com.agroo.agroo.dto.response.ApiResponse;
import com.agroo.agroo.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);
    AuthResponse verifyOtp(OtpVerificationRequest request);
    ApiResponse resendOtp(ResendOtpRequest request);
    AuthResponse login(LoginRequest request);
    ApiResponse changePassword(ChangePasswordRequest request, String token);
    ApiResponse forgotPassword(ForgotPasswordRequest request);
    ApiResponse resetPassword(ResetPasswordRequest request);
    ApiResponse logout(String token);
}