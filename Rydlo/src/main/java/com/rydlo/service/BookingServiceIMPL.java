package com.rydlo.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rydlo.client.PaymentClient;
import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.AdminBookingDTO;
import com.rydlo.dto.CreateBookingRequestDTO;
import com.rydlo.dto.CreateBookingResponseDTO;
import com.rydlo.dto.DropOffRequestDTO;
import com.rydlo.dto.DropOffResponseDTO;
import com.rydlo.dto.PaymentRequestDTO;
import com.rydlo.dto.PaymentResponseDTO;
import com.rydlo.dto.PaymentVerificationRequestDTO;
import com.rydlo.entities.BikeDetails;
import com.rydlo.entities.BookingDetails;
import com.rydlo.entities.BookingStatus;
import com.rydlo.entities.Customer;
import com.rydlo.entities.TransactionStatus;
import com.rydlo.entities.TransactionType;
import com.rydlo.pricing.PriceCalculationResult;
import com.rydlo.pricing.PricingService;
import com.rydlo.repository.BikeDetailsRepository;
import com.rydlo.repository.BookingRepository;
import com.rydlo.repository.CustomerRepository;
import com.rydlo.security.UserPrincipal;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class BookingServiceIMPL implements BookingService {

	private final BookingRepository bookingRepository;
	private final BikeDetailsRepository bikeRepository;
	private final CustomerRepository customerRepository;
	private final PricingService pricingService;
	private final ModelMapper modelMapper;
	private final PaymentClient paymentClient;

	@Override
	public CreateBookingResponseDTO createBooking(CreateBookingRequestDTO request) {

		// Fetch bike
		BikeDetails bike = bikeRepository.findById(request.getBikeId())
				.orElseThrow(() -> new ResourceNotFoundException("Bike not found"));

		// Fetch customer
		Customer customer = customerRepository.findByUser_Id(request.getCustomerId()).orElseThrow(
				() -> new ResourceNotFoundException("Customer profile not found for User ID: " + request.getCustomerId()));

		// Combine date + time
		LocalDateTime pickup = LocalDateTime.of(request.getPickupDate(), request.getPickupTime());
		LocalDateTime dropOff = LocalDateTime.of(request.getDropOffDate(), request.getDropOffTime());

		// Check for overlap
		boolean overlapping = bookingRepository.existsOverlappingBooking(bike.getId(), pickup, dropOff);

		if (overlapping) {
			throw new IllegalStateException("Bike is not available for the selected time range");
		}

		// PRICE CALCULATION
		PriceCalculationResult price = pricingService.calculate(bike, pickup, dropOff);

		// Save booking as PENDING_PAYMENT
		BookingDetails booking = new BookingDetails();
		booking.setBikeDetails(bike);
		booking.setCustomer(customer);
		booking.setPickupDateTime(pickup);
		booking.setDropOffDateTime(dropOff);
		booking.setInitialUsed(request.getInitialKm());

		booking.setBaseAmount(price.getBaseAmount());
		booking.setTotalAmount(price.getTotalPayable());
		booking.setBookingStatus(BookingStatus.PENDING_PAYMENT);

		booking = bookingRepository.save(booking);

		// Create Payment Order
		PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
		paymentRequest.setBookingId(booking.getId());
		paymentRequest.setAmount(price.getTotalPayable());
		paymentRequest.setTransactionType(TransactionType.BOOKING_PAYMENT);

		try {
			PaymentResponseDTO paymentResponse = paymentClient.createOrder(paymentRequest);
			
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
			
			res.setBookingStatus("PENDING_PAYMENT");
			res.setGatewayOrderId(paymentResponse.getGatewayOrderId());
			res.setRazorpayKeyId(paymentResponse.getRazorpayKeyId());
			
			return res;
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to initiate payment: " + e.getMessage());
		}
	}
	
	@Override
	public CreateBookingResponseDTO confirmBooking(Long bookingId, PaymentVerificationRequestDTO request) {
		BookingDetails booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
		
		if (booking.getBookingStatus() != BookingStatus.PENDING_PAYMENT) {
			throw new IllegalStateException("Booking is not in pending payment state");
		}
		
		try {
			paymentClient.verifyPayment(request);
			
			// If successful
			booking.setBookingStatus(BookingStatus.BOOKED);
			bookingRepository.save(booking);
			
			CreateBookingResponseDTO res = new CreateBookingResponseDTO();
			res.setBookingId(booking.getId());
			res.setBookingStatus("BOOKED");
			res.setTotalPayable(booking.getTotalAmount());
			return res;
			
		} catch (Exception e) {
			throw new RuntimeException("Payment verification failed: " + e.getMessage());
		}
	}

	@Override
	public DropOffResponseDTO completeRide(Long bookingId, DropOffRequestDTO request) {

		BookingDetails booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		if (booking.getBookingStatus() != BookingStatus.BOOKED
				&& booking.getBookingStatus() != BookingStatus.ONGOING) {

			throw new IllegalStateException("Booking is not eligible for drop-off");
		}

		int initialKm = booking.getInitialUsed();
		int finalKm = request.getFinalKm();

		if (finalKm < initialKm) {
			throw new IllegalArgumentException("Final km cannot be less than initial km");
		}

		// Calculate distance
		int travelledKm = finalKm - initialKm;

		// Calculate included km
		int daysCharged = (int) Math.ceil(
				Duration.between(booking.getPickupDateTime(), booking.getDropOffDateTime()).toHours() / 24.0);

		int includedKm = daysCharged * 100;

		// Extra km calculation
		int extraKm = Math.max(0, travelledKm - includedKm);

		double extraKmCharge = extraKm * booking.getBikeDetails().getRentPerKm();

		// Final amount update
		double finalAmount = booking.getTotalAmount() + extraKmCharge;

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
			PaymentRequestDTO paymentRequest = new PaymentRequestDTO();
			paymentRequest.setBookingId(booking.getId());
			paymentRequest.setAmount(extraKmCharge);
			paymentRequest.setTransactionType(TransactionType.EXTRA_KM_PAYMENT);

			try {
				
				paymentClient.createOrder(paymentRequest); 
			} catch (Exception e) {
				System.err.println("Payment processing failed for extra km: " + e.getMessage());
				
			}
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
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Customer customer = customerRepository.findByUser_Id(userPrincipal.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

		return bookingRepository.findByCustomer(customer).stream().map(booking -> {
			AdminBookingDTO dto = modelMapper.map(booking, AdminBookingDTO.class);
			booking.getBikeDetails(); // Load bike details
			if (booking.getBikeDetails() != null) {
				dto.setBikeModel(booking.getBikeDetails().getModel());
				dto.setBikeNumber(booking.getBikeDetails().getNumber());
			}
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public void cancelBooking(Long bookingId) {
		// Get current user
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Customer customer = customerRepository.findByUser_Id(userPrincipal.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

		BookingDetails booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		// Verify ownership
		if (!booking.getCustomer().getId().equals(customer.getId())) {
			throw new IllegalStateException("You can only cancel your own bookings");
		}

		if (booking.getBookingStatus() != BookingStatus.BOOKED && booking.getBookingStatus() != BookingStatus.PENDING_PAYMENT) {
			throw new IllegalStateException("Only BOOKED or PENDING rides can be cancelled");
		}

		LocalDateTime now = LocalDateTime.now();
		if (booking.getBookingStatus() == BookingStatus.BOOKED) {
			long hoursUntilPickup = Duration.between(now, booking.getPickupDateTime()).toHours();
			if (hoursUntilPickup < 24) {
				throw new IllegalStateException(
						"Cancellation is only allowed at least 24 hours before pickup time. Please contact support.");
			}
		}

		booking.setBookingStatus(BookingStatus.CANCELLED);

		bookingRepository.save(booking);
	}

}
