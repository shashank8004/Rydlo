package com.rydlo.dto;

import java.time.LocalDateTime;
import com.rydlo.entities.TransactionStatus;
import com.rydlo.entities.TransactionType;
import lombok.Data;

@Data
public class PaymentResponseDTO {
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
