package com.sms.test;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.main.AnalysisMain;
import com.sms.main.CourseMain;
import com.sms.main.DashboardMain;
import com.sms.main.FeeMain;
import com.sms.main.HelpdeskMain;
import com.sms.main.StudentMain;
import com.sms.main.TeacherMain;
import com.sms.utils.InputValidator;

public class Main {
	public static void main(String[] args) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		int choice;

		do {
			System.out.println("\n╔══════════════════════════════════════════╗");
			System.out.println("║       STUDENT MANAGEMENT SYSTEM          ║");
			System.out.println("╠══════════════════════════════════════════╣");
			System.out.println("║ 1. Student Management                    ║");
			System.out.println("║ 2. Teacher Management                    ║");
			System.out.println("║ 3. Fees Management                       ║");
			System.out.println("║ 4. Course Management                     ║");
			System.out.println("║ 5. Dashboard                             ║");
			System.out.println("║ 6. Analysis                              ║");
			System.out.println("║ 7. Helpdesk                              ║");
			System.out.println("║ 0. Exit                                  ║");
			System.out.println("╚══════════════════════════════════════════╝");

			choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-7): ", 7);

			switch (choice) {
			case 1 -> {
				StudentMain s = new StudentMain();
				s.show();
			}
			case 2 -> {
				TeacherMain t = new TeacherMain();
				t.show();
			}
			case 3 -> {
				FeeMain f = new FeeMain();
				f.show();
			}
			case 4 -> {
				CourseMain c = new CourseMain();
				c.show();
			}
			case 5 -> {
				DashboardMain d = new DashboardMain();
				d.show();
			}
			case 6 -> {
				AnalysisMain a = new AnalysisMain();
				a.show();
			}
			case 7 -> {
				HelpdeskMain h = new HelpdeskMain();
				h.show();
			}
			case 0 -> {
				System.out.println("Exiting Student Management System... Thank you!");
			}
			default -> System.out.println("❌ Invalid choice! Please enter a number between 0 and 7.");

			}
		} while (choice != 0);

		scanner.close();
	}
}