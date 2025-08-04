package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sms.model.Teacher;
import com.sms.service.TeacherService;
import com.sms.utils.InputValidator;

public class TeacherController {
	private final TeacherService service;
	private static final Scanner scanner = new Scanner(System.in);

	public TeacherController() {
		try {
			this.service = new TeacherService();
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
					System.out.println("❌ Experience cannot be negative. Please try again:");
					continue;
				}
				break;
			} catch (NumberFormatException e) {
				System.out.println("❌ Invalid Experience! Expected a number.");
			}
		}

		Teacher t = new Teacher(name, qualification, exp);
		if (!service.addTeacher(t)) {
			System.out.println("❌ Failed to add teacher.");
			return;
		}
		System.out.println("✅ Teacher added successfully.");
		int teacherId = t.getTeacherId();

		Map<Integer, String> availableSubjects = service.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		printSubjectsTable("Available Subjects:", availableSubjects);
		int subjectId = InputValidator.getValidIntegerAllowZero(scanner, "Enter Subject ID to assign or 0 to skip: ",
				"Subject ID");
		if (subjectId > 0) {
			if (service.assignSubject(teacherId, subjectId)) {
				System.out.println("✅ Subject assigned to the teacher.");
			} else {
				System.out.println("❌ Assignment failed. Invalid ID or already assigned.");
			}
		} else {
			System.out.println("Skipped subject assignment.");
		}
	}

	public void viewTeachers() {
		List<Teacher> list = service.fetchAllTeachers();
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
			Map<Integer, String> subjects = service.viewAssignedSubjects(t.getTeacherId());
			String subjectList = subjects.isEmpty() ? "None" : String.join(", ", subjects.values());

			// Trim subjectList if it’s too long
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
		if (service.deleteTeacher(id)) {
			System.out.println("✅ Teacher deleted.");
		} else {
			System.out.println("❗ Invalid Teacher ID or already deleted.");
		}
	}

	public void assignSubject() {

		viewTeachers();

		int teacherId = InputValidator.getValidInteger(scanner, "Enter Teacher ID: ", "Teacher ID");
		if (!service.isTeacherActive(teacherId)) {
			System.out.println("❗ This teacher is inactive or does not exist.");
			return;
		}
		if (service.viewAssignedSubjects(teacherId).size() >= 3) {
			System.out.println("❗ This teacher already has 3 subjects assigned.");
			return;
		}

		Map<Integer, String> availableSubjects = service.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		printSubjectsTable("Available Subjects:", availableSubjects);
		int subjectId = InputValidator.getValidInteger(scanner, "Enter Subject ID to assign: ", "Subject ID");
		if (availableSubjects.containsKey(subjectId)) {
			if (service.assignSubject(teacherId, subjectId)) {
				System.out.println("✅ Subject assigned.");
			} else {
				System.out.println("❌ Assignment failed. Already assigned or invalid ID.");
			}
		} else {
			System.out.println("❌ Invalid Subject ID. Choose from the list above.");
		}
	}

	public void removeSubject() {
		List<Teacher> list = service.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("No teachers found.");
			return;
		}

		int teacherId = InputValidator.getValidInteger(scanner, "Enter Teacher ID: ", "Teacher ID");
		if (!service.isTeacherActive(teacherId)) {
			System.out.println("❗ This teacher is inactive or does not exist.");
			return;
		}

		Map<Integer, String> subjects = service.viewAssignedSubjects(teacherId);
		if (subjects.isEmpty()) {
			System.out.println("No subjects assigned to this teacher.");
			return;
		}

		printSubjectsTable("Assigned Subjects:", subjects);
		int subjectId = InputValidator.getValidInteger(scanner, "Enter Subject ID to remove: ", "Subject ID");
		if (service.removeSubject(teacherId, subjectId)) {
			System.out.println("✅ Subject removed.");
		} else {
			System.out.println("❌ Failed to remove subject.");
		}
	}

	public void viewAssignedSubjects() {
		List<Teacher> list = service.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("No teachers found.");
			return;
		}

		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID: ", "Teacher ID");
		if (!service.isTeacherActive(id)) {
			System.out.println("❗ This teacher is inactive or does not exist.");
			return;
		}

		Map<Integer, String> subjects = service.viewAssignedSubjects(id);
		printSubjectsTable("Assigned Subjects:", subjects);
	}

	public void searchTeacherById() {
		List<Teacher> list = service.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("No teachers found.");
			return;
		}

		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID to search: ", "Teacher ID");
		Teacher teacher = service.getTeacherById(id);
		if (teacher == null || !service.isTeacherActive(id)) {
			System.out.println("❗ No active teacher found with ID: " + id);
			return;
		}

		System.out.println("\nTeacher Details:");
		System.out.printf("%-10s %-20s %-20s %-15s%n", "ID", "Name", "Qualification", "Experience");
		System.out.printf("%-10d %-20s %-20s %-15.1f%n", teacher.getTeacherId(), teacher.getName(),
				teacher.getQualification(), teacher.getExperience());

		Map<Integer, String> subjects = service.viewAssignedSubjects(id);
		printSubjectsTable("Assigned Subjects:", subjects);
	}

	public void restoreTeacher() {
		List<Teacher> list = service.fetchInactiveTeachers();
		if (list.isEmpty()) {
			System.out.println("No inactive teachers found.");
			return;
		}

		System.out.println("\n--- Inactive Teachers ---");
		System.out.printf("%-5s %-20s %-20s %-10s%n", "ID", "Name", "Qualification", "Experience");
		System.out.println("---------------------------------------------------------------");
		for (Teacher t : list) {
			System.out.printf("%-5d %-20s %-20s %-10.1f%n", t.getTeacherId(), t.getName(), t.getQualification(),
					t.getExperience());
		}

		int id = InputValidator.getValidInteger(scanner, "Enter Teacher ID to restore: ", "Teacher ID");
		boolean exists = list.stream().anyMatch(t -> t.getTeacherId() == id);
		if (!exists) {
			System.out.println("❗ ID not found in inactive list.");
			return;
		}

		if (service.restoreTeacher(id)) {
			System.out.println("✅ Teacher restored successfully.");
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
		System.out.printf("%-15s %-30s%n", "Subject ID", "Subject Name");
		System.out.printf("%-15s %-30s%n", "----------", "------------------------------");
		subjectMap.forEach((id, name) -> System.out.printf("%-15d %-30s%n", id, name));
	}
}