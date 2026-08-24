package com.agroo.agroo.repository;

import com.agroo.agroo.model.OtpToken;
import com.agroo.agroo.model.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmailAndOtpCodeAndOtpTypeAndIsUsedFalse(String email, String otpCode, OtpType otpType);
    void deleteByEmail(String email);
}