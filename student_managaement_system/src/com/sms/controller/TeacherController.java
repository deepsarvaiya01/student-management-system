package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sms.model.Teacher;
import com.sms.service.TeacherService;
import com.sms.utils.InputValidator;

public class TeacherController {
	private final TeacherService teacherService;
	private static final Scanner scanner = new Scanner(System.in);

	public TeacherController() {
		try {
			this.teacherService = new TeacherService();
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e);
		}
	}

	public void addTeacher() {
		String name = InputValidator.getValidName(scanner, "Enter Name: ");
		String qualification = InputValidator.getValidName(scanner, "Enter Qualification: ");
		System.out.print("Enter Experience: ");
		double exp;
		while (true) {
			try {
				exp = Double.parseDouble(scanner.nextLine().trim());
				if (exp < 0) {
					System.out.println("Experience cannot be negative. Please try again:");
					continue;
				}
				break;
			} catch (NumberFormatException e) {
				System.out.println("Invalid Experience! Expected a number.");
			}
		}

		Teacher t = new Teacher(name, qualification, exp);
		if (!teacherService.addTeacher(t)) {
			System.out.println("Failed to add teacher.");
			return;
		}
		System.out.println("Teacher added successfully.");
		int teacherId = t.getTeacherId();

		Map<Integer, String> availableSubjects = teacherService.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		printSubjectsTable("Available Subjects:", availableSubjects);
		int subjectId = InputValidator.getValidIntegerAllowZero(scanner, "Enter Subject ID to assign or 0 to skip: ",
				"Subject ID");
		if (subjectId > 0) {
			if (teacherService.assignSubject(teacherId, subjectId)) {
				System.out.println("Subject assigned to the teacher.");
			} else {
				System.out.println("Assignment failed. Invalid ID or already assigned.");
			}
		} else {
			System.out.println("Skipped subject assignment.");
		}
	}

	public void viewTeachers() {
		List<Teacher> list = teacherService.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("❗ No teachers found.");
			return;
		}

		String format = "| %-4s | %-20s | %-20s | %-5s | %-50s |%n";
		String line = "+------+----------------------+----------------------+-------+----------------------------------------------------+";

		System.out.println("\nList of Teachers");
		System.out.println(line);
		System.out.printf(format, "ID", "Name", "Qualification", "Exp", "Subjects");
		System.out.println(line);

		for (Teacher t : list) {
			Map<Integer, String> subjects = teacherService.viewAssignedSubjects(t.getTeacherId());
			String subjectList = subjects.isEmpty() ? "None" : String.join(", ", subjects.values());

			if (subjectList.length() > 50) {
				subjectList = subjectList.substring(0, 47) + "...";
			}

			System.out.printf(format, t.getTeacherId(), t.getName(), t.getQualification(),
					String.format("%.1f", t.getExperience()), subjectList);
		}

		System.out.println(line);
	}

	public void deleteTeacher() {
		viewTeachers();
//		List<Teacher> list = service.fetchAllTeachers();
//		if (list.isEmpty()) {
//			System.out.println("No teachers found.");
//			return;
//		}

		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID to delete: ", "Teacher ID");
		if (teacherService.deleteTeacher(id)) {
			System.out.println("✅ Teacher deleted.");
		} else {
			System.out.println("❗ Invalid Teacher ID or already deleted.");
		}
	}

	public void assignSubject() {

		viewTeachers();

		int teacherId = InputValidator.getValidInteger(scanner, "Enter Teacher ID: ", "Teacher ID");
		if (!teacherService.isTeacherActive(teacherId)) {
			System.out.println("❗ This teacher is inactive or does not exist.");
			return;
		}
		if (teacherService.viewAssignedSubjects(teacherId).size() >= 3) {
			System.out.println("❗ This teacher already has 3 subjects assigned.");
			return;
		}

		Map<Integer, String> availableSubjects = teacherService.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		printSubjectsTable("Available Subjects:", availableSubjects);
		int subjectId = InputValidator.getValidInteger(scanner, "Enter Subject ID to assign: ", "Subject ID");
		if (availableSubjects.containsKey(subjectId)) {
			if (teacherService.assignSubject(teacherId, subjectId)) {
				System.out.println("Subject assigned.");
			} else {
				System.out.println("Assignment failed. Already assigned or invalid ID.");
			}
		} else {
			System.out.println("Invalid Subject ID. Choose from the list above.");
		}
	}

	public void removeSubject() {
		viewTeacherName();

		int teacherId = InputValidator.getValidInteger(scanner, "Enter Teacher ID: ", "Teacher ID");
		if (!teacherService.isTeacherActive(teacherId)) {
			System.out.println("❗ This teacher is inactive or does not exist.");
			return;
		}

		Map<Integer, String> subjects = teacherService.viewAssignedSubjects(teacherId);
		if (subjects.isEmpty()) {
			System.out.println("No subjects assigned to this teacher.");
			return;
		}

		printSubjectsTable("Assigned Subjects:", subjects);
		int subjectId = InputValidator.getValidInteger(scanner, "Enter Subject ID to remove: ", "Subject ID");
		if (teacherService.removeSubject(teacherId, subjectId)) {
			System.out.println("Subject removed.");
		} else {
			System.out.println("Failed to remove subject.");
		}
	}

	public void viewAssignedSubjects() {
		viewTeacherName();

		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID: ", "Teacher ID");
		if (!teacherService.isTeacherActive(id)) {
			System.out.println("❗ This teacher is inactive or does not exist.");
			return;
		}

		Map<Integer, String> subjects = teacherService.viewAssignedSubjects(id);
		printSubjectsTable("Assigned Subjects:", subjects);
	}

	public void searchTeacherById() {
		viewTeacherName();
		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID to get full Details: ", "Teacher ID");
		Teacher teacher = teacherService.getTeacherById(id);
		if (teacher == null || !teacherService.isTeacherActive(id)) {
			System.out.println("❗ No active teacher found with ID: " + id);
			return;
		}

		System.out.println("\nTeacher Details");
		System.out.println("+----------+----------------------+----------------------+-------------+");
		System.out.printf("| %-8s | %-20s | %-20s | %-11s |%n", "ID", "Name", "Qualification", "Experience");
		System.out.println("+----------+----------------------+----------------------+-------------+");
		System.out.printf("| %-8d | %-20s | %-20s | %-11.1f |%n",
				teacher.getTeacherId(),
				teacher.getName(),
				teacher.getQualification(),
				teacher.getExperience());
		System.out.println("+----------+----------------------+----------------------+-------------+");

		// Assigned Subjects
		Map<Integer, String> subjects = teacherService.viewAssignedSubjects(id);
		if (subjects.isEmpty()) {
			System.out.println("\nNo subjects assigned to this teacher.");
		} else {
			System.out.println("\nAssigned Subjects:");
			String format = "| %-12s | %-30s |%n";
			String line = "+--------------+--------------------------------+";
			System.out.println(line);
			System.out.printf(format, "Subject ID", "Subject Name");
			System.out.println(line);
			subjects.forEach((subId, name) -> System.out.printf(format, subId, name));
			System.out.println(line);
		}

	}

	public void restoreTeacher() {
		List<Teacher> list = teacherService.fetchInactiveTeachers();
		if (list.isEmpty()) {
			System.out.println("No inactive teachers found.");
			return;
		}

		System.out.println("\nInactive Teachers");
		String line = "+-----+----------------------+----------------------+------------+";
		String format = "| %-3s | %-20s | %-20s | %-10s |%n";

		System.out.println(line);
		System.out.printf(format, "ID", "Name", "Qualification", "Experience");
		System.out.println(line);

		for (Teacher t : list) {
			System.out.printf(format, t.getTeacherId(), t.getName(), t.getQualification(), String.format("%.1f", t.getExperience()));
		}

		System.out.println(line);


		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID to restore: ", "Teacher ID");
		boolean exists = list.stream().anyMatch(t -> t.getTeacherId() == id);
		if (!exists) {
			System.out.println("❗ ID not found in inactive list.");
			return;
		}

		if (teacherService.restoreTeacher(id)) {
			System.out.println("Teacher restored successfully.");
		} else {
			System.out.println("❗ Failed to restore. Invalid ID?");
		}
	}

	private void printSubjectsTable(String header, Map<Integer, String> subjectMap) {
		if (subjectMap.isEmpty()) {
			System.out.println("No subjects found.");
			return;
		}
		System.out.println("\n" + header);
		System.out.println("\n List of Subjects:");
		System.out.println("+---------------+--------------------------------+");
		System.out.printf("| %-13s | %-30s |\n", "Subject ID", "Subject Name");
		System.out.println("+---------------+--------------------------------+");

		subjectMap.forEach((id, name) -> System.out.printf("| %-13d | %-30s |\n", id, name));

		System.out.println("+---------------+--------------------------------+");

	}

	public void viewTeacherName() {
		List<Teacher> list = teacherService.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("❗ No teachers found.");
			return;
		}

		String format = "| %-4s | %-25s |%n";
		String line = "+------+---------------------------+";

		System.out.println("\n📘 List of Teachers");
		System.out.println(line);
		System.out.printf(format, "ID", "Name");
		System.out.println(line);

		for (Teacher t : list) {
			System.out.printf(format, t.getTeacherId(), t.getName());
		}

		System.out.println(line);
	}

}