package com.sms.main;

import java.util.Scanner;

import com.sms.controller.TeacherController;

public class TeacherMain {

	private final TeacherController controller = new TeacherController();

	public void show() {
		Scanner scanner = new Scanner(System.in);
		int choice;
		do {
			System.out.println("\n--- Teacher Menu ---");
			System.out.println("1. Add Teacher");
			System.out.println("2. View All Teachers");
			System.out.println("3. Delete Teacher");
			System.out.println("4. Assign Subject to Teacher");
			System.out.println("5. Remove Subject from Teacher");
			System.out.println("6. View Assigned Subjects");
			System.out.println("7. Search teacher by ID");
			System.out.println("0. Back");
			System.out.print("Enter choice: ");

			try {
				choice = scanner.nextInt();
			} catch (Exception e) {
				choice = 10;
			}
			scanner.nextLine();

			switch (choice) {

			case 1 -> controller.addTeacher();
			case 2 -> controller.viewTeachers();
			case 3 -> controller.deleteTeacher();
			case 4 -> controller.assignSubject();
			case 5 -> controller.removeSubject();
			case 6 -> controller.viewAssignedSubjects();
			case 7 -> controller.searchTeacherById();

			case 0 -> System.out.println("Returning to main menu.");
			default -> System.out.println("Invalid input!");
			}
		} while (choice != 0);
	}
}
