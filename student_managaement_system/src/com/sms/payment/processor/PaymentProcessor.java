package com.sms.payment.processor;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.strategy.*;

public class PaymentProcessor {
    private Scanner scanner = new Scanner(System.in);

    public boolean process(int studentId, BigDecimal amount) {
        System.out.println("Choose payment method:\n1. Cash\n2. Card\n3. UPI");
        int choice = scanner.nextInt();
        scanner.nextLine(); // flush

        PaymentStrategy strategy = switch (choice) {
            case 1 -> new CashPayment();
            case 2 -> new CardPayment();
            case 3 -> new UPIPayment();
            default -> null;
        };

        if (strategy == null) {
            System.out.println("Invalid payment method.");
            return false;
        }

        return strategy.pay(studentId, amount);
    }
}
