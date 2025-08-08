package com.sms.dao;

import java.sql.*;
import java.util.*;

import com.sms.database.DBConnection;

public class CourseAnalysisDAO {
	private final Connection connection;

	public CourseAnalysisDAO() throws SQLException {
		this.connection = DBConnection.connect();
	}

	// Course Popularity Methods
	public Map<String, Integer> getMostEnrolledCourses() throws SQLException {
		Map<String, Integer> enrolledCourses = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, COUNT(sc.student_id) as enrollment_count " +
					"FROM courses c " +
					"LEFT JOIN student_courses sc ON c.course_id = sc.course_id AND sc.is_active = 1 " +
					"WHERE c.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY enrollment_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				int enrollmentCount = rs.getInt("enrollment_count");
				enrolledCourses.put(courseName, enrollmentCount);
			}
		}
		return enrolledCourses;
	}

	public Map<String, Integer> getCourseWiseStudentCount() throws SQLException {
		Map<String, Integer> courseStudentCount = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, COUNT(DISTINCT sc.student_id) as student_count " +
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
				courseStudentCount.put(courseName, studentCount);
			}
		}
		return courseStudentCount;
	}

	public Map<String, Map<String, Integer>> getSubjectDistributionPerCourse() throws SQLException {
		Map<String, Map<String, Integer>> subjectDistribution = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, s.subject_name, s.subject_type, COUNT(*) as subject_count " +
					"FROM courses c " +
					"JOIN subject_course sc ON c.course_id = sc.course_id " +
					"JOIN subjects s ON sc.subject_id = s.subject_id " +
					"WHERE c.is_active = 1 AND s.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name, s.subject_id, s.subject_name, s.subject_type " +
					"ORDER BY c.course_name, s.subject_type, s.subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				String subjectName = rs.getString("subject_name");
				String subjectType = rs.getString("subject_type");
				int subjectCount = rs.getInt("subject_count");

				subjectDistribution.computeIfAbsent(courseName, k -> new LinkedHashMap<>());
				subjectDistribution.get(courseName).put(subjectName + " (" + subjectType + ")", subjectCount);
			}
		}
		return subjectDistribution;
	}

	public Map<String, Object> getCourseCompletionStatus() throws SQLException {
		Map<String, Object> completionStatus = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, " +
					"COUNT(sc.student_id) as total_enrolled, " +
					"SUM(CASE WHEN f.pending_amount = 0 THEN 1 ELSE 0 END) as completed, " +
					"SUM(CASE WHEN f.pending_amount > 0 THEN 1 ELSE 0 END) as in_progress " +
					"FROM courses c " +
					"LEFT JOIN student_courses sc ON c.course_id = sc.course_id AND sc.is_active = 1 " +
					"LEFT JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name " +
					"ORDER BY total_enrolled DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				Map<String, Object> status = new HashMap<>();
				status.put("total_enrolled", rs.getInt("total_enrolled"));
				status.put("completed", rs.getInt("completed"));
				status.put("in_progress", rs.getInt("in_progress"));
				
				int totalEnrolled = rs.getInt("total_enrolled");
				int completed = rs.getInt("completed");
				double completionPercentage = totalEnrolled > 0 ? (completed * 100.0) / totalEnrolled : 0;
				status.put("completion_percentage", completionPercentage);
				
				completionStatus.put(courseName, status);
			}
		}
		return completionStatus;
	}

	// Teacher Analysis Methods
	public Map<String, Integer> getTeacherWorkload() throws SQLException {
		Map<String, Integer> teacherWorkload = new LinkedHashMap<>();
		String sql = "SELECT t.name, COUNT(st.subject_id) as subject_count " +
					"FROM teachers t " +
					"LEFT JOIN subject_teachers st ON t.teacher_id = st.teacher_id " +
					"WHERE t.is_active = 1 " +
					"GROUP BY t.teacher_id, t.name " +
					"ORDER BY subject_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String teacherName = rs.getString("name");
				int subjectCount = rs.getInt("subject_count");
				teacherWorkload.put(teacherName, subjectCount);
			}
		}
		return teacherWorkload;
	}

	public List<String> getTeachersWithMaxSubjects() throws SQLException {
		List<String> maxSubjectTeachers = new ArrayList<>();
		String sql = "SELECT t.name, COUNT(st.subject_id) as subject_count " +
					"FROM teachers t " +
					"LEFT JOIN subject_teachers st ON t.teacher_id = st.teacher_id " +
					"WHERE t.is_active = 1 " +
					"GROUP BY t.teacher_id, t.name " +
					"HAVING COUNT(st.subject_id) >= 3 " +
					"ORDER BY subject_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String teacherName = rs.getString("name");
				int subjectCount = rs.getInt("subject_count");
				maxSubjectTeachers.add(teacherName + " (" + subjectCount + " subjects)");
			}
		}
		return maxSubjectTeachers;
	}

	public List<String> getAvailableTeachersForAssignment() throws SQLException {
		List<String> availableTeachers = new ArrayList<>();
		String sql = "SELECT t.name, COUNT(st.subject_id) as subject_count " +
					"FROM teachers t " +
					"LEFT JOIN subject_teachers st ON t.teacher_id = st.teacher_id " +
					"WHERE t.is_active = 1 " +
					"GROUP BY t.teacher_id, t.name " +
					"HAVING COUNT(st.subject_id) < 3 " +
					"ORDER BY subject_count ASC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String teacherName = rs.getString("name");
				int subjectCount = rs.getInt("subject_count");
				availableTeachers.add(teacherName + " (" + subjectCount + " subjects)");
			}
		}
		return availableTeachers;
	}

	public Map<String, List<String>> getTeacherSubjectDistribution() throws SQLException {
		Map<String, List<String>> teacherSubjectDistribution = new LinkedHashMap<>();
		String sql = "SELECT t.name, s.subject_name, s.subject_type " +
					"FROM teachers t " +
					"JOIN subject_teachers st ON t.teacher_id = st.teacher_id " +
					"JOIN subjects s ON st.subject_id = s.subject_id " +
					"WHERE t.is_active = 1 AND s.is_active = 1 " +
					"ORDER BY t.name, s.subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String teacherName = rs.getString("name");
				String subjectName = rs.getString("subject_name");
				String subjectType = rs.getString("subject_type");
				String subjectInfo = subjectName + " (" + subjectType + ")";

				teacherSubjectDistribution.computeIfAbsent(teacherName, k -> new ArrayList<>());
				teacherSubjectDistribution.get(teacherName).add(subjectInfo);
			}
		}
		return teacherSubjectDistribution;
	}

	// Academic Structure Methods
	public Map<String, Integer> getMandatoryVsElectiveSubjects() throws SQLException {
		Map<String, Integer> subjectTypeCount = new LinkedHashMap<>();
		String sql = "SELECT subject_type, COUNT(*) as count " +
					"FROM subjects " +
					"WHERE is_active = 1 " +
					"GROUP BY subject_type " +
					"ORDER BY subject_type";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectType = rs.getString("subject_type");
				int count = rs.getInt("count");
				// Capitalize first letter for display
				String displayType = subjectType.substring(0, 1).toUpperCase() + subjectType.substring(1);
				subjectTypeCount.put(displayType, count);
			}
		}
		return subjectTypeCount;
	}

	public Map<String, Integer> getSubjectPopularity() throws SQLException {
		Map<String, Integer> subjectPopularity = new LinkedHashMap<>();
		String sql = "SELECT s.subject_name, COUNT(sc.student_id) as enrollment_count " +
					"FROM subjects s " +
					"LEFT JOIN subject_course sc_sub ON s.subject_id = sc_sub.subject_id " +
					"LEFT JOIN student_courses sc ON sc_sub.course_id = sc.course_id AND sc.is_active = 1 " +
					"WHERE s.is_active = 1 " +
					"GROUP BY s.subject_id, s.subject_name " +
					"ORDER BY enrollment_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				int enrollmentCount = rs.getInt("enrollment_count");
				subjectPopularity.put(subjectName, enrollmentCount);
			}
		}
		return subjectPopularity;
	}

	public Map<String, Object> getCourseFeeAnalysis() throws SQLException {
		Map<String, Object> feeAnalysis = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, " +
					"c.total_fee, " +
					"AVG(f.paid_amount) as avg_paid, " +
					"MIN(f.total_fee) as min_fee, " +
					"MAX(f.total_fee) as max_fee " +
					"FROM courses c " +
					"LEFT JOIN student_courses sc ON c.course_id = sc.course_id AND sc.is_active = 1 " +
					"LEFT JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE c.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name, c.total_fee " +
					"ORDER BY c.total_fee DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				Map<String, Object> feeInfo = new HashMap<>();
				feeInfo.put("total_fee", rs.getBigDecimal("total_fee"));
				feeInfo.put("avg_paid", rs.getBigDecimal("avg_paid"));
				feeInfo.put("min_fee", rs.getBigDecimal("min_fee"));
				feeInfo.put("max_fee", rs.getBigDecimal("max_fee"));
				
				feeAnalysis.put(courseName, feeInfo);
			}
		}
		return feeAnalysis;
	}

	public Map<String, Integer> getSemesterWiseStructure() throws SQLException {
		Map<String, Integer> semesterStructure = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, c.no_of_semester, COUNT(sc.student_id) as student_count " +
					"FROM courses c " +
					"LEFT JOIN student_courses sc ON c.course_id = sc.course_id AND sc.is_active = 1 " +
					"WHERE c.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name, c.no_of_semester " +
					"ORDER BY c.no_of_semester DESC, student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				int semesterCount = rs.getInt("no_of_semester");
				int studentCount = rs.getInt("student_count");
				semesterStructure.put(courseName + " (" + semesterCount + " semesters)", studentCount);
			}
		}
		return semesterStructure;
	}
} 