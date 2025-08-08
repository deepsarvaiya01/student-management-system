package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.SubjectController;
import com.sms.utils.InputValidator;

public class SubjectMain {
	public void show() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		SubjectController subjectController = new SubjectController();
		int input;
		do {
			System.out.println("\n╔═══════════════════════════════════════════╗");
			System.out.println("║            SUBJECT MANAGEMENT MENU        ║");
			System.out.println("╠═══════════════════════════════════════════╣");
			System.out.println("║ 1. Add Subject                            ║");
			System.out.println("║ 2. View All Subjects                      ║");
			System.out.println("║ 3. Update Subject                         ║");
			System.out.println("║ 0. Back                                   ║");
			System.out.println("╚═══════════════════════════════════════════╝");

			input = InputValidator.getValidMenuChoice(scanner, "Enter your choice (0-3): ", 3);

			switch (input) {
			case 1 -> subjectController.addSubject();
			case 2 -> subjectController.viewSubjects();
			case 3 -> subjectController.updateSubject();
			case 0 -> System.out.println("Going back to Student Main Menu...");
			}
		} while (input != 0);
	}
}