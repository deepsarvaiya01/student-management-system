package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sms.database.DBConnection;
import com.sms.model.DashboardModel;

public class DashboardDao {

	private Connection connection;

	public DashboardDao() {
		try {
			this.connection = DBConnection.connect();
			if (this.connection == null) {
				throw new SQLException("Failed to establish database connection");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database connection failed", e);
		}
	}

	public List<DashboardModel> getGeneralDashboardData() {
		List<DashboardModel> dashboardList = new ArrayList<>();
		String query = """
				SELECT
				    s.student_id,
				    s.name AS student_name,
				    c.course_name AS course,
				    f.total_fee,
				    f.paid_amount,
				    f.pending_amount,
				    GROUP_CONCAT(DISTINCT sub.subject_name) AS subjects,
				    GROUP_CONCAT(DISTINCT t.name) AS teachers
				FROM students s
				JOIN student_courses sc ON s.student_id = sc.student_id
				JOIN courses c ON sc.course_id = c.course_id
				LEFT JOIN fees f ON f.student_course_id = sc.student_course_id
				LEFT JOIN student_subjects ss ON sc.student_course_id = ss.student_course_id
				LEFT JOIN subject_course subj_c ON ss.subject_course_id = subj_c.id
				LEFT JOIN subjects sub ON subj_c.subject_id = sub.subject_id
				LEFT JOIN subject_teachers st ON sub.subject_id = st.subject_id
				LEFT JOIN teachers t ON st.teacher_id = t.teacher_id
				WHERE s.is_active = TRUE
				GROUP BY s.student_id, s.name, c.course_name, f.total_fee, f.paid_amount, f.pending_amount;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setStudentId(rs.getInt("student_id"));
				model.setName(rs.getString("student_name") != null ? rs.getString("student_name") : "--");
				model.setCourse(rs.getString("course") != null ? rs.getString("course") : "--");
				model.setTotalFee(rs.getDouble("total_fee"));
				model.setPaidFee(rs.getDouble("paid_amount"));
				model.setPendingFee(rs.getDouble("pending_amount"));
				model.setSubjects(rs.getString("subjects") != null ? rs.getString("subjects") : "--");
				model.setTeachers(rs.getString("teachers") != null ? rs.getString("teachers") : "--");
				dashboardList.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dashboardList;
	}

	public List<DashboardModel> getCourseWiseDashboardData() {
		List<DashboardModel> list = new ArrayList<>();
		String query = """
				SELECT
				    c.course_name,
				    COUNT(DISTINCT s.student_id) AS total_students,
				    GROUP_CONCAT(DISTINCT s.name) AS student_list,
				    SUM(COALESCE(f.total_fee, 0)) AS total_fees,
				    SUM(COALESCE(f.paid_amount, 0)) AS total_paid,
				    SUM(COALESCE(f.pending_amount, 0)) AS total_pending,
				    GROUP_CONCAT(DISTINCT sub.subject_name) AS subjects,
				    GROUP_CONCAT(DISTINCT t.name) AS teachers
				FROM students s
				JOIN student_courses sc ON s.student_id = sc.student_id
				JOIN courses c ON sc.course_id = c.course_id
				LEFT JOIN fees f ON f.student_course_id = sc.student_course_id
				LEFT JOIN student_subjects ss ON sc.student_course_id = ss.student_course_id
				LEFT JOIN subject_course subj_c ON ss.subject_course_id = subj_c.id
				LEFT JOIN subjects sub ON subj_c.subject_id = sub.subject_id
				LEFT JOIN subject_teachers st ON sub.subject_id = st.subject_id
				LEFT JOIN teachers t ON st.teacher_id = t.teacher_id
				WHERE s.is_active = TRUE AND c.is_active = TRUE
				GROUP BY c.course_name
				HAVING total_students > 0;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setCourse(rs.getString("course_name") != null ? rs.getString("course_name") : "--");
				model.setTotalStudents(rs.getInt("total_students"));
				model.setStudentList(rs.getString("student_list") != null ? rs.getString("student_list") : "--");
				model.setTotalFee(rs.getDouble("total_fees") != 0 ? rs.getDouble("total_fees") : 0.0);
				model.setPaidFee(rs.getDouble("total_paid") != 0 ? rs.getDouble("total_paid") : 0.0);
				model.setPendingFee(rs.getDouble("total_pending") != 0 ? rs.getDouble("total_pending") : 0.0);
				model.setSubjects(rs.getString("subjects") != null ? rs.getString("subjects") : "--");
				model.setTeachers(rs.getString("teachers") != null ? rs.getString("teachers") : "--");
				list.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<DashboardModel> getSubjectDashboardData(boolean showAll) {
		List<DashboardModel> list = new ArrayList<>();
		String query = """
				SELECT
				    sub.subject_name,
				    sub.subject_type,
				    c.course_name,
				    COUNT(DISTINCT ss.student_course_id) AS total_students,
				    GROUP_CONCAT(DISTINCT t.name) AS teachers
				FROM subjects sub
				JOIN subject_course sc ON sub.subject_id = sc.subject_id
				JOIN courses c ON sc.course_id = c.course_id
				LEFT JOIN subject_teachers st ON sub.subject_id = st.subject_id
				LEFT JOIN teachers t ON st.teacher_id = t.teacher_id
				LEFT JOIN student_subjects ss ON sc.id = ss.subject_course_id
				WHERE sub.is_active = TRUE AND c.is_active = TRUE
				GROUP BY sub.subject_name, sub.subject_type, c.course_name
				""" + (showAll ? "" : "HAVING total_students > 0") + ";";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setSubjects(rs.getString("subject_name") != null ? rs.getString("subject_name") : "--");
				model.setSubjectType(rs.getString("subject_type") != null ? rs.getString("subject_type") : "--");
				model.setCourse(rs.getString("course_name") != null ? rs.getString("course_name") : "--");
				model.setTotalStudents(rs.getInt("total_students"));
				model.setTeachers(rs.getString("teachers") != null ? rs.getString("teachers") : "--");
				list.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<DashboardModel> getTeacherLoadDashboardData() {
		List<DashboardModel> list = new ArrayList<>();
		String query = """
				SELECT
				    t.teacher_id,
				    t.name AS teacher_name,
				    COUNT(DISTINCT st.subject_id) AS total_subjects,
				    COUNT(DISTINCT ss.student_course_id) AS total_students,
				    GROUP_CONCAT(DISTINCT sub.subject_name) AS subjects,
				    GROUP_CONCAT(DISTINCT c.course_name) AS courses
				FROM teachers t
				LEFT JOIN subject_teachers st ON t.teacher_id = st.teacher_id
				LEFT JOIN subject_course sc ON st.subject_id = sc.subject_id
				LEFT JOIN subjects sub ON sc.subject_id = sub.subject_id
				LEFT JOIN courses c ON sc.course_id = c.course_id
				LEFT JOIN student_subjects ss ON sc.id = ss.subject_course_id
				WHERE t.is_active = TRUE
				GROUP BY t.teacher_id, t.name;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setTeacherId(rs.getInt("teacher_id"));
				model.setTeacherName(rs.getString("teacher_name") != null ? rs.getString("teacher_name") : "--");
				model.setTotalSubjects(rs.getInt("total_subjects"));
				model.setTotalStudents(rs.getInt("total_students"));
				model.setSubjects(rs.getString("subjects") != null ? rs.getString("subjects") : "--");
				model.setCourse(rs.getString("courses") != null ? rs.getString("courses") : "--");
				list.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<DashboardModel> getCourseWiseStudentCounts() {
		List<DashboardModel> courseCounts = new ArrayList<>();
		String query = """
				SELECT
				    c.course_name,
				    COUNT(DISTINCT s.student_id) AS student_count
				FROM courses c
				LEFT JOIN student_courses sc ON c.course_id = sc.course_id
				LEFT JOIN students s ON sc.student_id = s.student_id AND s.is_active = TRUE
				WHERE c.is_active = TRUE
				GROUP BY c.course_name, c.course_id
				ORDER BY student_count DESC, c.course_name;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setCourse(rs.getString("course_name"));
				model.setTotalStudents(rs.getInt("student_count"));
				courseCounts.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courseCounts;
	}

	public List<DashboardModel> getCourseWiseSubjectCounts() {
		List<DashboardModel> courseSubjectCounts = new ArrayList<>();
		String query = """
				SELECT
				    c.course_name,
				    COUNT(DISTINCT sub.subject_id) AS subject_count
				FROM courses c
				LEFT JOIN subject_course sc ON c.course_id = sc.course_id
				LEFT JOIN subjects sub ON sc.subject_id = sub.subject_id
				WHERE c.is_active = TRUE
				GROUP BY c.course_name, c.course_id
				ORDER BY subject_count DESC, c.course_name;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setCourse(rs.getString("course_name"));
				model.setTotalSubjects(rs.getInt("subject_count"));
				courseSubjectCounts.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courseSubjectCounts;
	}

	public List<DashboardModel> getTeacherWiseSubjectCounts() {
		List<DashboardModel> teacherSubjectCounts = new ArrayList<>();
		String query = """
				SELECT
				    t.name AS teacher_name,
				    COUNT(DISTINCT st.subject_id) AS subject_count
				FROM teachers t
				LEFT JOIN subject_teachers st ON t.teacher_id = st.teacher_id
				WHERE t.is_active = TRUE
				GROUP BY t.teacher_id, t.name
				ORDER BY subject_count DESC, t.name;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setTeacherName(rs.getString("teacher_name"));
				model.setTotalSubjects(rs.getInt("subject_count"));
				teacherSubjectCounts.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return teacherSubjectCounts;
	}

	public List<DashboardModel> getSubjectWiseStudentCounts() {
		List<DashboardModel> subjectStudentCounts = new ArrayList<>();
		String query = """
				SELECT
				    sub.subject_name,
				    COUNT(DISTINCT s.student_id) AS student_count
				FROM subjects sub
				LEFT JOIN subject_course sc ON sub.subject_id = sc.subject_id
				LEFT JOIN student_subjects ss ON sc.id = ss.subject_course_id
				LEFT JOIN student_courses stc ON ss.student_course_id = stc.student_course_id
				LEFT JOIN students s ON stc.student_id = s.student_id AND s.is_active = TRUE
				WHERE sub.is_active = TRUE
				GROUP BY sub.subject_id, sub.subject_name
				ORDER BY student_count DESC, sub.subject_name;
				""";

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setSubjects(rs.getString("subject_name"));
				model.setTotalStudents(rs.getInt("student_count"));
				subjectStudentCounts.add(model);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjectStudentCounts;
	}
}