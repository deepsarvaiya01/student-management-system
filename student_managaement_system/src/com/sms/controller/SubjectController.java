package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Subject;
import com.sms.service.SubjectService;
import com.sms.service.TeacherService;
import com.sms.utils.InputValidator;

public class SubjectController {
	private final SubjectService subjectService;
	private final TeacherController teacherController;
	private final TeacherService teacherService;
	private final Scanner scanner = new Scanner(System.in);

	public SubjectController() throws SQLException {
		this.subjectService = new SubjectService();
		this.teacherController = new TeacherController();
		this.teacherService = new TeacherService();

	}

	public void addSubject() {
		scanner.nextLine();
		String name = InputValidator.getValidName(scanner, "Enter subject name: ");
		String type = getSubjectTypeFromUser();

		if (type == null) {
			System.out.println("❗ Invalid subject type. Aborting...");
			return;
		}

		int subjectId = subjectService.addSubject(name, type);
		if (subjectId == -1) {
			System.out.println("❗ Subject could not be added to the database.");
			return;
		}

		System.out.println("\nPlease assign a teacher to the subject:");
		teacherController.viewTeachers();

		int attempts = 0;
		boolean assigned = false;
		int teacherId = -1;

		while (attempts < 3 && !assigned) {
			System.out.print("Enter Teacher ID: ");
			try {
				teacherId = scanner.nextInt();
				assigned = teacherService.assignSubject(teacherId, subjectId);
				if (!assigned) {
					System.out.println("Invalid or unavailable teacher. Try again.");
				}
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a numeric Teacher ID.");
				scanner.nextLine();
			}
			attempts++;
		}

		if (assigned) {
			System.out.println("Subject added and assigned to Teacher ID: " + teacherId);
		} else {
			subjectService.deleteSubject(subjectId);
			System.out.println("❗ Failed to assign teacher after 3 attempts. Subject creation rolled back.");
		}
	}

	public void viewSubjects() {
		try {
			List<Subject> subjects = subjectService.getAllSubjects();
			if (subjects.isEmpty()) {
				System.out.println("No subjects found.");
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
			System.out.println("Subjects listed successfully!");
		} catch (SQLException e) {
			System.out.println("❗ Error retrieving subjects: " + e.getMessage());
		}
	}

	public void updateSubject() {
		viewSubjects();
		int id = InputValidator.getValidInteger(scanner, "Enter subject ID to update: ", "Subject ID");
		if (!subjectService.subjectExists(id)) {
			System.out.println("❗ Subject not found.");
			return;
		}
		scanner.nextLine();
		String name = InputValidator.getValidName(scanner, "Enter new subject name: ");
		String type = getSubjectTypeFromUser();
		if (type == null) {
			System.out.println("❗ Invalid choice. Update cancelled.");
			return;
		}

		if (subjectService.updateSubject(id, name, type)) {
			System.out.println("Subject updated.");
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