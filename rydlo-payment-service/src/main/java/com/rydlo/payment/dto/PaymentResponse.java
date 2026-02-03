package com.rydlo.payment.dto;

import java.time.LocalDateTime;
import com.rydlo.payment.entities.TransactionStatus;
import com.rydlo.payment.entities.TransactionType;
import lombok.Data;

@Data
public class PaymentResponse {
    private Long transactionId;
    private Long bookingId;
    private double amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime creationTime;
    private String gatewayPaymentId;
    private String gatewayOrderId;
    private String razorpayKeyId;
}
