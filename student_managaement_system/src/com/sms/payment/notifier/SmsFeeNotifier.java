package com.sms.payment.notifier;

import java.math.BigDecimal;

public class SmsFeeNotifier implements IFeeNotifier {
    @Override
    public void notifyPayment(int studentId, BigDecimal amountPaid) {
        System.out.println("📲 SMS sent: " + studentId + " has paid ₹" + amountPaid);
    }
}