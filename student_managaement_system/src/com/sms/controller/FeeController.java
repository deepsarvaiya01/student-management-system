package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.service.FeeService;
import com.sms.utils.InputValidator;

public class FeeController {

	private FeeService feeService;
	private Scanner scanner = new Scanner(System.in);

	public FeeController() throws SQLException {
		this.feeService = new FeeService();
	}

	// View Total Paid Fees
	public void viewTotalPaidFees() {
		BigDecimal totalPaid = feeService.getTotalPaidFees();
		System.out.println("\n💰 Total Paid Fees: ₹" + totalPaid);
		System.out.println("=====================================");
	}

	// View Total Pending Fees
	public void viewTotalPendingFees() {
		BigDecimal totalPending = feeService.getTotalPendingFees();
		System.out.println("\n⏳ Total Pending Fees: ₹" + totalPending);
		System.out.println("=====================================");
	}

	// View Fees By Student
	public void viewFeesByStudent() {
		System.out.println("\n📚 Available Students:");
		List<Student> students = feeService.getAllStudents();
		if (students.isEmpty()) {
			System.out.println("No students found.");
			return;
		}

		printStudents(students);

		int studentId = InputValidator.getValidInteger(scanner, "\nEnter Student ID to view fees: ", "Student ID");
		String result = feeService.getFeesByStudent(studentId);
		if (result.equals("SUCCESS")) {
			List<Fee> fees = feeService.getFeesListByStudent(studentId);
			System.out.println("\nFees for Student ID " + studentId + ":");
			Fee.printHeader();
			for (Fee fee : fees) {
				System.out.println(fee);
			}
		} else {
			System.out.println(result);
		}
	}

	// View Fees By Course
	public void viewFeesByCourse() {
		System.out.println("\nAvailable Courses:");
		List<Course> courses = feeService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses found.");
			return;
		}

		printCourses(courses);

		int courseId = InputValidator.getValidInteger(scanner, "\nEnter Course ID to view fees: ", "Course ID");
		String result = feeService.getFeesByCourse(courseId);
		if (result.equals("SUCCESS")) {
			List<Fee> fees = feeService.getFeesListByCourse(courseId);
			System.out.println("\nFees for Course ID " + courseId + ":");
			Fee.printHeader();
			for (Fee fee : fees) {
				System.out.println(fee);
			}
		} else {
			System.out.println(result);
		}
	}

	// Update Fees Of A Course
	public void updateFeesOfCourse() {
		System.out.println("\nAvailable Courses:");
		List<Course> courses = feeService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses found.");
			return;
		}

		printCourses(courses);

		int courseId = InputValidator.getValidInteger(scanner, "\nEnter Course ID to update fees: ", "Course ID");
		BigDecimal newTotalFee = InputValidator.getValidDecimal(scanner, "Enter new total fee amount: ₹", "Fee Amount");

		String result = feeService.updateCourseFees(courseId, newTotalFee);
		System.out.println(result);
	}

	// View Total Earning
	public void viewTotalEarning() {
		BigDecimal totalEarning = feeService.getTotalEarning();
		System.out.println("\nTotal Earning: ₹" + totalEarning);
		System.out.println("=====================================");
	}

	// Helper: Print students in tabular format
	private void printStudents(List<Student> students) {
		System.out.printf("\n%-10s %-20s %-25s %-10s\n", "Student ID", "Name", "Email", "GR Number");
		System.out.println("-------------------------------------------------------------");
		for (Student s : students) {
			System.out.printf("%-10d %-20s %-25s %-10d\n", s.getStudent_id(), s.getName(), s.getEmail(),
					s.getGr_number());
		}
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