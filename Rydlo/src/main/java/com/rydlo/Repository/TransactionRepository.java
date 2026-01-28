package com.rydlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	java.util.List<Transaction> findByBookingDetails_Customer(com.rydlo.entities.Customer customer);
	
	java.util.List<Transaction> findByBookingDetails_BikeDetails_Owner(com.rydlo.entities.Owner owner);
}
