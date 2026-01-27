package com.rydlo.entities;

import java.time.LocalDateTime;

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
@Entity
@Table(name="booking_details")
@AttributeOverride(name = "id",column = @Column(name="booking_id"))
@ToString(callSuper = true,exclude = {"customer","bikeDetails"})
public class BookingDetails extends BaseEntity {


	    // ===== TIME RANGE =====
	    @Column(name = "pickup_date_time", nullable = false)
	    private LocalDateTime pickupDateTime;

	    @Column(name = "drop_off_date_time", nullable = false)
	    private LocalDateTime dropOffDateTime;

	    // ===== ODOMETER =====
	    
	    @Column(name = "initial_used", nullable = false)
	    private int initialUsed;   // km at pickup

	    @Column(name = "final_used")
	    private Integer finalUsed; // km at drop-off

	    // ===== PRICING BREAKUP =====

	    // min price for duration (no tax, no extra km)
	    
	    @Column(name = "base_amount", nullable = false)
	    private double baseAmount;

	    // tax calculated at booking time
	    
	    @Column(name = "cgst", nullable = false)
	    private double cgst;

	    @Column(name = "sgst", nullable = false)
	    private double sgst;

	    // amount charged at booking time
	    
	    @Column(name = "total_amount", nullable = false)
	    private double totalAmount;

	    // final payable amount (after extra km at drop-off)
	    
	    @Column(name = "final_amount")
	    private Double finalAmount;

	
	    @Enumerated(EnumType.STRING)
	    @Column(name = "booking_status", nullable = false)
	    private BookingStatus bookingStatus;

	  
	    @ManyToOne
	    @JoinColumn(name = "bike_id", nullable = false)
	    private BikeDetails bikeDetails;

	    @ManyToOne
	    @JoinColumn(name = "customer_id", nullable = false)
	    private Customer customer;
	}

	



