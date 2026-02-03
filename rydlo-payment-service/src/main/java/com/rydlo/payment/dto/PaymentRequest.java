package com.rydlo.payment.dto;

import com.rydlo.payment.entities.TransactionStatus;
import com.rydlo.payment.entities.TransactionType;
import lombok.Data;

@Data
public class PaymentRequest {
    private Long bookingId;
    private double amount;
    private TransactionType transactionType;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    private String gatewaySignature;
    private TransactionStatus transactionStatus;
}
