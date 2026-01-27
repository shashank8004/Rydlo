package com.rydlo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.LoginRequestDTO;
import com.rydlo.dto.LoginResponseDTO;
import com.rydlo.security.JwtUtils;
import com.rydlo.security.UserPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        // 1️⃣ Authenticate user
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        // 2️⃣ Set authentication in security context
        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        // 3️⃣ Generate JWT
        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        String token = jwtUtils.generateToken(principal);

        // 4️⃣ Return JWT
        String role = principal.getAuthorities().stream()
                        .findFirst()
                        .map(item -> item.getAuthority())
                        .orElse("ROLE_CUSTOMER");

        return ResponseEntity.ok(
                new LoginResponseDTO(token, principal.getUserId(), principal.getFirstName(), principal.getLastName(), role)
        );
    }
    
    @org.springframework.beans.factory.annotation.Autowired
    private com.rydlo.service.PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        return ResponseEntity.ok(passwordResetService.generateOtp(request.get("email")));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody java.util.Map<String, String> request) {
        return ResponseEntity.ok(passwordResetService.verifyOtp(request.get("email"), request.get("otp")));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        return ResponseEntity.ok(passwordResetService.resetPassword(
            request.get("email"), 
            request.get("password"),
            request.get("otp")
        ));
    }
}
