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
import com.sms.model.Fee;

public class FeeDao {

	private Connection connection = null;

	public FeeDao() throws SQLException {
		this.connection = DBConnection.connect();
	}

	// View Total Paid Fees
	public BigDecimal getTotalPaidFees() {
		BigDecimal totalPaid = BigDecimal.ZERO;
		String sql = "SELECT SUM(paid_amount) as total_paid FROM fees";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				totalPaid = rs.getBigDecimal("total_paid");
				if (totalPaid == null) {
					totalPaid = BigDecimal.ZERO;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalPaid;
	}

	// View Total Pending Fees
	public BigDecimal getTotalPendingFees() {
		BigDecimal totalPending = BigDecimal.ZERO;
		String sql = "SELECT SUM(pending_amount) as total_pending FROM fees";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				totalPending = rs.getBigDecimal("total_pending");
				if (totalPending == null) {
					totalPending = BigDecimal.ZERO;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalPending;
	}

	// View Fees By Student
	public List<Fee> getFeesByStudent(int studentId) {
		List<Fee> fees = new ArrayList<>();
		String sql = "SELECT f.fee_id, f.student_course_id, f.paid_amount, f.pending_amount, "
				+ "f.total_fee, f.last_payment_date, s.name as student_name, c.course_name, "
				+ "s.student_id, c.course_id " + "FROM fees f "
				+ "JOIN student_courses sc ON f.student_course_id = sc.student_course_id "
				+ "JOIN students s ON sc.student_id = s.student_id " + "JOIN courses c ON sc.course_id = c.course_id "
				+ "WHERE s.student_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, studentId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Fee fee = new Fee();
				fee.setFeeId(rs.getInt("fee_id"));
				fee.setStudentCourseId(rs.getInt("student_course_id"));
				fee.setPaidAmount(rs.getBigDecimal("paid_amount"));
				fee.setPendingAmount(rs.getBigDecimal("pending_amount"));
				fee.setTotalFee(rs.getBigDecimal("total_fee"));
				fee.setLastPaymentDate(
						rs.getDate("last_payment_date") != null ? rs.getDate("last_payment_date").toLocalDate() : null);
				fee.setStudentName(rs.getString("student_name"));
				fee.setCourseName(rs.getString("course_name"));
				fee.setStudentId(rs.getInt("student_id"));
				fee.setCourseId(rs.getInt("course_id"));
				fees.add(fee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fees;
	}

	// View Fees By Course
	public List<Fee> getFeesByCourse(int courseId) {
		List<Fee> fees = new ArrayList<>();
		String sql = "SELECT f.fee_id, f.student_course_id, f.paid_amount, f.pending_amount, "
				+ "f.total_fee, f.last_payment_date, s.name as student_name, c.course_name, "
				+ "s.student_id, c.course_id " + "FROM fees f "
				+ "JOIN student_courses sc ON f.student_course_id = sc.student_course_id "
				+ "JOIN students s ON sc.student_id = s.student_id " + "JOIN courses c ON sc.course_id = c.course_id "
				+ "WHERE c.course_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, courseId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Fee fee = new Fee();
				fee.setFeeId(rs.getInt("fee_id"));
				fee.setStudentCourseId(rs.getInt("student_course_id"));
				fee.setPaidAmount(rs.getBigDecimal("paid_amount"));
				fee.setPendingAmount(rs.getBigDecimal("pending_amount"));
				fee.setTotalFee(rs.getBigDecimal("total_fee"));
				fee.setLastPaymentDate(
						rs.getDate("last_payment_date") != null ? rs.getDate("last_payment_date").toLocalDate() : null);
				fee.setStudentName(rs.getString("student_name"));
				fee.setCourseName(rs.getString("course_name"));
				fee.setStudentId(rs.getInt("student_id"));
				fee.setCourseId(rs.getInt("course_id"));
				fees.add(fee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fees;
	}

	// Update Fees Of A Course
	public boolean updateCourseFees(int courseId, BigDecimal newTotalFee) {
		String sql = "UPDATE courses SET total_fee = ? WHERE course_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setBigDecimal(1, newTotalFee);
			pstmt.setInt(2, courseId);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<Fee> getPaidFeesByStudents() {
		List<Fee> paidFeesList = new ArrayList<>();
		String sql = "SELECT s.name AS student_name, SUM(f.paid_amount) AS total_paid " +
		             "FROM fees f " +
		             "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
		             "JOIN students s ON sc.student_id = s.student_id " +
		             "GROUP BY s.student_id, s.name " +
		             "ORDER BY s.name";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Fee fee = new Fee();
				fee.setStudentName(rs.getString("student_name"));
				fee.setPaidAmount(rs.getBigDecimal("total_paid"));
				paidFeesList.add(fee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return paidFeesList;
	}

	public List<Fee> getPendingFeesByStudents() {
		List<Fee> pendingFeesList = new ArrayList<>();
		String sql = "SELECT s.name AS student_name, SUM(f.pending_amount) AS total_pending " +
		             "FROM fees f " +
		             "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
		             "JOIN students s ON sc.student_id = s.student_id " +
		             "GROUP BY s.student_id, s.name " +
		             "ORDER BY s.name";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Fee fee = new Fee();
				fee.setStudentName(rs.getString("student_name"));
				fee.setPendingAmount(rs.getBigDecimal("total_pending"));
				pendingFeesList.add(fee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pendingFeesList;
	}


	// Get Total Earning
	public BigDecimal getTotalEarning() {
		BigDecimal totalEarning = BigDecimal.ZERO;
		String sql = "SELECT SUM(total_fee) as total_earning FROM fees";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				totalEarning = rs.getBigDecimal("total_earning");
				if (totalEarning == null) {
					totalEarning = BigDecimal.ZERO;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalEarning;
	}

	// Get all fees
	public List<Fee> getAllFees() {
		List<Fee> fees = new ArrayList<>();
		String sql = "SELECT f.fee_id, f.student_course_id, f.paid_amount, f.pending_amount, "
				+ "f.total_fee, f.last_payment_date, s.name as student_name, c.course_name, "
				+ "s.student_id, c.course_id " + "FROM fees f "
				+ "JOIN student_courses sc ON f.student_course_id = sc.student_course_id "
				+ "JOIN students s ON sc.student_id = s.student_id " + "JOIN courses c ON sc.course_id = c.course_id "
				+ "ORDER BY f.fee_id";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				Fee fee = new Fee();
				fee.setFeeId(rs.getInt("fee_id"));
				fee.setStudentCourseId(rs.getInt("student_course_id"));
				fee.setPaidAmount(rs.getBigDecimal("paid_amount"));
				fee.setPendingAmount(rs.getBigDecimal("pending_amount"));
				fee.setTotalFee(rs.getBigDecimal("total_fee"));
				fee.setLastPaymentDate(
						rs.getDate("last_payment_date") != null ? rs.getDate("last_payment_date").toLocalDate() : null);
				fee.setStudentName(rs.getString("student_name"));
				fee.setCourseName(rs.getString("course_name"));
				fee.setStudentId(rs.getInt("student_id"));
				fee.setCourseId(rs.getInt("course_id"));
				fees.add(fee);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fees;
	}

	// Get all courses for selection
	public List<com.sms.model.Course> getAllCourses() {
		List<com.sms.model.Course> courses = new ArrayList<>();
		String sql = "SELECT course_id, course_name, no_of_semester, total_fee FROM courses WHERE is_active = TRUE";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				com.sms.model.Course course = new com.sms.model.Course();
				course.setCourse_id(rs.getInt("course_id"));
				course.setCourse_name(rs.getString("course_name"));
				course.setNo_of_semester(rs.getInt("no_of_semester"));
				course.setTotal_fee(rs.getBigDecimal("total_fee"));
				courses.add(course);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return courses;
	}

	// Get all students for selection
	public List<com.sms.model.Student> getAllStudents() {
		List<com.sms.model.Student> students = new ArrayList<>();
		String sql = "SELECT student_id, name, email, gr_number FROM students WHERE is_active = TRUE";

		try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
	
	public int getStudentCourseId(int studentId, int courseId) throws SQLException {
        String query = "SELECT student_course_id FROM student_courses WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_course_id");
            }
            return -1; // No record found
        }
    }

    public void createFeeRecord(int studentCourseId, BigDecimal totalFee) throws SQLException {
        String query = "INSERT INTO fees (student_course_id, paid_amount, pending_amount, total_fee) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, studentCourseId);
            pstmt.setBigDecimal(2, BigDecimal.ZERO);
            pstmt.setBigDecimal(3, totalFee);
            pstmt.setBigDecimal(4, totalFee);
            pstmt.executeUpdate();
        }
    }

	// Update fee payment
	public boolean updateFeePayment(int studentId, BigDecimal paymentAmount, int courseId) {
		String selectSql = "SELECT f.fee_id, f.paid_amount, f.pending_amount " + "FROM fees f "
				+ "JOIN student_courses sc ON f.student_course_id = sc.student_course_id "
				+ "WHERE sc.student_id = ? AND sc.course_id = ?";
		String updateSql = "UPDATE fees SET paid_amount = ?, pending_amount = ?, last_payment_date = CURRENT_DATE WHERE fee_id = ?";

		try (PreparedStatement selectStmt = connection.prepareStatement(selectSql);
				PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
			// Step 1: Get fee details for the specific course
			selectStmt.setInt(1, studentId);
			selectStmt.setInt(2, courseId);
			ResultSet rs = selectStmt.executeQuery();

			if (rs.next()) {
				int feeId = rs.getInt("fee_id");
				BigDecimal currentPaid = rs.getBigDecimal("paid_amount");
				BigDecimal currentPending = rs.getBigDecimal("pending_amount");

				// Step 2: Calculate new values
				BigDecimal newPaid = currentPaid.add(paymentAmount);
				BigDecimal newPending = currentPending.subtract(paymentAmount);
				if (newPending.compareTo(BigDecimal.ZERO) < 0) {
					return false;
				}

				// Step 3: Update fees table
				updateStmt.setBigDecimal(1, newPaid);
				updateStmt.setBigDecimal(2, newPending);
				updateStmt.setInt(3, feeId);

				int rowsUpdated = updateStmt.executeUpdate();
				return rowsUpdated > 0;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}