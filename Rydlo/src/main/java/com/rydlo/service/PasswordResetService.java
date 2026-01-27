package com.rydlo.service;

import com.rydlo.dto.ApiResponse;

public interface PasswordResetService {
    ApiResponse generateOtp(String email);
    ApiResponse verifyOtp(String email, String otp);
    ApiResponse resetPassword(String email, String newPassword, String otp);
}
