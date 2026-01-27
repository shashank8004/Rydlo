package com.rydlo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rydlo.entities.BikeDetails;
import com.rydlo.entities.BikeType;

public interface BikeDetailsRepository extends JpaRepository<BikeDetails, Long> {

	boolean existsByNumber(String number);
    List<BikeDetails> findByOwner(com.rydlo.entities.Owner owner);

	
	
	List<BikeDetails> findByPickupLocationCityAndBikeType(String city,BikeType bikeType );

	List<BikeDetails> findByPickupLocationCityContainingIgnoreCaseOrPickupLocationLocalityContainingIgnoreCase(
			String city, String locality);

	List<BikeDetails> findByPickupLocationPincode(String query);



	List<BikeDetails> findByPickupLocationIdAndAvailabilityStatusTrue(Long pickupLocationId);

 
	

}
