package com.rydlo.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.CreateBookingRequestDTO;
import com.rydlo.dto.CreateBookingResponseDTO;
import com.rydlo.dto.DropOffRequestDTO;
import com.rydlo.dto.DropOffResponseDTO;
import com.rydlo.entities.BikeDetails;
import com.rydlo.entities.BookingDetails;
import com.rydlo.entities.BookingStatus;
import com.rydlo.entities.Customer;
import com.rydlo.entities.Transaction;
import com.rydlo.entities.TransactionStatus;
import com.rydlo.pricing.PriceCalculationResult;
import com.rydlo.pricing.PricingService;
import com.rydlo.repository.BikeDetailsRepository;
import com.rydlo.repository.BookingRepository;
import com.rydlo.repository.CustomerRepository;
import com.rydlo.repository.TransactionRepository;
import java.util.stream.Collectors;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import com.rydlo.security.UserPrincipal;
import com.rydlo.dto.AdminBookingDTO;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class BookingServiceIMPL implements BookingService {
	
	    @Autowired
	    private final BookingRepository bookingRepository;
	    private final BikeDetailsRepository bikeRepository;
	    private final CustomerRepository customerRepository;
	    private final TransactionRepository transactionRepository;
	    private final PricingService pricingService;
	    private final ModelMapper modelMapper;
	
	
	    @Override
	    public CreateBookingResponseDTO createBooking(
	            CreateBookingRequestDTO request) {

	        // Fetch bike
	        BikeDetails bike = bikeRepository.findById(request.getBikeId())
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Bike not found"));

	        // Fetch customer
	        // Fetch customer using User ID (passed as customerId)
	        Customer customer = customerRepository.findByUser_Id(request.getCustomerId())
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Customer profile not found for User ID: " + request.getCustomerId()));

	        // Combine date + time
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

	      

	        //Re-check overlap (MANDATORY)
	        boolean overlapping =
	                bookingRepository.existsOverlappingBooking(
	                        bike.getId(),
	                        pickup,
	                        dropOff
	                );

	        if (overlapping) {
	            throw new IllegalStateException(
	                    "Bike is not available for the selected time range"
	            );
	        }

	        // PRICE CALCULATION (SINGLE SOURCE OF TRUTH)
	        PriceCalculationResult price =
	                pricingService.calculate(bike, pickup, dropOff);

	        // Save booking
	        BookingDetails booking = new BookingDetails();
	        booking.setBikeDetails(bike);
	        booking.setCustomer(customer);
	        booking.setPickupDateTime(pickup);
	        booking.setDropOffDateTime(dropOff);
	        booking.setInitialUsed(request.getInitialKm());

	        booking.setBaseAmount(price.getBaseAmount());
	        booking.setTotalAmount(price.getTotalPayable());
	        booking.setBookingStatus(BookingStatus.BOOKED);

	        bookingRepository.save(booking);

	        // Response
	        CreateBookingResponseDTO res = new CreateBookingResponseDTO();
	        res.setBookingId(booking.getId());
	        res.setPickupDateTime(pickup);
	        res.setDropOffDateTime(dropOff);

	        res.setDurationHours(price.getDurationHours());
	        res.setDaysCharged(price.getDaysCharged());
	        res.setIncludedKm(price.getIncludedKm());

	        res.setBaseAmount(price.getBaseAmount());
	        res.setCgst(price.getCgst());
	        res.setSgst(price.getSgst());
	        res.setTotalPayable(price.getTotalPayable());

	        res.setBookingStatus("BOOKED");

	        return res;
	    }


	    @Override
	    public DropOffResponseDTO completeRide(
	            Long bookingId,
	            DropOffRequestDTO request) {

	        BookingDetails booking = bookingRepository.findById(bookingId)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Booking not found"));

	        if (booking.getBookingStatus() != BookingStatus.BOOKED
	                && booking.getBookingStatus() != BookingStatus.ONGOING) {

	            throw new IllegalStateException(
	                    "Booking is not eligible for drop-off");
	        }

	        int initialKm = booking.getInitialUsed();
	        int finalKm = request.getFinalKm();

	        if (finalKm < initialKm) {
	            throw new IllegalArgumentException(
	                    "Final km cannot be less than initial km");
	        }

	        // Calculate distance
	        int travelledKm = finalKm - initialKm;

	        // Calculate included km
	        int daysCharged =
	                (int) Math.ceil(
	                        Duration.between(
	                                booking.getPickupDateTime(),
	                                booking.getDropOffDateTime()
	                        ).toHours() / 24.0
	                );

	        int includedKm = daysCharged * 100;

	        //  Extra km calculation
	        int extraKm =
	                Math.max(0, travelledKm - includedKm);

	        double extraKmCharge =
	                extraKm * booking.getBikeDetails().getRentPerKm();

	        // Final amount update
	        double finalAmount =
	                booking.getTotalAmount() + extraKmCharge;

	        booking.setFinalUsed(finalKm);
	        booking.setFinalAmount(finalAmount);
	        booking.setBookingStatus(BookingStatus.COMPLETED);

	        bookingRepository.save(booking);

	        // Update bike 
	        BikeDetails bike = booking.getBikeDetails();
	        bike.setUsedKm(finalKm);
	        bikeRepository.save(bike);

	        // Create transaction for extra km
	        if (extraKmCharge > 0) {
	            Transaction txn = new Transaction();
	            txn.setAmount(extraKmCharge);
	            txn.setTransactionStatus(TransactionStatus.SUCCESSFUL);
	            txn.setBookingDetails(booking);
	            transactionRepository.save(txn);
	        }

	        // Response
	        DropOffResponseDTO res = new DropOffResponseDTO();
	        res.setBookingId(booking.getId());
	        res.setInitialKm(initialKm);
	        res.setFinalKm(finalKm);
	        res.setIncludedKm(includedKm);
	        res.setExtraKm(extraKm);
	        res.setExtraKmCharge(extraKmCharge);
	        res.setFinalAmount(finalAmount);
	        res.setBookingStatus("COMPLETED");

	        return res;
	    }

	    @Override
	    public List<AdminBookingDTO> getMyBookings() {
	        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        Customer customer = customerRepository.findByUser_Id(userPrincipal.getUserId())
	                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

	        return bookingRepository.findByCustomer(customer).stream()
	                .map(booking -> {
	                    AdminBookingDTO dto = modelMapper.map(booking, AdminBookingDTO.class);
	                    booking.getBikeDetails(); // Load bike details
	                    if (booking.getBikeDetails() != null) {
	                        dto.setBikeModel(booking.getBikeDetails().getModel());
	                        dto.setBikeNumber(booking.getBikeDetails().getNumber());
	                    }
	                    return dto;
	                })
	                .collect(Collectors.toList());
	    }

}
