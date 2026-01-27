package com.rydlo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.CustomerRegDTO;
import com.rydlo.service.CustomerService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/customers")
@RestController
@AllArgsConstructor
public class CustomerController 
{

	@Autowired
	private final CustomerService customerService;
	
	@PostMapping("/register")
	public ResponseEntity<?> registerCustomer(@org.springframework.web.bind.annotation.RequestBody @Valid CustomerRegDTO customerRegDTO)
	{
		String msg=customerService.addCustomer(customerRegDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(msg);
	}
	
}
