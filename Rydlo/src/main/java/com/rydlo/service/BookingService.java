package com.rydlo.service;

import java.util.List;

import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.CreateBookingRequestDTO;
import com.rydlo.dto.CreateBookingResponseDTO;
import com.rydlo.dto.DropOffRequestDTO;
import com.rydlo.dto.DropOffResponseDTO;
import com.rydlo.dto.PaymentVerificationRequestDTO;

public interface BookingService {

    CreateBookingResponseDTO createBooking(CreateBookingRequestDTO request);
    
    DropOffResponseDTO completeRide(Long bookingId, DropOffRequestDTO request);
    
    List<AdminBookingDTO> getMyBookings();
    
    void cancelBooking(Long bookingId);

    CreateBookingResponseDTO confirmBooking(Long bookingId, PaymentVerificationRequestDTO request);
}
