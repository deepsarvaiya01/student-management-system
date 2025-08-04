package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.validator.PaymentValidator;
import com.sms.utils.InputValidator;

public class CardPayment implements PaymentStrategy {

	@Override
	public boolean pay(int studentId, BigDecimal amount, Scanner scanner) {
		try {
			scanner.nextLine();
			PaymentValidator.getValidCardNumber(scanner, "Enter card number: ");
			PaymentValidator.getValidExpiry(scanner, "Enter expiry (MM/YY): ");
			PaymentValidator.getValidCVV(scanner, "Enter CVV: ");
			InputValidator.getValidName(scanner, "Enter cardholder name: ");

			System.out.println("Processing card payment for Student ID " + studentId + " of ₹" + amount + "...");
			return true;
		} catch (Exception e) {
			System.out.println("❌ Card payment failed: " + e.getMessage());
			return false;
		}
	}
}