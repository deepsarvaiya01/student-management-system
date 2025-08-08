package com.sms.payment.notifier;

import java.math.BigDecimal;

public interface IFeeNotifier {

	void notifyPayment(int studentId, BigDecimal amountPaid);
}
