package com.sms.payment.notifier;

import java.math.BigDecimal;

public class EmailFeeNotifier implements IFeeNotifier {
    @Override
    public void notifyPayment(int studentId, BigDecimal amountPaid) {
        System.out.println("Email sent: " + studentId + " has paid ₹" + amountPaid);
    }
}
