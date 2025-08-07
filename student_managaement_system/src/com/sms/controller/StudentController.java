package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.sms.main.FeeNotifierMain;
import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Gender;
import com.sms.model.Student;
import com.sms.model.Subject;
import com.sms.payment.processor.PaymentProcessor;
import com.sms.service.CourseService;
import com.sms.service.FeeService;
import com.sms.service.StudentService;
import com.sms.utils.HelperUtils;
import com.sms.utils.InputValidator;
import com.sms.utils.payFeesUtils;

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

	public void addNewStudent() {
		String name = InputValidator.getValidName(scanner, "Enter Student Name: ");
		int lastGrNumber = studentService.getLastGrNumber();
		int grNumber = (lastGrNumber == 0) ? 1001 : lastGrNumber + 1;
		// System.out.println("Auto-assigned GR Number: " + grNumber);
		String email = InputValidator.getValidEmail(scanner, "Enter Email: ", studentService);
		String city = InputValidator.getValidCity(scanner, "Enter City: ");
		String mobileNo = InputValidator.getValidMobile(scanner, "Enter Mobile No: ", name, studentService);
		int age = InputValidator.getValidAge(scanner, "Enter Age: ");
		Gender gender = InputValidator.getValidGender(scanner,
				"Enter Gender (M for Male, F for Female, O for Other): ");

		System.out.println("\nAvailable Courses:");
		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available. Please add a course first.");
			return;
		}
		HelperUtils.printCourses(courses);

		int courseId = InputValidator.getValidIntegerWithNewline(scanner, "Enter Course ID to assign: ", "Course ID");

		List<Subject> availableSubjects = studentService.getSubjectsByCourseId(courseId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No subjects available for this course. Please add subjects to the course first.");
			return;
		}

		List<Integer> selectedSubjectIds = HelperUtils.selectSubjectsForCourse(courseId, availableSubjects, scanner);

		Student student = new Student();
		student.setName(name);
		student.setGr_number(grNumber);
		student.setEmail(email);
		student.setCity(city);
		student.setMobile_no(mobileNo);
		student.setAge(age);
		student.setGender(gender);
		String result = studentService.addStudentWithProfileAndCourseAndSubjects(student, courseId, selectedSubjectIds);

		if (result.contains("successfully")) {
			displayStudentSuccessDetails(student, courseId, selectedSubjectIds, availableSubjects);
			askForFeePayment(student.getName(), courseId);
		} else {
			System.out.println("eroor");
		}
	}

	private void displayStudentSuccessDetails(Student student, int courseId, List<Integer> selectedSubjectIds,
			List<Subject> availableSubjects) {
		try {
			Course course = courseService.getCourseById(courseId);
			List<Subject> selectedSubjects = availableSubjects.stream()
					.filter(subject -> selectedSubjectIds.contains(subject.getSubject_id()))
					.collect(Collectors.toList());

			Student createdStudent = null;
			List<Student> allStudents = studentService.readAllStudent();
			for (Student s : allStudents) {
				if (s.getName().equals(student.getName()) && s.getEmail().equals(student.getEmail())
						&& s.getGr_number() == student.getGr_number()) {
					createdStudent = s;
					break;
				}
			}
			String studentIdDisplay = "Auto-generated (will be assigned)";
			if (createdStudent != null) {
				studentIdDisplay = String.valueOf(createdStudent.getStudent_id());
			}

			System.out.println("\n" + "╔" + "═".repeat(78) + "╗");
			System.out.println("║" + " ".repeat(25) + "STUDENT ADDED SUCCESSFULLY!" + " ".repeat(25) + "║");
			System.out.println("╚" + "═".repeat(78) + "╝");

			// Student information card
			System.out.println("\nSTUDENT INFORMATION");
			System.out.println("┌" + "─".repeat(25) + "┬" + "─".repeat(50) + "┐");
			System.out.printf("│ %-23s │ %-48s │%n", "Student ID", studentIdDisplay);
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48s │%n", "Name", student.getName());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48d │%n", "GR Number", student.getGr_number());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48s │%n", "Email", student.getEmail());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48s │%n", "Mobile No", student.getMobile_no());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48s │%n", "City", student.getCity());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48d │%n", "Age", student.getAge());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48s │%n", "Gender",
					student.getGender() != null ? student.getGender().getDisplayName() : "N/A");
			System.out.println("└" + "─".repeat(25) + "┴" + "─".repeat(50) + "┘");

			if (course != null) {
				System.out.println("\nASSIGNED COURSE");
				System.out.println("┌" + "─".repeat(25) + "┬" + "─".repeat(50) + "┐");
				System.out.printf("│ %-23s │ %-48d │%n", "Course ID", course.getCourse_id());
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
				System.out.printf("│ %-23s │ %-48s │%n", "Course Name", course.getCourse_name());
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
				System.out.printf("│ %-23s │ %-48d │%n", "Semesters", course.getNo_of_semester());
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
				System.out.printf("│ %-23s │ ₹%-46s │%n", "Total Fee",
						course.getTotal_fee() != null ? course.getTotal_fee().toString() : "N/A");
				System.out.println("└" + "─".repeat(25) + "┴" + "─".repeat(50) + "┘");
			}

			if (!selectedSubjects.isEmpty()) {
				System.out.println("\nASSIGNED SUBJECTS");
				System.out.println("┌" + "─".repeat(10) + "┬" + "─".repeat(40) + "┬" + "─".repeat(25) + "┐");
				System.out.printf("│ %-8s │ %-38s │ %-23s │%n", "ID", "Subject Name", "Type");
				System.out.println("├" + "─".repeat(10) + "┼" + "─".repeat(40) + "┼" + "─".repeat(25) + "┤");

				for (Subject subject : selectedSubjects) {
					
					String displayType = (subject.getSubject_type() != null)
							? subject.getSubject_type().substring(0, 1).toUpperCase()
									+ subject.getSubject_type().substring(1).toLowerCase()
							: "N/A";

					String truncatedSubjectName = subject.getSubject_name().length() > 38
							? subject.getSubject_name().substring(0, 35) + "..."
							: subject.getSubject_name();

					System.out.printf("│ %-8d │ %-38s │ %-23s │%n", subject.getSubject_id(), truncatedSubjectName,
							displayType);
				}
				System.out.println("└" + "─".repeat(10) + "┴" + "─".repeat(40) + "┴" + "─".repeat(25) + "┘");

				long mandatoryCount = selectedSubjects.stream()
						.filter(s -> "mandatory".equalsIgnoreCase(s.getSubject_type())).count();
				long electiveCount = selectedSubjects.stream()
						.filter(s -> "elective".equalsIgnoreCase(s.getSubject_type())).count();

				System.out.println("\nSUBJECT SUMMARY");
				System.out.println("┌" + "─".repeat(25) + "┬" + "─".repeat(15) + "┐");
				System.out.printf("│ %-23s │ %-13s │%n", "Metric", "Count");
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Total Subjects", selectedSubjects.size());
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Mandatory Subjects", mandatoryCount);
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Elective Subjects", electiveCount);
				System.out.println("└" + "─".repeat(25) + "┴" + "─".repeat(15) + "┘");
			}

			System.out.println("\nSUCCESS MESSAGE");
			System.out.println("┌" + "─".repeat(78) + "┐");
			String studentName = student.getName();
			String courseName = course != null ? course.getCourse_name() : "N/A";

			// Truncate long names for better display
			if (studentName.length() > 30) {
				studentName = studentName.substring(0, 27) + "...";
			}
			if (courseName.length() > 30) {
				courseName = courseName.substring(0, 27) + "...";
			}

			// Format the success messages to fit within the box
			String msg1 = String.format("│ Student '%s' has been successfully registered!", studentName);
			String msg2 = String.format("│ Course '%s' has been assigned.", courseName);
			String msg3 = String.format("│ %d subject(s) have been assigned to the student.",
					selectedSubjects.size());
			String msg4 = "│ You can now proceed with fee payment or do it later from the menu.";

			// Pad each message to fit the 78-character box
			System.out.printf("%-78s│%n", msg1);
			System.out.printf("%-78s│%n", msg2);
			System.out.printf("%-78s│%n", msg3);
			System.out.printf("%-78s│%n", msg4);
			System.out.println("└" + "─".repeat(78) + "┘");

		} catch (Exception e) {
			System.out.println("Error displaying student details: " + e.getMessage());
		}
	}

	private void askForFeePayment(String studentName, int courseId) {
		System.out.println("\n=== IMMEDIATE FEE PAYMENT OPTION ===");
		System.out.println("Student: " + studentName);
		System.out.println("Course ID: " + courseId);

		boolean wantsToPay = InputValidator.getValidConfirmation(scanner, "\nWould you like to pay fees now? (y/n): ");

		if (wantsToPay) {
			try {
				List<Student> students = studentService.readAllStudent();
				Student newStudent = students.stream().filter(s -> s.getName().equals(studentName)).findFirst()
						.orElse(null);

				if (newStudent != null) {
					processImmediateFeePayment(newStudent.getStudent_id(), courseId);
				} else {
					System.out.println(
							"Could not find the newly created student. Please use the main fee payment option.");
				}
			} catch (Exception e) {
				System.out.println("Error processing immediate fee payment: " + e.getMessage());
				System.out.println("Please use the main fee payment option from the menu.");
			}
		} else {
			System.out.println("Fee payment skipped. You can pay fees later from the main menu.");
		}
	}

	private void processImmediateFeePayment(int studentId, int courseId) {
		System.out.println("\n=== PROCESSING IMMEDIATE FEE PAYMENT ===");

		try {
			Fee fee = showFeeStatus(studentId, courseId);
			if (fee == null) {
				return;
			}

			if (!hasPendingFees(fee)) {
				return;
			}

			BigDecimal paymentAmount = getPaymentAmount(fee);
			if (paymentAmount == null) {
				return;
			}

			processPayment(studentId, courseId, paymentAmount);

		} catch (Exception e) {
			System.out.println("Error during immediate fee payment: " + e.getMessage());
		}
	}

	private Fee showFeeStatus(int studentId, int courseId) {
		try {
			FeeService feeService = new FeeService();
			String result = feeService.getFeesByStudent(studentId);
			if (result.equals("SUCCESS")) {
				List<Fee> fees = feeService.getFeesListByStudent(studentId);
				Fee selectedFee = fees.stream().filter(fee -> fee.getCourseId() == courseId).findFirst().orElse(null);

				if (selectedFee == null) {
					System.out.println("No fee record found for Course ID " + courseId);
					return null;
				}

				System.out.println("\nCurrent Fee Status:");
				Fee.printHeader();
				System.out.println(selectedFee);
				Fee.printFooter();
				return selectedFee;
			} else {
				System.out.println("❌ " + result);
				return null;
			}
		} catch (SQLException e) {
			System.out.println("Error retrieving fee status: " + e.getMessage());
			return null;
		}
	}

	private boolean hasPendingFees(Fee fee) {
		if (fee == null) {
			return false;
		}
		boolean pending = fee.getPendingAmount().compareTo(BigDecimal.ZERO) > 0;
		if (!pending) {
			System.out.println("\nAll fees are already paid for this course!");
		}
		return pending;
	}

	private BigDecimal getPaymentAmount(Fee fee) {
		if (fee == null) {
			return null;
		}

		BigDecimal amount = InputValidator.getValidDecimal(scanner, "\nEnter payment amount: ₹", "Payment Amount");

		BigDecimal totalPending = fee.getPendingAmount();
		if (amount.compareTo(totalPending) > 0) {
			System.out.println("Payment amount (₹" + amount + ") exceeds pending amount (₹" + totalPending + ").");
			return null;
		}

		return amount;
	}

	private void processPayment(int studentId, int courseId, BigDecimal paymentAmount) {
		try {
			System.out.println("\nChoose payment method:");
			System.out.println("1. Cash");
			System.out.println("2. Card");
			System.out.println("3. UPI");
			System.out.println("0. Cancel");
			int choice = InputValidator.getValidIntegerInRange(scanner, "Enter your choice (0-3): ",
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
					System.out.println("\nPayment of ₹" + paymentAmount + " processed successfully!");
					System.out.println("Updated fee status:");
					Fee updatedFee = feeService.getFeesListByStudent(studentId).stream()
							.filter(fee -> fee.getCourseId() == courseId).findFirst().orElse(null);
					if (updatedFee != null) {
						Fee.printHeader();
						System.out.println(updatedFee);
						Fee.printFooter();
					}
				} else {
					System.out.println("❌ " + result);
				}
			} else {
				System.out.println("Payment failed. Please try again later.");
			}
		} catch (Exception e) {
			System.out.println("Error processing payment: " + e.getMessage());
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

		List<Course> allCourses = courseService.getAllCourses();

		List<Course> assignedCourses = studentService.getCoursesByStudentId(studentId);

		List<Course> unassignedCourses = allCourses.stream()
				.filter(c -> assignedCourses.stream().noneMatch(ac -> ac.getCourse_id() == c.getCourse_id()))
				.collect(Collectors.toList());

		if (unassignedCourses.isEmpty()) {
			System.out.println("All courses are already assigned to this student!");
			return;
		}

		System.out.println("\nAvailable Courses:");
		String line = "+------------+---------------------------+";
		String format = "| %-10s | %-25s |%n";

		System.out.println(line);
		System.out.printf(format, "Course ID", "Course Name");
		System.out.println(line);

		for (Course course : unassignedCourses) {
			System.out.printf(format, course.getCourse_id(), course.getCourse_name());
		}

		System.out.println(line);
		int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to assign: ", "Course ID");
		if (unassignedCourses.stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Invalid Course ID.");
			return;
		}

		List<Subject> availableSubjects = studentService.getSubjectsByCourseId(courseId);
		if (availableSubjects.isEmpty()) {
			System.out.println("No subjects available for Course ID " + courseId + ".");
			return;
		}

		scanner.nextLine();
		List<Integer> selectedSubjectIds = HelperUtils.selectSubjectsForCourse(courseId, availableSubjects, scanner);

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

		String line = "+------------+----------------------+";
		String format = "| %-10s | %-20s |%n";

		System.out.println("\nList of Students:");
		System.out.println(line);
		System.out.printf(format, "Student ID", "Name");
		System.out.println(line);

		for (Student s : students) {
			System.out.printf(format, s.getStudent_id(), s.getName());
		}

		System.out.println(line);

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to search: ", "Student ID");
		String result = studentService.searchStudentById(studentId);
		if (result.equals("SUCCESS")) {
			Student student = studentService.getStudentById(studentId);
			System.out.println("\nStudent Details:");
			Student.printHeader();
			System.out.println(student);
			Student.printFooter();
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
				System.out.println("This student has pending fees. Please clear the dues before deletion.");
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
			System.out.println("\n=== FEES PAYMENT ===\nNo students available.");
			return;
		}

		payFeesUtils payFeesUtil = new payFeesUtils();
		System.out.println("\n=== FEES PAYMENT ===");
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

		System.out.println("\nInactive Students");
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

	public void manageFeeNotifierPreferences() {
		FeeNotifierMain.managePreferences();
	}

}