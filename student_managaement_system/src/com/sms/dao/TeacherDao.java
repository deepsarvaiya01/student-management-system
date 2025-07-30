package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sms.database.DBConnection;
import com.sms.model.Teacher;

public class TeacherDao {

	private final Connection connection;

	public TeacherDao() throws SQLException {
		this.connection = DBConnection.connect();
	}

	public boolean addTeacher(Teacher t) {
		String sql = "INSERT INTO teachers (name, qualification, experience) VALUES (?, ?, ?)";
		try {
			Connection conn = DBConnection.connect();
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			stmt.setString(1, t.getName());
			stmt.setString(2, t.getQualification());
			stmt.setDouble(3, t.getExperience());

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						int generatedId = rs.getInt(1);
						t.setTeacherId(generatedId);
					}
				}
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Teacher> getAllTeachers() {
		List<Teacher> list = new ArrayList<>();
		String sql = "SELECT * FROM teachers WHERE is_active = TRUE";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Teacher(rs.getInt("teacher_id"), rs.getString("name"), rs.getString("qualification"),
						rs.getDouble("experience")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean softDeleteTeacher(int id) {
		String sql = "UPDATE teachers SET is_active = FALSE WHERE teacher_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean assignSubject(int teacherId, int subjectId) {
		String sql = "INSERT INTO subject_teachers (subject_id, teacher_id) VALUES (?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, subjectId);
			ps.setInt(2, teacherId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Subject Assigment Failed..");
			return false;
		}
	}

	public boolean removeSubject(int teacherId, int subjectId) {
		String sql = "DELETE FROM subject_teachers WHERE teacher_id = ? AND subject_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, teacherId);
			ps.setInt(2, subjectId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<Integer, String> getAssignedSubjects(int teacherId) {
		Map<Integer, String> subjects = new HashMap<>();
		String sql = "SELECT s.subject_id, s.subject_name " + "FROM subjects s "
				+ "JOIN subject_teachers st ON s.subject_id = st.subject_id "
				+ "JOIN teachers t ON t.teacher_id = st.teacher_id " + "WHERE st.teacher_id = ? AND t.is_active = 1";

		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, teacherId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				subjects.put(rs.getInt("subject_id"), rs.getString("subject_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return subjects;
	}

	public Teacher getTeacherById(int id) {
		String sql = "SELECT * FROM teachers WHERE teacher_id = ? AND is_active = 1";
		try {
			Connection con = DBConnection.connect();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Teacher(rs.getInt("teacher_id"), rs.getString("name"), rs.getString("qualification"),
						rs.getDouble("experience"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<Integer, String> fetchAvailableSubjectsForTeacher(int teacherId) {
		Map<Integer, String> map = new HashMap<>();

		try {
			Connection con = DBConnection.connect();
			PreparedStatement ps = con
					.prepareStatement("SELECT subject_id, subject_name FROM subjects WHERE subject_id NOT IN "
							+ "(SELECT subject_id FROM subject_teachers WHERE teacher_id = ?)");

			ps.setInt(1, teacherId);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				map.put(rs.getInt("subject_id"), rs.getString("subject_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}

}
