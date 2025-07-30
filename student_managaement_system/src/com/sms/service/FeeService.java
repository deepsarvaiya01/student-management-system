package com.sms.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sms.dao.FeeDao;
import com.sms.model.Fee;

public class FeeService {
	private FeeDao feeDao;

	public FeeService() throws SQLException {
		this.feeDao = new FeeDao();
	}

	// View Total Paid Fees
	public BigDecimal getTotalPaidFees() {
		return feeDao.getTotalPaidFees();
	}

	// View Total Pending Fees
	public BigDecimal getTotalPendingFees() {
		return feeDao.getTotalPendingFees();
	}

	// View Fees By Student
	public List<Fee> getFeesByStudent(int studentId) {
		if (studentId <= 0 || getAllStudents().stream().noneMatch(s -> s.getStudent_id() == studentId)) {
			System.out.println("Invalid or non-existent student ID: " + studentId);
			return new ArrayList<>();
		}
		return feeDao.getFeesByStudent(studentId);
	}

	// View Fees By Course
	public List<Fee> getFeesByCourse(int courseId) {
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Invalid or non-existent course ID: " + courseId);
			return new ArrayList<>();
		}
		return feeDao.getFeesByCourse(courseId);
	}

	// Update Fees Of A Course
	public boolean updateCourseFees(int courseId, BigDecimal newTotalFee) {
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Invalid or non-existent course ID: " + courseId);
			return false;
		}
		if (newTotalFee == null || newTotalFee.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid fee amount (must be positive).");
			return false;
		}
		return feeDao.updateCourseFees(courseId, newTotalFee);
	}

	// Get Total Earning
	public BigDecimal getTotalEarning() {
		return feeDao.getTotalEarning();
	}

	// Get all fees
	public List<Fee> getAllFees() {
		return feeDao.getAllFees();
	}

	// Get all courses for selection
	public List<com.sms.model.Course> getAllCourses() {
		return feeDao.getAllCourses();
	}

	// Get all students for selection
	public List<com.sms.model.Student> getAllStudents() {
		return feeDao.getAllStudents();
	}

	// Update fee payment
	public boolean updateFeePayment(int feeId, BigDecimal paymentAmount) {
		if (feeId <= 0 || getAllFees().stream().noneMatch(f -> f.getFeeId() == feeId)) {
			System.out.println("Invalid or non-existent fee ID: " + feeId);
			return false;
		}
		if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid payment amount (must be positive).");
			return false;
		}
		Fee fee = getAllFees().stream().filter(f -> f.getFeeId() == feeId).findFirst().orElse(null);
		if (fee != null && paymentAmount.compareTo(fee.getPendingAmount()) > 0) {
			System.out.println("Payment amount exceeds pending amount: " + fee.getPendingAmount());
			return false;
		}
		return feeDao.updateFeePayment(feeId, paymentAmount);
	}
}