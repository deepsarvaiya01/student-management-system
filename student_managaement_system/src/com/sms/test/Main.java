package com.sms.test;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.main.CourseMain;
import com.sms.main.DashboradMain;
import com.sms.main.FeeMain;
import com.sms.main.StudentMain;
import com.sms.main.TeacherMain;

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
			System.out.println("║ 0. Exit                                  ║");
			System.out.println("╚══════════════════════════════════════════╝");
			System.out.print("👉 Enter your choice (0-4): ");
<<<<<<< HEAD

			while (!scanner.hasNextInt()) {
				System.out.println("❗ Invalid input. Please enter a number between 0 and 4.");
				scanner.next(); // discard invalid input
				System.out.print("👉 Enter your choice (0-4): ");
=======

			while (!scanner.hasNextInt()) {
				System.out.println("❗ Invalid input. Please enter a number between 0 and 4.");
				scanner.next(); 
				System.out.print("👉 Enter your choice (0-4): ");

>>>>>>> f912da889b573caaba854d215dfb1f51ec6cdb61
			}

			choice = scanner.nextInt();

			switch (choice) {
<<<<<<< HEAD
			case 1 -> {
				StudentMain s = new StudentMain();
				s.show(); // Don't close scanner inside this method
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
				break;
			}
			default -> System.out.println("❗ Invalid choice! Please enter a number between 0 and 4.");
			}
=======
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
					DashboradMain d = new DashboradMain();
					d.show();
				}
				case 0 -> {
					System.out.println("✅ Exiting Student Management System... Thank you!");
					break;
				}
				default -> System.out.println("❗ Invalid choice! Please enter a number between 0 and 4.");
			}
>>>>>>> f912da889b573caaba854d215dfb1f51ec6cdb61
		} while (choice != 0);

		scanner.close(); 
	}
}