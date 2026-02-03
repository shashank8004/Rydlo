package com.rydlo.dto;

import com.rydlo.entities.TransactionStatus;
import com.rydlo.entities.TransactionType;
import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long bookingId;
    private double amount;
    private TransactionType transactionType;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    private TransactionStatus transactionStatus;
}
