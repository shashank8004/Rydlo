package com.rydlo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.dto.BikeResDTO;
import com.rydlo.dto.PickupLocationDto;
import com.rydlo.entities.PickupLocation;
import com.rydlo.entities.User;
import com.rydlo.repository.BikeDetailsRepository;
import com.rydlo.repository.BookingDetailsRepository;
import com.rydlo.repository.PickupLocationRepository;
import com.rydlo.repository.TransactionRepository;
import com.rydlo.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceIMPL implements AdminService {

	private final ModelMapper modelMapper;
	private final UserRepository userRepository;
	private final PickupLocationRepository pickupLocationRepository;
	private final BookingDetailsRepository bookingDetailsRepository;
	private final TransactionRepository transactionRepository;
	private final BikeDetailsRepository bikeDetailsRepository;
	
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public String addPickupLocation(@Valid PickupLocationDto pikupLocationDto) 
	{
		PickupLocation transientPickupLocation= modelMapper.map(pikupLocationDto,PickupLocation.class);
		PickupLocation persistentPickupLocation=pickupLocationRepository.save(transientPickupLocation);
		return "Picup location added with id= "+persistentPickupLocation.getId();
	}

	@Override
	public String updatePickupLocation(Long id, @Valid PickupLocationDto pickupLocationDto) {
		PickupLocation existingLocation = pickupLocationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Location not found"));
		
		modelMapper.map(pickupLocationDto, existingLocation);
		// Ensure ID remains the same
		existingLocation.setId(id);
		
		pickupLocationRepository.save(existingLocation);
		return "Location updated successfully";
	}
	
	@Override
	public String deletePickupLocation(Long id) {
		if (!pickupLocationRepository.existsById(id)) {
			throw new ResourceNotFoundException("Location not found");
		}
		try {
			pickupLocationRepository.deleteById(id);
		} catch (Exception e) {
			throw new RuntimeException("Cannot delete location. It might be in use by bikes.");
		}
		return "Location deleted successfully";
	}

	@Override
	public List<PickupLocation> getAllPickupLocations() {
		return pickupLocationRepository.findAll();
	}

	@Override
	public Map<String, Long> getDashboardStats() {
		Map<String, Long> stats = new HashMap<>();
		stats.put("users", userRepository.count());
		stats.put("locations", pickupLocationRepository.count());
		stats.put("bikes", bikeDetailsRepository.count());
		return stats;
	}

	@Override
	public List<BikeResDTO> getAllBikes() {
		return bikeDetailsRepository.findAll().stream()
				.map(bike -> {
					BikeResDTO dto = modelMapper.map(bike, BikeResDTO.class);
					if (bike.getPickupLocation() != null) {
						dto.setCity(bike.getPickupLocation().getCity());
						dto.setLocality(bike.getPickupLocation().getLocality());
					}
					return dto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<AdminBookingDTO> getAllBookings() {
		return bookingDetailsRepository.findAll().stream()
				.map(booking -> {
					AdminBookingDTO dto = modelMapper.map(booking, AdminBookingDTO.class);
					if (booking.getCustomer() != null) {
						dto.setCustomerEmail(booking.getCustomer().getUser().getEmail());
						dto.setCustomerName(booking.getCustomer().getUser().getFirstName() + " " + booking.getCustomer().getUser().getLastName());
					}
					if (booking.getBikeDetails() != null) {
						dto.setBikeModel(booking.getBikeDetails().getModel());
						dto.setBikeNumber(booking.getBikeDetails().getNumber());
					}
					return dto;
				})
				.collect(Collectors.toList());
	}

	@Override
	public List<AdminTransactionDTO> getAllTransactions() {
		return transactionRepository.findAll().stream()
				.map(txn -> {
					AdminTransactionDTO dto = modelMapper.map(txn, AdminTransactionDTO.class);
					if (txn.getBookingDetails() != null) {
						dto.setBookingId(txn.getBookingDetails().getId());
					}
					return dto;
				})
				.collect(Collectors.toList());
	}

}
