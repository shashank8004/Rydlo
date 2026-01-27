package com.rydlo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PriceRequestDTO {

    @NotNull
    private LocalDate pickupDate;

    @NotNull
    private LocalTime pickupTime;

    @NotNull
    private LocalDate dropOffDate;

    @NotNull
    private LocalTime dropOffTime;

   
}
