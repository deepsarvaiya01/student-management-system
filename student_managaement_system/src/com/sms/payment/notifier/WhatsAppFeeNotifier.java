package com.sms.payment.notifier;

import java.math.BigDecimal;

public class WhatsAppFeeNotifier implements IFeeNotifier {
    @Override
    public void notifyPayment(int studentId, BigDecimal amountPaid) {
        System.out.println("WhatsApp sent: " + studentId + " has paid ₹" + amountPaid);
    }
}
