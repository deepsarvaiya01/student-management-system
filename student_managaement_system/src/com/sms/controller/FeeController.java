package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.service.FeeService;
import com.sms.utils.InputValidator;
import com.sms.utils.HelperUtils;

public class FeeController {
	private FeeService feeService;
	private java.util.Scanner scanner = new java.util.Scanner(System.in);

	public FeeController() throws SQLException {
		this.feeService = new FeeService();
	}

	public void viewTotalPaidFees() {
		BigDecimal totalPaid = feeService.getTotalPaidFees();
		System.out.println("\n💰 Total Paid Fees: ₹" + totalPaid);
		System.out.println("=====================================");
	}

	public void viewTotalPendingFees() {
		BigDecimal totalPending = feeService.getTotalPendingFees();
		System.out.println("\n⏳ Total Pending Fees: ₹" + totalPending);
		System.out.println("=====================================");
	}

	public void viewFeesByStudent() {
		System.out.println("\n📚 Available Students:");
		List<Student> students = feeService.getAllStudents();
		if (students.isEmpty()) {
			System.out.println("No students found.");
			return;
		}

		HelperUtils.printStudents(students);

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to view fees: ", "Student ID");
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

	public void viewFeesByCourse() {
		System.out.println("\nAvailable Courses:");
		List<Course> courses = feeService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses found.");
			return;
		}

		HelperUtils.printCourses(courses);

		int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to view fees: ", "Course ID");
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

	public void updateFeesOfCourse() {
		System.out.println("\nAvailable Courses:");
		List<Course> courses = feeService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses found.");
			return;
		}

		HelperUtils.printCourses(courses);

		int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to update fees: ", "Course ID");
		BigDecimal newTotalFee = InputValidator.getValidDecimal(scanner, "Enter new total fee amount: ₹", "Fee Amount");

		String result = feeService.updateCourseFees(courseId, newTotalFee);
		System.out.println(result);
	}

	public void viewTotalEarning() {
		BigDecimal totalEarning = feeService.getTotalEarning();
		System.out.println("\nTotal Earning: ₹" + totalEarning);
		System.out.println("=====================================");
	}


}