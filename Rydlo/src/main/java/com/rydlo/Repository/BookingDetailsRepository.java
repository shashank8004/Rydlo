package com.rydlo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rydlo.entities.BookingDetails;

public interface BookingDetailsRepository extends JpaRepository<BookingDetails, Long> {
}
