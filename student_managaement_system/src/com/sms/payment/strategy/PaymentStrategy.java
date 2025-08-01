package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

public interface PaymentStrategy {
	boolean pay(int studentId, BigDecimal amount, Scanner scanner);
}