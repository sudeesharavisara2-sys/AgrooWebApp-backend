package com.agroo.agroo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private String role;
    private String userType;      // FARMER, FERTILIZER_SELLER, MACHINERY_OWNER, BUYER
    private Boolean isVerified;
    private String message;
    private Boolean success;
}