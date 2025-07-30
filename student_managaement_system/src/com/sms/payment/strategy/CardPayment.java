package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.validator.PaymentValidator;

public class CardPayment implements PaymentStrategy {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public boolean pay(int studentId, BigDecimal amount) {
        System.out.print("Enter card number: ");
        String card = scanner.nextLine();
        System.out.print("Enter expiry (MM/YY): ");
        String expiry = scanner.nextLine();
        System.out.print("Enter CVV: ");
        String cvv = scanner.nextLine();
        System.out.print("Enter cardholder name: ");
        String name = scanner.nextLine();

        if (!PaymentValidator.validateCard(card) || !PaymentValidator.validateCVV(cvv) || !PaymentValidator.validateExpiry(expiry)) {
            System.out.println("Invalid card details.");
            return false;
        }

        System.out.println("Processing card payment...");
        return true;
    }
}
