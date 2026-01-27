package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Table(name="bike_details")
@Entity
@ToString(callSuper = true,exclude = "owner")
@AttributeOverride(name = "id",column = @Column(name="bike_id"))

public class BikeDetails extends BaseEntity {

	
@Column(length = 20)	
private String model;

@Column(length = 20)
private String number;

@Column(name="bike_type")
@Enumerated(EnumType.STRING)

private BikeType bikeType;

@Column(name="rent_per_day")
private double rentPerDay;


//@Column(name="rent_per_hour")
//private double rentPerHour;

@Column(name="rent_per_km")
private double rentPerKm;


@Column(name="availability_status")
private boolean availabilityStatus=true;

@Column(name="used_km")
private int usedKm;

@ManyToOne
@JoinColumn(name="owner_id",nullable = false)
private Owner owner;

@ManyToOne
@JoinColumn(name="pickup_locatio_id",nullable = false)
private PickupLocation pickupLocation;





}
