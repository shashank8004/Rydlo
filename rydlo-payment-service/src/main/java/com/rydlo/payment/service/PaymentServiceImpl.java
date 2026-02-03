package com.rydlo.payment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.rydlo.payment.dto.PaymentRequest;
import com.rydlo.payment.dto.PaymentResponse;
import com.rydlo.payment.dto.PaymentVerificationRequest;
import com.rydlo.payment.entities.Transaction;
import com.rydlo.payment.entities.TransactionStatus;
import com.rydlo.payment.repository.TransactionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private RazorpayClient razorpayClient;

    @PostConstruct
    public void init() throws RazorpayException
    {
    	
        // Initialize client only if keys are provided
        if (!isMockMode()) {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        }
    }

    private boolean isMockMode() {
        return razorpayKeyId == null || razorpayKeyId.contains("YOUR_KEY") || razorpayKeySecret == null;
    }

    @Override
    public PaymentResponse createOrder(PaymentRequest request) {
        try {
            String razorpayOrderId;
            
            if (isMockMode()) {
                System.out.println("WARN: Using Mock Razorpay Mode");
                razorpayOrderId = "order_mock_" + System.currentTimeMillis();
            } else {
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", (int)(request.getAmount() * 100));
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

                Order order = razorpayClient.orders.create(orderRequest);
                razorpayOrderId = order.get("id");
            }

            Transaction transaction = new Transaction();
            transaction.setBookingId(request.getBookingId());
            transaction.setAmount(request.getAmount());
            transaction.setTransactionType(request.getTransactionType());
            transaction.setGatewayOrderId(razorpayOrderId);
            transaction.setTransactionStatus(TransactionStatus.INITIATED);
            
            Transaction savedTransaction = transactionRepository.save(transaction);
            
            PaymentResponse response = modelMapper.map(savedTransaction, PaymentResponse.class);
            response.setGatewayPaymentId(null); 
            response.setGatewayOrderId(savedTransaction.getGatewayOrderId());
            response.setRazorpayKeyId(this.razorpayKeyId);
            return response;

        } catch (RazorpayException e) {
            throw new RuntimeException("Razorpay Order Creation Failed: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse verifyPayment(PaymentVerificationRequest request) {
        try {
            boolean isValid;
            
            if (isMockMode()) {
                 isValid = true;
            } else {
                String signature = request.getSignature();
                String payload = request.getOrderId() + "|" + request.getPaymentId();
                isValid = Utils.verifySignature(payload, signature, razorpayKeySecret);
            }

            Transaction transaction = transactionRepository.findByGatewayOrderId(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found for Order ID: " + request.getOrderId()));

            if (isValid) {
                transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
                transaction.setGatewayPaymentId(request.getPaymentId());
                transaction.setGatewaySignature(request.getSignature()); // Save whatever signature passed
                Transaction saved = transactionRepository.save(transaction);
                return modelMapper.map(saved, PaymentResponse.class);
            } else {
                transaction.setTransactionStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                throw new RuntimeException("Payment Verification Failed: Invalid Signature");
            }

        } catch (RazorpayException e) {
            throw new RuntimeException("Razorpay Verification Error: " + e.getMessage());
        }
    }

    @Override
    public List<PaymentResponse> getPaymentsByBooking(Long bookingId) {
        return transactionRepository.findByBookingId(bookingId).stream()
                .map(txn -> {
                    PaymentResponse res = modelMapper.map(txn, PaymentResponse.class);
                   
                    res.setGatewayPaymentId(txn.getGatewayPaymentId());
                    return res;
                })
                .collect(Collectors.toList());
    }
}
