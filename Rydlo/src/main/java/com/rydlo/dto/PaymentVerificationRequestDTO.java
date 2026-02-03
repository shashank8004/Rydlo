package com.rydlo.dto;

import lombok.Data;

@Data
public class PaymentVerificationRequestDTO {
    private String paymentId;
    private String orderId;
    private String signature;
}
