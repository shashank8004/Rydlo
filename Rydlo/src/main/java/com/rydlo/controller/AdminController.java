package com.rydlo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.PickupLocationDto;
import com.rydlo.entities.User;
import com.rydlo.service.AdminService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RequestMapping("/admin")
@RestController
@AllArgsConstructor
public class AdminController 
{
	
	
	@Autowired
	private final AdminService adminService;
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUsers()
	{
		List <User> userList= adminService.getAllUsers();
		
		return ResponseEntity.status(HttpStatus.OK).body(userList);
	}
	
	@PostMapping("/add-pickup-location")
	public ResponseEntity<?> addPickupLocation(@org.springframework.web.bind.annotation.RequestBody @Valid PickupLocationDto pikupLocationDto)
	{
		String msg=adminService.addPickupLocation(pikupLocationDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(msg);
	}
	
	@org.springframework.web.bind.annotation.PutMapping("/pickup-locations/{id}")
	public ResponseEntity<?> updatePickupLocation(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestBody @Valid PickupLocationDto pickupLocationDto) {
		return ResponseEntity.ok(adminService.updatePickupLocation(id, pickupLocationDto));
	}
	
	@org.springframework.web.bind.annotation.DeleteMapping("/pickup-locations/{id}")
	public ResponseEntity<?> deletePickupLocation(@org.springframework.web.bind.annotation.PathVariable Long id) {
		return ResponseEntity.ok(adminService.deletePickupLocation(id));
	}
	
	@GetMapping("/pickup-locations")
	public ResponseEntity<?> getAllPickupLocations() {
		return ResponseEntity.ok(adminService.getAllPickupLocations());
	}
	
	@GetMapping("/stats")
	public ResponseEntity<?> getDashboardStats() {
		return ResponseEntity.ok(adminService.getDashboardStats());
	}
	
	@GetMapping("/bikes")
	public ResponseEntity<?> getAllBikes() {
		return ResponseEntity.ok(adminService.getAllBikes());
	}
	
	@GetMapping("/bookings")
	public ResponseEntity<?> getAllBookings() {
		return ResponseEntity.ok(adminService.getAllBookings());
	}

	@GetMapping("/transactions")
	public ResponseEntity<?> getAllTransactions() {
		return ResponseEntity.ok(adminService.getAllTransactions());
	}
}