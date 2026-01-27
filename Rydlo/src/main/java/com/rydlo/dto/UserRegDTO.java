package com.rydlo.dto;

import com.rydlo.entities.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserRegDTO {
	
	
	@NotBlank(message="First Name is mandatory")
	private String firstName;

	@NotBlank(message="Last Name is mandatory")
	private String lastName;
	
	@NotBlank(message=" Email is mandatory")
	private String email;
	
	@NotBlank(message=" Phone is mandatory")
	
	private String phone;
	
	@NotBlank(message=" Password is mandatory")

	private String password;
	
	@NotNull(message=" Role is mandatory")
	private Role role;

	
	
	


}
