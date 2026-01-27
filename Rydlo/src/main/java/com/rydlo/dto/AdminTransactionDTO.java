package com.rydlo.dto;

import com.rydlo.entities.TransactionStatus;
import com.rydlo.entities.TransactionType;

import lombok.Data;

@Data
public class AdminTransactionDTO {
    private Long id;
    private double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private Long bookingId;
    private String gatewayPaymentId;
}
