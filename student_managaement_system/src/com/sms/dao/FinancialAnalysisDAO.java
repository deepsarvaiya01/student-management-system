package com.sms.dao;

import java.sql.*;
import java.util.*;

import com.sms.database.DBConnection;

public class FinancialAnalysisDAO {
	private final Connection connection;

	public FinancialAnalysisDAO() throws SQLException {
		this.connection = DBConnection.connect();
	}

	// Revenue Analysis Methods
	public Map<String, Object> getTotalRevenueCollected() throws SQLException {
		Map<String, Object> revenueStats = new HashMap<>();
		String sql = "SELECT " +
					"SUM(f.paid_amount) as total_collected, " +
					"SUM(f.pending_amount) as total_pending, " +
					"SUM(f.total_fee) as total_expected " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				revenueStats.put("total_collected", rs.getBigDecimal("total_collected"));
				revenueStats.put("total_pending", rs.getBigDecimal("total_pending"));
				revenueStats.put("total_expected", rs.getBigDecimal("total_expected"));
			}
		}
		return revenueStats;
	}

	public Map<String, Double> getRevenueByCourse() throws SQLException {
		Map<String, Double> courseRevenue = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, " +
					"SUM(f.paid_amount) as total_revenue, " +
					"SUM(f.pending_amount) as total_pending " +
					"FROM courses c " +
					"JOIN student_courses sc ON c.course_id = sc.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 AND sc.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY total_revenue DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				double revenue = rs.getDouble("total_revenue");
				courseRevenue.put(courseName, revenue);
			}
		}
		return courseRevenue;
	}

	public double getAveragePaymentPerStudent() throws SQLException {
		String sql = "SELECT AVG(f.paid_amount) as avg_payment " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1 AND f.paid_amount > 0";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return rs.getDouble("avg_payment");
			}
		}
		return 0.0;
	}

	// Payment Analysis Methods
	public Map<String, Object> getPaymentCompletionPercentage() throws SQLException {
		Map<String, Object> completionStats = new HashMap<>();
		String sql = "SELECT " +
					"COUNT(*) as total_students, " +
					"SUM(CASE WHEN f.pending_amount = 0 THEN 1 ELSE 0 END) as completed_payments, " +
					"SUM(CASE WHEN f.pending_amount > 0 THEN 1 ELSE 0 END) as pending_payments " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				int totalStudents = rs.getInt("total_students");
				int completedPayments = rs.getInt("completed_payments");
				int pendingPayments = rs.getInt("pending_payments");
				
				double completionPercentage = totalStudents > 0 ? (completedPayments * 100.0) / totalStudents : 0;
				
				completionStats.put("total_students", totalStudents);
				completionStats.put("completed_payments", completedPayments);
				completionStats.put("pending_payments", pendingPayments);
				completionStats.put("completion_percentage", completionPercentage);
			}
		}
		return completionStats;
	}

	public Map<String, Map<String, Object>> getCourseWisePaymentStatus() throws SQLException {
		Map<String, Map<String, Object>> coursePaymentStatus = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, " +
					"COUNT(*) as total_students, " +
					"SUM(CASE WHEN f.pending_amount = 0 THEN 1 ELSE 0 END) as completed_payments, " +
					"SUM(CASE WHEN f.pending_amount > 0 THEN 1 ELSE 0 END) as pending_payments, " +
					"SUM(f.paid_amount) as total_collected, " +
					"SUM(f.pending_amount) as total_pending " +
					"FROM courses c " +
					"JOIN student_courses sc ON c.course_id = sc.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 AND sc.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY total_collected DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				Map<String, Object> status = new HashMap<>();
				status.put("total_students", rs.getInt("total_students"));
				status.put("completed_payments", rs.getInt("completed_payments"));
				status.put("pending_payments", rs.getInt("pending_payments"));
				status.put("total_collected", rs.getBigDecimal("total_collected"));
				status.put("total_pending", rs.getBigDecimal("total_pending"));
				
				int totalStudents = rs.getInt("total_students");
				int completedPayments = rs.getInt("completed_payments");
				double completionPercentage = totalStudents > 0 ? (completedPayments * 100.0) / totalStudents : 0;
				status.put("completion_percentage", completionPercentage);
				
				coursePaymentStatus.put(courseName, status);
			}
		}
		return coursePaymentStatus;
	}

	public Map<String, Integer> getRecentPaymentTrends() throws SQLException {
		Map<String, Integer> paymentTrends = new LinkedHashMap<>();
		String sql = "SELECT DATE_FORMAT(f.last_payment_date, '%Y-%m') as month_year, " +
					"COUNT(*) as payment_count " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1 AND f.last_payment_date IS NOT NULL " +
					"GROUP BY month_year " +
					"ORDER BY month_year DESC " +
					"LIMIT 12";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String monthYear = rs.getString("month_year");
				int paymentCount = rs.getInt("payment_count");
				paymentTrends.put(monthYear, paymentCount);
			}
		}
		return paymentTrends;
	}

	public Map<String, Double> getOutstandingAmountByCourse() throws SQLException {
		Map<String, Double> outstandingAmounts = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, SUM(f.pending_amount) as outstanding_amount " +
					"FROM courses c " +
					"JOIN student_courses sc ON c.course_id = sc.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 AND sc.is_active = 1 AND f.pending_amount > 0 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY outstanding_amount DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				double outstandingAmount = rs.getDouble("outstanding_amount");
				outstandingAmounts.put(courseName, outstandingAmount);
			}
		}
		return outstandingAmounts;
	}

	// Financial Metrics Methods
	public Map<String, Object> getCollectionEfficiency() throws SQLException {
		Map<String, Object> efficiencyStats = new HashMap<>();
		String sql = "SELECT " +
					"SUM(f.total_fee) as total_expected, " +
					"SUM(f.paid_amount) as total_collected, " +
					"SUM(f.pending_amount) as total_pending, " +
					"(SUM(f.paid_amount) / SUM(f.total_fee)) * 100 as collection_efficiency " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				efficiencyStats.put("total_expected", rs.getBigDecimal("total_expected"));
				efficiencyStats.put("total_collected", rs.getBigDecimal("total_collected"));
				efficiencyStats.put("total_pending", rs.getBigDecimal("total_pending"));
				efficiencyStats.put("collection_efficiency", rs.getDouble("collection_efficiency"));
			}
		}
		return efficiencyStats;
	}

	public Map<String, Double> getRevenuePerCourse() throws SQLException {
		Map<String, Double> revenuePerCourse = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, " +
					"SUM(f.paid_amount) as revenue, " +
					"COUNT(DISTINCT sc.student_id) as student_count, " +
					"SUM(f.paid_amount) / COUNT(DISTINCT sc.student_id) as revenue_per_student " +
					"FROM courses c " +
					"JOIN student_courses sc ON c.course_id = sc.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 AND sc.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY revenue DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				double revenuePerStudent = rs.getDouble("revenue_per_student");
				revenuePerCourse.put(courseName, revenuePerStudent);
			}
		}
		return revenuePerCourse;
	}

	public Map<String, Object> getAverageFeeStructure() throws SQLException {
		Map<String, Object> feeStructure = new HashMap<>();
		String sql = "SELECT " +
					"AVG(f.total_fee) as avg_total_fee, " +
					"MIN(f.total_fee) as min_fee, " +
					"MAX(f.total_fee) as max_fee, " +
					"COUNT(DISTINCT f.total_fee) as unique_fee_structures " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				feeStructure.put("avg_total_fee", rs.getBigDecimal("avg_total_fee"));
				feeStructure.put("min_fee", rs.getBigDecimal("min_fee"));
				feeStructure.put("max_fee", rs.getBigDecimal("max_fee"));
				feeStructure.put("unique_fee_structures", rs.getInt("unique_fee_structures"));
			}
		}
		return feeStructure;
	}

	public Map<String, Object> getPaymentDelayAnalysis() throws SQLException {
		Map<String, Object> delayAnalysis = new HashMap<>();
		String sql = "SELECT " +
					"COUNT(*) as total_students, " +
					"SUM(CASE WHEN f.pending_amount > 0 THEN 1 ELSE 0 END) as students_with_pending, " +
					"SUM(CASE WHEN f.pending_amount = 0 THEN 1 ELSE 0 END) as students_without_pending, " +
					"AVG(f.pending_amount) as avg_pending_amount " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				delayAnalysis.put("total_students", rs.getInt("total_students"));
				delayAnalysis.put("students_with_pending", rs.getInt("students_with_pending"));
				delayAnalysis.put("students_without_pending", rs.getInt("students_without_pending"));
				delayAnalysis.put("avg_pending_amount", rs.getBigDecimal("avg_pending_amount"));
			}
		}
		return delayAnalysis;
	}
} 