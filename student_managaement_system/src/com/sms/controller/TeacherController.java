package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sms.model.Teacher;
import com.sms.service.TeacherService;

public class TeacherController {

	private final TeacherService service;
	private static final Scanner scanner = new Scanner(System.in); // Global scanner

	public TeacherController() {
		try {
			this.service = new TeacherService();
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e);
		}
	}

	public void addTeacher() {
		System.out.print("Enter Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Qualification: ");
		String qualification = scanner.nextLine();
		System.out.print("Enter Experience: ");
		double exp = scanner.nextDouble();

		Teacher t = new Teacher(name, qualification, exp);
		boolean added = service.addTeacher(t);

		if (added) {
			System.out.println("Teacher added successfully.");
			int teacherId = t.getTeacherId();

			Map<Integer, String> availableSubjects = service.getAvailableSubjects(teacherId);
			if (availableSubjects.isEmpty()) {
				System.out.println("No available subjects left to assign.");
				return;
			}

			System.out.println("\nAvailable Subjects:");
			System.out.printf("%-15s %-30s%n", "Subject ID", "Subject Name");
			System.out.printf("%-15s %-30s%n", "----------", "------------------------------");
			availableSubjects.forEach((id, subjectName) -> System.out.printf("%-15d %-30s%n", id, subjectName));

			System.out.print("\nEnter Subject ID to assign: ");
			int subjectId = scanner.nextInt();

			if (service.assignSubject(teacherId, subjectId)) {
				System.out.println("Subject assigned to the teacher.");
			} else {
				System.out.println("Assignment failed. Invalid ID.");
			}
		} else {
			System.out.println("Failed to add teacher.");
		}
	}

	public void viewTeachers() {
		List<Teacher> list = service.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("No teachers found.");
		} else {
			System.out.println("\n================= List of Teachers =================");
			System.out.printf("%-5s %-20s %-20s %-10s %-50s%n", "ID", "Name", "Qualification", "Exp", "Subjects");
			System.out.println(
					"----------------------------------------------------------------------------------------------");

			for (Teacher t : list) {
				Map<Integer, String> subjects = service.viewAssignedSubjects(t.getTeacherId());
				String subjectList = subjects.isEmpty() ? "None" : String.join(", ", subjects.values());

				System.out.printf("%-5d %-20s %-20s %-10.1f %-50s%n", t.getTeacherId(), t.getName(),
						t.getQualification(), t.getExperience(), subjectList);
			}
		}
	}

	public void deleteTeacher() {
		System.out.print("Enter Teacher ID to delete: ");
		int id = scanner.nextInt();
		if (service.deleteTeacher(id)) {
			System.out.println("Teacher deleted.");
		} else {
			System.out.println("Invalid Teacher ID.");
		}
	}

	public void assignSubject() {
		System.out.print("Enter Teacher ID: ");
		int teacherId = scanner.nextInt();

		Map<Integer, String> assignedSubjects = service.viewAssignedSubjects(teacherId);

		if (assignedSubjects.size() >= 3) {
			System.out.println("This teacher already has 3 subjects assigned (maximum allowed).");
			return;
		}

		Map<Integer, String> availableSubjects = service.getAvailableSubjects(teacherId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No available subjects left to assign.");
			return;
		}

		System.out.println("\nAvailable Subjects:");
		System.out.printf("%-15s %-30s%n", "Subject ID", "Subject Name");
		System.out.printf("%-15s %-30s%n", "----------", "------------------------------");
		availableSubjects.forEach((id, name) -> System.out.printf("%-15d %-30s%n", id, name));

		System.out.print("\nEnter Subject ID to assign: ");
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

		Map<Integer, String> subjects = service.viewAssignedSubjects(teacherId);
		if (subjects.isEmpty()) {
			System.out.println("No subjects assigned to this teacher.");
			return;
		}

		System.out.println("\nAssigned Subjects for Teacher ID " + teacherId + ":");
		System.out.printf("%-15s %-30s%n", "Subject ID", "Subject Name");
		System.out.printf("%-15s %-30s%n", "----------", "------------------------------");
		subjects.forEach((id, name) -> System.out.printf("%-15d %-30s%n", id, name));

		System.out.print("\nEnter Subject ID to remove: ");
		int subjectId = scanner.nextInt();

		if (service.removeSubject(teacherId, subjectId)) {
			System.out.println("Subject removed.");
		} else {
			System.out.println("Could not remove subject.");
		}
	}

	public void viewAssignedSubjects() {
		System.out.print("Enter Teacher ID: ");
		int id = scanner.nextInt();

		Map<Integer, String> subjects = service.viewAssignedSubjects(id);
		if (subjects.isEmpty()) {
			System.out.println("\nNo subjects assigned or No teacher found.");
		} else {
			System.out.println("\nAssigned Subjects for Teacher ID " + id + ":");
			System.out.printf("%-15s %-30s%n", "Subject ID", "Subject Name");
			System.out.printf("%-15s %-30s%n", "----------", "------------------------------");
			subjects.forEach((subjectId, subjectName) -> System.out.printf("%-15d %-30s%n", subjectId, subjectName));
		}
	}

	public void searchTeacherById() {
		System.out.print("Enter Teacher ID to search: ");
		int id = scanner.nextInt();

		Teacher teacher = service.getTeacherById(id);
		if (teacher != null) {
			Map<Integer, String> subjects = service.viewAssignedSubjects(id);

			System.out.println("\n================== Teacher Details ==================\n");
			System.out.printf("%-10s %-20s %-20s %-15s%n", "ID", "Name", "Qualification", "Experience");
			System.out.println("--------------------------------------------------------------------------");
			System.out.printf("%-10d %-20s %-20s %-15.1f%n", teacher.getTeacherId(), teacher.getName(),
					teacher.getQualification(), teacher.getExperience());

			if (subjects.isEmpty()) {
				System.out.println("\nNo subjects assigned to this teacher.");
			} else {
				System.out.println("\nAssigned Subjects:");
				System.out.printf("%-15s %-30s%n", "Subject ID", "Subject Name");
				System.out.printf("%-15s %-30s%n", "----------", "------------------------------");
				subjects.forEach((subId, subName) -> System.out.printf("%-15d %-30s%n", subId, subName));
			}
		} else {
			System.out.println("No teacher found with ID: " + id);
		}
	}
}
