package com.rydlo.dto;

import com.rydlo.entities.Address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OwnerRegDTO {

     @NotBlank(message = "Company Name is Required")
	private String companyName;

     @NotBlank(message = "GST Number is Required")
     @jakarta.validation.constraints.Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST Number format")
    private String gstNumber;

     @NotNull(message = "Address is Required")
     @jakarta.validation.Valid
    private Address address;
}
