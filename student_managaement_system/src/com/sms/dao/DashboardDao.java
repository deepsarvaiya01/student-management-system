// 📁 DashboardDao.java
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<DashboardModel> getDashboardData() {
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
				model.setName(rs.getString("student_name"));
				model.setCourse(rs.getString("course"));
				model.setTotalFee(rs.getDouble("total_fee"));
				model.setPaidFee(rs.getDouble("paid_amount"));
				model.setPendingFee(rs.getDouble("pending_amount"));
				model.setSubjects(rs.getString("subjects"));
				model.setTeachers(rs.getString("teachers"));
				dashboardList.add(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dashboardList;
	}

	public List<DashboardModel> getGeneralDashboardData() {
		return getDashboardData();
	}

	public List<DashboardModel> getCourseWiseDashboardData() {
		List<DashboardModel> list = new ArrayList<>();
		String query = """
								SELECT
				    c.course_name,
				    COUNT(DISTINCT s.student_id) AS total_students,
				    SUM(f.total_fee) AS total_fees,
				    SUM(f.paid_amount) AS total_paid,
				    SUM(f.pending_amount) AS total_pending,
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
				GROUP BY c.course_name;

							""";
		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setCourse(rs.getString("course_name"));
				model.setTotalStudents(rs.getInt("total_students"));
				model.setTotalFee(rs.getDouble("total_fees"));
				model.setPaidFee(rs.getDouble("total_paid"));
				model.setPendingFee(rs.getDouble("total_pending"));
				model.setSubjects(rs.getString("subjects"));
				model.setTeachers(rs.getString("teachers"));
				list.add(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<DashboardModel> getSubjectDashboardData() {
		List<DashboardModel> list = new ArrayList<>();
		String query = """
									SELECT
				    sub.subject_name,
				    c.course_name,
				    COUNT(DISTINCT ss.student_course_id) AS total_students,
				    GROUP_CONCAT(DISTINCT t.name) AS teachers
				FROM subjects sub
				JOIN subject_course sc ON sub.subject_id = sc.subject_id
				JOIN courses c ON sc.course_id = c.course_id
				LEFT JOIN subject_teachers st ON sub.subject_id = st.subject_id
				LEFT JOIN teachers t ON st.teacher_id = t.teacher_id
				LEFT JOIN student_subjects ss ON sc.id = ss.subject_course_id
				GROUP BY sub.subject_name, c.course_name;

								""";
		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setSubjects(rs.getString("subject_name"));
				model.setCourse(rs.getString("course_name"));
				model.setTotalStudents(rs.getInt("total_students"));
				model.setTeachers(rs.getString("teachers"));
				list.add(model);
			}
		} catch (Exception e) {
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
				    COUNT(DISTINCT ss.student_course_id) AS total_students
				FROM teachers t
				LEFT JOIN subject_teachers st ON t.teacher_id = st.teacher_id
				LEFT JOIN subject_course sc ON st.subject_id = sc.subject_id
				LEFT JOIN student_subjects ss ON sc.id = ss.subject_course_id
				GROUP BY t.teacher_id, t.name;

								""";
		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
			int srNo = 0;
			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setTeacherId(rs.getInt("teacher_id"));
				model.setTeacherName(rs.getString("teacher_name"));
				model.setTotalSubjects(rs.getInt("total_subjects"));
				model.setTotalStudents(rs.getInt("total_students"));
				list.add(model);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}