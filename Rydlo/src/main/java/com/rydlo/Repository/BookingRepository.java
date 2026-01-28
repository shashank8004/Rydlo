package com.rydlo.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rydlo.entities.BookingDetails;

public interface BookingRepository extends JpaRepository<BookingDetails, Long> 
{

	@Query("""
		    SELECT COUNT(b) > 0
		    FROM BookingDetails b
		    WHERE b.bikeDetails.id = :bikeId
		      AND b.bookingStatus = 'BOOKED'
		      AND :pickupDateTime < b.dropOffDateTime
		      AND :dropOffDateTime > b.pickupDateTime
		""")
	
		boolean existsOverlappingBooking(
		        @Param("bikeId") Long bikeId,
		        @Param("pickupDateTime") LocalDateTime pickupDateTime,
		        @Param("dropOffDateTime") LocalDateTime dropOffDateTime
		);
		
		java.util.List<BookingDetails> findByCustomer(com.rydlo.entities.Customer customer);
	
	java.util.List<BookingDetails> findByBikeDetails_Owner(com.rydlo.entities.Owner owner);
}
