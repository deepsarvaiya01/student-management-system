package com.sms.payment.processor;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.strategy.CardPayment;
import com.sms.payment.strategy.CashPayment;
import com.sms.payment.strategy.PaymentStrategy;
import com.sms.payment.strategy.UPIPayment;

public class PaymentProcessor {

	public boolean process(int studentId, BigDecimal amount, String method, Scanner scanner) {
		PaymentStrategy strategy;
		switch (method.toLowerCase()) {
		case "cash":
			strategy = new CashPayment();
			break;
		case "card":
			strategy = new CardPayment();
			break;
		case "upi":
			strategy = new UPIPayment();
			break;
		default:
			System.out.println("❌ Invalid payment method.");
			return false;
		}

		return strategy.pay(studentId, amount, scanner);
	}
}