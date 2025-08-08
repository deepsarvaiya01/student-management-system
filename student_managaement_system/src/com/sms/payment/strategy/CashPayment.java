package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.utils.InputValidator;

public class CashPayment implements PaymentStrategy {

	@Override
	public boolean pay(int studentId, BigDecimal amount, Scanner scanner) {
		try {
			BigDecimal received = InputValidator.getValidDecimal(scanner, "Enter received amount: ₹",
					"Received Amount");
			if (received.compareTo(amount) < 0) {
				System.out.println("Insufficient amount. Received ₹" + received + ", required ₹" + amount + ".");
				return false;
			}
			BigDecimal change = received.subtract(amount);
			if (change.compareTo(BigDecimal.ZERO) > 0) {
				System.out.println("Return change: ₹" + change);
			}
			System.out.println("Processing cash payment for Student ID " + studentId + " of ₹" + amount + "...");
			return true;
		} catch (Exception e) {
			System.out.println("Cash payment failed: " + e.getMessage());
			return false;
		}
	}
}