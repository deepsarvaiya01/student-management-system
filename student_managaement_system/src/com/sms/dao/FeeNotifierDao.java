package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sms.database.DBConnection;
import com.sms.model.FeeNotifier;

public class FeeNotifierDao {

	private Connection conn;
	public FeeNotifierDao() {
		try {
			this.conn =  DBConnection.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public FeeNotifier getPreferences(int studentId) {
	    String sql = "SELECT * FROM fee_notifier WHERE student_id = ?";
	    try (
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, studentId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            FeeNotifier prefs = new FeeNotifier();
	            prefs.setStudentId(rs.getInt("student_id"));
	            prefs.setSmsEnabled(rs.getBoolean("sms_enabled"));
	            prefs.setEmailEnabled(rs.getBoolean("email_enabled"));
	            prefs.setWhatsappEnabled(rs.getBoolean("whatsapp_enabled"));
	            return prefs;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public boolean updatePreferences(FeeNotifier prefs) {
	    String sql = "UPDATE fee_notifier SET sms_enabled = ?, email_enabled = ?, whatsapp_enabled = ? WHERE student_id = ?";
	    try (
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setBoolean(1, prefs.isSmsEnabled());
	        ps.setBoolean(2, prefs.isEmailEnabled());
	        ps.setBoolean(3, prefs.isWhatsappEnabled());
	        ps.setInt(4, prefs.getStudentId());
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public boolean insertDefaultPreferences(int studentId) {
	    String sql = "INSERT INTO fee_notifier (student_id, sms_enabled, email_enabled, whatsapp_enabled) VALUES (?, TRUE, TRUE, TRUE)";
	    try (
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, studentId);
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        System.out.println(e.getMessage());
	    }
	    return false;
	}
	
	public List<com.sms.model.Student> getAllStudents() {
		List<com.sms.model.Student> students = new ArrayList<>();
		String sql = "SELECT student_id, name, email, gr_number FROM students WHERE is_active = TRUE";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				com.sms.model.Student student = new com.sms.model.Student();
				student.setStudent_id(rs.getInt("student_id"));
				student.setName(rs.getString("name"));
				student.setEmail(rs.getString("email"));
				student.setGr_number(rs.getInt("gr_number"));
				students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return students;
	}

}
