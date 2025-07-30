package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;
import com.sms.controller.StudentController;

public class StudentMain {

	public void show() throws SQLException {
		StudentController controller = new StudentController();
		Scanner scanner = new Scanner(System.in);
		int choice;

		while (true) {
			System.out.println("\n========== Student Management Menu ==========");
			System.out.println("1. View All Students");
			System.out.println("2. Add New Student");
			System.out.println("3. Assign a Course by Student ID");
			System.out.println("4. View All Courses by Student ID");
			System.out.println("5. Search a Student by Student ID");
			System.out.println("6. Delete a Student by Student ID");
			System.out.println("7. Pay Fees");
			System.out.println("0. Exit");
			System.out.println("=============================================");
			System.out.print("Enter your choice (0-7): ");

			if (!scanner.hasNextInt()) {
				System.out.println("❗ Please enter a valid number.");
				scanner.next(); // clear invalid input
				continue;
			}

			choice = scanner.nextInt();

			switch (choice) {
			case 1 -> controller.viewAllStudents();
			case 2 -> controller.addNewStudent();
			case 3 -> controller.assignCourse();
			case 4 -> controller.viewAllCourses();
			case 5 -> controller.searchStudent();
			case 6 -> controller.deleteStudent();
			case 7 -> controller.payFees();
			case 0 -> {
				System.out.println("Exiting... Thank you!");
				scanner.close();
				System.exit(0);
			}
			default -> System.out.println("Invalid choice! Please enter a number between 0 and 7.");
			}
		}
	}
}
