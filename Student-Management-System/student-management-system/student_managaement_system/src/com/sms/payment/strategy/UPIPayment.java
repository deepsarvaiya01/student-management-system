package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.validator.PaymentValidator;

public class UPIPayment implements PaymentStrategy {

	@Override
	public boolean pay(int studentId, BigDecimal amount, Scanner scanner) {
		try {
			scanner.nextLine();
			PaymentValidator.getValidUPI(scanner, "Enter UPI ID: ");
			PaymentValidator.getValidMobile(scanner, "Enter mobile number: ");

			System.out.println("Processing UPI payment for Student ID " + studentId + " of ₹" + amount + "...");
			return true;
		} catch (Exception e) {
			System.out.println("❌ UPI payment failed: " + e.getMessage());
			return false;
		}
	}
}