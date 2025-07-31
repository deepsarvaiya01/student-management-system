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
				    s.name AS name,
				    ANY_VALUE(c.course_name) AS course,
				    ANY_VALUE(f.paid_amount) AS paidFee,               
				    ANY_VALUE(f.pending_amount) AS pendingFee,         
				    ANY_VALUE(f.total_fee) AS totalFee,                
				    GROUP_CONCAT(DISTINCT sub.subject_name) AS subjects,
				    GROUP_CONCAT(DISTINCT t.name) AS teachers
				FROM students s
				JOIN student_courses sc ON s.student_id = sc.student_id
				JOIN courses c ON sc.course_id = c.course_id
				LEFT JOIN fees f ON sc.student_course_id = f.student_course_id
				LEFT JOIN student_subjects ss ON sc.student_course_id = ss.student_course_id
				LEFT JOIN subject_course subj_c ON ss.subject_course_id = subj_c.id
				LEFT JOIN subjects sub ON subj_c.subject_id = sub.subject_id
				LEFT JOIN subject_teachers st ON sub.subject_id = st.subject_id
				LEFT JOIN teachers t ON st.teacher_id = t.teacher_id
				WHERE s.is_active = TRUE
				GROUP BY s.student_id;


																        """;

		try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

			int srNo = 0;

			while (rs.next()) {
				DashboardModel model = new DashboardModel();
				model.setSrNo(++srNo);
				model.setStudentId(rs.getInt("student_id"));
				model.setName(rs.getString("name"));
				model.setCourse(rs.getString("course"));
				model.setPaidFee(rs.getDouble("paidFee"));
				model.setPendingFee(rs.getDouble("pendingFee"));
				model.setTotalFee(rs.getDouble("totalFee"));
				model.setSubjects(rs.getString("subjects"));
				model.setTeachers(rs.getString("teachers"));

				dashboardList.add(model);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return dashboardList;
	}
}
