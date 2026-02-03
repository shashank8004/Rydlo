package com.rydlo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import com.rydlo.dto.CreateBookingRequestDTO;
import com.rydlo.dto.CreateBookingResponseDTO;
import com.rydlo.dto.DropOffRequestDTO;
import com.rydlo.dto.DropOffResponseDTO;
import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.PaymentVerificationRequestDTO;
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

		CreateBookingResponseDTO response = bookingService.createBooking(request);
		return ResponseEntity.ok(
				response);
	}

	@PostMapping("/{bookingId}/drop-off")
	public ResponseEntity<DropOffResponseDTO> completeRide(
			@PathVariable Long bookingId,
			@Valid @RequestBody DropOffRequestDTO request) {

		DropOffResponseDTO response = bookingService.completeRide(bookingId, request);
		return ResponseEntity.ok(
				response);
	}

	@GetMapping("/my")
	public ResponseEntity<List<AdminBookingDTO>> getMyBookings() {
		return ResponseEntity.ok(bookingService.getMyBookings());
	}

	@PostMapping("/{bookingId}/cancel")
	public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
		System.out.println("Cancel booking request received for booking ID: " + bookingId);
		bookingService.cancelBooking(bookingId);
		return ResponseEntity.ok("Booking cancelled successfully");
	}

	@PostMapping("/{bookingId}/confirm-payment")
	public ResponseEntity<CreateBookingResponseDTO> confirmPayment(
			@PathVariable Long bookingId,
			@RequestBody PaymentVerificationRequestDTO request) {
		return ResponseEntity.ok(bookingService.confirmBooking(bookingId, request));
	}

}
