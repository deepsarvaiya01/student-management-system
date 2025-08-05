package com.sms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sms.database.DBConnection;
import com.sms.model.Course;
import com.sms.model.Subject;

public class CourseDAO {

	private Connection connection = null;

	public CourseDAO() throws SQLException {
		this.connection = DBConnection.connect();
	}

	public List<Course> getAllCourses() {
		List<Course> courses = new ArrayList<>();
		String sql = "SELECT * FROM courses WHERE is_active = 1";
		try (Statement stmt = connection.createStatement(); ResultSet result = stmt.executeQuery(sql)) {
			while (result.next()) {
				Course course = new Course();
				course.setCourse_id(result.getInt("course_id"));
				course.setCourse_name(result.getString("course_name"));
				course.setNo_of_semester(result.getInt("no_of_semester"));
				course.setTotal_fee(result.getBigDecimal("total_fee"));
				courses.add(course);
			}
		} catch (SQLException e) {
			System.err.println("Error getting all courses: " + e.getMessage());
			e.printStackTrace();
		}
		return courses;
	}

	public int addCourse(Course course) throws SQLException {
		String sql = "INSERT INTO courses (course_name, no_of_semester, total_fee) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, course.getCourse_name());
			stmt.setInt(2, course.getNo_of_semester());
			stmt.setBigDecimal(3, course.getTotal_fee());

			stmt.executeUpdate();
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}
		return -1;
	}

	public void assignSubjectToCourse(int courseId, int subjectId) throws SQLException {
		String sql = "INSERT INTO subject_course (course_id, subject_id) VALUES (?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, courseId);
			stmt.setInt(2, subjectId);
			stmt.executeUpdate();
		}
	}

	public Course getCourseById(int id) {
		String sql = "SELECT * FROM courses WHERE course_id = ? AND is_active = 1";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Course course = new Course();
				course.setCourse_id(rs.getInt("course_id"));
				course.setCourse_name(rs.getString("course_name"));
				course.setNo_of_semester(rs.getInt("no_of_semester"));
				course.setTotal_fee(rs.getBigDecimal("total_fee"));
				return course;
			}
		} catch (SQLException e) {
			System.err.println("Error getting course by ID: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public boolean deleteCourse(int courseId) {
        String deleteSubjectMapping = "DELETE FROM subject_course WHERE course_id = ?";
        String softDeleteCourse = "UPDATE courses SET is_active = 0 WHERE course_id = ?";
        
        try (PreparedStatement stmt1 = connection.prepareStatement(deleteSubjectMapping);
             PreparedStatement stmt2 = connection.prepareStatement(softDeleteCourse)) {

            stmt1.setInt(1, courseId);
            stmt1.executeUpdate();

            stmt2.setInt(1, courseId);
            return stmt2.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
	
	public List<Subject> getSubjectsByCourseId(int courseId) {
		List<Subject> subjects = new ArrayList<>();
		String sql = "SELECT s.subject_id, s.subject_name FROM subjects s "
				+ "JOIN subject_course cs ON s.subject_id = cs.subject_id WHERE cs.course_id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setInt(1, courseId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Subject subject = new Subject();
				subject.setSubject_id(rs.getInt("subject_id"));
				subject.setSubject_name(rs.getString("subject_name"));
				subjects.add(subject);
			}
		} catch (SQLException e) {
			System.err.println("Error getting subjects by course ID: " + e.getMessage());
			e.printStackTrace();
		}
		return subjects;
	}

	public Course getCourseByName(String name) {
		String sql = "SELECT * FROM courses WHERE course_name = ? AND is_active = 1";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				Course course = new Course();
				course.setCourse_id(rs.getInt("course_id"));
				course.setCourse_name(rs.getString("course_name"));
				course.setNo_of_semester(rs.getInt("no_of_semester"));
				course.setTotal_fee(rs.getBigDecimal("total_fee"));
				return course;
			}
		} catch (SQLException e) {
			System.err.println("Error getting course by name: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	// Update course fees
	public boolean updateCourseFees(int courseId, java.math.BigDecimal newTotalFee) {
		String sql = "UPDATE courses SET total_fee = ? WHERE course_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setBigDecimal(1, newTotalFee);
			pstmt.setInt(2, courseId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			System.err.println("Error updating course fees: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

}
