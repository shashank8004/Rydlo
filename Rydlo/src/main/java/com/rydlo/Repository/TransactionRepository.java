package com.rydlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
