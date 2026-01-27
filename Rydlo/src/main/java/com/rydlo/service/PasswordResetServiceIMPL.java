package com.rydlo.service;

import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.ApiResponse;
import com.rydlo.entities.PasswordResetToken;
import com.rydlo.entities.User;
import com.rydlo.repository.PasswordResetTokenRepository;
import com.rydlo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetServiceIMPL implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse generateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Generate 6 digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Check if token exists for user (Use ID to be safe against detached entities)
        PasswordResetToken token = tokenRepository.findByUser_Id(user.getId()).orElse(null);

        if (token == null) {
            // Create new if not exists
            token = new PasswordResetToken(otp, user, 10);
        } else {
            // Update existing
            token.setOtp(otp);
            token.setExpiryDate(java.time.LocalDateTime.now().plusMinutes(10));
        }

        tokenRepository.save(token);

        // Send Email
        emailService.sendOtpEmail(email, otp);

        return new ApiResponse("SUCCESS", "OTP sent successfully to " + email);
    }

    @Override
    public ApiResponse verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        PasswordResetToken token = tokenRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (token.isExpired()) {
            tokenRepository.delete(token);
            throw new IllegalArgumentException("OTP Expired");
        }

        return new ApiResponse("SUCCESS", "OTP Verified Successfully");
    }

    @Override
    public ApiResponse resetPassword(String email, String newPassword, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        // Re-verify OTP to be safe (or rely on frontend flow, but safe is better)
        PasswordResetToken token = tokenRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (token.isExpired()) {
            throw new IllegalArgumentException("OTP Expired");
        }

        // Update Password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Consume Token
        tokenRepository.delete(token);

        return new ApiResponse("SUCCESS", "Password Reset Successfully");
    }
}
