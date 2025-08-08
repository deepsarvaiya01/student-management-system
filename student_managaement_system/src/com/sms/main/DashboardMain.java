package com.sms.main;

import java.util.Scanner;
import com.sms.controller.DashboardController;
import com.sms.utils.InputValidator;

public class DashboardMain {
	public void show() {
		Scanner scanner = new Scanner(System.in);
		DashboardController controller = new DashboardController();
		int choice;
		do {
			System.out.println("\n╔══════════════════════════════════════════╗");
			System.out.println("║        DASHBOARD OPTIONS                 ║");
			System.out.println("╠══════════════════════════════════════════╣");
			System.out.println("║ 1. General Student Dashboard             ║");
			System.out.println("║ 2. Course-wise Dashboard                 ║");
			System.out.println("║ 3. Subject-wise Dashboard                ║");
			System.out.println("║ 4. Teacher-wise Dashboard                ║");
			System.out.println("║ 0. Back                                  ║");
			System.out.println("╚══════════════════════════════════════════╝");

			choice = InputValidator.getValidMenuChoice(scanner, "Enter your choice (0-4): ", 4);

			switch (choice) {
			case 1 -> controller.showGeneralDashboard();
			case 2 -> controller.showCourseDashboard();
			case 3 -> controller.showSubjectDashboard();
			case 4 -> controller.showTeacherDashboard();
			case 0 -> System.out.println("\nGoing back to Student Main Menu...");
			default -> System.out.println("Invalid choice. Please try again.");
			}
		} while (choice != 0);
	}
}