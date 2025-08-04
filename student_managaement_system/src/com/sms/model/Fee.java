package com.sms.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Fee {
	private int feeId;
	private int studentCourseId;
	private BigDecimal paidAmount;
	private BigDecimal pendingAmount;
	private BigDecimal totalFee;
	private LocalDate lastPaymentDate;

	// Additional fields for display purposes
	private String studentName;
	private String courseName;
	private int studentId;
	private int courseId;

	public Fee() {
	}

	public Fee(int feeId, int studentCourseId, BigDecimal paidAmount, BigDecimal pendingAmount, BigDecimal totalFee,
			LocalDate lastPaymentDate) {
		this.feeId = feeId;
		this.studentCourseId = studentCourseId;
		this.paidAmount = paidAmount;
		this.pendingAmount = pendingAmount;
		this.totalFee = totalFee;
		this.lastPaymentDate = lastPaymentDate;
	}

	// Getters and Setters
	public int getFeeId() {
		return feeId;
	}

	public void setFeeId(int feeId) {
		this.feeId = feeId;
	}

	public int getStudentCourseId() {
		return studentCourseId;
	}

	public void setStudentCourseId(int studentCourseId) {
		this.studentCourseId = studentCourseId;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}

	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}

	public LocalDate getLastPaymentDate() {
		return lastPaymentDate;
	}

	public void setLastPaymentDate(LocalDate lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getStudentId() {
		return studentId;
	}

	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public static void printHeader() {
		System.out.println(
				"+--------+----------------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
		System.out.printf("| %-6s | %-20s | %-15s | %-15s | %-15s | %-15s | %-15s |\n", "Fee ID", "Student Name",
				"Course", "Total Fee", "Paid Amount", "Pending Amount", "Last Payment");
		System.out.println(
				"+--------+----------------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
	}

	// Tabular Display toString
	@Override
	public String toString() {
		String lastPaymentStr = (lastPaymentDate != null) ? lastPaymentDate.toString() : "N/A";
		return String.format("| %-6d | %-20s | %-15s | %-15.2f | %-15.2f | %-15.2f | %-15s |", feeId,
				(studentName != null) ? studentName : "N/A", (courseName != null) ? courseName : "N/A", totalFee,
				paidAmount, pendingAmount, lastPaymentStr);
	}

}