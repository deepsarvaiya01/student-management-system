package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.FeeController;

public class FeeMain {

    public void show() throws SQLException {
        FeeController controller = new FeeController();
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
        	System.out.println("\n╔══════════════════════════════════════════╗");
        	System.out.println("║          FEES MANAGEMENT MENU            ║");
        	System.out.println("╠══════════════════════════════════════════╣");
        	System.out.println("║ 1. View Total Paid Fees                  ║");
        	System.out.println("║ 2. View Total Pending Fees               ║");
        	System.out.println("║ 3. View Fees By Student                  ║");
        	System.out.println("║ 4. View Fees By Course                   ║");
        	System.out.println("║ 5. Update Fees Of A Course               ║");
        	System.out.println("║ 6. Total Earning                         ║");
        	System.out.println("║ 0. Exit                                  ║");
        	System.out.println("╚══════════════════════════════════════════╝");
        	System.out.print("👉 Enter your choice (0-6): ");


            if (!scanner.hasNextInt()) {
                System.out.println("❗ Please enter a valid number.");
                scanner.next(); // clear invalid input
                continue;
            }

            choice = scanner.nextInt();

            switch (choice) {
            case 1 -> controller.viewTotalPaidFees();
            case 2 -> controller.viewTotalPendingFees();
            case 3 -> controller.viewFeesByStudent();
            case 4 -> controller.viewFeesByCourse();
            case 5 -> controller.updateFeesOfCourse();
            case 6 -> controller.viewTotalEarning();
            case 0 -> {
                System.out.println("Exiting Fees Management... Thank you!");
                return;
            }
            default -> System.out.println("Invalid choice! Please enter a number between 0 and 6.");
            }
        }
    }
} 