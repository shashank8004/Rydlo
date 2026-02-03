package com.rydlo.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.payment.dto.PaymentRequest;
import com.rydlo.payment.dto.PaymentResponse;
import com.rydlo.payment.dto.PaymentVerificationRequest;
import com.rydlo.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponse> createOrder(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBooking(@PathVariable Long bookingId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByBooking(bookingId);
        return ResponseEntity.ok(responses);
    }
}
