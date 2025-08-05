// 📁 DashboardMain.java
package com.sms.main;

import java.util.Scanner;
import com.sms.controller.DashboardController;

public class DashboardMain {
    public static void show() {
        Scanner scanner = new Scanner(System.in);
        DashboardController controller = new DashboardController();
        int choice;
        do {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║        DASHBOARD OPTIONS                 ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ 1. General Student Dashboard             ║");
            System.out.println("║ 2. Course-wise Dashboard                 ║");
            System.out.println("║ 3. Subject Management Dashboard          ║");
            System.out.println("║ 4. Teacher Load Dashboard                ║");
            System.out.println("║ 0. Back to Main Menu                     ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("👉 Enter your choice: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> controller.showGeneralDashboard();
                case 2 -> controller.showCourseDashboard();
                case 3 -> controller.showSubjectDashboard();
                case 4 -> controller.showTeacherDashboard();
                case 0 -> System.out.println("\n🔚 Returning to Main Menu...");
                default -> System.out.println("⚠ Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }
}