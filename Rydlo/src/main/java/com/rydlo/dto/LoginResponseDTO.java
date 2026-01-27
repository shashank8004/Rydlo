package com.rydlo.dto;

import lombok.Getter;

@Getter
public class LoginResponseDTO {

    private final String token;
    private final String tokenType = "Bearer";
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String role;

    public LoginResponseDTO(String token, Long userId, String firstName, String lastName, String role) {
        this.token = token;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }
}