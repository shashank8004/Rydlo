package com.rydlo.dto;

import com.rydlo.entities.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    @NotBlank(message = "First Name is mandatory")
    private String firstName;

    @NotBlank(message = "Last Name is mandatory")
    private String lastName;

    private String email;

    @NotBlank(message = "Phone is mandatory")
    @Pattern(regexp = "^(\\+91)?[6-9][0-9]{9}$", message = "Invalid Phone Number")
    private String phone;

    private Role role;
}
