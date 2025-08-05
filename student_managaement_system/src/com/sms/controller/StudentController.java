package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.model.Subject;
import com.sms.model.Teacher;
import com.sms.payment.processor.PaymentProcessor;
import com.sms.service.CourseService;
import com.sms.service.FeeService;
import com.sms.service.StudentService;

import com.sms.utils.InputValidator;
import com.sms.utils.payFeesUtils;
import com.sms.utils.HelperUtils;

public class StudentController {
	private StudentService studentService;
	private FeeService feeService;
	private CourseService courseService;
	private Scanner scanner = new Scanner(System.in);

	public StudentController() throws SQLException {
		this.studentService = new StudentService();
		this.feeService = new FeeService();
		this.courseService = new CourseService();
	}

	public void viewAllStudents() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students found.");
			return;
		}

		HelperUtils.printStudents(students);
	}

	// Add New Student with Profile and Course Assignment
	public void addNewStudent() {
		String name = InputValidator.getValidName(scanner, "Enter Student Name: ");
		int lastGrNumber = studentService.getLastGrNumber();
		int grNumber = (lastGrNumber == 0) ? 1001 : lastGrNumber + 1;
		//System.out.println("Auto-assigned GR Number: " + grNumber);
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
		HelperUtils.printCourses(courses);

		int courseId = InputValidator.getValidIntegerWithNewline(scanner, "Enter Course ID to assign: ", "Course ID");

		// Get available subjects for the selected course
		List<Subject> availableSubjects = studentService.getSubjectsByCourseId(courseId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No subjects available for this course. Please add subjects to the course first.");
			return;
		}

		// Separate mandatory and elective subjects
		List<Subject> mandatorySubjects = availableSubjects.stream()
				.filter(s -> "mandatory".equalsIgnoreCase(s.getSubject_type())).collect(Collectors.toList());
		List<Subject> electiveSubjects = availableSubjects.stream()
				.filter(s -> "elective".equalsIgnoreCase(s.getSubject_type())).collect(Collectors.toList());

		System.out.println("\n📚 Available Subjects for Course ID " + courseId + ":");
		System.out.println("=".repeat(80));

		if (!mandatorySubjects.isEmpty()) {
			System.out.println("🔴 MANDATORY SUBJECTS (Must select all):");
			for (Subject subject : mandatorySubjects) {
				System.out.printf("   ID: %-3d | %-40s | Type: %s\n", subject.getSubject_id(),
						subject.getSubject_name(), subject.getSubject_type());
			}
		}

		if (!electiveSubjects.isEmpty()) {
			System.out.println("\n🟢 ELECTIVE SUBJECTS (Optional):");
			for (Subject subject : electiveSubjects) {
				System.out.printf("   ID: %-3d | %-40s | Type: %s\n", subject.getSubject_id(),
						subject.getSubject_name(), subject.getSubject_type());
			}
		}

		// Select mandatory subjects
		List<Integer> selectedSubjectIds = new ArrayList<>();
		if (!mandatorySubjects.isEmpty()) {
			System.out.println("\n🔴 Selecting Mandatory Subjects:");
			for (Subject subject : mandatorySubjects) {
				selectedSubjectIds.add(subject.getSubject_id());
				System.out.println("   ✓ Auto-selected: " + subject.getSubject_name());
			}
		}

		// Select elective subjects
		if (!electiveSubjects.isEmpty()) {
			System.out.println("\n🟢 Select Elective Subjects:");
			System.out.print("Enter elective subject IDs (comma-separated, or press Enter to skip): ");
			String electiveInput = scanner.nextLine().trim();

			if (!electiveInput.isEmpty()) {
				try {
					String[] electiveIds = electiveInput.split(",");
					for (String idStr : electiveIds) {
						int electiveId = Integer.parseInt(idStr.trim());
						boolean isValidElective = electiveSubjects.stream()
								.anyMatch(s -> s.getSubject_id() == electiveId);
						if (isValidElective) {
							selectedSubjectIds.add(electiveId);
							Subject selectedSubject = electiveSubjects.stream()
									.filter(s -> s.getSubject_id() == electiveId).findFirst().orElse(null);
							System.out.println("   ✓ Selected: " + selectedSubject.getSubject_name());
						} else {
							System.out.println("   ❌ Invalid elective subject ID: " + electiveId);
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("   ❌ Invalid input format. No elective subjects selected.");
				}
			} else {
				System.out.println("   ⏭️ No elective subjects selected.");
			}
		}

		Student student = new Student();
		student.setName(name);
		student.setGr_number(grNumber);
		student.setEmail(email);
		student.setCity(city);
		student.setMobile_no(mobileNo);
		student.setAge(age);

		String result = studentService.addStudentWithProfileAndCourseAndSubjects(student, courseId, selectedSubjectIds);
		System.out.println(result);

		// Ask if the student wants to pay fees immediately
		if (result.contains("successfully")) {
			askForFeePayment(student.getName(), courseId);
		}
	}

	// Ask student if they want to pay fees immediately after registration
	private void askForFeePayment(String studentName, int courseId) {
		System.out.println("\n💰 === IMMEDIATE FEE PAYMENT OPTION ===");
		System.out.println("Student: " + studentName);
		System.out.println("Course ID: " + courseId);

		boolean wantsToPay = InputValidator.getValidConfirmation(scanner, "\nWould you like to pay fees now? (y/n): ");

		if (wantsToPay) {
			try {
				// Get the newly created student ID
				List<Student> students = studentService.readAllStudent();
				Student newStudent = students.stream().filter(s -> s.getName().equals(studentName)).findFirst()
						.orElse(null);

				if (newStudent != null) {
					processImmediateFeePayment(newStudent.getStudent_id(), courseId);
				} else {
					System.out.println(
							"❌ Could not find the newly created student. Please use the main fee payment option.");
				}
			} catch (Exception e) {
				System.out.println("❌ Error processing immediate fee payment: " + e.getMessage());
				System.out.println("Please use the main fee payment option from the menu.");
			}
		} else {
			System.out.println("✅ Fee payment skipped. You can pay fees later from the main menu.");
		}
	}

	// Process immediate fee payment for newly created student
	private void processImmediateFeePayment(int studentId, int courseId) {
		System.out.println("\n💰 === PROCESSING IMMEDIATE FEE PAYMENT ===");

		try {
			// Show fee status
			Fee fee = showFeeStatus(studentId, courseId);
			if (fee == null) {
				return;
			}

			// Check if there are pending fees
			if (!hasPendingFees(fee)) {
				return;
			}

			// Get payment amount
			BigDecimal paymentAmount = getPaymentAmount(fee);
			if (paymentAmount == null) {
				return;
			}

			// Process payment
			processPayment(studentId, courseId, paymentAmount);

		} catch (Exception e) {
			System.out.println("❌ Error during immediate fee payment: " + e.getMessage());
		}
	}

	// Show fee status for the student and course
	private Fee showFeeStatus(int studentId, int courseId) {
		try {
			FeeService feeService = new FeeService();
			String result = feeService.getFeesByStudent(studentId);
			if (result.equals("SUCCESS")) {
				List<Fee> fees = feeService.getFeesListByStudent(studentId);
				Fee selectedFee = fees.stream().filter(fee -> fee.getCourseId() == courseId).findFirst().orElse(null);

				if (selectedFee == null) {
					System.out.println("❌ No fee record found for Course ID " + courseId);
					return null;
				}

				System.out.println("\n📊 Current Fee Status:");
				Fee.printHeader();
				System.out.println(selectedFee);
				return selectedFee;
			} else {
				System.out.println("❌ " + result);
				return null;
			}
		} catch (SQLException e) {
			System.out.println("❌ Error retrieving fee status: " + e.getMessage());
			return null;
		}
	}

	// Check if there are pending fees
	private boolean hasPendingFees(Fee fee) {
		if (fee == null) {
			return false;
		}
		boolean pending = fee.getPendingAmount().compareTo(BigDecimal.ZERO) > 0;
		if (!pending) {
			System.out.println("\n✅ All fees are already paid for this course!");
		}
		return pending;
	}

	// Get payment amount from user
	private BigDecimal getPaymentAmount(Fee fee) {
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

	// Process the payment
	private void processPayment(int studentId, int courseId, BigDecimal paymentAmount) {
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
					Fee updatedFee = feeService.getFeesListByStudent(studentId).stream()
							.filter(fee -> fee.getCourseId() == courseId).findFirst().orElse(null);
					if (updatedFee != null) {
						Fee.printHeader();
						System.out.println(updatedFee);
					}
				} else {
					System.out.println("❌ " + result);
				}
			} else {
				System.out.println("❌ Payment failed. Please try again later.");
			}
		} catch (Exception e) {
			System.out.println("❌ Error processing payment: " + e.getMessage());
		}
	}

	public void assignCourse() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		HelperUtils.printStudents(students);

		int studentId = InputValidator.getValidInteger(scanner, "\nEnter Student ID to assign a course: ",
				"Student ID");
		String searchResult = studentService.searchStudentById(studentId);
		if (!searchResult.equals("SUCCESS")) {
			System.out.println("❌ " + searchResult);
			return;
		}

		// 🔽 Fetch all courses
		List<Course> allCourses = courseService.getAllCourses();

		// 🔽 Fetch already assigned courses
		List<Course> assignedCourses = studentService.getCoursesByStudentId(studentId);

		// 🔁 Filter out assigned courses from all courses
		List<Course> unassignedCourses = allCourses.stream()
				.filter(c -> assignedCourses.stream().noneMatch(ac -> ac.getCourse_id() == c.getCourse_id()))
				.collect(Collectors.toList());

		if (unassignedCourses.isEmpty()) {
			System.out.println("🎉 All courses are already assigned to this student!");
			return;
		}

		// 🔽 Display unassigned courses
		System.out.println("Available Courses:");
		for (Course course : unassignedCourses) {
			System.out.println(course.getCourse_id() + ". " + course.getCourse_name());
		}

		int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to assign: ", "Course ID");
		if (unassignedCourses.stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("❌ Invalid Course ID.");
			return;
		}

		List<Subject> availableSubjects = studentService.getSubjectsByCourseId(courseId);
		if (availableSubjects.isEmpty()) {
			System.out.println("❌ No subjects available for Course ID " + courseId + ".");
			return;
		}

		System.out.println("\n📚 Available Subjects for Course ID " + courseId + ":");
		System.out.println("=".repeat(80));

		List<Subject> mandatorySubjects = availableSubjects.stream()
				.filter(s -> "mandatory".equalsIgnoreCase(s.getSubject_type())).collect(Collectors.toList());
		List<Subject> electiveSubjects = availableSubjects.stream()
				.filter(s -> "elective".equalsIgnoreCase(s.getSubject_type())).collect(Collectors.toList());

		if (!mandatorySubjects.isEmpty()) {
			System.out.println("🔴 MANDATORY SUBJECTS (Must select all):");
			for (Subject subject : mandatorySubjects) {
				System.out.printf("   ID: %-3d | %-40s | Type: %s\n", subject.getSubject_id(),
						subject.getSubject_name(), subject.getSubject_type());
			}
		}

		if (!electiveSubjects.isEmpty()) {
			System.out.println("\n🟢 ELECTIVE SUBJECTS (Optional):");
			for (Subject subject : electiveSubjects) {
				System.out.printf("   ID: %-3d | %-40s | Type: %s\n", subject.getSubject_id(),
						subject.getSubject_name(), subject.getSubject_type());
			}
		}

		List<Integer> selectedSubjectIds = new ArrayList<>();
		if (!mandatorySubjects.isEmpty()) {
			System.out.println("\n🔴 Selecting Mandatory Subjects:");
			for (Subject subject : mandatorySubjects) {
				selectedSubjectIds.add(subject.getSubject_id());
				System.out.println("   ✓ Auto-selected: " + subject.getSubject_name());
			}
		}

		if (!electiveSubjects.isEmpty()) {
			System.out.println("\n🟢 Select Elective Subjects:");
			scanner.nextLine();
			System.out.print("Enter elective subject IDs (comma-separated, or press Enter to skip): ");
			String electiveInput = scanner.nextLine().trim();

			if (!electiveInput.isEmpty()) {
				try {
					String[] electiveIds = electiveInput.split(",");
					for (String idStr : electiveIds) {
						int electiveId = Integer.parseInt(idStr.trim());
						boolean isValidElective = electiveSubjects.stream()
								.anyMatch(s -> s.getSubject_id() == electiveId);
						if (isValidElective) {
							selectedSubjectIds.add(electiveId);
							Subject selectedSubject = electiveSubjects.stream()
									.filter(s -> s.getSubject_id() == electiveId).findFirst().orElse(null);
							System.out.println("   ✓ Selected: " + selectedSubject.getSubject_name());
						} else {
							System.out.println("   ❌ Invalid elective subject ID: " + electiveId);
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("   ❌ Invalid input format. No elective subjects selected.");
				}
			} else {
				System.out.println("   ⏭️ No elective subjects selected.");
			}
		}

		String result = studentService.assignCourseToStudent(studentId, courseId, selectedSubjectIds);
		System.out.println(result);

		if (result.toLowerCase().contains("success")) {
			String studentName = students.stream().filter(s -> s.getStudent_id() == studentId).map(Student::getName)
					.findFirst().orElse("Student");

			askForFeePayment(studentName, courseId);
		}
	}

	public void viewAllCourses() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		HelperUtils.printStudents(students);

		int studentId = InputValidator.getValidInteger(scanner, "\nEnter Student ID: ", "Student ID");
		String result = studentService.readAllCourses(studentId);
		if (result.equals("SUCCESS")) {
			List<Course> courses = studentService.getCoursesByStudentId(studentId);
			System.out.println("\nCourses for Student ID " + studentId + ":");
			HelperUtils.printCourses(courses);
		} else {
			System.out.println(result);
		}
	}

	public void searchStudent() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		System.out.printf("\n%-10s | %-20s\n", "Student ID", "Name");
		System.out.println("-------------------------");
		for (Student s : students) {
			System.out.printf("%-10d | %-20s\n", s.getStudent_id(), s.getName());
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

	public void deleteStudent() {
		List<Student> students = studentService.readAllStudent();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		HelperUtils.printStudents(students);

		int studentId = InputValidator.getValidInteger(scanner, "\nEnter Student ID to delete: ", "Student ID");
		String searchResult = studentService.searchStudentById(studentId);
		if (searchResult.equals("SUCCESS")) {
			Student student = studentService.getStudentById(studentId);
			System.out.println("\nStudent Details:");
			Student.printHeader();
			System.out.println(student);

			scanner.nextLine();
			boolean confirmation = InputValidator.getValidConfirmation(scanner,
					"\nAre you sure you want to delete this student? (y/n): ");
			if (!confirmation) {
				System.out.println("Deletion cancelled.");
				return;
			}

			if (!studentService.isFeeClearedForStudent(studentId)) {
				System.out.println("❌ This student has pending fees. Please clear the dues before deletion.");
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
				
		List<Student> list = studentService.fetchInactiveStudents();
		if (list.isEmpty()) {
			System.out.println("No inactive students found.");
			return;
		}

		System.out.println("\n📋 Inactive Students");
		String line = "+-----+----------------------+----------------------+------------+";
		String format = "| %-3s | %-20s | %-20s | %-10s |%n";

		System.out.println(line);
		System.out.printf(format, "ID", "Name", "Email", "GR number");
		System.out.println(line);

		for (Student s : list) {
			System.out.printf(format, s.getStudent_id(), s.getName(), s.getEmail(), s.getGr_number());
		}

		System.out.println(line);
		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to restore: ", "Student ID");
		String result = studentService.restoreStudentById(studentId);
		System.out.println(result);
	}

}