package com.rydlo.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.DuplicateResourceException;
import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.CustomerRegDTO;
import com.rydlo.entities.Customer;
import com.rydlo.entities.User;
import com.rydlo.repository.CustomerRepository;
import com.rydlo.repository.UserRepository;
import com.rydlo.security.UserPrincipal;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class CustomerServiceIMPL implements CustomerService {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final ModelMapper modelMapper;
	@Override
	public String addCustomer(@Valid CustomerRegDTO customerRegDTO) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long id = userPrincipal.getUserId();
		
		if(customerRepository.existsByDrivingLicence(customerRegDTO.getDrivingLicence()))
		{
			throw new DuplicateResourceException("Driving License already exists");
		}
		 
		User user= userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User Not Found"));
		
		Customer transientCustomer = modelMapper.map(customerRegDTO,Customer.class);
        transientCustomer.setUser(user);
        Customer persistentCustomer=customerRepository.save(transientCustomer);
        return "Customer Added with customer id = "+ persistentCustomer.getId() ; 
		
	}

}
