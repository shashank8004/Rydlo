package com.rydlo.pricing;

import lombok.Getter;

@Getter
public class PriceCalculationResult {

	private long durationHours;
	private int daysCharged;
	private int includedKm;

	private double baseAmount;
	private double cgst;
	private double sgst;
	private double totalPayable;

	public PriceCalculationResult(long durationHours, int daysCharged, int includedKm, double baseAmount, double cgst,
			double sgst, double totalPayable) {

		this.durationHours = durationHours;
		this.daysCharged = daysCharged;
		this.includedKm = includedKm;
		this.baseAmount = baseAmount;
		this.cgst = cgst;
		this.sgst = sgst;
		this.totalPayable = totalPayable;
	}

}
