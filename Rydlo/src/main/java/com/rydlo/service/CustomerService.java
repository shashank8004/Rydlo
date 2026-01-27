package com.rydlo.service;

import com.rydlo.dto.CustomerRegDTO;

import jakarta.validation.Valid;

public interface CustomerService 
{

	String addCustomer(@Valid CustomerRegDTO customerRegDTO);
	

}
