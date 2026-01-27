package com.rydlo.dto;

import com.rydlo.entities.BikeType;

import lombok.Data;

@Data
public class BikeResDTO {
	
	
	
	private Long id;
	private String model;
	private BikeType bikeType;
	private double rentPerDay;
	//private double rentPerHour;
	private double rentPerKm;
	

}

