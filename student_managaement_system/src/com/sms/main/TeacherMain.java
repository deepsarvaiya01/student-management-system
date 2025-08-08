package com.sms.main;

import java.util.Scanner;

import com.sms.controller.TeacherController;
import com.sms.utils.InputValidator;

public class TeacherMain {

	private final TeacherController controller = new TeacherController();

	public void show() {
		Scanner scanner = new Scanner(System.in);
		int choice;
		do {
			System.out.println("\n╔════════════════════════════════════════════════════════╗");
			System.out.println("║           TEACHER MANAGEMENT MENU                      ║");
			System.out.println("╠════════════════════════════════════════════════════════╣");
			System.out.println("║ 1. View All Teachers                                   ║");
			System.out.println("║ 2. Add Teacher                                         ║");
			System.out.println("║ 3. Delete Teacher                                      ║");
			System.out.println("║ 4. Assign Subject to Teacher                           ║");
			System.out.println("║ 5. Remove Subject from Teacher                         ║");
			System.out.println("║ 6. View Assigned Subjects                              ║");
			System.out.println("║ 7. Search Teacher by ID                                ║");
			System.out.println("║ 8. Restore Deleted Teacher                             ║");
			System.out.println("║ 0. Back                                                ║");
			System.out.println("╚════════════════════════════════════════════════════════╝");

			choice = InputValidator.getValidMenuChoice(scanner, "Enter your choice (0-8): ", 8);

			switch (choice) {
			case 1 -> controller.viewTeachers();
			case 2 -> controller.addTeacher();
			case 3 -> controller.deleteTeacher();
			case 4 -> controller.assignSubject();
			case 5 -> controller.removeSubject();
			case 6 -> controller.viewAssignedSubjects();
			case 7 -> controller.searchTeacherById();
			case 8 -> controller.restoreTeacher();
			case 0 -> System.out.println("Going back to Student Main Menu...");
			}
		} while (choice != 0);
	}
}