package com.rydlo.dto;

import java.time.LocalDateTime;

import com.rydlo.entities.BookingStatus;

import lombok.Data;

@Data
public class AdminBookingDTO {
    private Long id;
    private LocalDateTime pickupDateTime;
    private LocalDateTime dropOffDateTime;
    private double totalAmount;
    private BookingStatus bookingStatus;
    private String customerEmail;
    private String customerName;
    private String bikeModel;
    private String bikeNumber;
}
