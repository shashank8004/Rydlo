package com.rydlo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PriceResponseDTO {
	
	    private long durationHours;
	 
	    private int daysCharged;

	    private int includedKm;

	    private double baseAmount;

	    private double cgst; 
	    
	    private double sgst; 
	    
	    private double taxAmount; 

	    private double totalPayable;

	    private String note;
}
