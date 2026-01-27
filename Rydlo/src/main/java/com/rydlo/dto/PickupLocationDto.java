package com.rydlo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PickupLocationDto {
        
	    @NotBlank(message="Street is mandatory")
	    private String  street;
	    @NotBlank(message="Locality is mandatory")
	    private String  locality;
	    @NotBlank(message="City is mandatory")
	    private String city;
	    @NotBlank(message="State is mandatory")
	    private String state;
	    @NotBlank(message="Pincode is mandatory")
	    private String pincode;
}
