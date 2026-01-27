package com.rydlo.entities;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name="vehicle_details")
@Entity
@ToString(callSuper = true,exclude = "owner")
@AttributeOverride(name = "id",column = @Column(name="vehicle_id"))

public class VehicleDetails extends BaseEntity {

	
@Column(length = 20)	
private String model;
@Column(length = 20)
private String number;

@Column(name="vehicle_type")
@Enumerated(EnumType.STRING)
private VehicleType vehicleType;

@Column(name="rent_per_day")
private double rentPerDay;

@Column(name="availability_status")
private boolean availabilityStatus;

@ManyToOne
@JoinColumn(name="owner_id",nullable = false)
private Owner owner;

@Embedded
private PickupLocation pickupLocation;


}
