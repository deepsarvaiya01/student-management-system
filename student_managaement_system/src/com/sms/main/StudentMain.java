package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;
import com.sms.controller.StudentController;

public class StudentMain {
<<<<<<< HEAD
    public void show(Scanner scanner) throws SQLException {
        StudentController controller = new StudentController();
        int choice;
        do {
            System.out.println("\n========== Student Management Menu ==========");
            System.out.println("1. View All Students");
            System.out.println("2. Add New Student");
            System.out.println("3. Assign a Course by Student ID");
            System.out.println("4. View All Courses by Student ID");
            System.out.println("5. Search a Student by Student ID");
            System.out.println("6. Delete a Student by Student ID");
            System.out.println("7. Pay Fees");
            System.out.println("8. Restore a Deleted Student");
            System.out.println("0. Exit");
            System.out.println("=============================================");
            System.out.print("Enter your choice (0-8): ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Enter a number (0-8): ");
                scanner.next();
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
                case 8 -> controller.restoreStudent();
                case 0 -> System.out.println("Exiting Student Menu...");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }
=======
	public void show() throws SQLException {
		StudentController controller = new StudentController();
		Scanner scanner = new Scanner(System.in);
		int choice;
		do {
			System.out.println("\n╔══════════════════════════════════════════════════════════╗");
			System.out.println("║          STUDENT MANAGEMENT MENU                         ║");
			System.out.println("╠══════════════════════════════════════════════════════════╣");
			System.out.println("║ 1. View All Students                                     ║");
			System.out.println("║ 2. Add New Student                                       ║");
			System.out.println("║ 3. Assign a Course by Student ID                         ║");
			System.out.println("║ 4. View All Courses by Student ID                        ║");
			System.out.println("║ 5. Search a Student by Student ID                        ║");
			System.out.println("║ 6. Delete a Student by Student ID                        ║");
			System.out.println("║ 7. Pay Fees                                              ║");
			System.out.println("║ 8. Restore a Deleted Student                             ║");
			System.out.println("║ 0. Exit                                                  ║");
			System.out.println("╚══════════════════════════════════════════════════════════╝");
			System.out.print("👉 Enter your choice (0-8): ");

			while (!scanner.hasNextInt()) {
				System.out.println("Invalid input. Enter a number (0-8): ");
				scanner.next();
			}
			choice = scanner.nextInt();
			switch (choice) {
			case 1:
				controller.viewAllStudents();
				break;
			case 2:
				controller.addNewStudent();
				break;
			case 3:
				controller.assignCourse();
				break;
			case 4:
				controller.viewAllCourses();
				break;
			case 5:
				controller.searchStudent();
				break;
			case 6:
				controller.deleteStudent();
				break;
			case 7:
				controller.payFees();
				break;
			case 8:
				controller.restoreStudent();
				break;
			case 0:
				System.out.println("Exiting...");
				break;
			default:
				System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 0);
		scanner.close();
	}
>>>>>>> 5cfcef909d89289d9f8c1b70af3a589f425a91a2
}
