package com.sms.utils;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.payment.processor.PaymentProcessor;
import com.sms.service.FeeService;

public class payFeesUtils {
	private FeeService feeService;
	Scanner scanner = new Scanner(System.in);

	public payFeesUtils() throws SQLException {
		feeService = new FeeService();
	}

	public List<Student> showAndGetAllStudents() {
		List<Student> students = feeService.getAllStudents();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return students;
		}
		System.out.println("📚 Available Students:");
		printStudents(students);
		return students;
	}

	public int inputStudentId() {
		if (!scanner.hasNextInt()) {
			scanner.next(); // clear invalid input
			return -1;
		}
		return scanner.nextInt();
	}

	public List<Fee> showStudentFees(int studentId) {
		List<Fee> fees = feeService.getFeesByStudent(studentId);
		if (fees.isEmpty()) {
			System.out.println("No fees found for Student ID: " + studentId);
			return null;
		}

		System.out.println("\n📊 Current Fee Status for Student ID " + studentId + ":");
		Fee.printHeader();
		fees.forEach(System.out::println);
		return fees;
	}

	public boolean hasPendingFees(List<Fee> fees) {
		boolean pending = fees.stream().anyMatch(fee -> fee.getPendingAmount().compareTo(BigDecimal.ZERO) > 0);
		if (!pending) {
			System.out.println("\n✅ All fees are already paid for this student!");
		}
		return pending;
	}

	public BigDecimal inputPaymentAmount(List<Fee> fees) {
		System.out.print("\nEnter payment amount: ₹");
		if (!scanner.hasNextBigDecimal()) {
			System.out.println("Invalid payment amount.");
			scanner.next();
			return null;
		}

		BigDecimal amount = scanner.nextBigDecimal();
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid payment amount.");
			return null;
		}

		BigDecimal totalPending = fees.stream().map(Fee::getPendingAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (amount.compareTo(totalPending) > 0) {
			System.out.println("Invalid payment amount.");
			return null;
		}

		return amount;
	}

	public void processAndDisplayPayment(int studentId, BigDecimal paymentAmount) {
		try {
			PaymentProcessor processor = new PaymentProcessor();
			boolean paymentSuccess = processor.process(studentId, paymentAmount);

			if (paymentSuccess) {
				// Update fees in the database
				boolean updateSuccess = feeService.updateFeePayment(studentId, paymentAmount);
				if (updateSuccess) {
					System.out.println("\n✅ Payment of ₹" + paymentAmount + " processed successfully!");
					System.out.println("Updated fee status:");
					List<Fee> updatedFees = feeService.getFeesByStudent(studentId);
					Fee.printHeader();
					updatedFees.forEach(System.out::println);
				} else {
					System.out.println("❌ Failed to update fee record. No pending fees found.");
				}
			} else {
				System.out.println("❌ Payment failed. Please try again.");
			}
		} catch (Exception e) {
			System.out.println("❌ Error while processing payment: " + e.getMessage());
			e.printStackTrace();
		}
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
}
