package com.rydlo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.AvailableBikeRequestDTO;
import com.rydlo.dto.BikeDetailsResDTO;
import com.rydlo.dto.BikeResDTO;
import com.rydlo.dto.PriceRequestDTO;
import com.rydlo.dto.PriceResponseDTO;
import com.rydlo.entities.BikeDetails;
import com.rydlo.entities.BikeType;
import com.rydlo.entities.PickupLocation;
import com.rydlo.pricing.PriceCalculationResult;
import com.rydlo.pricing.PricingService;
import com.rydlo.repository.BikeDetailsRepository;
import com.rydlo.repository.BookingRepository;
import com.rydlo.repository.PickupLocationRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class BikeServiceIMPL implements BikeService {
	
	@Autowired
	private final ModelMapper modelMapper;
	private final BikeDetailsRepository bikeDetailsRepository;
	private final PickupLocationRepository pickupLocationRepository;
	private final PricingService pricingService;
	private final BookingRepository bookingRepository;


	@Override
	public List<BikeResDTO> getBikesByCityAndType(String cityName, String bikeType) {
		
		List<BikeDetails> bikes = bikeDetailsRepository.findByPickupLocationCityAndBikeType(cityName,BikeType.valueOf(bikeType.toUpperCase()));
		
		List<BikeResDTO> bikeResDTOs = bikes.stream().map(bike ->modelMapper.map(bike, BikeResDTO.class))
		        .collect(Collectors.toList());
			
		return bikeResDTOs;
	}


	@Override
	public List<BikeResDTO> getBikesByPinCode(String query) {
		
		List<BikeDetails> bikes = bikeDetailsRepository.findByPickupLocationPincode(query);
		List<BikeResDTO> bikeResDTOs = bikes.stream().map(bike ->modelMapper.map(bike, BikeResDTO.class))
		        .collect(Collectors.toList());		
		return bikeResDTOs;
	}


	@Override
	public List<BikeResDTO> getBikesByLocation(String query) {
	
					 List<BikeDetails> bikes = bikeDetailsRepository.findByPickupLocationCityContainingIgnoreCaseOrPickupLocationLocalityContainingIgnoreCase(
						      query,
						       query
						);
			List<BikeResDTO> bikeResDTOs = bikes.stream().map(bike ->modelMapper.map(bike, BikeResDTO.class))
			        .collect(Collectors.toList());		
			return bikeResDTOs;
		
	}


	@Override
	public List<BikeResDTO> getAvailableBikes(@Valid AvailableBikeRequestDTO request)
	{
		  // Combine date + time
        LocalDateTime pickupDateTime =
                LocalDateTime.of(
                        request.getPickupDate(),
                        request.getPickupTime()
                );

        LocalDateTime dropOffDateTime =
                LocalDateTime.of(
                        request.getDropOffDate(),
                        request.getDropOffTime()
                );

        // Validate time range
        if (!dropOffDateTime.isAfter(pickupDateTime)) {
            throw new IllegalArgumentException(
                    "Drop-off date & time must be after pickup date & time"
            );
        }
        
		// Fetch bikes based on location query
        List<BikeDetails> bikes;
        if (request.getLocationQuery().matches("\\d{6}")) {
            bikes = bikeDetailsRepository.findByPickupLocationPincode(request.getLocationQuery());
        } else {
            bikes = bikeDetailsRepository.findByPickupLocationCityContainingIgnoreCaseOrPickupLocationLocalityContainingIgnoreCase(
                    request.getLocationQuery(), request.getLocationQuery());
        }
		
		
		//Filter only AVAILABLE bikes (NO OVERLAP)
        List<BikeDetails> availableBikes = bikes.stream()
                .filter(bike ->
                        !bookingRepository.existsOverlappingBooking(
                                bike.getId(),
                                pickupDateTime,
                                dropOffDateTime
                        )
                )
                .toList();
		
        // Convert to DTOs
        List<BikeResDTO> bikeResDTOs = availableBikes.stream()
				.map(bike -> modelMapper.map(bike, BikeResDTO.class))
				.collect(Collectors.toList());
		return bikeResDTOs;
	}


	@Override
	public BikeDetailsResDTO getBikeDetails(Long bikeId) {
		
		BikeDetails bikeDetails = bikeDetailsRepository.findById(bikeId).orElseThrow(() -> new ResourceNotFoundException("Bike not found with id: " + bikeId));
		PickupLocation location = bikeDetails.getPickupLocation();
		BikeDetailsResDTO bikeDetailsResDTO = modelMapper.map(bikeDetails, BikeDetailsResDTO.class);
		
		// Set pickup location details
		
		bikeDetailsResDTO.setStreet(location.getStreet());
		bikeDetailsResDTO.setLocality(location.getLocality());
		bikeDetailsResDTO.setCity(location.getCity());
		bikeDetailsResDTO.setState(location.getState());
		bikeDetailsResDTO.setPincode(location.getPincode());
		
		// Set owner company details
		
		bikeDetailsResDTO.setOwnerCompanyName(
	            bikeDetails.getOwner().getCompanyName()
			    );
		
		return bikeDetailsResDTO;
	}


	@Override
	public PriceResponseDTO calculatePrice(Long bikeId, @Valid PriceRequestDTO request) {
		
		
		BikeDetails bike = bikeDetailsRepository.findById(bikeId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Bike not found"));
		
		 LocalDateTime pickup =
		            LocalDateTime.of(
		                    request.getPickupDate(),
		                    request.getPickupTime()
		            );

		    LocalDateTime dropOff =
		            LocalDateTime.of(
		                    request.getDropOffDate(),
		                    request.getDropOffTime()
		            );

	    PriceCalculationResult price= pricingService.calculate(bike, pickup, dropOff);
		
		// Response
		PriceResponseDTO res =new PriceResponseDTO();
		
		
		res.setDurationHours(price.getDurationHours());
	    res.setDaysCharged(price.getDaysCharged());
	    res.setIncludedKm(price.getIncludedKm());
	    res.setBaseAmount(price.getBaseAmount());
	    res.setCgst(price.getCgst());
	    res.setSgst(price.getSgst());
	    res.setTotalPayable(price.getTotalPayable());

		res.setNote("Extra kilometers will be charged at drop-off");

		return res;

		
	}
	
	
}
