package com.rydlo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        
        log.info("Generating OTP for {}: {}", toEmail, otp);

        if (javaMailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(toEmail);
                message.setSubject("Rydlo Password Reset OTP");
                message.setText("Your OTP for password reset is: " + otp + "\nThis OTP is valid for 10 minutes.");
                javaMailSender.send(message);
                log.info("OTP Email sent to: {}", toEmail);
            } catch (Exception e) {
                log.error("Failed to send email: {}", e.getMessage());
            }
        } else {
            log.warn("JavaMailSender not configured.");
        }
    }
}
