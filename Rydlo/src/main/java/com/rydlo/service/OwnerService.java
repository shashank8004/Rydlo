package com.rydlo.service;

import java.util.List;

import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.dto.BikeRegDTO;
import com.rydlo.dto.BikeResDTO;
import com.rydlo.dto.OwnerRegDTO;
import com.rydlo.entities.PickupLocation;

import jakarta.validation.Valid;

public interface OwnerService {

	String addOwner(@Valid OwnerRegDTO owner);

	String registerBikes(@Valid BikeRegDTO bikeRegDTO);
	
	List<BikeResDTO> getMyBikes();
	
	String updateBike(Long bikeId, @Valid BikeRegDTO bikeRegDTO);
	
	String deleteBike(Long bikeId);
	
	List<PickupLocation> getPickupLocations(); 
	
	// Booking management
	List<AdminBookingDTO> getMyBikesBookings();
	
	void cancelBikeBooking(Long bookingId);
	
	// Transaction viewing
List<AdminTransactionDTO> getMyBikesTransactions();

}
