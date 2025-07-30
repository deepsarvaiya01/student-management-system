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
			e.printStackTrace();
		}
		return students;
	}

	public List<Course> readAllCourses(int studentId) {
		if (studentId <= 0 || searchStudentById(studentId) == null) {
			System.out.println("Invalid or non-existent student ID: " + studentId);
			return new ArrayList<>();
		}

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
			System.out.println("Invalid or non-existent student ID: " + studentId);
			return false;
		}
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Invalid or non-existent course ID: " + courseId);
			return false;
		}
		if (readAllCourses(studentId).stream().anyMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Course already assigned to student.");
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
			System.out.println("Invalid student ID: " + studentId);
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
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return student;
	}

	public boolean deleteStudentById(int studentId) {
		if (studentId <= 0 || searchStudentById(studentId) == null) {
			System.out.println("Invalid or non-existent student ID: " + studentId);
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
			// Attempt to update profiles, ignore if is_active column doesn't exist
			try (PreparedStatement pstmt = connection.prepareStatement(updateProfile)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Skipping profile update (is_active may not exist): " + e.getMessage());
			}
			// Attempt to update student_courses
			try (PreparedStatement pstmt = connection.prepareStatement(updateStudentCourses)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Skipping student_courses update (is_active may not exist): " + e.getMessage());
			}
			// Attempt to update fees
			try (PreparedStatement pstmt = connection.prepareStatement(updateFees)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Skipping fees update (is_active may not exist): " + e.getMessage());
			}
			// Attempt to update student_subjects
			try (PreparedStatement pstmt = connection.prepareStatement(updateStudentSubjects)) {
				pstmt.setInt(1, studentId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("Skipping student_subjects update (is_active may not exist): " + e.getMessage());
			}
			connection.commit();
			System.out.println("Student ID " + studentId + " and related data marked as inactive.");
			success = true;
		} catch (SQLException e) {
			System.out.println("Failed to mark student ID " + studentId + " as inactive: " + e.getMessage());
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

	public boolean restoreStudentById(int studentId) {
		if (studentId <= 0) {
			System.out.println("Invalid student ID: " + studentId);
			return false;
		}

		// Check if student exists and is inactive
		String checkSql = "SELECT is_active FROM students WHERE student_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkSql)) {
			pstmt.setInt(1, studentId);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				System.out.println("Student ID " + studentId + " does not exist.");
				return false;
			}
			if (rs.getBoolean("is_active")) {
				System.out.println("Student ID " + studentId + " is already active.");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("Error checking student status: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		String sql = "UPDATE students SET is_active = true WHERE student_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				System.out.println("Student ID " + studentId + " restored successfully.");
				return true;
			} else {
				System.out.println("Failed to restore student ID " + studentId + ".");
				return false;
			}
		} catch (SQLException e) {
			System.out.println("Failed to restore student ID " + studentId + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public boolean addStudentWithProfileAndCourse(Student student, int courseId) {
		if (student == null || student.getName() == null || !student.getName().matches("[a-zA-Z ]{1,50}")) {
			System.out.println("Invalid name (letters/spaces, max 50 chars).");
			return false;
		}
		String checkGrSql = "SELECT COUNT(*) FROM students WHERE gr_number = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkGrSql)) {
			pstmt.setInt(1, student.getGr_number());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Duplicate GR number: " + student.getGr_number());
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if (student.getGr_number() <= 0 || String.valueOf(student.getGr_number()).length() < 4
				|| String.valueOf(student.getGr_number()).length() > 10) {
			System.out.println("Invalid GR number (4-10 digits).");
			return false;
		}
		String checkEmailSql = "SELECT COUNT(*) FROM students WHERE email = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkEmailSql)) {
			pstmt.setString(1, student.getEmail());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				System.out.println("Duplicate email: " + student.getEmail());
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		if (student.getEmail() == null || !student.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
				|| student.getEmail().length() > 100) {
			System.out.println("Invalid email.");
			return false;
		}
		if (student.getCity() == null || !student.getCity().matches("[a-zA-Z ]{1,50}")) {
			System.out.println("Invalid city (letters/spaces, max 50 chars).");
			return false;
		}
		if (student.getMobile_no() == null || !student.getMobile_no().matches("\\d{10}")
				|| student.getMobile_no().length() > 15) {
			System.out.println("Invalid mobile number (10 digits).");
			return false;
		}
		if (student.getAge() < 15 || student.getAge() > 100) {
			System.out.println("Invalid age (15-100).");
			return false;
		}
		String checkCourseSql = "SELECT COUNT(*) FROM courses WHERE course_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(checkCourseSql)) {
			pstmt.setInt(1, courseId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt(1) == 0) {
				System.out.println("Invalid course ID: " + courseId);
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

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