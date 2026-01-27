package com.rydlo.dto;

import com.rydlo.entities.BikeType;

import lombok.Getter;
import lombok.ToString;

@Getter
@lombok.Setter
@ToString
public class BikeDetailsResDTO {

	

	    private Long bikeId;
	    private String model;
	    private String number;
	    private BikeType bikeType;

	 //   private double rentPerHour;
	    private double rentPerDay;
	    private double rentPerKm;

	    private int usedKm;
	

	  
	    private String street;
	    private String locality;
	    private String city;
	    private String state;
	    private String pincode;

	    
	    private String ownerCompanyName;
	

}
