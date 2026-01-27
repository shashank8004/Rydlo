package com.rydlo.entities;

import java.time.LocalDate;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="transactions")
@AttributeOverride(name = "id",column = @Column(name="transaction_id"))
public class Transaction extends BaseEntity {

	private double ammount;
	
	@Column(name = "order_id")
	private String orderId;
	
	@Column(name = "payment_id")
	private String paymentId;
	
	@Column(name = "transaction_status")
	private TransactionStatus transactionStatus;
	
	@Column(name = "transaction_time")
	private LocalDate transactionTime;
	
	@OneToOne
	@JoinColumn(name="booking_details",nullable = false)
	private BookingDetails bookingDetails;
	
	
}

