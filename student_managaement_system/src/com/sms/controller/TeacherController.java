package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Teacher;
import com.sms.service.TeacherService;

public class TeacherController {

	private final TeacherService service;

	public TeacherController() {
		try {
			this.service = new TeacherService();
		} catch (SQLException e) {
			throw new RuntimeException("Database error", e);
		}
	}

	public void addTeacher() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Qualification: ");
		String qualification = scanner.nextLine();
		System.out.print("Enter Experience: ");
		double exp = scanner.nextDouble();
		Teacher t = new Teacher(name, qualification, exp);
		if (service.addTeacher(t)) {
			System.out.println("Teacher added successfully.");
		} else {
			System.out.println("Failed to add teacher.");
		}
	}

	public void viewTeachers() {
		List<Teacher> list = service.fetchAllTeachers();
		if (list.isEmpty()) {
			System.out.println("No teachers found.");
		} else {
			System.out.printf("%-5s %-20s %-20s %-10s %-80s%n", "ID", "Name", "Qualification", "Experience",
					"Subjects");
			System.out.println(
					"-------------------------------------------------------------------------------------------");

			for (Teacher t : list) {
				List<String> subjects = service.getAssignedSubjectsForTeacher(t.getTeacherId());
				String subjectList = String.join(", ", subjects);
				System.out.printf("%-5d %-20s %-20s %-10.1f %-30s%n", t.getTeacherId(), t.getName(),
						t.getQualification(), t.getExperience(), subjectList);
			}
		}
	}

	public void deleteTeacher() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Teacher ID to delete: ");
		int id = scanner.nextInt();
		if (service.deleteTeacher(id)) {
			System.out.println("Teacher deleted.");
		} else {
			System.out.println("Invalid Teacher ID.");
		}
	}

	public void assignSubject() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Teacher ID: ");
		int teacherId = scanner.nextInt();
		System.out.print("Enter Subject ID: ");
		int subjectId = scanner.nextInt();
		if (service.assignSubject(teacherId, subjectId)) {
			System.out.println("Subject assigned.");
		} else {
			System.out.println("Already assigned or invalid IDs.");
		}
	}

	public void removeSubject() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Teacher ID: ");
		int teacherId = scanner.nextInt();

		// Display teacher and assigned subjects
		List<String> subjects = service.viewAssignedSubjects(teacherId);
		if (subjects.isEmpty()) {
			System.out.println("No subjects assigned to this teacher.");
			return;
		}

		System.out.println("\nSubjects currently assigned to Teacher ID " + teacherId + ":");
		System.out.println("---------------------------------------------------");
		for (String subject : subjects) {
			System.out.println(subject);
		}
		System.out.print("\nEnter Subject ID to remove from above list: ");
		int subjectId = scanner.nextInt();

		// Proceed to remove
		if (service.removeSubject(teacherId, subjectId)) {
			System.out.println("Subject removed.");
		} else {
			System.out.println("Could not remove subject.");
		}
	}

	public void viewAssignedSubjects() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Teacher ID: ");
		int id = scanner.nextInt();
		List<String> subjects = service.viewAssignedSubjects(id);
		if (subjects.isEmpty()) {
			System.out.println("\nNo subjects assigned.");
		} else {
			System.out.println("\nSubjects currently assigned to Teacher ID " + id + ":");
			System.out.println("---------------------------------------------------");
			for (String subject : subjects) {
				System.out.println(subject);
			}
		}
	}

	public void searchTeacherById() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Teacher ID to search: ");
		int id = scanner.nextInt();

		Teacher teacher = service.getTeacherById(id);

		if (teacher != null) {
			// Fetch assigned subjects
			List<String> subjects = service.viewAssignedSubjects(id);
			String subjectList = subjects.isEmpty() ? "None" : String.join(", ", subjects);

			// Display in tabular format
			System.out.println("\n================== Teacher Details ==================\n");
			System.out.printf("%-10s %-20s %-20s %-15s %-30s%n", "ID", "Name", "Qualification", "Experience",
					"Subjects Assigned");
			System.out.println("--------------------------------------------------------------------------------------");
			System.out.printf("%-10d %-20s %-20s %-15.1f %-30s%n", teacher.getTeacherId(), teacher.getName(),
					teacher.getQualification(), teacher.getExperience(), subjectList);
		} else {
			System.out.println("No teacher found with ID: " + id);
		}
	}

}
