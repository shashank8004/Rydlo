package com.rydlo.dto;

import com.rydlo.entities.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerRegDTO 
{

    
	@NotBlank(message = "Driving License is Required")
	private String drivingLicence;
	
	@NotNull(message = "Address is Required")
	private Address address;
	
	
}
