package com.sms.payment.notifier;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FeeAlert {
    private List<IFeeNotifier> notifiers = new ArrayList<>();

    public void registerNotifier(IFeeNotifier notifier) {
        notifiers.add(notifier);
    }

    public void removeNotifier(Class<?> clazz) {
        notifiers.removeIf(n -> n.getClass() == clazz);
    }

    public void notifyAll(int studentId, BigDecimal amountPaid) {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║              Sending Payment Notifications           ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");

        for (IFeeNotifier notifier : notifiers) {
            System.out.print("║ ");
            notifier.notifyPayment(studentId, amountPaid);
        }

        System.out.println("╚══════════════════════════════════════════════════════╝");
    }
}
