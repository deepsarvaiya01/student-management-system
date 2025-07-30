package com.sms.payment.strategy;

import java.math.BigDecimal;
import java.util.Scanner;

import com.sms.payment.validator.PaymentValidator;

public class UPIPayment implements PaymentStrategy {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public boolean pay(int studentId, BigDecimal amount) {
        System.out.print("Enter UPI ID: ");
        String upi = scanner.nextLine();
        System.out.print("Enter mobile number: ");
        String mobile = scanner.nextLine();

        if (!PaymentValidator.validateUPI(upi) || !PaymentValidator.validateMobile(mobile)) {
            System.out.println("Invalid UPI or mobile number.");
            return false;
        }

        System.out.println("Processing UPI payment...");
        return true;
    }
}
