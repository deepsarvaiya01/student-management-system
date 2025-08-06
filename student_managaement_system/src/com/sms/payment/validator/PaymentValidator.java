package com.sms.payment.validator;

import java.util.Scanner;

import com.sms.exception.AppException;
import com.sms.utils.InputValidator;

public class PaymentValidator {

    public static String getValidCardNumber(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String card = scanner.nextLine().trim();
                if (!card.matches("\\d{16}")) {
                    throw new AppException("❌ Invalid card number! Must be exactly 16 digits.\nPlease try again:");
                }
                return card;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String getValidCVV(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String cvv = scanner.nextLine().trim();
                if (!cvv.matches("\\d{3,4}")) {
                    throw new AppException("❌ Invalid CVV! Must be 3 or 4 digits.\nPlease try again:");
                }
                return cvv;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static String getValidExpiry(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String expiry = scanner.nextLine().trim();
                if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    throw new AppException("❌ Invalid expiry date! Must be in MM/YY format (e.g., 12/25).\nPlease try again:");
                }
                // Basic expiry validation: check if date is not in the past
                String[] parts = expiry.split("/");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt(parts[1]) + 2000; // Assume 20XX
                int currentYear = java.time.Year.now().getValue();
                int currentMonth = java.time.MonthDay.now().getMonthValue();
                if (year < currentYear || (year == currentYear && month < currentMonth)) {
                    throw new AppException("❌ Card has expired! Please use a valid card.\nPlease try again:");
                }
                return expiry;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid expiry format! Must be MM/YY.\nPlease try again:");
            }
        }
    }

    public static String getValidUPI(Scanner scanner, String prompt) {
        while (true) {
            try {
            	scanner.nextLine();
                System.out.print(prompt);
                String upi = scanner.nextLine().trim();
                if (!upi.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+")) {
                    throw new AppException("❌ Invalid UPI ID! Must be in format user@provider (e.g., user@upi).\nPlease try again:");
                }
                return upi;
            } catch (AppException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Reuse InputValidator's mobile validation for consistency
    public static String getValidMobile(Scanner scanner, String prompt) {
        return InputValidator.getValidMobileOnly(scanner, prompt);
    }
}