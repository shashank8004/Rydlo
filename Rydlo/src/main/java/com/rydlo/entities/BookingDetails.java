package com.rydlo.entities;

import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name="booking_details")
@AttributeOverride(name = "id",column = @Column(name="booking_id"))
@ToString(callSuper = true,exclude = {"customer","vehicleDetails"})
public class BookingDetails extends BaseEntity {

	@Column(name="start_date")
	private LocalDate startDate;
	
	@Column(name="end_date")
	private LocalDate endDate;
	
	
	
	@Enumerated(EnumType.STRING)
    @Column(name="booking_status")
	private BookingStatus bookingStatus;
	
	@OneToOne
	@JoinColumn(name="customer_id",nullable = false)
	private Customer customer;
	
	@OneToOne
	@JoinColumn(name="vehicle_id",nullable = false)
	private VehicleDetails vehicleDetails;
	


}
