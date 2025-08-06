package com.sms.dao;

import java.sql.*;
import java.util.*;

import com.sms.database.DBConnection;

public class StudentAnalysisDAO {
	private final Connection connection;

	public StudentAnalysisDAO() throws SQLException {
		this.connection = DBConnection.connect();
	}

	// Enrollment Analysis Methods
	public Map<String, Integer> getTotalStudentsByCourse() throws SQLException {
		Map<String, Integer> courseStats = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, COUNT(sc.student_id) as student_count " +
					"FROM courses c " +
					"LEFT JOIN student_courses sc ON c.course_id = sc.course_id AND sc.is_active = 1 " +
					"WHERE c.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				int studentCount = rs.getInt("student_count");
				courseStats.put(courseName, studentCount);
			}
		}
		return courseStats;
	}

	public Map<String, Integer> getEnrollmentDateTrends() throws SQLException {
		Map<String, Integer> enrollmentTrends = new LinkedHashMap<>();
		String sql = "SELECT DATE_FORMAT(enrollment_date, '%Y-%m') as month_year, " +
					"COUNT(*) as enrollments " +
					"FROM student_courses " +
					"WHERE is_active = 1 " +
					"GROUP BY month_year " +
					"ORDER BY month_year";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String monthYear = rs.getString("month_year");
				int enrollments = rs.getInt("enrollments");
				enrollmentTrends.put(monthYear, enrollments);
			}
		}
		return enrollmentTrends;
	}

	public Map<String, Integer> getActiveVsInactiveStudents() throws SQLException {
		Map<String, Integer> statusStats = new LinkedHashMap<>();
		String sql = "SELECT " +
					"CASE WHEN is_active = 1 THEN 'Active' ELSE 'Inactive' END as status, " +
					"COUNT(*) as count " +
					"FROM students " +
					"GROUP BY is_active " +
					"ORDER BY is_active DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String status = rs.getString("status");
				int count = rs.getInt("count");
				statusStats.put(status, count);
			}
		}
		return statusStats;
	}

	public Map<String, Integer> getStudentDistributionByCity() throws SQLException {
		Map<String, Integer> cityStats = new LinkedHashMap<>();
		String sql = "SELECT p.city, COUNT(s.student_id) as student_count " +
					"FROM students s " +
					"JOIN profiles p ON s.student_id = p.student_id " +
					"WHERE s.is_active = 1 " +
					"GROUP BY p.city " +
					"ORDER BY student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String city = rs.getString("city");
				int count = rs.getInt("student_count");
				cityStats.put(city, count);
			}
		}
		return cityStats;
	}

	// Student Performance Methods
	public List<Map<String, Object>> getStudentsWithCompletePayment() throws SQLException {
		List<Map<String, Object>> completePayments = new ArrayList<>();
		String sql = "SELECT s.name, c.course_name, f.paid_amount, f.last_payment_date " +
					"FROM students s " +
					"JOIN student_courses sc ON s.student_id = sc.student_id " +
					"JOIN courses c ON sc.course_id = c.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE s.is_active = 1 AND sc.is_active = 1 " +
					"AND f.pending_amount = 0 " +
					"ORDER BY f.last_payment_date DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				Map<String, Object> student = new HashMap<>();
				student.put("name", rs.getString("name"));
				student.put("course_name", rs.getString("course_name"));
				student.put("paid_amount", rs.getBigDecimal("paid_amount"));
				student.put("last_payment_date", rs.getDate("last_payment_date"));
				completePayments.add(student);
			}
		}
		return completePayments;
	}

	public List<Map<String, Object>> getStudentsWithPendingFees() throws SQLException {
		List<Map<String, Object>> pendingFees = new ArrayList<>();
		String sql = "SELECT s.name, c.course_name, f.pending_amount, " +
					"((f.paid_amount / f.total_fee) * 100) as completion_percentage " +
					"FROM students s " +
					"JOIN student_courses sc ON s.student_id = sc.student_id " +
					"JOIN courses c ON sc.course_id = c.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE s.is_active = 1 AND sc.is_active = 1 " +
					"AND f.pending_amount > 0 " +
					"ORDER BY f.pending_amount DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				Map<String, Object> student = new HashMap<>();
				student.put("name", rs.getString("name"));
				student.put("course_name", rs.getString("course_name"));
				student.put("pending_amount", rs.getBigDecimal("pending_amount"));
				student.put("completion_percentage", rs.getDouble("completion_percentage"));
				pendingFees.add(student);
			}
		}
		return pendingFees;
	}

	public Map<String, Double> getAverageFeePaymentPerStudent() throws SQLException {
		Map<String, Double> averagePayments = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, AVG(f.paid_amount) as avg_payment " +
					"FROM courses c " +
					"JOIN student_courses sc ON c.course_id = sc.course_id " +
					"JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 AND sc.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY avg_payment DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				double avgPayment = rs.getDouble("avg_payment");
				averagePayments.put(courseName, avgPayment);
			}
		}
		return averagePayments;
	}

	public Map<String, Object> getPaymentCompletionRate() throws SQLException {
		Map<String, Object> completionStats = new HashMap<>();
		String sql = "SELECT " +
					"SUM(f.total_fee) as total_expected, " +
					"SUM(f.paid_amount) as total_collected, " +
					"(SUM(f.paid_amount) / SUM(f.total_fee)) * 100 as completion_rate " +
					"FROM fees f " +
					"JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
					"WHERE sc.is_active = 1";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				completionStats.put("total_expected", rs.getDouble("total_expected"));
				completionStats.put("total_collected", rs.getDouble("total_collected"));
				completionStats.put("completion_rate", rs.getDouble("completion_rate"));
			}
		}
		return completionStats;
	}

	// Student Demographics Methods
	public Map<String, Integer> getAgeDistribution() throws SQLException {
		Map<String, Integer> ageStats = new LinkedHashMap<>();
		String sql = "SELECT " +
					"CASE " +
					"  WHEN p.age < 18 THEN 'Under 18' " +
					"  WHEN p.age BETWEEN 18 AND 20 THEN '18-20' " +
					"  WHEN p.age BETWEEN 21 AND 25 THEN '21-25' " +
					"  WHEN p.age BETWEEN 26 AND 30 THEN '26-30' " +
					"  ELSE 'Over 30' " +
					"END as age_group, " +
					"COUNT(s.student_id) as student_count " +
					"FROM students s " +
					"JOIN profiles p ON s.student_id = p.student_id " +
					"WHERE s.is_active = 1 " +
					"GROUP BY age_group " +
					"ORDER BY age_group";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String ageGroup = rs.getString("age_group");
				int count = rs.getInt("student_count");
				ageStats.put(ageGroup, count);
			}
		}
		return ageStats;
	}

	public Map<String, Map<String, Integer>> getCoursePreferenceByAge() throws SQLException {
		Map<String, Map<String, Integer>> ageCourseStats = new LinkedHashMap<>();
		String sql = "SELECT " +
					"CASE " +
					"  WHEN p.age < 18 THEN 'Under 18' " +
					"  WHEN p.age BETWEEN 18 AND 20 THEN '18-20' " +
					"  WHEN p.age BETWEEN 21 AND 25 THEN '21-25' " +
					"  WHEN p.age BETWEEN 26 AND 30 THEN '26-30' " +
					"  ELSE 'Over 30' " +
					"END as age_group, " +
					"c.course_name, " +
					"COUNT(s.student_id) as student_count " +
					"FROM students s " +
					"JOIN profiles p ON s.student_id = p.student_id " +
					"JOIN student_courses sc ON s.student_id = sc.student_id " +
					"JOIN courses c ON sc.course_id = c.course_id " +
					"WHERE s.is_active = 1 AND sc.is_active = 1 " +
					"GROUP BY age_group, c.course_id, c.course_name " +
					"ORDER BY age_group, student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String ageGroup = rs.getString("age_group");
				String courseName = rs.getString("course_name");
				int count = rs.getInt("student_count");

				ageCourseStats.computeIfAbsent(ageGroup, k -> new LinkedHashMap<>());
				ageCourseStats.get(ageGroup).put(courseName, count);
			}
		}
		return ageCourseStats;
	}

	// Gender Analysis Method
	public Map<String, Integer> getGenderDistribution() throws SQLException {
		Map<String, Integer> genderStats = new LinkedHashMap<>();
		String sql = "SELECT " +
					"CASE " +
					"  WHEN gender = 'm' THEN 'Male' " +
					"  WHEN gender = 'f' THEN 'Female' " +
					"  ELSE 'Not Specified' " +
					"END as gender_group, " +
					"COUNT(*) as student_count " +
					"FROM students " +
					"WHERE is_active = 1 " +
					"GROUP BY gender " +
					"ORDER BY gender_group";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String genderGroup = rs.getString("gender_group");
				int count = rs.getInt("student_count");
				genderStats.put(genderGroup, count);
			}
		}
		return genderStats;
	}

	public Map<String, Map<String, Integer>> getGenderDistributionByCourse() throws SQLException {
		Map<String, Map<String, Integer>> genderCourseStats = new LinkedHashMap<>();
		String sql = "SELECT " +
					"CASE " +
					"  WHEN s.gender = 'm' THEN 'Male' " +
					"  WHEN s.gender = 'f' THEN 'Female' " +
					"  ELSE 'Not Specified' " +
					"END as gender_group, " +
					"c.course_name, " +
					"COUNT(s.student_id) as student_count " +
					"FROM students s " +
					"JOIN student_courses sc ON s.student_id = sc.student_id " +
					"JOIN courses c ON sc.course_id = c.course_id " +
					"WHERE s.is_active = 1 AND sc.is_active = 1 " +
					"GROUP BY s.gender, c.course_id, c.course_name " +
					"ORDER BY gender_group, student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String genderGroup = rs.getString("gender_group");
				String courseName = rs.getString("course_name");
				int count = rs.getInt("student_count");

				genderCourseStats.computeIfAbsent(genderGroup, k -> new LinkedHashMap<>());
				genderCourseStats.get(genderGroup).put(courseName, count);
			}
		}
		return genderCourseStats;
	}

	public Map<String, Map<String, Integer>> getGenderDistributionByCity() throws SQLException {
		Map<String, Map<String, Integer>> genderCityStats = new LinkedHashMap<>();
		String sql = "SELECT " +
					"CASE " +
					"  WHEN s.gender = 'm' THEN 'Male' " +
					"  WHEN s.gender = 'f' THEN 'Female' " +
					"  ELSE 'Not Specified' " +
					"END as gender_group, " +
					"p.city, " +
					"COUNT(s.student_id) as student_count " +
					"FROM students s " +
					"JOIN profiles p ON s.student_id = p.student_id " +
					"WHERE s.is_active = 1 " +
					"GROUP BY s.gender, p.city " +
					"ORDER BY gender_group, student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String genderGroup = rs.getString("gender_group");
				String city = rs.getString("city");
				int count = rs.getInt("student_count");

				genderCityStats.computeIfAbsent(genderGroup, k -> new LinkedHashMap<>());
				genderCityStats.get(genderGroup).put(city, count);
			}
		}
		return genderCityStats;
	}
} 