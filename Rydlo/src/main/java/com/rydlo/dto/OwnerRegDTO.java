package com.rydlo.dto;

import com.rydlo.entities.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OwnerRegDTO {

     @NotBlank(message = "Company Name is Required")
	private String companyName;

     @NotBlank(message = "Company Name is Required")
    private String gstNumber;

     @NotNull(message = "Company Name is Required")
    private Address address;
}
