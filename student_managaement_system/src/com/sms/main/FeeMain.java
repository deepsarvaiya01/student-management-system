package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.FeeController;
import com.sms.utils.InputValidator;

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
            System.out.println("║ 0. Back                                  ║");
            System.out.println("╚══════════════════════════════════════════╝");
            
            choice = InputValidator.getValidMenuChoice(scanner, "Enter your choice (0-6): ", 6);

            switch (choice) {
                case 1 -> controller.viewTotalPaidFees();
                case 2 -> controller.viewTotalPendingFees();
                case 3 -> controller.viewFeesByStudent();
                case 4 -> controller.viewFeesByCourse();
                case 5 -> controller.updateFeesOfCourse();
                case 6 -> controller.viewTotalEarning();
                case 0 -> {
                    System.out.println("Going back to Student Main Menu...");
                    return;
                }
            }
        }
    }
}