package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Subject;
import com.sms.service.SubjectService;
import com.sms.utils.InputValidator;

public class SubjectController {
	private final SubjectService subjectService;
	private final Scanner scanner = new Scanner(System.in);

	public SubjectController() throws SQLException {
		this.subjectService = new SubjectService();
	}

	public void addSubject() {
		String name = InputValidator.getValidName(scanner, "Enter subject name: ");
		String type = getSubjectTypeFromUser();
		if (type == null) {
			System.out.println("❗ Invalid choice. Subject not added.");
			return;
		}

		int id = subjectService.addSubject(name, type);
		if (id != -1) {
			System.out.println("✅ Subject added with ID: " + id);
		} else {
			System.out.println("❗ Failed to add subject.");
		}
	}

	public void viewSubjects() {
		try {
			List<Subject> subjects = subjectService.getAllSubjects();
			if (subjects.isEmpty()) {
				System.out.println("⚠️ No subjects found.");
				return;
			}

			System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
			System.out.println("║                             SUBJECT LIST                                    ║");
			System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");
			System.out.println("+--------+--------+-------------------------------------+----------------------+");
			System.out.printf("| %-6s | %-6s | %-35s | %-20s |\n", "SrNo", "ID", "Subject Name", "Type");
			System.out.println("+--------+--------+-------------------------------------+----------------------+");

			int srNo = 1;
			for (Subject s : subjects) {
				System.out.printf("| %-6d | %-6d | %-35s | %-20s |\n", srNo++, s.getSubject_id(), s.getSubject_name(),
						s.getSubject_type().toLowerCase());
			}

			System.out.println("+--------+---------+-------------------------------------+---------------------+");
			System.out.println("✅ Subjects listed successfully!");
		} catch (SQLException e) {
			System.out.println("❗ Error retrieving subjects: " + e.getMessage());
		}
	}

	public void updateSubject() {
		int id = InputValidator.getValidInteger(scanner, "Enter subject ID to update: ", "Subject ID");
		if (!subjectService.subjectExists(id)) {
			System.out.println("❗ Subject not found.");
			return;
		}

		String name = InputValidator.getValidName(scanner, "Enter new subject name: ");
		String type = getSubjectTypeFromUser();
		if (type == null) {
			System.out.println("❗ Invalid choice. Update cancelled.");
			return;
		}

		if (subjectService.updateSubject(id, name, type)) {
			System.out.println("✅ Subject updated.");
		} else {
			System.out.println("❗ Failed to update subject.");
		}
	}

	private String getSubjectTypeFromUser() {
		System.out.println("\n╔═══════════════════════════════╗");
		System.out.println("║      SELECT SUBJECT TYPE      ║");
		System.out.println("╠═══════════════════════════════╣");
		System.out.println("║ 1. Mandatory                  ║");
		System.out.println("║ 2. Elective                   ║");
		System.out.println("╚═══════════════════════════════╝");
		int choice = InputValidator.getValidIntegerInRange(scanner, "Enter choice (1-2): ", "Choice", 1, 2);
		return choice == 1 ? "Mandatory" : "Elective";
	}
}