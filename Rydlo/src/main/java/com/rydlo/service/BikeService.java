package com.rydlo.service;

import java.util.List;

import com.rydlo.dto.AvailableBikeRequestDTO;
import com.rydlo.dto.BikeDetailsResDTO;
import com.rydlo.dto.BikeResDTO;
import com.rydlo.dto.PriceRequestDTO;
import com.rydlo.dto.PriceResponseDTO;

import jakarta.validation.Valid;

public interface BikeService {

	
	List<BikeResDTO> getBikesByCityAndType(String cityName, String bikeType);
	List<BikeResDTO> getBikesByPinCode(String query);
	List<BikeResDTO> getBikesByLocation(String query);
	List<BikeResDTO> getAvailableBikes(@Valid AvailableBikeRequestDTO request);
	BikeDetailsResDTO getBikeDetails(Long bikeId);
	PriceResponseDTO calculatePrice(Long bikeId, @Valid PriceRequestDTO request);

}
