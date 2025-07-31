package com.sms.test;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.main.CourseMain;
import com.sms.main.FeeMain;
import com.sms.main.StudentMain;
import com.sms.main.TeacherMain;

public class Main {
	public static void main(String[] args) throws SQLException {
		Scanner scanner = new Scanner(System.in);
		int choice;

		while (true) {
			System.out.println("\n╔══════════════════════════════════════════╗");
			System.out.println("║       STUDENT MANAGEMENT SYSTEM          ║");
			System.out.println("╠══════════════════════════════════════════╣");
			System.out.println("║ 1. Student Management                    ║");
			System.out.println("║ 2. Teacher Management                    ║");
			System.out.println("║ 3. Fees Management                       ║");
			System.out.println("║ 4. Course Management                     ║");
			System.out.println("║ 0. Exit                                  ║");
			System.out.println("╚══════════════════════════════════════════╝");
			System.out.print("👉 Enter your choice (0-4): ");

			if (!scanner.hasNextInt()) {
				System.out.println("❗ Invalid input. Please enter a number between 0 and 5.");
				scanner.next(); // flush the invalid input
				continue;
			}

			choice = scanner.nextInt();

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
			case 0 -> {
				System.out.println("✅ Exiting Student Management System... Thank you!");
				System.out.println("Exiting Student Management System... Thank you!");
				scanner.close();
				System.exit(0);
			}
			default -> System.out.println("❗ Invalid choice! Please enter a number between 0 and 5.");
			}
		}
	}
}
