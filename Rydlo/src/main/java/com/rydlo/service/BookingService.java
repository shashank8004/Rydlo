package com.rydlo.service;

import com.rydlo.dto.CreateBookingRequestDTO;
import com.rydlo.dto.CreateBookingResponseDTO;
import com.rydlo.dto.DropOffRequestDTO;
import com.rydlo.dto.DropOffResponseDTO;

import jakarta.validation.Valid;

public interface BookingService {

	CreateBookingResponseDTO createBooking(@Valid CreateBookingRequestDTO request);

	DropOffResponseDTO completeRide(Long bookingId, @Valid DropOffRequestDTO request);
	
	java.util.List<com.rydlo.dto.AdminBookingDTO> getMyBookings();

}
