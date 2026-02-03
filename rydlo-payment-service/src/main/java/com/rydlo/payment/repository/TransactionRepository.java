package com.rydlo.payment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.rydlo.payment.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBookingId(Long bookingId);
    Optional<Transaction> findByGatewayOrderId(String gatewayOrderId);
}
