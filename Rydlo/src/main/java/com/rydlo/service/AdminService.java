package com.rydlo.service;

import java.util.List;
import com.rydlo.entities.User;
import jakarta.validation.Valid;
import com.rydlo.dto.PickupLocationDto;
import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.dto.BikeResDTO;
import com.rydlo.entities.PickupLocation;

public interface AdminService {

	List<User> getAllUsers();

	String addPickupLocation(@Valid PickupLocationDto pikupLocationDto);
	
	String updatePickupLocation(Long id, @Valid PickupLocationDto pickupLocationDto);
	
	String deletePickupLocation(Long id);
	
	List<PickupLocation> getAllPickupLocations();
	
	// Simple map for stats: { "users": 10, "bikes": 5, "locations": 3 }
	java.util.Map<String, Long> getDashboardStats();

	List<BikeResDTO> getAllBikes();
	
	List<AdminBookingDTO> getAllBookings();
	
	List<AdminTransactionDTO> getAllTransactions();
}
