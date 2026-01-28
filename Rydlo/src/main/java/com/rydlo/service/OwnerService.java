package com.rydlo.service;

import com.rydlo.dto.BikeRegDTO;
import com.rydlo.dto.OwnerRegDTO;

import jakarta.validation.Valid;

public interface OwnerService {

	String addOwner(@Valid OwnerRegDTO owner);

	String registerBikes(@Valid BikeRegDTO bikeRegDTO);
	
	java.util.List<com.rydlo.dto.BikeResDTO> getMyBikes();
	
	String updateBike(Long bikeId, @Valid BikeRegDTO bikeRegDTO);
	
	String deleteBike(Long bikeId);
	
	java.util.List<com.rydlo.entities.PickupLocation> getPickupLocations(); // Or DTO if preferred
	
	// Booking management
	java.util.List<com.rydlo.dto.AdminBookingDTO> getMyBikesBookings();
	
	void cancelBikeBooking(Long bookingId);
	
	// Transaction viewing
	java.util.List<com.rydlo.dto.AdminTransactionDTO> getMyBikesTransactions();

}
