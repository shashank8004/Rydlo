package com.rydlo.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.DuplicateResourceException;
import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.BikeRegDTO;
import com.rydlo.dto.OwnerRegDTO;
import com.rydlo.entities.BikeDetails;
import com.rydlo.entities.Owner;
import com.rydlo.entities.PickupLocation;
import com.rydlo.entities.User;
import com.rydlo.repository.BikeDetailsRepository;
import com.rydlo.repository.BookingRepository;
import com.rydlo.repository.OwnerRepository;
import com.rydlo.repository.PickupLocationRepository;
import com.rydlo.repository.TransactionRepository;
import com.rydlo.repository.UserRepository;
import com.rydlo.security.UserPrincipal;
import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.entities.BookingDetails;
import com.rydlo.entities.BookingStatus;
import com.rydlo.entities.Transaction;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class OwnerServiceIMPL implements OwnerService {
	
	@Autowired
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
    private final OwnerRepository ownerRepository;
    private final BikeDetailsRepository bikeDetailsRepository;
    private final PickupLocationRepository pickupLocationRepository;
    private final BookingRepository bookingRepository;
    private final TransactionRepository transactionRepository;
    
	@Override
	public String addOwner(@Valid OwnerRegDTO owner) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long id = userPrincipal.getUserId();
		
		User user=userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User does not exist") );
		
		if(ownerRepository.existsByGstNumber(owner.getGstNumber()))
		{
			throw new DuplicateResourceException("Gst number is duplicate");
		}
		  Owner transientOwner= modelMapper.map(owner,Owner.class);
		                        transientOwner.setUser(user);
		  Owner persistentOwner=ownerRepository.save(transientOwner);
		  
		  return "Owner details added with id = " + persistentOwner.getId();
		   
		 
	}
	
	@Override
	public String registerBikes(@Valid BikeRegDTO bikeRegDTO)
	{
		if(bikeDetailsRepository.existsByNumber(bikeRegDTO.getNumber()))
		{
			throw new DuplicateResourceException("Bike already Exists");
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long userId = userPrincipal.getUserId();
		
		Owner owner= ownerRepository.findByUser_Id(userId).orElseThrow(()-> new ResourceNotFoundException("Owner is not registerd"));
		PickupLocation location=pickupLocationRepository.findById(bikeRegDTO.getPickupLocationId()).orElseThrow(()-> new ResourceNotFoundException("Pickup Location not found"));
		
		BikeDetails transientBikeDetails=modelMapper.map(bikeRegDTO,BikeDetails.class);
		 transientBikeDetails.setOwner(owner);
		 transientBikeDetails.setPickupLocation(location);
		 BikeDetails persistentBikeDetails=bikeDetailsRepository.save(transientBikeDetails);
		return "Bike details added with id = "+persistentBikeDetails.getId();
	}
	
	@Override
	public java.util.List<com.rydlo.dto.BikeResDTO> getMyBikes() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long userId = userPrincipal.getUserId();
		
		Owner owner= ownerRepository.findByUser_Id(userId).orElseThrow(()-> new ResourceNotFoundException("Owner is not registerd"));
		
		java.util.List<BikeDetails> bikes = bikeDetailsRepository.findByOwner(owner);
		
		return bikes.stream()
				.map(bike -> modelMapper.map(bike, com.rydlo.dto.BikeResDTO.class))
				.collect(java.util.stream.Collectors.toList());
	}

	@Override
	public String updateBike(Long bikeId, @Valid BikeRegDTO bikeRegDTO) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal(); // Assuming you have UserPrincipal
		Long userId = userPrincipal.getUserId();
		
		Owner owner = ownerRepository.findByUser_Id(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
		
		BikeDetails bike = bikeDetailsRepository.findById(bikeId)
				.orElseThrow(() -> new ResourceNotFoundException("Bike not found"));
		
		// Security Check: Ensure bike belongs to logged-in owner
		if (!bike.getOwner().getId().equals(owner.getId())) {
			throw new com.rydlo.custom_exception.ResourceNotFoundException("You do not own this bike"); 
			// Or AccessDeniedException if you have it
		}
		
		PickupLocation location = pickupLocationRepository.findById(bikeRegDTO.getPickupLocationId())
				.orElseThrow(() -> new ResourceNotFoundException("Location not found"));

		// Update fields
		bike.setModel(bikeRegDTO.getModel());
		bike.setNumber(bikeRegDTO.getNumber());
		bike.setBikeType(bikeRegDTO.getBikeType());
		bike.setRentPerDay(bikeRegDTO.getRentPerDay());
		bike.setRentPerKm(bikeRegDTO.getRentPerKm());
		bike.setUsedKm(bikeRegDTO.getUsedKm());
		bike.setPickupLocation(location);
		
		bikeDetailsRepository.save(bike);
		
		return "Bike updated successfully";
	}

	@Override
	public String deleteBike(Long bikeId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal(); 
		Long userId = userPrincipal.getUserId();
		
		Owner owner = ownerRepository.findByUser_Id(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
		
		BikeDetails bike = bikeDetailsRepository.findById(bikeId)
				.orElseThrow(() -> new ResourceNotFoundException("Bike not found"));
		
		if (!bike.getOwner().getId().equals(owner.getId())) {
			throw new com.rydlo.custom_exception.ResourceNotFoundException("You do not own this bike");
		}
		
		// Soft delete is better, but hard delete for now as per request
		bikeDetailsRepository.delete(bike);
		
		return "Bike deleted successfully";
	}

	@Override
	public java.util.List<PickupLocation> getPickupLocations() {
		return pickupLocationRepository.findAll();
	}

	@Override
	public java.util.List<AdminBookingDTO> getMyBikesBookings() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long userId = userPrincipal.getUserId();
		
		Owner owner = ownerRepository.findByUser_Id(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
		
		// Get all bookings for this owner's bikes
		java.util.List<BookingDetails> bookings = bookingRepository.findByBikeDetails_Owner(owner);
		
		// Filter for upcoming/active bookings (BOOKED status)
		return bookings.stream()
				.filter(booking -> booking.getBookingStatus() == BookingStatus.BOOKED)
				.map(booking -> {
					AdminBookingDTO dto = new AdminBookingDTO();
					dto.setId(booking.getId());
					dto.setPickupDateTime(booking.getPickupDateTime());
					dto.setDropOffDateTime(booking.getDropOffDateTime());
					dto.setTotalAmount(booking.getTotalAmount());
					dto.setBookingStatus(booking.getBookingStatus());
					dto.setCustomerEmail(booking.getCustomer().getUser().getEmail());
					dto.setCustomerName(booking.getCustomer().getUser().getFirstName() + " " + 
											booking.getCustomer().getUser().getLastName());
					dto.setBikeModel(booking.getBikeDetails().getModel());
					dto.setBikeNumber(booking.getBikeDetails().getNumber());
					return dto;
				})
				.collect(java.util.stream.Collectors.toList());
	}

	@Override
	public void cancelBikeBooking(Long bookingId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long userId = userPrincipal.getUserId();
		
		Owner owner = ownerRepository.findByUser_Id(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
		
		BookingDetails booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
		
		// Security Check: Ensure booking is for owner's bike
		if (!booking.getBikeDetails().getOwner().getId().equals(owner.getId())) {
			throw new ResourceNotFoundException("You do not own the bike for this booking");
		}
		
		// Check if booking can be cancelled
		if (booking.getBookingStatus() != BookingStatus.BOOKED) {
			throw new IllegalStateException("Only BOOKED bookings can be cancelled");
		}
		
		// Update booking status
		booking.setBookingStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);
		
		// Make bike available again
		BikeDetails bike = booking.getBikeDetails();
		bike.setAvailabilityStatus(true);
		bikeDetailsRepository.save(bike);
	}

	@Override
	public java.util.List<AdminTransactionDTO> getMyBikesTransactions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		Long userId = userPrincipal.getUserId();
		
		Owner owner = ownerRepository.findByUser_Id(userId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
		
		// Get all transactions for this owner's bikes
		java.util.List<Transaction> transactions = transactionRepository.findByBookingDetails_BikeDetails_Owner(owner);
		
		return transactions.stream()
				.map(transaction -> {
					AdminTransactionDTO dto = new AdminTransactionDTO();
					dto.setId(transaction.getId());
					dto.setAmount(transaction.getAmount());
					dto.setTransactionType(transaction.getTransactionType());
					dto.setTransactionStatus(transaction.getTransactionStatus());
					dto.setBookingId(transaction.getBookingDetails().getId());
					dto.setGatewayPaymentId(transaction.getGatewayPaymentId());
					return dto;
				})
				.collect(java.util.stream.Collectors.toList());
	}

}
