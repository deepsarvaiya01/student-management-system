package com.sms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sms.database.DBConnection;
import com.sms.model.Course;
import com.sms.model.Gender;
import com.sms.model.Student;
import com.sms.model.Subject;

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
		String sql = "SELECT s.student_id, s.name, s.email, s.gr_number, s.gender, p.city, p.mobile_no, p.age "
				+ "FROM students s LEFT JOIN profiles p ON s.student_id = p.student_id " + "WHERE s.is_active = true";
		try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
			while (result.next()) {
				Student student = new Student();
				student.setStudent_id(result.getInt("student_id"));
				student.setName(result.getString("name"));
				student.setEmail(result.getString("email"));
				student.setGr_number(result.getInt("gr_number"));
				student.setGender(Gender.fromString(result.getString("gender")));
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
				+ "FROM courses c JOIN student_courses sc ON c.course_id = sc.course_id "
				+ "WHERE sc.student_id = ? and c.is_active = true";

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
		String sql = "SELECT s.student_id, s.name, s.email, s.gr_number, s.gender, s.is_active, "
				+ "p.city, p.mobile_no, p.age " + "FROM students s LEFT JOIN profiles p ON s.student_id = p.student_id "
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
				student.setGender(Gender.fromString(result.getString("gender")));
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

	public boolean isFeeClearedForStudent(int studentId) {
		String query = """
					        SELECT
				    COALESCE(SUM(fees.total_fee), 0) AS total_fee,
				    COALESCE(SUM(fees.paid_amount), 0) AS total_paid,
				    COALESCE(SUM(fees.pending_amount), 0) AS total_pending
				FROM
				    student_courses sc
				JOIN
				    fees ON sc.student_course_id = fees.student_course_id
				WHERE
				    sc.student_id = ?;

					    """;

		try (PreparedStatement ps = connection.prepareStatement(query)) {

			ps.setInt(1, studentId);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				double totalFee = rs.getDouble("total_fee");
				double totalPaid = rs.getDouble("total_paid");
				return totalPaid >= totalFee;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteStudentById(int studentId) {
		if (studentId <= 0 || searchStudentById(studentId) == null) {
			return false;
		}

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

	public List<Student> getInactiveStudents() {
		List<Student> list = new ArrayList<>();
		String sql = "SELECT student_id, name, email, gr_number, gender FROM students WHERE is_active = FALSE";
		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new Student(rs.getInt("student_id"), rs.getString("name"), rs.getString("email"),
						rs.getInt("gr_number"), Gender.fromString(rs.getString("gender"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
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

	// Get subjects by course ID
	public List<Subject> getSubjectsByCourseId(int courseId) {
		List<Subject> subjects = new ArrayList<>();
		String sql = "SELECT s.subject_id, s.subject_name, s.subject_type FROM subjects s "
				+ "JOIN subject_course sc ON s.subject_id = sc.subject_id "
				+ "WHERE sc.course_id = ? AND s.is_active = 1";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, courseId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Subject subject = new Subject();
				subject.setSubject_id(rs.getInt("subject_id"));
				subject.setSubject_name(rs.getString("subject_name"));
				subject.setSubject_type(rs.getString("subject_type"));
				subjects.add(subject);
			}
		} catch (SQLException e) {
			System.err.println("Error getting subjects by course ID: " + e.getMessage());
			e.printStackTrace();
		}
		return subjects;
	}

	// Add student with profile, course, and subjects
	public boolean addStudentWithProfileAndCourseAndSubjects(Student student, int courseId, List<Integer> subjectIds) {
		try {
			// Check for duplicate GR number
			String checkGrSql = "SELECT COUNT(*) FROM students WHERE gr_number = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(checkGrSql)) {
				pstmt.setInt(1, student.getGr_number());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					System.out.println("Error: Duplicate GR number " + student.getGr_number());
					return false;
				}
			}
			// Check for duplicate email
			String checkEmailSql = "SELECT COUNT(*) FROM students WHERE email = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(checkEmailSql)) {
				pstmt.setString(1, student.getEmail());
				ResultSet rs = pstmt.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					System.out.println("Error: Duplicate email " + student.getEmail());
					return false;
				}
			}
			// Check if course exists and get total_fee
			String checkCourseSql = "SELECT total_fee FROM courses WHERE course_id = ?";
			BigDecimal courseFee = null;
			try (PreparedStatement pstmt = connection.prepareStatement(checkCourseSql)) {
				pstmt.setInt(1, courseId);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					courseFee = rs.getBigDecimal("total_fee");
					if (courseFee == null) {
						System.out.println("Error: Course ID " + courseId + " has no total fee set.");
						return false;
					}
				} else {
					System.out.println("Error: Invalid course ID " + courseId);
					return false;
				}
			}

			String insertStudent = "INSERT INTO students (name, gr_number, email, gender) VALUES (?, ?, ?, ?)";
			String insertProfile = "INSERT INTO profiles (student_id, city, mobile_no, age) VALUES (?, ?, ?, ?)";
			String insertStudentCourse = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
			String insertFee = "INSERT INTO fees (student_course_id, paid_amount, pending_amount, total_fee) VALUES (?, 0.0, ?, ?)";
			String insertStudentSubject = "INSERT INTO student_subjects (student_course_id, subject_course_id) VALUES (?, ?)";
			String getSubjectCourseId = "SELECT id FROM subject_course WHERE subject_id = ? AND course_id = ?";

			try {
				connection.setAutoCommit(false);
				int studentId = -1;
				// Insert student
				try (PreparedStatement psStudent = connection.prepareStatement(insertStudent,
						Statement.RETURN_GENERATED_KEYS)) {
					psStudent.setString(1, student.getName());
					psStudent.setInt(2, student.getGr_number());
					psStudent.setString(3, student.getEmail());
					psStudent.setString(4, student.getGender() != null ? student.getGender().name() : null);
					int affectedRows = psStudent.executeUpdate();
					if (affectedRows == 0) {
						throw new SQLException("Creating student failed, no rows affected.");
					}
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
				// Insert student course
				int studentCourseId = -1;
				try (PreparedStatement psCourse = connection.prepareStatement(insertStudentCourse,
						Statement.RETURN_GENERATED_KEYS)) {
					psCourse.setInt(1, studentId);
					psCourse.setInt(2, courseId);
					int affectedRows = psCourse.executeUpdate();
					if (affectedRows == 0) {
						throw new SQLException("Assigning course failed, no rows affected.");
					}
					try (ResultSet generatedKeys = psCourse.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							studentCourseId = generatedKeys.getInt(1);
						} else {
							throw new SQLException("Assigning course failed, no ID obtained.");
						}
					}
				}
				// Insert fee
				try (PreparedStatement psFee = connection.prepareStatement(insertFee)) {
					psFee.setInt(1, studentCourseId);
					psFee.setBigDecimal(2, courseFee);
					psFee.setBigDecimal(3, courseFee);
					psFee.executeUpdate();
				}
				// Insert subject assignments
				for (Integer subjectId : subjectIds) {
					// Get subject_course_id
					int subjectCourseId = -1;
					try (PreparedStatement psGetSubjectCourse = connection.prepareStatement(getSubjectCourseId)) {
						psGetSubjectCourse.setInt(1, subjectId);
						psGetSubjectCourse.setInt(2, courseId);
						ResultSet rs = psGetSubjectCourse.executeQuery();
						if (rs.next()) {
							subjectCourseId = rs.getInt("id");
						} else {
							System.out.println(
									"Warning: Subject ID " + subjectId + " not found for course ID " + courseId);
							continue;
						}
					}
					// Insert student subject
					try (PreparedStatement psStudentSubject = connection.prepareStatement(insertStudentSubject)) {
						psStudentSubject.setInt(1, studentCourseId);
						psStudentSubject.setInt(2, subjectCourseId);
						psStudentSubject.executeUpdate();
					}
				}
				connection.commit();
				return true;
			} catch (SQLException e) {
				connection.rollback();
				System.out.println("Database error: " + e.getMessage());
				return false;
			} finally {
				try {
					connection.setAutoCommit(true);
				} catch (SQLException ex) {
					System.err.println("Error resetting auto-commit: " + ex.getMessage());
				}
			}
		} catch (SQLException e) {
			System.err.println("Error adding student with subjects: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public int getLastGrNumber() {
		int lastGr = 0;
		String sql = "SELECT MAX(gr_number) FROM students";
		try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				lastGr = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lastGr;
	}

	public boolean assignCourseAndSubjectsToStudent(int studentId, int courseId, List<Integer> subjectIds) {
		String courseSql = "INSERT INTO student_courses (student_id, course_id) VALUES (?, ?)";
		String subjectSql = "INSERT INTO student_subjects (student_course_id, subject_course_id) VALUES (?, ?)";
		String getSubjectCourseId = "SELECT id FROM subject_course WHERE subject_id = ? AND course_id = ?";
		String checkCourseSql = "SELECT total_fee FROM courses WHERE course_id = ?";
		String insertFee = "INSERT INTO fees (student_course_id, paid_amount, pending_amount, total_fee) VALUES (?, 0.0, ?, ?)";

		Connection conn = null;
		try {
			conn = connection != null ? connection : DBConnection.connect();
			conn.setAutoCommit(false);

			BigDecimal courseFee = null;
			try (PreparedStatement pstmt = conn.prepareStatement(checkCourseSql)) {
				pstmt.setInt(1, courseId);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					courseFee = rs.getBigDecimal("total_fee");
					if (courseFee == null) {
						System.out.println("Error: Course ID " + courseId + " has no total fee set.");
						conn.rollback();
						return false;
					}
				} else {
					System.out.println("Error: Invalid course ID " + courseId);
					conn.rollback();
					return false;
				}
			}

			int studentCourseId;
			try (PreparedStatement courseStmt = conn.prepareStatement(courseSql, Statement.RETURN_GENERATED_KEYS)) {
				courseStmt.setInt(1, studentId);
				courseStmt.setInt(2, courseId);
				int rowsAffected = courseStmt.executeUpdate();
				if (rowsAffected == 0) {
					conn.rollback();
					return false;
				}
				try (ResultSet generatedKeys = courseStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						studentCourseId = generatedKeys.getInt(1);
					} else {
						conn.rollback();
						return false;
					}
				}
			}

			try (PreparedStatement feeStmt = conn.prepareStatement(insertFee)) {
				feeStmt.setInt(1, studentCourseId);
				feeStmt.setBigDecimal(2, courseFee);
				feeStmt.setBigDecimal(3, courseFee);
				feeStmt.executeUpdate();
			}

			try (PreparedStatement subjectStmt = conn.prepareStatement(subjectSql)) {
				for (Integer subjectId : subjectIds) {
					int subjectCourseId = -1;
					try (PreparedStatement psGetSubjectCourse = conn.prepareStatement(getSubjectCourseId)) {
						psGetSubjectCourse.setInt(1, subjectId);
						psGetSubjectCourse.setInt(2, courseId);
						ResultSet rs = psGetSubjectCourse.executeQuery();
						if (rs.next()) {
							subjectCourseId = rs.getInt("id");
						} else {
							System.out.println(
									"Warning: Subject ID " + subjectId + " not found for course ID " + courseId);
							continue;
						}
					}
					subjectStmt.setInt(1, studentCourseId);
					subjectStmt.setInt(2, subjectCourseId);
					subjectStmt.addBatch();
				}
				subjectStmt.executeBatch();
			}

			conn.commit();
			return true;
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			System.err.println("Database error: " + e.getMessage());
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public boolean isNameAndMobileExists(String name, String mobile) {
		String sql = """
				    SELECT COUNT(*)
				    FROM students s
				    JOIN profiles p ON s.student_id = p.student_id
				    WHERE s.name = ? AND p.mobile_no = ?
				""";

		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, mobile);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}