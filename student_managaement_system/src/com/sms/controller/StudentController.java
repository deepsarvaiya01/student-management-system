package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.service.StudentService;

public class StudentController {

	private StudentService studentService;
	private Scanner scanner = new Scanner(System.in);

	public StudentController() throws SQLException {
		this.studentService = new StudentService();
	}

	// View All Students
	public void viewAllStudents() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students found.");
			return;
		}

		Student.printHeader();
		for (Student student : students) {
			System.out.println(student);
		}
	}

	// Add New Student with Profile and Course Assignment
	public void addNewStudent() {
		scanner.nextLine(); // Consume leftover newline
		System.out.print("Enter Student Name: ");
		String name = scanner.nextLine();

		System.out.print("Enter GR Number: ");
		int grNumber = scanner.nextInt();

		System.out.print("Enter Email: ");
		String email = scanner.next();

		System.out.print("Enter City: ");
		String city = scanner.next();

		System.out.print("Enter Mobile No: ");
		String mobileNo = scanner.next();

		System.out.print("Enter Age: ");
		int age = scanner.nextInt();

		System.out.println("\nAvailable Courses:");
		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available. Please add a course first.");
			return;
		}

		printCourses(courses);

		System.out.print("Enter Course ID to assign: ");
		int courseId = scanner.nextInt();

		Student student = new Student();
		student.setName(name);
		student.setGr_number(grNumber);
		student.setEmail(email);
		student.setCity(city);
		student.setMobile_no(mobileNo);
		student.setAge(age);

		boolean success = studentService.addStudentWithProfileAndCourse(student, courseId);
		System.out.println(success ? "Student added and course assigned successfully."
				: "Failed to add student. Please try again.");
	}

	// Assign Course to Student
	public void assignCourse() {
		System.out.print("Enter Student ID to assign a course: ");
		int studentId = scanner.nextInt();

		Student student = studentService.searchStudentById(studentId);
		if (student == null) {
			System.out.println("Student not found with ID: " + studentId);
			return;
		}

		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available.");
			return;
		}

		System.out.println("\nAvailable Courses:");
		printCourses(courses);

		System.out.print("Enter Course ID to assign: ");
		int courseId = scanner.nextInt();

		boolean success = studentService.assignCourseToStudent(studentId, courseId);
		System.out.println(success ? "Course assigned successfully."
				: "Failed to assign course. It may already be assigned or invalid.");
	}

	// View All Courses by Student ID
	public void viewAllCourses() {
		System.out.print("Enter Student ID: ");
		int studentId = scanner.nextInt();

		List<Course> courses = studentService.readAllCourses(studentId);
		if (courses.isEmpty()) {
			System.out.println("No courses found for student ID: " + studentId);
			return;
		}

		System.out.println("\nCourses Assigned:");
		printCourses(courses);
	}

	// Search Student by ID
	public void searchStudent() {
		System.out.print("Enter Student ID to search: ");
		int studentId = scanner.nextInt();

		Student student = studentService.searchStudentById(studentId);
		if (student != null) {
			System.out.println("\nStudent Details:");
			Student.printHeader();
			System.out.println(student);
		} else {
			System.out.println("Student not found with ID: " + studentId);
		}
	}

	// Delete Student by ID
	public void deleteStudent() {
		System.out.print("Enter Student ID to delete: ");
		int studentId = scanner.nextInt();

		boolean deleted = studentService.deleteStudentById(studentId);
		System.out.println(deleted ? "Student and related data deleted successfully."
				: "Failed to delete. Check ID and try again.");
	}

	// Helper: Print courses in tabular format
	private void printCourses(List<Course> courses) {
		System.out.printf("\n%-10s %-25s %-20s\n", "Course ID", "Course Name", "No. of Semesters");
		System.out.println("-------------------------------------------------------------");
		for (Course c : courses) {
			System.out.printf("%-10d %-25s %-20d\n", c.getCourse_id(), c.getCourse_name(), c.getNo_of_semester());
		}
	}
}
