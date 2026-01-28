package com.rydlo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.entities.Customer;
import com.rydlo.entities.Transaction;
import com.rydlo.repository.CustomerRepository;
import com.rydlo.repository.TransactionRepository;
import com.rydlo.security.UserPrincipal;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TransactionServiceIMPL implements TransactionService {

	@Autowired
	private final TransactionRepository transactionRepository;
	private final CustomerRepository customerRepository;
	private final ModelMapper modelMapper;

	@Override
	public List<AdminTransactionDTO> getMyTransactions() {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Customer customer = customerRepository.findByUser_Id(userPrincipal.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

		return transactionRepository.findByBookingDetails_Customer(customer).stream()
				.map(t -> {
					AdminTransactionDTO dto = modelMapper.map(t, AdminTransactionDTO.class);
					dto.setBookingId(t.getBookingDetails().getId());
					return dto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<AdminTransactionDTO> getAllTransactions() {
		return transactionRepository.findAll().stream().map(t -> {
			AdminTransactionDTO dto = modelMapper.map(t, AdminTransactionDTO.class);
			dto.setBookingId(t.getBookingDetails().getId());
			return dto;
		}).collect(Collectors.toList());
	}

}
