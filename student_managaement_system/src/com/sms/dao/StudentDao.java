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

	private static final int INVALID_ID = 0;
	private Connection connection = null;

	public StudentDao() throws SQLException {
		try {
			this.connection = DBConnection.connect();
		} catch (SQLException e) {
			throw new SQLException("Failed to establish database connection: " + e.getMessage());
		}
	}

	public List<Student> readAllStudents() {
		List<Student> students = new ArrayList<>();
		String sql = "SELECT s.student_id, s.name, s.email, s.gr_number, p.city, p.mobile_no, p.age "
				+ "FROM students s LEFT JOIN profiles p ON s.student_id = p.student_id " + "WHERE s.is_active = true";
		try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				Student student = new Student();
				student.setStudent_id(result.getInt("student_id"));
				student.setName(result.getString("name"));
				student.setEmail(result.getString("email"));
				student.setGr_number(result.getInt("gr_number"));
				student.setCity(result.getString("city"));
				student.setMobile_no(result.getString("mobile_no"));
				student.setAge(result.getInt("age"));
				students.add(student);
			}
		} catch (SQLException e) {
			System.err.println("Error reading students: " + e.getMessage());
			e.printStackTrace();
		}
		return students;
	}

	public List<Course> readAllCourses(int studentId) {
		if (studentId <= INVALID_ID || searchStudentById(studentId) == null) {
			return new ArrayList<>();
		}

		List<Course> courses = new ArrayList<>();
		String sql = "SELECT c.course_id, c.course_name, c.no_of_semester, c.total_fee "
				+ "FROM courses c JOIN student_courses sc ON c.course_id = sc.course_id " + "WHERE sc.student_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			ResultSet result = pstmt.executeQuery();

			while (result.next()) {
				Course course = new Course();
				course.setCourse_id(result.getInt("course_id"));
				course.setCourse_name(result.getString("course_name"));
				course.setNo_of_semester(result.getInt("no_of_semester"));
				course.setTotal_fee(result.getBigDecimal("total_fee"));
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
				course.setTotal_fee(result.getBigDecimal("total_fee"));
				courses.add(course);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	public boolean assignCourseToStudent(int studentId, int courseId) {
		if (studentId <= 0 || searchStudentById(studentId) == null) {
			return false;
		}
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			return false;
		}
		if (readAllCourses(studentId).stream().anyMatch(c -> c.getCourse_id() == courseId)) {
			return false;
		}

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
		if (studentId <= 0) {
			return null;
		}

		Student student = null;
		String sql = "SELECT s.student_id, s.name, s.email, s.gr_number, s.is_active, " + "p.city, p.mobile_no, p.age "
				+ "FROM students s LEFT JOIN profiles p ON s.student_id = p.student_id "
				+ "WHERE s.student_id = ? AND s.is_active = true";

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
				student.setIs_active(result.getBoolean("is_active"));
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return student;
	}

	public boolean deleteStudentById(int studentId) {
		if (studentId <= 0 || searchStudentById(studentId) == null) {
			return false;
		}
		boolean success = false;
		String updateStudent = "UPDATE students SET is_active = false WHERE student_id = ?";
		String updateProfile = "UPDATE profiles SET is_active = false WHERE student_id = ?";
		String updateStudentCourses = "UPDATE student_courses SET is_active = false WHERE student_id = ?";
		String updateFees = "UPDATE fees SET is_active = false WHERE student_course_id IN "
				+ "(SELECT student_course_id FROM student_courses WHERE student_id = ?)";
		String updateStudentSubjects = "UPDATE student_subjects SET is_active = false WHERE student_course_id IN "
				+ "(SELECT student_course_id FROM student_courses WHERE student_id = ?)";
		try {
			connection.setAutoCommit(false);
			try (PreparedStatement pstmt = connection.prepareStatement(updateStudent)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			}
			try (PreparedStatement pstmt = connection.prepareStatement(updateProfile)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// Skip profile update if is_active column doesn't exist
			}
			try (PreparedStatement pstmt = connection.prepareStatement(updateStudentCourses)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// Skip student_courses update if is_active column doesn't exist
			}
			try (PreparedStatement pstmt = connection.prepareStatement(updateFees)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// Skip fees update if is_active column doesn't exist
			}
			try (PreparedStatement pstmt = connection.prepareStatement(updateStudentSubjects)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// Skip student_subjects update if is_active column doesn't exist
			}
			connection.commit();
			return true;
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			e.printStackTrace();
			return false;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean restoreStudentById(int studentId) {
		if (studentId <= 0) {
			return false;
		}

		String checkSql = "SELECT is_active FROM students WHERE student_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
			pstmt.setInt(1, studentId);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				return false;
			}
			if (rs.getBoolean("is_active")) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		String sql = "UPDATE students SET is_active = true WHERE student_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean addStudentWithProfileAndCourse(Student student, int courseId) {
		try {
			// Check for duplicate GR number
			String checkGrSql = "SELECT COUNT(*) FROM students WHERE gr_number = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(checkGrSql)) {
				pstmt.setInt(1, student.getGr_number());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					return false; // Duplicate GR number
				}
			}
			// Check for duplicate email
			String checkEmailSql = "SELECT COUNT(*) FROM students WHERE email = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(checkEmailSql)) {
				pstmt.setString(1, student.getEmail());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					return false; // Duplicate email
				}
			}
			// Check if course exists
			String checkCourseSql = "SELECT COUNT(*) FROM courses WHERE course_id = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(checkCourseSql)) {
				pstmt.setInt(1, courseId);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) == 0) {
					return false; // Invalid course ID
				}
			}
			boolean success = false;
			String insertStudent = "INSERT INTO students (name, gr_number, email) VALUES (?, ?, ?)";
			String insertProfile = "INSERT INTO profiles (student_id, city, mobile_no, age) VALUES (?, ?, ?, ?)";
			String insertStudentCourse = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
			try {
				connection.setAutoCommit(false);
				// Insert student
				int studentId = -1;
				try (PreparedStatement psStudent = connection.prepareStatement(insertStudent, Statement.RETURN_GENERATED_KEYS)) {
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
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}
			return success;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}