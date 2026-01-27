package com.rydlo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rydlo.dto.AvailableBikeRequestDTO;
import com.rydlo.dto.BikeDetailsResDTO;
import com.rydlo.dto.BikeResDTO;
import com.rydlo.dto.PriceRequestDTO;
import com.rydlo.dto.PriceResponseDTO;
import com.rydlo.service.BikeService;
import com.rydlo.service.BookingService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bikes")
@AllArgsConstructor
public class BikeController {
	
	@Autowired
	private final BikeService bikeService;
	private final BookingService bookingService;
	
	//Get Bikes by city or pincode or locality
	
	@GetMapping("/search")
	public ResponseEntity<List<BikeResDTO>> searchBikes(@RequestParam String query)
	{ 
		List<BikeResDTO> bikes=null;
	   
		 if (query.matches("\\d{6}")) {
		       
		      bikes=bikeService.getBikesByPinCode(query);
		    }
		 else {
			 bikes = bikeService.getBikesByLocation(query);
		 }
	      
	    return ResponseEntity.ok(bikes);
	}
	
	//Get Bikes by city name and bike type
	
	@GetMapping("/city-and-type")
	public ResponseEntity<List<BikeResDTO>> getBikesByCityAndType(@RequestParam String city, @RequestParam String bikeType) {
	    List<BikeResDTO> bikes = bikeService.getBikesByCityAndType(city, bikeType);
	    return ResponseEntity.ok(bikes);
	}
	
	
	@PostMapping("/available")
	public ResponseEntity<List<BikeResDTO>> getAvailableBikes(@RequestBody @Valid AvailableBikeRequestDTO request) 
	{
	    List<BikeResDTO> bikes = bikeService.getAvailableBikes(request);
	    return ResponseEntity.ok(bikes);
	}
	
	@GetMapping("/{bikeId}")
    public ResponseEntity<BikeDetailsResDTO> getBikeDetails(
            @PathVariable Long bikeId) {
		
		BikeDetailsResDTO bikeDetails = bikeService.getBikeDetails(bikeId);

        return ResponseEntity.ok(bikeDetails
                
        );
    }
	
	
	@PostMapping("/{bikeId}/price-preview")
    public ResponseEntity<PriceResponseDTO> previewPrice(@PathVariable Long bikeId,@Valid @RequestBody PriceRequestDTO request)
	{

		PriceResponseDTO priceResponse = bikeService.calculatePrice(bikeId, request);
        return ResponseEntity.ok(priceResponse);
    }
	

}
