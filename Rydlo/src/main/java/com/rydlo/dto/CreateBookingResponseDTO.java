package com.rydlo.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingResponseDTO {

    private Long bookingId;

    private LocalDateTime pickupDateTime;
    private LocalDateTime dropOffDateTime;

    private long durationHours;
    private int daysCharged;
    private int includedKm;

    private double baseAmount;
    private double cgst;
    private double sgst;
    private double totalPayable;

    private String bookingStatus;

    
}
