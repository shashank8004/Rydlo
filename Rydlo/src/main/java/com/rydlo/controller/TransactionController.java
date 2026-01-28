package com.rydlo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.service.TransactionService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/transactions")
@AllArgsConstructor
public class TransactionController {

	@Autowired
	private final TransactionService transactionService;

	@GetMapping("/my")
	public ResponseEntity<List<AdminTransactionDTO>> getMyTransactions() {
		return ResponseEntity.ok(transactionService.getMyTransactions());
	}

	// Admin only
	@GetMapping
	public ResponseEntity<List<AdminTransactionDTO>> getAllTransactions() {
		return ResponseEntity.ok(transactionService.getAllTransactions());
	}

}
