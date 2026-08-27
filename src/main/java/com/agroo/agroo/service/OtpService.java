package com.agroo.agroo.service;

import com.agroo.agroo.model.enums.OtpType;

public interface OtpService {
    String generateAndSendOtp(String email, OtpType otpType);
    Boolean verifyOtp(String email, String otpCode, OtpType otpType);
    void resendOtp(String email, OtpType otpType);
}