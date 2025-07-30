package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.sms.model.Teacher;
import com.sms.service.TeacherService;

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
		scanner.nextLine();
		System.out.print("\nEnter Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Qualification: ");
		String qualification = scanner.nextLine();
		System.out.print("Enter Experience: ");
		double exp = readDouble();

		Teacher t = new Teacher(name, qualification, exp);
		if (!service.addTeacher(t)) {
			System.out.println("Failed to add teacher.");
			return;
		}
		System.out.println("Teacher added successfully.");
		int teacherId = t.getTeacherId();

		Map<Integer, String> availableSubjects = service.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		printSubjectsTable("Available Subjects:", availableSubjects);
		System.out.print("Enter Subject ID to assign: ");
		int subjectId = scanner.nextInt();

		if (service.assignSubject(teacherId, subjectId)) {
			System.out.println("Subject assigned to the teacher.");
		} else {
			System.out.println("Assignment failed. Invalid ID or already assigned.");
		}
	}

	public void viewTeachers() {
		List<Teacher> list = service.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("No teachers found.");
			return;
		}

		System.out.println("\nList of Teachers");
		System.out.printf("%-5s %-20s %-20s %-10s %-50s%n", "ID", "Name", "Qualification", "Exp", "Subjects");
		System.out.println(
				"---------------------------------------------------------------------------------------------");
		for (Teacher t : list) {
			Map<Integer, String> subjects = service.viewAssignedSubjects(t.getTeacherId());
			String subjectList = subjects.isEmpty() ? "None" : String.join(", ", subjects.values());
			System.out.printf("%-5d %-20s %-20s %-10.1f %-50s%n", t.getTeacherId(), t.getName(), t.getQualification(),
					t.getExperience(), subjectList);
		}
	}

	public void deleteTeacher() {
		System.out.print("Enter Teacher ID to delete: ");
		int id = scanner.nextInt();
		if (service.deleteTeacher(id)) {
			System.out.println("Teacher deleted.");
		} else {
			System.out.println("Invalid Teacher ID or already deleted.");
		}
	}

	public void assignSubject() {
		System.out.print("Enter Teacher ID: ");
		int teacherId = scanner.nextInt();
		if (!service.isTeacherActive(teacherId)) {
			System.out.println("This teacher is inactive or does not exist.");
			return;
		}
		if (service.viewAssignedSubjects(teacherId).size() >= 3) {
			System.out.println("This teacher already has 3 subjects assigned.");
			return;
		}

		Map<Integer, String> availableSubjects = service.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		printSubjectsTable("Available Subjects:", availableSubjects);
		System.out.print("Enter Subject ID to assign: ");
		int subjectId = scanner.nextInt();

		if (service.assignSubject(teacherId, subjectId)) {
			System.out.println("Subject assigned.");
		} else {
			System.out.println("Assignment failed. Already assigned or invalid ID.");
		}
	}

	public void removeSubject() {
		System.out.print("Enter Teacher ID: ");
		int teacherId = scanner.nextInt();
		if (!service.isTeacherActive(teacherId)) {
			System.out.println("This teacher is inactive or does not exist.");
			return;
		}

		Map<Integer, String> subjects = service.viewAssignedSubjects(teacherId);
		if (subjects.isEmpty()) {
			System.out.println("No subjects assigned to this teacher.");
			return;
		}

		printSubjectsTable("Assigned Subjects:", subjects);
		System.out.print("Enter Subject ID to remove: ");
		int subjectId = scanner.nextInt();

		if (service.removeSubject(teacherId, subjectId)) {
			System.out.println("Subject removed.");
		} else {
			System.out.println("Failed to remove subject.");
		}
	}

	public void viewAssignedSubjects() {
		System.out.print("Enter Teacher ID: ");
		int id = scanner.nextInt();
		if (!service.isTeacherActive(id)) {
			System.out.println("This teacher is inactive or does not exist.");
			return;
		}

		Map<Integer, String> subjects = service.viewAssignedSubjects(id);
		printSubjectsTable("Assigned Subjects:", subjects);
	}

	public void searchTeacherById() {
		System.out.print("Enter Teacher ID to search: ");
		int id = scanner.nextInt();

		Teacher teacher = service.getTeacherById(id);
		if (teacher == null || !service.isTeacherActive(id)) {
			System.out.println("No active teacher found with ID: " + id);
			return;
		}

		System.out.println("\nTeacher Details:");
		System.out.printf("%-10s %-20s %-20s %-15s%n", "ID", "Name", "Qualification", "Experience");
		System.out.printf("%-10d %-20s %-20s %-15.1f%n", teacher.getTeacherId(), teacher.getName(),
				teacher.getQualification(), teacher.getExperience());

		Map<Integer, String> subjects = service.viewAssignedSubjects(id);
		printSubjectsTable("Assigned Subjects:", subjects);
	}

	private double readDouble() {
		double value;
		while (true) {
			while (!scanner.hasNextDouble()) {
				System.out.print("Invalid input. Enter a valid number: ");
				scanner.next(); // consume invalid token
			}
			value = scanner.nextDouble();
			if (value >= 0)
				return value;
			System.out.print("Negative values are not allowed. Enter again: ");
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
