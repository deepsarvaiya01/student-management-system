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
import com.sms.model.Student;

public class StudentDao {

	private Connection connection = null;
	private Statement statement = null;

	public StudentDao() throws SQLException {
		this.connection = DBConnection.connect();
	}

	public List<Student> readAllStudents() {
		List<Student> students = new ArrayList<Student>();
		try {
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery("select * from students");

			while (result.next()) {
				Student student = new Student();
				student.setStudent_id(result.getInt("student_id"));
				student.setName(result.getString("name"));
				student.setEmail(result.getString("email"));
				student.setGr_number(result.getInt("gr_number"));
				students.add(student);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return students;
	}

	public List<Course> readAllCourses(int studentId) {
		List<Course> courses = new ArrayList<>();
		String sql = "SELECT c.course_id, c.course_name, c.no_of_semester " + "FROM courses c "
				+ "JOIN student_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			ResultSet result = pstmt.executeQuery();

			while (result.next()) {
				Course course = new Course();
				course.setCourse_id(result.getInt("course_id"));
				course.setCourse_name(result.getString("course_name"));
				course.setNo_of_semester(result.getInt("no_of_semester"));
				courses.add(course);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return courses;
	}

	public List<Course> getAllCourses() {
		List<Course> courses = new ArrayList<>();
		String sql = "SELECT * FROM courses";
		try (Statement stmt = connection.createStatement(); ResultSet result = stmt.executeQuery(sql)) {
			while (result.next()) {
				Course course = new Course();
				course.setCourse_id(result.getInt("course_id"));
				course.setCourse_name(result.getString("course_name"));
				course.setNo_of_semester(result.getInt("no_of_semester"));
				courses.add(course);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	public boolean assignCourseToStudent(int studentId, int courseId) {
		String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			pstmt.setInt(2, courseId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Student searchStudentById(int studentId) {
		Student student = null;
		String sql = "SELECT s.student_id, s.name, s.email, s.gr_number, s.is_active, " + "p.city, p.mobile_no, p.age "
				+ "FROM students s " + "LEFT JOIN profiles p ON s.student_id = p.student_id "
				+ "WHERE s.student_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			ResultSet result = pstmt.executeQuery();

			if (result.next()) {
				student = new Student();
				student.setStudent_id(result.getInt("student_id"));
				student.setName(result.getString("name"));
				student.setEmail(result.getString("email"));
				student.setGr_number(result.getInt("gr_number"));
				student.setCity(result.getString("city"));
				student.setMobile_no(result.getString("mobile_no"));
				student.setAge(result.getInt("age"));
//                student.setIs_active(result.getBoolean("is_active")); // Optional, if needed
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return student;
	}

	public boolean deleteStudentById(int studentId) {
		boolean deleted = false;
		String deleteStudentSubjects = "DELETE ss FROM student_subjects ss "
				+ "JOIN student_courses sc ON ss.student_course_id = sc.student_course_id " + "WHERE sc.student_id = ?";

		String deleteFees = "DELETE f FROM fees f "
				+ "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " + "WHERE sc.student_id = ?";

		String deleteStudentCourses = "DELETE FROM student_courses WHERE student_id = ?";

		String deleteProfile = "DELETE FROM profiles WHERE student_id = ?";

		String deleteStudent = "DELETE FROM students WHERE student_id = ?";

		try {
			connection.setAutoCommit(false); // Start transaction

			try (PreparedStatement ps1 = connection.prepareStatement(deleteStudentSubjects);
					PreparedStatement ps2 = connection.prepareStatement(deleteFees);
					PreparedStatement ps3 = connection.prepareStatement(deleteStudentCourses);
					PreparedStatement ps4 = connection.prepareStatement(deleteProfile);
					PreparedStatement ps5 = connection.prepareStatement(deleteStudent)) {

				for (PreparedStatement ps : List.of(ps1, ps2, ps3, ps4, ps5)) {
					ps.setInt(1, studentId);
					ps.executeUpdate();
				}

				connection.commit();
				deleted = true;
			} catch (SQLException e) {
				connection.rollback();
				e.printStackTrace();
			}

			connection.setAutoCommit(true); // End transaction
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return deleted;
	}

	public boolean addStudentWithProfileAndCourse(Student student, int courseId) {
		boolean success = false;
		String insertStudent = "INSERT INTO students (name, gr_number, email) VALUES (?, ?, ?)";
		String insertProfile = "INSERT INTO profiles (student_id, city, mobile_no, age) VALUES (?, ?, ?, ?)";
		String insertStudentCourse = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
		try {
			connection.setAutoCommit(false);
			// Insert student
			int studentId = -1;
			try (PreparedStatement psStudent = connection.prepareStatement(insertStudent,
					Statement.RETURN_GENERATED_KEYS)) {
				psStudent.setString(1, student.getName());
				psStudent.setInt(2, student.getGr_number());
				psStudent.setString(3, student.getEmail());
				int affectedRows = psStudent.executeUpdate();
				if (affectedRows == 0)
					throw new SQLException("Creating student failed, no rows affected.");
				try (ResultSet generatedKeys = psStudent.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						studentId = generatedKeys.getInt(1);
					} else {
						throw new SQLException("Creating student failed, no ID obtained.");
					}
				}
			}
			// Insert profile
			try (PreparedStatement psProfile = connection.prepareStatement(insertProfile)) {
				psProfile.setInt(1, studentId);
				psProfile.setString(2, student.getCity());
				psProfile.setString(3, student.getMobile_no());
				psProfile.setInt(4, student.getAge());
				psProfile.executeUpdate();
			}
			// Assign course
			try (PreparedStatement psCourse = connection.prepareStatement(insertStudentCourse)) {
				psCourse.setInt(1, studentId);
				psCourse.setInt(2, courseId);
				psCourse.executeUpdate();
			}
			connection.commit();
			success = true;
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return success;
	}

}