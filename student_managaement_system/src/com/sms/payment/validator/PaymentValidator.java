package com.sms.payment.validator;

public class PaymentValidator {
    public static boolean validateCard(String card) {
        return card != null && card.matches("\\d{16}");
    }

    public static boolean validateExpiry(String expiry) {
        return expiry != null && expiry.matches("\\d{2}/\\d{2}");
    }

    public static boolean validateCVV(String cvv) {
        return cvv != null && cvv.matches("\\d{3}");
    }

    public static boolean validateUPI(String upi) {
        return upi != null && upi.matches("[\\w.-]+@[a-zA-Z]+");
    }

    public static boolean validateMobile(String mobile) {
        return mobile != null && mobile.matches("\\d{10}");
    }
}
