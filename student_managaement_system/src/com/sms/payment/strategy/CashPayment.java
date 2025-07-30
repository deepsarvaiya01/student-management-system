package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

public class CashPayment implements PaymentStrategy {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public boolean pay(int studentId, BigDecimal amount) {
        System.out.print("Enter received amount: ₹");
        BigDecimal received = scanner.nextBigDecimal();
        if (received.compareTo(amount) < 0) {
            System.out.println("Insufficient amount.");
            return false;
        }
        BigDecimal change = received.subtract(amount);
        if (change.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("Return change: ₹" + change);
        }
        return true;
    }
}
