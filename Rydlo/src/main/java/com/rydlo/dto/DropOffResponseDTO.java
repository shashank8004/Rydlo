package com.rydlo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DropOffResponseDTO {

    private Long bookingId;
    private int initialKm;
    private int finalKm;

    private int includedKm;
    private int extraKm;

    private double extraKmCharge;
    private double finalAmount;

    private String bookingStatus;


}
