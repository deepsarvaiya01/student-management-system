package com.sms.dao;

import java.sql.*;
import java.util.*;

import com.sms.database.DBConnection;
import com.sms.model.Teacher;

public class TeacherDao {

	private final Connection connection;

	public TeacherDao() throws SQLException {
		this.connection = DBConnection.connect();
	}

	public boolean addTeacher(Teacher t) {
		String sql = "INSERT INTO teachers (name, qualification, experience) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, t.getName());
			stmt.setString(2, t.getQualification());
			stmt.setDouble(3, t.getExperience());

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						t.setTeacherId(rs.getInt(1));
					}
				}
				return true;
			}
		} catch (SQLException e) {
			System.out.println("Error inserting teacher: " + e.getMessage());
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
			System.out.println("Error fetching teachers: " + e.getMessage());
		}
		return list;
	}

	public boolean softDeleteTeacher(int id) {
		String sql = "UPDATE teachers SET is_active = FALSE WHERE teacher_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			System.out.println("Error deleting teacher: " + e.getMessage());
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
			System.out.println("Subject assignment failed: " + e.getMessage());
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
			System.out.println("Error removing subject: " + e.getMessage());
			return false;
		}
	}

	public Map<Integer, String> getAssignedSubjects(int teacherId) {
		Map<Integer, String> subjects = new HashMap<>();
		String sql = "SELECT s.subject_id, s.subject_name FROM subjects s "
				+ "JOIN subject_teachers st ON s.subject_id = st.subject_id "
				+ "JOIN teachers t ON t.teacher_id = st.teacher_id " + "WHERE st.teacher_id = ? AND t.is_active = 1";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, teacherId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				subjects.put(rs.getInt("subject_id"), rs.getString("subject_name"));
			}
		} catch (SQLException e) {
			System.out.println("Error fetching assigned subjects: " + e.getMessage());
		}
		return subjects;
	}

	public Map<Integer, String> fetchAvailableSubjectsForTeacher(int teacherId) {
		Map<Integer, String> map = new HashMap<>();
		String sql = "SELECT subject_id, subject_name FROM subjects WHERE subject_id NOT IN "
				+ "(SELECT subject_id FROM subject_teachers WHERE teacher_id = ?)";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, teacherId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				map.put(rs.getInt("subject_id"), rs.getString("subject_name"));
			}
		} catch (SQLException e) {
			System.out.println("Error fetching available subjects: " + e.getMessage());
		}
		return map;
	}

	public Teacher getTeacherById(int id) {
		String sql = "SELECT * FROM teachers WHERE teacher_id = ? AND is_active = 1";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Teacher(rs.getInt("teacher_id"), rs.getString("name"), rs.getString("qualification"),
						rs.getDouble("experience"));
			}
		} catch (SQLException e) {
			System.out.println("Error fetching teacher by ID: " + e.getMessage());
		}
		return null;
	}

	public boolean isTeacherActive(int teacherId) {
		String sql = "SELECT is_active FROM teachers WHERE teacher_id = ?";
		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, teacherId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("is_active");
			}
		} catch (SQLException e) {
			System.out.println("Error checking teacher status: " + e.getMessage());
		}
		return false;
	}

	// Fetch all inactive teachers
	public List<Teacher> getInactiveTeachers() {
		List<Teacher> list = new ArrayList<>();
		String sql = "SELECT * FROM teachers WHERE is_active = FALSE";
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

	// Restore teacher
	public boolean restoreTeacher(int id) {
		String restoreSql = "UPDATE teachers SET is_active = TRUE WHERE teacher_id = ?";
		String deleteSubjectsSql = "DELETE FROM subject_teachers WHERE teacher_id = ?";

		try {
			connection.setAutoCommit(false); // Begin transaction

			// Step 1: Restore the teacher
			try (PreparedStatement ps1 = connection.prepareStatement(restoreSql)) {
				ps1.setInt(1, id);
				int updated = ps1.executeUpdate();

				if (updated == 0) {
					connection.rollback();
					return false; // No rows updated, teacher not found
				}
			}

			// Step 2: Remove all subject assignments
			try (PreparedStatement ps2 = connection.prepareStatement(deleteSubjectsSql)) {
				ps2.setInt(1, id);
				ps2.executeUpdate(); // No need to check count, just delete if any
			}

			connection.commit();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;
		} finally {
			try {
				connection.setAutoCommit(true); // Reset to default
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


}
