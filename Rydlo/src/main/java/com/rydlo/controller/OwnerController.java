package com.rydlo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.BikeRegDTO;
import com.rydlo.dto.OwnerRegDTO;
import com.rydlo.service.OwnerService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/owners")
@RestController
@AllArgsConstructor
public class OwnerController 
{

	@Autowired
	
	private final OwnerService ownerService;
	
	
	@PostMapping("/register")
	
	public ResponseEntity<?> registerOwner(@RequestBody @Valid OwnerRegDTO owner)
	{
		String msg= ownerService.addOwner(owner);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(msg);
	}
	

	
	@PostMapping("/bikes")
	public ResponseEntity<?> registerBikes(@RequestBody @Valid BikeRegDTO bikeRegDTO)
	{
		String msg=ownerService.registerBikes(bikeRegDTO);
		return  ResponseEntity.status(HttpStatus.CREATED).body(msg);
	}
	
	@org.springframework.web.bind.annotation.GetMapping("/bikes")
	public ResponseEntity<?> getMyBikes() {
		return ResponseEntity.ok(ownerService.getMyBikes());
	}
	
	@org.springframework.web.bind.annotation.GetMapping("/pickup-locations")
	public ResponseEntity<?> getPickupLocations() {
		return ResponseEntity.ok(ownerService.getPickupLocations());
	}
	
	@org.springframework.web.bind.annotation.PutMapping("/bikes/{bikeId}")
	public ResponseEntity<?> updateBike(@PathVariable Long bikeId, @RequestBody @Valid BikeRegDTO bikeRegDTO) {
		return ResponseEntity.ok(ownerService.updateBike(bikeId, bikeRegDTO));
	}
	
	@org.springframework.web.bind.annotation.DeleteMapping("/bikes/{bikeId}")
	public ResponseEntity<?> deleteBike(@PathVariable Long bikeId) {
		return ResponseEntity.ok(ownerService.deleteBike(bikeId));
	}
	
	@org.springframework.web.bind.annotation.GetMapping("/bookings")
	public ResponseEntity<java.util.List<com.rydlo.dto.AdminBookingDTO>> getMyBikesBookings() {
		return ResponseEntity.ok(ownerService.getMyBikesBookings());
	}
	
	@PostMapping("/bookings/{bookingId}/cancel")
	public ResponseEntity<String> cancelBikeBooking(@PathVariable Long bookingId) {
		ownerService.cancelBikeBooking(bookingId);
		return ResponseEntity.ok("Booking cancelled successfully");
	}
	
	@org.springframework.web.bind.annotation.GetMapping("/transactions")
	public ResponseEntity<java.util.List<com.rydlo.dto.AdminTransactionDTO>> getMyBikesTransactions() {
		return ResponseEntity.ok(ownerService.getMyBikesTransactions());
	}
	

}
