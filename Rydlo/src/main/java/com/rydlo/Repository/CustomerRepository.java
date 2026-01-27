package com.rydlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByDrivingLicence(String drivingLicence);
	
	java.util.Optional<Customer> findByUser_Id(Long userId);

}
