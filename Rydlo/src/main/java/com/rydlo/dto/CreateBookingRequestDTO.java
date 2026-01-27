package com.rydlo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequestDTO {

    @NotNull
    private Long bikeId;

    @NotNull
    private Long customerId;

    @NotNull
    private LocalDate pickupDate;

    @NotNull
    private LocalTime pickupTime;

    @NotNull
    private LocalDate dropOffDate;

    @NotNull
    private LocalTime dropOffTime;

    @Min(0)
    private int initialKm;

  
}
