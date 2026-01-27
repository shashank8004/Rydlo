package com.rydlo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.PasswordResetToken;
import com.rydlo.entities.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByOtpAndUser(String otp, User user);
    void deleteByUser(User user);
    Optional<PasswordResetToken> findByUser(User user);
    Optional<PasswordResetToken> findByUser_Id(Long userId);
}
