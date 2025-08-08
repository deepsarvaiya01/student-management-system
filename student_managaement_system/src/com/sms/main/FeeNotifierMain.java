package com.sms.main;

import com.sms.controller.FeeNotifierController;
import com.sms.model.FeeNotifier;
import com.sms.utils.InputValidator;

import java.util.Scanner;

public class FeeNotifierMain {
    public static void managePreferences() {
        FeeNotifierController controller = new FeeNotifierController();
        Scanner scanner = new Scanner(System.in);

        controller.showStudents();
        int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID: ", "Student ID");

        FeeNotifier prefs = controller.getOrCreatePreferences(studentId);
        if (prefs == null) {
            System.out.println("Could not retrieve preferences.");
            return;
        }

        while (true) {
            System.out.println("\n╔══════════════════════════════════════════════════╗");
            System.out.printf ("║  NOTIFIER PREFERENCES FOR STUDENT ID %-10s  ║\n", studentId);
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.printf ("║ SMS Notifier       : %-4s                        ║\n", prefs.isSmsEnabled() ? "ON" : "OFF");
            System.out.printf ("║ Email Notifier     : %-4s                        ║\n", prefs.isEmailEnabled() ? "ON" : "OFF");
            System.out.printf ("║ WhatsApp Notifier  : %-4s                        ║\n", prefs.isWhatsappEnabled() ? "ON" : "OFF");
            System.out.println("╚══════════════════════════════════════════════════╝");

            System.out.println("\n╔══════════════════════════════════════════════════╗");
            System.out.println("║             MANAGE PREFERENCES                   ║");
            System.out.println("╠══════════════════════════════════════════════════╣");
            System.out.println("║ 1. Toggle SMS Notifier                           ║");
            System.out.println("║ 2. Toggle Email Notifier                         ║");
            System.out.println("║ 3. Toggle WhatsApp Notifier                      ║");
            System.out.println("║ 0. Back                                          ║");
            System.out.println("╚══════════════════════════════════════════════════╝");

            int choice = InputValidator.getValidMenuChoice(scanner, "Enter choice (0-3):", 3);
            switch (choice) {
                case 1 -> prefs.setSmsEnabled(!prefs.isSmsEnabled());
                case 2 -> prefs.setEmailEnabled(!prefs.isEmailEnabled());
                case 3 -> prefs.setWhatsappEnabled(!prefs.isWhatsappEnabled());
                case 0 -> { return; }
                default -> System.out.println("Invalid option.");
            }

            boolean updated = controller.updatePreferences(prefs);
            System.out.println(updated ? "Preferences updated." : "Update failed.");
        }
    }
}
