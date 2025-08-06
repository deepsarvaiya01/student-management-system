package com.sms.dao;

import java.sql.*;
import java.util.*;

import com.sms.database.DBConnection;

public class SubjectAnalysisDAO {
	private final Connection connection;

	public SubjectAnalysisDAO() throws SQLException {
		this.connection = DBConnection.connect();
	}

	// Subject Distribution Methods
	public Map<String, Integer> getMandatorySubjectsCount() throws SQLException {
		Map<String, Integer> mandatorySubjects = new LinkedHashMap<>();
		String sql = "SELECT subject_name, subject_type FROM subjects " +
					"WHERE subject_type = 'mandatory' AND is_active = 1 " +
					"ORDER BY subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				mandatorySubjects.put(subjectName, 1);
			}
		}
		return mandatorySubjects;
	}

	public Map<String, Integer> getElectiveSubjectsCount() throws SQLException {
		Map<String, Integer> electiveSubjects = new LinkedHashMap<>();
		String sql = "SELECT subject_name, subject_type FROM subjects " +
					"WHERE subject_type = 'elective' AND is_active = 1 " +
					"ORDER BY subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				electiveSubjects.put(subjectName, 1);
			}
		}
		return electiveSubjects;
	}

	public Map<String, Integer> getMostAssignedSubjects() throws SQLException {
		Map<String, Integer> assignedSubjects = new LinkedHashMap<>();
		String sql = "SELECT s.subject_name, COUNT(sc.course_id) as assignment_count " +
					"FROM subjects s " +
					"JOIN subject_course sc ON s.subject_id = sc.subject_id " +
					"WHERE s.is_active = 1 " +
					"GROUP BY s.subject_id, s.subject_name " +
					"ORDER BY assignment_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				int assignmentCount = rs.getInt("assignment_count");
				assignedSubjects.put(subjectName, assignmentCount);
			}
		}
		return assignedSubjects;
	}

	public List<String> getUnassignedSubjects() throws SQLException {
		List<String> unassignedSubjects = new ArrayList<>();
		String sql = "SELECT s.subject_name, s.subject_type " +
					"FROM subjects s " +
					"LEFT JOIN subject_course sc ON s.subject_id = sc.subject_id " +
					"WHERE s.is_active = 1 AND sc.course_id IS NULL " +
					"ORDER BY s.subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				String subjectType = rs.getString("subject_type");
				unassignedSubjects.add(subjectName + " (" + subjectType + ")");
			}
		}
		return unassignedSubjects;
	}

	// Teacher Assignment Methods
	public Map<String, String> getSubjectsWithTeachers() throws SQLException {
		Map<String, String> subjectsWithTeachers = new LinkedHashMap<>();
		String sql = "SELECT s.subject_name, t.name as teacher_name, s.subject_type " +
					"FROM subjects s " +
					"JOIN subject_teachers st ON s.subject_id = st.subject_id " +
					"JOIN teachers t ON st.teacher_id = t.teacher_id " +
					"WHERE s.is_active = 1 AND t.is_active = 1 " +
					"ORDER BY s.subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				String teacherName = rs.getString("teacher_name");
				String subjectType = rs.getString("subject_type");
				subjectsWithTeachers.put(subjectName + " (" + subjectType + ")", teacherName);
			}
		}
		return subjectsWithTeachers;
	}

	public List<String> getSubjectsWithoutTeachers() throws SQLException {
		List<String> subjectsWithoutTeachers = new ArrayList<>();
		String sql = "SELECT s.subject_name, s.subject_type " +
					"FROM subjects s " +
					"LEFT JOIN subject_teachers st ON s.subject_id = st.subject_id " +
					"WHERE s.is_active = 1 AND st.teacher_id IS NULL " +
					"ORDER BY s.subject_name";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				String subjectType = rs.getString("subject_type");
				subjectsWithoutTeachers.add(subjectName + " (" + subjectType + ")");
			}
		}
		return subjectsWithoutTeachers;
	}

	public Map<String, Integer> getTeacherWorkloadDistribution() throws SQLException {
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

	public Map<String, Object> getAssignmentEfficiency() throws SQLException {
		Map<String, Object> assignmentEfficiency = new LinkedHashMap<>();
		String sql = "SELECT " +
					"(SELECT COUNT(*) FROM subjects WHERE is_active = 1) as total_subjects, " +
					"(SELECT COUNT(DISTINCT s.subject_id) FROM subjects s " +
					"JOIN subject_teachers st ON s.subject_id = st.subject_id " +
					"WHERE s.is_active = 1) as assigned_subjects, " +
					"(SELECT COUNT(DISTINCT t.teacher_id) FROM teachers t " +
					"JOIN subject_teachers st ON t.teacher_id = st.teacher_id " +
					"WHERE t.is_active = 1) as teachers_with_subjects, " +
					"(SELECT COUNT(*) FROM teachers WHERE is_active = 1) as total_teachers";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				int totalSubjects = rs.getInt("total_subjects");
				int assignedSubjects = rs.getInt("assigned_subjects");
				int teachersWithSubjects = rs.getInt("teachers_with_subjects");
				int totalTeachers = rs.getInt("total_teachers");

				double subjectAssignmentRate = totalSubjects > 0 ? (assignedSubjects * 100.0) / totalSubjects : 0;
				double teacherUtilizationRate = totalTeachers > 0 ? (teachersWithSubjects * 100.0) / totalTeachers : 0;

				assignmentEfficiency.put("total_subjects", totalSubjects);
				assignmentEfficiency.put("assigned_subjects", assignedSubjects);
				assignmentEfficiency.put("unassigned_subjects", totalSubjects - assignedSubjects);
				assignmentEfficiency.put("subject_assignment_rate", subjectAssignmentRate);
				assignmentEfficiency.put("total_teachers", totalTeachers);
				assignmentEfficiency.put("teachers_with_subjects", teachersWithSubjects);
				assignmentEfficiency.put("teacher_utilization_rate", teacherUtilizationRate);
			}
		}
		return assignmentEfficiency;
	}

	// Subject Performance Methods
	public Map<String, Map<String, Integer>> getSubjectEnrollmentByCourse() throws SQLException {
		Map<String, Map<String, Integer>> subjectEnrollmentByCourse = new LinkedHashMap<>();
		String sql = "SELECT c.course_name, s.subject_name, COUNT(sc.student_id) as enrollment_count " +
					"FROM courses c " +
					"JOIN subject_course sc_sub ON c.course_id = sc_sub.course_id " +
					"JOIN subjects s ON sc_sub.subject_id = s.subject_id " +
					"LEFT JOIN student_courses sc ON c.course_id = sc.course_id AND sc.is_active = 1 " +
					"WHERE c.is_active = 1 AND s.is_active = 1 " +
					"GROUP BY c.course_id, c.course_name, s.subject_id, s.subject_name " +
					"ORDER BY c.course_name, enrollment_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String courseName = rs.getString("course_name");
				String subjectName = rs.getString("subject_name");
				int enrollmentCount = rs.getInt("enrollment_count");

				subjectEnrollmentByCourse.computeIfAbsent(courseName, k -> new LinkedHashMap<>());
				subjectEnrollmentByCourse.get(courseName).put(subjectName, enrollmentCount);
			}
		}
		return subjectEnrollmentByCourse;
	}

	public Map<String, Integer> getSubjectPopularityTrends() throws SQLException {
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

	public Map<String, Integer> getSubjectWiseStudentCount() throws SQLException {
		Map<String, Integer> subjectStudentCount = new LinkedHashMap<>();
		String sql = "SELECT s.subject_name, COUNT(DISTINCT sc.student_id) as student_count " +
					"FROM subjects s " +
					"LEFT JOIN subject_course sc_sub ON s.subject_id = sc_sub.subject_id " +
					"LEFT JOIN student_courses sc ON sc_sub.course_id = sc.course_id AND sc.is_active = 1 " +
					"WHERE s.is_active = 1 " +
					"GROUP BY s.subject_id, s.subject_name " +
					"ORDER BY student_count DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				int studentCount = rs.getInt("student_count");
				subjectStudentCount.put(subjectName, studentCount);
			}
		}
		return subjectStudentCount;
	}

	public Map<String, Object> getSubjectCompletionRates() throws SQLException {
		Map<String, Object> subjectCompletionRates = new LinkedHashMap<>();
		String sql = "SELECT s.subject_name, " +
					"COUNT(sc.student_id) as total_enrolled, " +
					"SUM(CASE WHEN f.pending_amount = 0 THEN 1 ELSE 0 END) as completed, " +
					"SUM(CASE WHEN f.pending_amount > 0 THEN 1 ELSE 0 END) as in_progress " +
					"FROM subjects s " +
					"LEFT JOIN subject_course sc_sub ON s.subject_id = sc_sub.subject_id " +
					"LEFT JOIN student_courses sc ON sc_sub.course_id = sc.course_id AND sc.is_active = 1 " +
					"LEFT JOIN fees f ON sc.student_course_id = f.student_course_id " +
					"WHERE s.is_active = 1 " +
					"GROUP BY s.subject_id, s.subject_name " +
					"ORDER BY total_enrolled DESC";

		try (PreparedStatement stmt = connection.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				String subjectName = rs.getString("subject_name");
				Map<String, Object> completionData = new HashMap<>();
				
				int totalEnrolled = rs.getInt("total_enrolled");
				int completed = rs.getInt("completed");
				int inProgress = rs.getInt("in_progress");
				
				double completionRate = totalEnrolled > 0 ? (completed * 100.0) / totalEnrolled : 0;
				
				completionData.put("total_enrolled", totalEnrolled);
				completionData.put("completed", completed);
				completionData.put("in_progress", inProgress);
				completionData.put("completion_rate", completionRate);
				
				subjectCompletionRates.put(subjectName, completionData);
			}
		}
		return subjectCompletionRates;
	}
} 