package com.rydlo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.rydlo.entities.BikeType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AvailableBikeRequestDTO 
{
	
	@NotNull(message = "Location query is required")
    private String locationQuery;

    @NotNull(message = "Pickup date is required")
    @FutureOrPresent(message = "Pickup date cannot be in the past")
    private LocalDate pickupDate;
    
    private BikeType bikeType;

    @NotNull(message = "Pickup time is required")
    private LocalTime pickupTime;

    @NotNull(message = "Drop-off date is required")
    @Future(message = "Drop-off date must be in the future")
    private LocalDate dropOffDate;

    @NotNull(message = "Drop-off time is required")
    private LocalTime dropOffTime;

}
