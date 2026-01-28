package com.rydlo.dto;

import com.rydlo.entities.BikeType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BikeRegDTO {
	
	
@NotBlank(message="Model is required")
private String model;

@NotBlank(message="Registration number is required ")
@jakarta.validation.constraints.Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$", message = "Invalid Bike Number format")
private String number;



@NotNull(message="BikeType is required")
private BikeType bikeType;

@NotNull(message="RentPerDay is required")
private double rentPerDay;

//@NotNull(message="RentPerHour is required")
//private double rentPerHour;


@NotNull(message="RentPerKm is required")
private double rentPerKm;



@NotNull(message="UsedKm is required")
private int usedKm;


@NotNull(message="Pickup Loaction is required")
private Long pickupLocationId;

}
