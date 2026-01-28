package com.rydlo.service;

import java.util.List;

import com.rydlo.dto.AdminTransactionDTO;

public interface TransactionService {

	List<AdminTransactionDTO> getMyTransactions();

	List<AdminTransactionDTO> getAllTransactions();

}
