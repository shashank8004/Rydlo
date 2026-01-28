package com.rydlo.dto;

import com.rydlo.entities.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRegDTO 
{

    
	@NotBlank(message = "Driving License is Required")
	@jakarta.validation.constraints.Pattern(regexp = "^[A-Z]{2}[0-9]{2}\\s?[0-9]{11}$", message = "Invalid Driving License format")
	private String drivingLicence;
	
	@NotNull(message = "Address is Required")
	@jakarta.validation.Valid
	private Address address;
	
	
}
