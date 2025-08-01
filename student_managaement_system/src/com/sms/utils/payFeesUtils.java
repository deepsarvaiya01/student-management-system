package com.sms.utils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.payment.processor.PaymentProcessor;
import com.sms.service.FeeService;
import com.sms.service.StudentService;

public class payFeesUtils {
	private FeeService feeService;
	private StudentService studentService;
	private Scanner scanner = new Scanner(System.in);

	public payFeesUtils() throws SQLException {
		feeService = new FeeService();
		studentService = new StudentService();
	}

	public List<Student> showAndGetAllStudents() {
		System.out.println("\n📚 Available Students:");
		List<Student> students = feeService.getAllStudents();

		if (students.isEmpty()) {
			System.out.println("No students found.");
			return List.of();
		}

		printStudents(students);
		return students;
	}

	public int inputStudentId() {
		return InputValidator.getValidInteger(scanner, "\nEnter Student ID to pay fees: ", "Student ID");
	}

	public List<Course> showAndGetStudentCourses(int studentId) {
		System.out.println("\n📚 Courses for Student ID " + studentId + ":");
		List<Course> courses = studentService.getCoursesByStudentId(studentId);
		if (courses.isEmpty()) {
			System.out.println("No courses assigned to this student.");
			return List.of();
		}
		printCourses(courses);
		return courses;
	}

	public int inputCourseId(List<Course> courses) {
		int courseId = InputValidator.getValidInteger(scanner, "\nEnter Course ID to pay fees for: ", "Course ID");
		boolean validCourse = courses.stream().anyMatch(c -> c.getCourse_id() == courseId);
		if (!validCourse) {
			System.out.println("❌ Invalid Course ID. Please select a course from the list.");
			return -1;
		}
		return courseId;
	}

	public Fee showStudentFeeForCourse(int studentId, int courseId) {
		try {
			String result = feeService.getFeesByStudent(studentId);
			if (!result.equals("SUCCESS")) {
				System.out.println("❌ " + result);
				return null;
			}

			List<Fee> fees = feeService.getFeesListByStudent(studentId);
			Fee selectedFee = fees.stream().filter(fee -> fee.getCourseId() == courseId).findFirst().orElse(null);

			if (selectedFee == null) {
				// No fee record found; create a new one
				List<Course> courses = studentService.getCoursesByStudentId(studentId);
				Course selectedCourse = courses.stream().filter(c -> c.getCourse_id() == courseId).findFirst()
						.orElse(null);

				if (selectedCourse == null) {
					System.out.println("❌ Course ID " + courseId + " not assigned to Student ID " + studentId + ".");
					return null;
				}

				BigDecimal totalFee = selectedCourse.getTotal_fee();
				if (totalFee == null) {
					System.out.println("❌ Course ID " + courseId + " has no total fee defined.");
					return null;
				}

				// Get student_course_id from student_courses table
				int studentCourseId = feeService.getStudentCourseId(studentId, courseId);
				if (studentCourseId == -1) {
					System.out.println("❌ No student-course record found for Student ID " + studentId
							+ " and Course ID " + courseId + ".");
					return null;
				}

				// Get student name
				List<Student> students = feeService.getAllStudents();
				Student selectedStudent = students.stream().filter(s -> s.getStudent_id() == studentId).findFirst()
						.orElse(null);
				if (selectedStudent == null) {
					System.out.println("❌ Student ID " + studentId + " not found.");
					return null;
				}

				// Create new fee record
				selectedFee = new Fee();
				selectedFee.setStudentCourseId(studentCourseId);
				selectedFee.setPaidAmount(BigDecimal.ZERO);
				selectedFee.setPendingAmount(totalFee);
				selectedFee.setTotalFee(totalFee);
				selectedFee.setCourseId(courseId);
				selectedFee.setStudentName(selectedStudent.getName());
				selectedFee.setCourseName(selectedCourse.getCourse_name());
				String createResult = feeService.createFeeRecord(studentCourseId, totalFee);
				if (!createResult.equals("SUCCESS")) {
					System.out.println("❌ Failed to create fee record: " + createResult);
					return null;
				}
			}

			System.out.println(
					"\n📊 Current Fee Status for Student ID " + studentId + " and Course ID " + courseId + ":");
			Fee.printHeader();
			System.out.println(selectedFee);
			return selectedFee;
		} catch (Exception e) {
			System.out.println("❌ Error retrieving fee record: " + e.getMessage());
			return null;
		}
	}

	public boolean hasPendingFees(Fee fee) {
		if (fee == null) {
			return false;
		}
		boolean pending = fee.getPendingAmount().compareTo(BigDecimal.ZERO) > 0;
		if (!pending) {
			System.out.println("\n✅ All fees are already paid for this course!");
		}
		return pending;
	}

	public BigDecimal inputPaymentAmount(Fee fee) {
		if (fee == null) {
			return null;
		}
		BigDecimal amount = InputValidator.getValidDecimal(scanner, "\nEnter payment amount: ₹", "Payment Amount");

		// Validate payment amount against pending fees
		BigDecimal totalPending = fee.getPendingAmount();
		if (amount.compareTo(totalPending) > 0) {
			System.out.println("❌ Payment amount (₹" + amount + ") exceeds pending amount (₹" + totalPending + ").");
			return null;
		}

		return amount;
	}

	public void processAndDisplayPayment(int studentId, int courseId, BigDecimal paymentAmount) {
		try {
			System.out.println("\nChoose payment method:");
			System.out.println("1. Cash");
			System.out.println("2. Card");
			System.out.println("3. UPI");
			System.out.println("0. Cancel");
			int choice = InputValidator.getValidIntegerInRange(scanner, "👉 Enter your choice (0-3): ",
					"Payment Method", 0, 3);

			if (choice == 0) {
				System.out.println("Payment cancelled.");
				return;
			}

			PaymentProcessor processor = new PaymentProcessor();
			boolean paymentSuccess = processor.process(studentId, paymentAmount, choice, scanner);

			if (paymentSuccess) {
				String result = feeService.updateFeePayment(studentId, paymentAmount, courseId);
				if (result.contains("successfully")) {
					System.out.println("\n✅ Payment of ₹" + paymentAmount + " processed successfully!");
					System.out.println("Updated fee status:");
					Fee selectedFee = feeService.getFeesListByStudent(studentId).stream()
							.filter(fee -> fee.getCourseId() == courseId).findFirst().orElse(null);
					if (selectedFee != null) {
						Fee.printHeader();
						System.out.println(selectedFee);
					} else {
						System.out.println("❌ Failed to retrieve updated fee record.");
					}
				} else {
					System.out.println("❌ " + result);
				}
			} else {
				System.out.println("❌ Payment failed. Please try again.");
			}
		} catch (Exception e) {
			System.out.println("❌ Error while processing payment: " + e.getMessage());
		}
	}

	private void printStudents(List<Student> students) {
		System.out.printf("\n%-10s %-20s %-25s %-10s\n", "Student ID", "Name", "Email", "GR Number");
		System.out.println("-------------------------------------------------------------");
		for (Student s : students) {
			System.out.printf("%-10d %-20s %-25s %-10d\n", s.getStudent_id(), s.getName(), s.getEmail(),
					s.getGr_number());
		}
	}

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