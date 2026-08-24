package com.agroo.agroo.service.impl;

import com.agroo.agroo.model.OtpToken;
import com.agroo.agroo.model.enums.OtpType;
import com.agroo.agroo.repository.OtpTokenRepository;
import com.agroo.agroo.service.OtpService;
import com.agroo.agroo.util.OtpGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpTokenRepository otpTokenRepository;
    private final OtpGenerator otpGenerator;
    private final JavaMailSender mailSender;

    @Value("${otp.expiration-minutes:10}")
    private int expirationMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    @Override
    @Transactional
    public String generateAndSendOtp(String email, OtpType otpType) {
        // Delete existing OTPs for this email and type
        otpTokenRepository.deleteByEmail(email);

        // Generate new OTP
        String otpCode = otpGenerator.generateOtp(otpLength);

        // Save OTP token
        OtpToken otpToken = new OtpToken();
        otpToken.setEmail(email);
        otpToken.setOtpCode(otpCode);
        otpToken.setOtpType(otpType);
        otpToken.setIsUsed(false);
        otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));

        otpTokenRepository.save(otpToken);

        // Send OTP via email (async)
        sendOtpEmail(email, otpCode, otpType);

        return otpCode;
    }

    @Override
    public Boolean verifyOtp(String email, String otpCode, OtpType otpType) {
        OtpToken otpToken = otpTokenRepository
                .findByEmailAndOtpCodeAndOtpTypeAndIsUsedFalse(email, otpCode, otpType)
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        // Check if OTP has expired
        if (otpToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }

        // Mark OTP as used
        otpToken.setIsUsed(true);
        otpTokenRepository.save(otpToken);

        return true;
    }

    @Override
    @Transactional
    public void resendOtp(String email, OtpType otpType) {
        generateAndSendOtp(email, otpType);
    }

    @Async
    protected void sendOtpEmail(String email, String otpCode, OtpType otpType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(getEmailSubject(otpType));
            message.setText(getEmailBody(otpCode, otpType));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    private String getEmailSubject(OtpType otpType) {
        switch (otpType) {
            case REGISTRATION:
                return "🌾 Agroo - Verify Your Account";
            case FORGOT_PASSWORD:
                return "🔐 Agroo - Reset Your Password";
            case CHANGE_PASSWORD:
                return "🔐 Agroo - Change Password Verification";
            default:
                return "🌾 Agroo - OTP Verification";
        }
    }

    private String getEmailBody(String otpCode, OtpType otpType) {
        String message = "";
        switch (otpType) {
            case REGISTRATION:
                message = "Welcome to Agroo! Please verify your account with the OTP below:\n\n";
                break;
            case FORGOT_PASSWORD:
                message = "You requested to reset your password. Use this OTP:\n\n";
                break;
            case CHANGE_PASSWORD:
                message = "You requested to change your password. Use this OTP:\n\n";
                break;
        }

        return message +
                "🔑 OTP Code: " + otpCode + "\n\n" +
                "⏰ This OTP is valid for " + expirationMinutes + " minutes.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "🌾 Agroo - Empowering Farmers";
    }
}