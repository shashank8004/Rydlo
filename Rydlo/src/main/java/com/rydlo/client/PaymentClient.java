package com.rydlo.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.rydlo.dto.PaymentRequestDTO;
import com.rydlo.dto.PaymentResponseDTO;
import com.rydlo.dto.PaymentVerificationRequestDTO;

@FeignClient(name = "rydlo-payment-service", url = "http://localhost:8081")
public interface PaymentClient {

    @PostMapping("/payments/create-order")
    PaymentResponseDTO createOrder(@RequestBody PaymentRequestDTO request);

    @PostMapping("/payments/verify")
    PaymentResponseDTO verifyPayment(@RequestBody PaymentVerificationRequestDTO request);

    @GetMapping("/payments/booking/{bookingId}")
    List<PaymentResponseDTO> getPaymentsByBooking(@PathVariable("bookingId") Long bookingId);
}
