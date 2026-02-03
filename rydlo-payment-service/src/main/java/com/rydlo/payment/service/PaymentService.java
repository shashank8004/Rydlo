package com.rydlo.payment.service;

import java.util.List;
import com.rydlo.payment.dto.PaymentRequest;
import com.rydlo.payment.dto.PaymentResponse;
import com.rydlo.payment.dto.PaymentVerificationRequest;

public interface PaymentService {
    PaymentResponse createOrder(PaymentRequest request);
    PaymentResponse verifyPayment(PaymentVerificationRequest request);
    List<PaymentResponse> getPaymentsByBooking(Long bookingId);
}
