
package com.rydlo.pricing;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.rydlo.entities.BikeDetails;

@Service
public class PricingService {

	private static final double CGST_RATE = 0.09;
	private static final double SGST_RATE = 0.09;
	private static final int FREE_KM_PER_DAY = 100;

	public PriceCalculationResult calculate(
	        BikeDetails bike,
	        LocalDateTime pickup,
	        LocalDateTime dropOff
	)

		
	{
		    long durationMinutes =
		            Duration.between(pickup, dropOff).toMinutes();

		    long durationHours =
		            (long) Math.ceil(durationMinutes / 60.0);


		int daysCharged = (int) Math.ceil(durationHours / 24.0);

		double baseAmount = daysCharged * bike.getRentPerDay();

		int includedKm = daysCharged * FREE_KM_PER_DAY;

		double cgst = baseAmount * CGST_RATE;
		double sgst = baseAmount * SGST_RATE;

		double totalPayable = baseAmount + cgst + sgst;

		return new PriceCalculationResult(durationHours, daysCharged, includedKm, baseAmount, cgst, sgst, totalPayable);
	}
}
