package com.rydlo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rydlo.entities.BookingDetails;
import com.rydlo.entities.Customer;
import com.rydlo.entities.Owner;

public interface BookingRepository extends JpaRepository<BookingDetails, Long> {

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
			@Param("dropOffDateTime") LocalDateTime dropOffDateTime);

	List<BookingDetails> findByCustomer(Customer customer);

	List<BookingDetails> findByBikeDetails_Owner(Owner owner);
}
