package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.service.FeeService;
import com.sms.service.StudentService;
import com.sms.utils.payFeesUtils;

public class StudentController {

	private StudentService studentService;
	private FeeService feeService;
	private Scanner scanner = new Scanner(System.in);

	public StudentController() throws SQLException {
		this.studentService = new StudentService();
		this.feeService = new FeeService();
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
		int attempts = 0;
		System.out.print("Enter Student Name: ");
		String name = scanner.nextLine();
		while (!name.matches("[a-zA-Z ]{1,50}") && attempts++ < 3) {
			System.out.println("Invalid name (letters/spaces, max 50 chars). Try again: ");
			name = scanner.nextLine();
		}
		if (!name.matches("[a-zA-Z ]{1,50}")) {
			System.out.println("Too many invalid attempts. Aborting.");
			return;
		}

		System.out.print("Enter GR Number: ");
		while (!scanner.hasNextInt() && attempts++ < 3) {
			System.out.println("Invalid GR number (positive integer). Try again: ");
			scanner.next();
		}
		if (attempts >= 3) {
			System.out.println("Too many invalid attempts. Aborting.");
			return;
		}
		int grNumber = scanner.nextInt();
		if (grNumber <= 0 || String.valueOf(grNumber).length() < 4 || String.valueOf(grNumber).length() > 10) {
			System.out.println("Invalid GR number (4-10 digits). Aborting.");
			return;
		}

		System.out.print("Enter Email: ");
		scanner.nextLine(); // Consume newline
		String email = scanner.nextLine();
		while (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") && attempts++ < 3) {
			System.out.println("Invalid email format. Try again: ");
			email = scanner.nextLine();
		}
		if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") || email.length() > 100) {
			System.out.println("Invalid email or too long. Aborting.");
			return;
		}

		System.out.print("Enter City: ");
		String city = scanner.nextLine();
		while (!city.matches("[a-zA-Z ]{1,50}") && attempts++ < 3) {
			System.out.println("Invalid city (letters/spaces, max 50 chars). Try again: ");
			city = scanner.nextLine();
		}
		if (!city.matches("[a-zA-Z ]{1,50}")) {
			System.out.println("Invalid city. Aborting.");
			return;
		}

		System.out.print("Enter Mobile No: ");
		String mobileNo = scanner.nextLine();
		while (!mobileNo.matches("\\d{10}") && attempts++ < 3) {
			System.out.println("Invalid mobile number (10 digits). Try again: ");
			mobileNo = scanner.nextLine();
		}
		if (!mobileNo.matches("\\d{10}") || mobileNo.length() > 15) {
			System.out.println("Invalid mobile number. Aborting.");
			return;
		}

		System.out.print("Enter Age: ");
		while (!scanner.hasNextInt() && attempts++ < 3) {
			System.out.println("Invalid age (integer). Try again: ");
			scanner.next();
		}
		if (attempts >= 3) {
			System.out.println("Too many invalid attempts. Aborting.");
			return;
		}
		int age = scanner.nextInt();
		if (age < 15 || age > 100) {
			System.out.println("Invalid age (15-100). Aborting.");
			return;
		}

		System.out.println("\nAvailable Courses:");
		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available. Please add a course first.");
			return;
		}
		printCourses(courses);

		System.out.print("Enter Course ID to assign: ");
		while (!scanner.hasNextInt() && attempts++ < 3) {
			System.out.println("Invalid course ID (positive integer). Try again: ");
			scanner.next();
		}
		if (attempts >= 3) {
			System.out.println("Too many invalid attempts. Aborting.");
			return;
		}
		int courseId = scanner.nextInt();
		boolean validCourse = courses.stream().anyMatch(c -> c.getCourse_id() == courseId);
		if (!validCourse) {
			System.out.println("Invalid course ID. Aborting.");
			return;
		}

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
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		System.out.print("Enter Student ID to assign a course: ");
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid student ID.");
			scanner.next();
			return;
		}
		int studentId = scanner.nextInt();
		if (studentId <= 0 || studentService.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
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
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid course ID.");
			scanner.next();
			return;
		}
		int courseId = scanner.nextInt();
		boolean validCourse = courses.stream().anyMatch(c -> c.getCourse_id() == courseId);
		if (!validCourse) {
			System.out.println("No such course exists.");
			return;
		}

		boolean success = studentService.assignCourseToStudent(studentId, courseId);
		System.out.println(success ? "Course ID " + courseId + " assigned to student ID " + studentId + " successfully."
				: "Failed to assign course. It may already be assigned.");
	}

	// View All Courses by Student ID
	public void viewAllCourses() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		System.out.print("Enter Student ID: ");
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid student ID.");
			scanner.next();
			return;
		}
		int studentId = scanner.nextInt();
		if (studentId <= 0 || studentService.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
			return;
		}

		List<Course> courses = studentService.readAllCourses(studentId);
		if (courses.isEmpty()) {
			System.out.println("No courses assigned to student ID: " + studentId);
			return;
		}

		System.out.println("\nCourses for Student ID " + studentId + ":");
		printCourses(courses);
	}

	// Search Student by ID
	public void searchStudent() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		System.out.print("Enter Student ID to search: ");
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid student ID.");
			scanner.next();
			return;
		}
		int studentId = scanner.nextInt();
		if (studentId <= 0 || studentService.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
			return;
		}

		Student student = studentService.searchStudentById(studentId);
		System.out.println("\nStudent Details:");
		Student.printHeader();
		System.out.println(student);
	}

	// Delete Student by ID
	public void deleteStudent() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		System.out.print("Enter Student ID to delete: ");
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid student ID.");
			scanner.next();
			return;
		}
		int studentId = scanner.nextInt();
		if (studentId <= 0 || studentService.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
			return;
		}

		Student student = studentService.searchStudentById(studentId);
		System.out.println("\nStudent Details:");
		Student.printHeader();
		System.out.println(student);
		System.out.print("Are you sure you want to delete this student? (y/n): ");
		scanner.nextLine(); // Consume newline
		String confirmation = scanner.nextLine().trim().toLowerCase();

		if (!confirmation.equals("y")) {
			System.out.println("Deletion cancelled.");
			return;
		}

		boolean deleted = studentService.deleteStudentById(studentId);
		System.out.println(deleted ? "Student ID " + studentId + " marked as inactive successfully."
				: "Failed to mark student ID " + studentId + " as inactive. Check database.");
	}

	public void payFees() throws SQLException {
	    List<Student> students = studentService.readAllStudent();
	    if (students.isEmpty()) {
	        System.out.println("\n💰 === FEES PAYMENT ===\nNo students available.");
	        return;
	    }

	    payFeesUtils payFeesUtil = new payFeesUtils();
	    System.out.println("\n💰 === FEES PAYMENT ===");
	    payFeesUtil.showAndGetAllStudents();

	    System.out.print("\nEnter Student ID to pay fees: ");
	    if (!scanner.hasNextInt()) {
	        System.out.println("Invalid student ID.");
	        scanner.next();
	        return;
	    }
	    int studentId = scanner.nextInt();
	    if (studentId <= 0 || studentService.searchStudentById(studentId) == null) {
	        System.out.println("No such student exists.");
	        return;
	    }

	    List<Fee> fees = payFeesUtil.showStudentFees(studentId);
	    if (fees == null || fees.isEmpty()) {
	        return;
	    }

	    if (!payFeesUtil.hasPendingFees(fees)) {
	        return;
	    }

	    BigDecimal paymentAmount = payFeesUtil.inputPaymentAmount(fees);
	    if (paymentAmount == null) {
	        return;
	    }

	    payFeesUtil.processAndDisplayPayment(studentId, paymentAmount);
	}

	public void restoreStudent() {
		System.out.print("Enter Student ID to restore: ");
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid student ID.");
			scanner.next();
			return;
		}
		int studentId = scanner.nextInt();
		if (studentId <= 0) {
			System.out.println("No such student exists.");
			return;
		}

		boolean success = studentService.restoreStudentById(studentId);
		System.out.println(success ? "Student ID " + studentId + " restored successfully."
				: "No such student exists or is already active.");
	}

	// Helper: Print courses in tabular format
	private void printCourses(List<Course> courses) {
		System.out.printf("\n%-10s %-25s %-20s %-15s\n", "Course ID", "Course Name", "No. of Semesters", "Total Fee");
		System.out.println("-------------------------------------------------------------");
		for (Course c : courses) {
			String totalFee = (c.getTotal_fee() != null) ? "₹" + c.getTotal_fee() : "N/A";
			System.out.printf("%-10d %-25s %-20d %-15s\n", c.getCourse_id(), c.getCourse_name(), c.getNo_of_semester(),
					totalFee);
		}
	}
}
