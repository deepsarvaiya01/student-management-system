package com.sms.payment.processor;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.strategy.CardPayment;
import com.sms.payment.strategy.CashPayment;
import com.sms.payment.strategy.PaymentStrategy;
import com.sms.payment.strategy.UPIPayment;

public class PaymentProcessor {

	public boolean process(int studentId, BigDecimal amount, int choice, Scanner scanner) {
		PaymentStrategy strategy;
		switch (choice) {
		case 1:
			strategy = new CashPayment();
			break;
		case 2:
			strategy = new CardPayment();
			break;
		case 3:
			strategy = new UPIPayment();
			break;
		default:
			System.out.println("❌ Invalid payment method choice.");
			return false;
		}

		return strategy.pay(studentId, amount, scanner);
	}
}