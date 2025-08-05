package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.service.CourseService;
import com.sms.service.FeeService;
import com.sms.utils.HelperUtils;
import com.sms.utils.InputValidator;

public class FeeController {
	private FeeService feeService;
	private CourseService courseService;
	private java.util.Scanner scanner = new java.util.Scanner(System.in);

	public FeeController() throws SQLException {
		this.feeService = new FeeService();
		this.courseService = new CourseService();
	}

	
	private void printBox(String title, BigDecimal amount) {
		String valueStr = "₹" + amount;
		int totalWidth = 50;
		String top = "╔" + "═".repeat(totalWidth - 2) + "╗";
		String bottom = "╚" + "═".repeat(totalWidth - 2) + "╝";
		String content = String.format("║ %-46s ║", title + valueStr);

		System.out.println("\n" + top);
		System.out.println(content);
		System.out.println(bottom);
	}
	
	public void viewTotalPaidFees() {
		List<Fee> paidFees = feeService.getPaidFeesByStudents();

		if (paidFees.isEmpty()) {
			System.out.println("No fee records found.");
			return;
		}

		System.out.println("\n╔═════════════════════════════════════════════╗");
		System.out.println("║              PAID FEES BY STUDENT           ║");
		System.out.println("╠═════════════════════════════════════════════╣");
		System.out.printf("║%-25s  │ %-15s ║\n", "Student Name", "Paid Amount");
		System.out.println("╠═════════════════════════════════════════════╣");

		BigDecimal total = BigDecimal.ZERO;
		for (Fee fee : paidFees) {
			System.out.printf("║ %-25s │ ₹%-13.2f  ║\n", fee.getStudentName(), fee.getPaidAmount());
			total = total.add(fee.getPaidAmount());
		}

		System.out.println("╠═════════════════════════════════════════════╣");
		System.out.printf("║ %-25s │ ₹%-13.2f  ║\n", "TOTAL", total);
		System.out.println("╚═════════════════════════════════════════════╝");
	}


	public void viewTotalPendingFees() {
		List<Fee> pendingFees = feeService.getPendingFeesByStudents();

		if (pendingFees.isEmpty()) {
			System.out.println("No pending fee records found.");
			return;
		}

		System.out.println("\n╔═════════════════════════════════════════════╗");
		System.out.println("║         PENDING FEES BY STUDENT             ║");
		System.out.println("╠═════════════════════════════════════════════╣");
		System.out.printf("║ %-25s │ %-15s ║\n", "Student Name", "Pending Amount");
		System.out.println("╠═════════════════════════════════════════════╣");

		BigDecimal total = BigDecimal.ZERO;
		for (Fee fee : pendingFees) {
			System.out.printf("║ %-25s │ ₹%-13.2f  ║\n", fee.getStudentName(), fee.getPendingAmount());
			total = total.add(fee.getPendingAmount());
		}

		System.out.println("╠═════════════════════════════════════════════╣");
		System.out.printf("║ %-25s │ ₹%-13.2f  ║\n", "TOTAL", total);
		System.out.println("╚═════════════════════════════════════════════╝");
	}


	public void viewFeesByStudent() {
		List<Student> students = feeService.getAllStudents();
		if (students.isEmpty()) {
			System.out.println("No students found.");
			return;
		}

		HelperUtils.printStudents(students);

		int studentId = InputValidator.getValidInteger(scanner, "Enter Student ID to view fees: ", "Student ID");

		String result = feeService.getFeesByStudent(studentId);
		if (!result.equals("SUCCESS")) {
			System.out.println(result);
			return;
		}

		List<Fee> fees = feeService.getFeesListByStudent(studentId);

		if (fees.isEmpty()) {
			System.out.println("No fee records found.");
			return;
		}

		System.out.println("\nFees for Student ID " + studentId + ":");
		Fee.printHeader();

		BigDecimal totalPending = BigDecimal.ZERO;

		for (Fee fee : fees) {
			System.out.println(fee);
			totalPending = totalPending.add(fee.getPendingAmount());
		}

		// ✅ Bottom line to close the table
		System.out.println("+--------+----------------------+-----------------+-----------------+-----------------+-----------------+-----------------+");

		// ✅ Show total pending fees
		System.out.println("Total Pending Fees: ₹" + totalPending);
	}


	public void viewFeesByCourse() {
	    List<Course> courses = feeService.getAllCourses();
	    if (courses.isEmpty()) {
	        System.out.println("No courses found.");
	        return;
	    }

	    System.out.println("\n📘 Available Courses:");
	    System.out.println("+--------+--------------------------+");
	    System.out.printf("| %-6s | %-24s |\n", "ID", "Course Name");
	    System.out.println("+--------+--------------------------+");
	    for (Course course : courses) {
	        System.out.printf("| %-6d | %-24s |\n", course.getCourse_id(), course.getCourse_name());
	    }
	    System.out.println("+--------+--------------------------+");


	    int courseId = InputValidator.getValidInteger(scanner, "\n👉 Enter Course ID to view fees: ", "Course ID");

	    String result = feeService.getFeesByCourse(courseId);
	    if (!result.equals("SUCCESS")) {
	        System.out.println(result);
	        return;
	    }

	    List<Fee> fees = feeService.getFeesListByCourse(courseId);
	    if (fees.isEmpty()) {
	        System.out.println("\nNo fee records found for this course.");
	        return;
	    }

	    Course course = feeService.getCourseById(courseId);
	    BigDecimal courseFee = course.getTotal_fee();
	    int studentCount = fees.size();
	    BigDecimal totalExpected = courseFee.multiply(BigDecimal.valueOf(studentCount));
	    BigDecimal totalPaid = BigDecimal.ZERO;
	    BigDecimal totalPending = BigDecimal.ZERO;

	    for (Fee fee : fees) {
	        totalPaid = totalPaid.add(fee.getPaidAmount());
	        totalPending = totalPending.add(fee.getPendingAmount());
	    }

	    // 📘 Course Summary Box
	    System.out.println();
	    System.out.println("╔════════════════════════════════════════════════════════════╗");
	    System.out.println("║                    📘 COURSE FEE SUMMARY                    ║");
	    System.out.println("╠════════════════════════════════════════════════════════════╣");
	    System.out.printf ("║ Course Name         : %-35s ║%n", course.getCourse_name());
	    System.out.printf ("║ Students Enrolled   : %-35d ║%n", studentCount);
	    System.out.printf ("║ Fee Per Student     : ₹%-34.2f ║%n", courseFee);
	    System.out.printf ("║ Total Expected Fees : ₹%-34.2f ║%n", totalExpected);
	    System.out.printf ("║ Total Paid Amount   : ₹%-34.2f ║%n", totalPaid);
	    System.out.printf ("║ Total Pending Amount: ₹%-34.2f ║%n", totalPending);
	    System.out.println("╚════════════════════════════════════════════════════════════╝");

	    // 📄 Detailed Fee Table
	    System.out.println("\n📄 Detailed Fee Records:");
	    System.out.println("+--------+----------------------+-----------------+-----------------+-----------------+-----------------+");
	    System.out.printf("| %-6s | %-20s | %-15s | %-15s | %-15s | %-15s | %-15s |%n",
	            "Fee ID", "Student Name", "Course", "Total Fee", "Paid Amount", "Pending Amount", "Last Payment");
	    System.out.println("+--------+----------------------+-----------------+-----------------+-----------------+-----------------+");

	    for (Fee fee : fees) {
	        System.out.printf("| %-6d | %-20s | %-15s | %-15.2f | %-15.2f | %-15.2f |%n",
	                fee.getFeeId(),
	                fee.getStudentName(),
	                course.getCourse_name(),
	                fee.getTotalFee(),
	                fee.getPaidAmount(),
	                fee.getPendingAmount()
	               );
	    }

	    System.out.println("+--------+----------------------+-----------------+-----------------+-----------------+-----------------+");
	}



	public void updateFeesOfCourse() {
	    List<Course> courses = courseService.getAllCourses();
	    if (courses.isEmpty()) {
	        System.out.println("No courses found.");
	        return;
	    }

	    System.out.println("\n📘 Available Courses:");
	    HelperUtils.printCourses(courses);

	    int courseId = InputValidator.getValidInteger(scanner, "\n👉 Enter Course ID to update fees: ", "Course ID");

	    Course course = courseService.getCourseById(courseId);
	    if (course == null) {
	        System.out.println("❌ Invalid Course ID.");
	        return;
	    }

	    BigDecimal previousFee = course.getTotal_fee();
	    BigDecimal newFee = InputValidator.getValidDecimal(scanner, "💰 Enter new total fee amount: ₹", "Fee");

	    // Update the course fee
	    course.setTotal_fee(newFee);
	    boolean updated = courseService.updateCourseFees(course); // returns true if update successful

	    if (updated) {
	        // ✅ Formatted Confirmation Table
	        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
	        System.out.println("║                 ✅ COURSE FEE UPDATE CONFIRMATION                 ║");
	        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
	        System.out.printf ("║ %-24s : %-42d ║%n", "Course ID", course.getCourse_id());
	        System.out.printf ("║ %-24s : %-42s ║%n", "Course Name", course.getCourse_name());
	        System.out.printf ("║ %-24s : ₹%-41.2f ║%n", "Previous Total Fee", previousFee);
	        System.out.printf ("║ %-24s : ₹%-41.2f ║%n", "Updated Total Fee", newFee);
	        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
	    } else {
	        System.out.println("❌ Failed to update course fee. Please try again.");
	    }
	}
	
	public void viewTotalEarning() {
		BigDecimal totalEarning = feeService.getTotalEarning();
		printBox("💼Total Earning: ", totalEarning);
	}

}
