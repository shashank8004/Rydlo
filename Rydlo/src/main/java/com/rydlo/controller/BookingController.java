package com.rydlo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.CreateBookingRequestDTO;
import com.rydlo.dto.CreateBookingResponseDTO;
import com.rydlo.dto.DropOffRequestDTO;
import com.rydlo.dto.DropOffResponseDTO;
import com.rydlo.service.BookingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
public class BookingController {
	
	@Autowired
	private final BookingService bookingService;

	 @PostMapping
	    public ResponseEntity<CreateBookingResponseDTO> createBooking(
	            @Valid @RequestBody CreateBookingRequestDTO request) {

		 CreateBookingResponseDTO response =
	                bookingService.createBooking(request);
	        return ResponseEntity.ok(
	               response
	        );
	    }

	 @PostMapping("/{bookingId}/drop-off")
	    public ResponseEntity<DropOffResponseDTO> completeRide(
	            @PathVariable Long bookingId,
	            @Valid @RequestBody DropOffRequestDTO request) {

		 DropOffResponseDTO response =
	                bookingService.completeRide(bookingId, request);
	        return ResponseEntity.ok(
	               response
	        );
	    }
	 
	 
    @org.springframework.web.bind.annotation.GetMapping("/my")
    public ResponseEntity<java.util.List<com.rydlo.dto.AdminBookingDTO>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

}
