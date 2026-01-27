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
import com.rydlo.repository.OwnerRepository;
import com.rydlo.repository.PickupLocationRepository;
import com.rydlo.repository.UserRepository;
import com.rydlo.security.UserPrincipal;

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

}
