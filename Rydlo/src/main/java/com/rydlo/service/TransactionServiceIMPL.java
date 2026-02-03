package com.rydlo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rydlo.client.PaymentClient;

import com.rydlo.custom_exception.ResourceNotFoundException;
import com.rydlo.dto.AdminTransactionDTO;
import com.rydlo.dto.PaymentResponseDTO;
import com.rydlo.entities.BookingDetails;
import com.rydlo.entities.Customer;
import com.rydlo.repository.BookingRepository;
import com.rydlo.repository.CustomerRepository;
import com.rydlo.security.UserPrincipal;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TransactionServiceIMPL implements TransactionService {

	private final BookingRepository bookingRepository;
	private final CustomerRepository customerRepository;
	private final ModelMapper modelMapper;
	private final PaymentClient paymentClient;

	@Override
	public List<AdminTransactionDTO> getMyTransactions() {
		UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Customer customer = customerRepository.findByUser_Id(userPrincipal.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

		List<BookingDetails> bookings = bookingRepository.findByCustomer(customer);
		return fetchTransactionsForBookings(bookings);
	}

	@Override
	public List<AdminTransactionDTO> getAllTransactions() {
		List<BookingDetails> bookings = bookingRepository.findAll();
		return fetchTransactionsForBookings(bookings);
	}
	
	private List<AdminTransactionDTO> fetchTransactionsForBookings(List<BookingDetails> bookings) {
        List<AdminTransactionDTO> allTransactions = new ArrayList<>();
        for (BookingDetails booking : bookings) {
            try {
                List<PaymentResponseDTO> payments = paymentClient.getPaymentsByBooking(booking.getId());
                if (payments != null) {
                    for (PaymentResponseDTO payment : payments) {
                        AdminTransactionDTO dto = modelMapper.map(payment, AdminTransactionDTO.class);
                        dto.setId(payment.getTransactionId());
                        dto.setBookingId(payment.getBookingId());
                        allTransactions.add(dto);
                    }
                }
            } catch (Exception e) {
            	System.err.println("Failed to fetch transactions for booking " + booking.getId() + ": " + e.getMessage());
            }
        }
        return allTransactions;
   }

}
