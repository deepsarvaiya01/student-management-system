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
import com.sms.utils.InputValidator;

public class StudentController {

	private StudentService studentService;
	@SuppressWarnings("unused")
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
		scanner.nextLine(); // Clear buffer from menu choice
		String name = InputValidator.getValidName(scanner, "Enter Student Name: ");
		int grNumber = InputValidator.getValidGRNumber(scanner, "Enter GR Number: ");
		scanner.nextLine(); // Clear buffer after GR number
		String email = InputValidator.getValidEmail(scanner, "Enter Email: ");
		String city = InputValidator.getValidCity(scanner, "Enter City: ");
		String mobileNo = InputValidator.getValidMobile(scanner, "Enter Mobile No: ");
		int age = InputValidator.getValidAge(scanner, "Enter Age: ");

		System.out.println("\nAvailable Courses:");
		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available. Please add a course first.");
			return;
		}
		printCourses(courses);

		int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to assign: ", "Course ID");

		Student student = new Student();
		student.setName(name);
		student.setGr_number(grNumber);
		student.setEmail(email);
		student.setCity(city);
		student.setMobile_no(mobileNo);
		student.setAge(age);

		String result = studentService.addStudentWithProfileAndCourse(student, courseId);
		System.out.println(result);
	}

	// Assign Course to Student
	public void assignCourse() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to assign a course: ", "Student ID");

		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available.");
			return;
		}
		System.out.println("\nAvailable Courses:");
		printCourses(courses);

		int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to assign: ", "Course ID");

		String result = studentService.assignCourseToStudent(studentId, courseId);
		System.out.println(result);
	}

	// View All Courses by Student ID
	public void viewAllCourses() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID: ", "Student ID");
		String result = studentService.readAllCourses(studentId);
		if (result.equals("SUCCESS")) {
			List<Course> courses = studentService.getCoursesByStudentId(studentId);
			System.out.println("\nCourses for Student ID " + studentId + ":");
			printCourses(courses);
		} else {
			System.out.println(result);
		}
	}

	// Search Student by ID
	public void searchStudent() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to search: ", "Student ID");
		String result = studentService.searchStudentById(studentId);
		if (result.equals("SUCCESS")) {
			Student student = studentService.getStudentById(studentId);
			System.out.println("\nStudent Details:");
			Student.printHeader();
			System.out.println(student);
		} else {
			System.out.println(result);
		}
	}

	// Delete Student by ID
	public void deleteStudent() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to delete: ", "Student ID");
		String searchResult = studentService.searchStudentById(studentId);
		if (searchResult.equals("SUCCESS")) {
			Student student = studentService.getStudentById(studentId);
			System.out.println("\nStudent Details:");
			Student.printHeader();
			System.out.println(student);

			boolean confirmation = InputValidator.getValidConfirmation(scanner,
					"Are you sure you want to delete this student? (y/n): ");
			if (!confirmation) {
				System.out.println("Deletion cancelled.");
				return;
			}

			String result = studentService.deleteStudentById(studentId);
			System.out.println(result);
		} else {
			System.out.println(searchResult);
		}
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

		int studentId = InputValidator.getValidInteger(scanner, "\nEnter Student ID to pay fees: ", "Student ID");
		String searchResult = studentService.searchStudentById(studentId);
		if (!searchResult.equals("SUCCESS")) {
			System.out.println(searchResult);
			return;
		}

		List<Course> courses = payFeesUtil.showAndGetStudentCourses(studentId);
		if (courses.isEmpty()) {
			return;
		}

		int courseId = payFeesUtil.inputCourseId(courses);
		if (courseId == -1) {
			return;
		}

		Fee fee = payFeesUtil.showStudentFeeForCourse(studentId, courseId);
		if (fee == null) {
			return;
		}

		if (!payFeesUtil.hasPendingFees(fee)) {
			return;
		}

		BigDecimal paymentAmount = payFeesUtil.inputPaymentAmount(fee);
		if (paymentAmount == null) {
			return;
		}

		payFeesUtil.processAndDisplayPayment(studentId, courseId, paymentAmount);
	}

	public void restoreStudent() {
		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to restore: ", "Student ID");
		String result = studentService.restoreStudentById(studentId);
		System.out.println(result);
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