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

@Getter
@Setter
@Entity
@Table(name="transactions")
@AttributeOverride(name = "id",column = @Column(name="transaction_id"))
public class Transaction extends BaseEntity {


    @Column(nullable = false)
    private double amount;
    
    @Column(nullable = false,name="transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;


    // Payment gateway references
    @Column(name = "gateway_order_id")
    private String gatewayOrderId;

    @Column(name = "gateway_payment_id")
    private String gatewayPaymentId;

    @Column(name = "gateway_signature")
    private String gatewaySignature;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingDetails bookingDetails;
    
    @Column(name = "transaction_status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

	
	
	
}

