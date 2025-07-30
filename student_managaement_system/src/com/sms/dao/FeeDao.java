package com.sms.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
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
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
        String sql = "SELECT f.fee_id, f.student_course_id, f.paid_amount, f.pending_amount, " +
                    "f.total_fee, f.last_payment_date, s.name as student_name, c.course_name, " +
                    "s.student_id, c.course_id " +
                    "FROM fees f " +
                    "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
                    "JOIN students s ON sc.student_id = s.student_id " +
                    "JOIN courses c ON sc.course_id = c.course_id " +
                    "WHERE s.student_id = ?";

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
                fee.setLastPaymentDate(rs.getDate("last_payment_date") != null ? 
                    rs.getDate("last_payment_date").toLocalDate() : null);
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
        String sql = "SELECT f.fee_id, f.student_course_id, f.paid_amount, f.pending_amount, " +
                    "f.total_fee, f.last_payment_date, s.name as student_name, c.course_name, " +
                    "s.student_id, c.course_id " +
                    "FROM fees f " +
                    "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
                    "JOIN students s ON sc.student_id = s.student_id " +
                    "JOIN courses c ON sc.course_id = c.course_id " +
                    "WHERE c.course_id = ?";

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
                fee.setLastPaymentDate(rs.getDate("last_payment_date") != null ? 
                    rs.getDate("last_payment_date").toLocalDate() : null);
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
            
            if (affectedRows > 0) {
                // Also update the fees table for all students enrolled in this course
                updateFeesForCourse(courseId, newTotalFee);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to update fees for all students in a course
    private void updateFeesForCourse(int courseId, BigDecimal newTotalFee) {
        String sql = "UPDATE fees f " +
                    "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
                    "SET f.total_fee = ?, f.pending_amount = ? - f.paid_amount " +
                    "WHERE sc.course_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newTotalFee);
            pstmt.setBigDecimal(2, newTotalFee);
            pstmt.setInt(3, courseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get Total Earning (Total of all fees)
    public BigDecimal getTotalEarning() {
        BigDecimal totalEarning = BigDecimal.ZERO;
        String sql = "SELECT SUM(total_fee) as total_earning FROM fees";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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

    // Get all fees with student and course details
    public List<Fee> getAllFees() {
        List<Fee> fees = new ArrayList<>();
        String sql = "SELECT f.fee_id, f.student_course_id, f.paid_amount, f.pending_amount, " +
                    "f.total_fee, f.last_payment_date, s.name as student_name, c.course_name, " +
                    "s.student_id, c.course_id " +
                    "FROM fees f " +
                    "JOIN student_courses sc ON f.student_course_id = sc.student_course_id " +
                    "JOIN students s ON sc.student_id = s.student_id " +
                    "JOIN courses c ON sc.course_id = c.course_id " +
                    "ORDER BY f.fee_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Fee fee = new Fee();
                fee.setFeeId(rs.getInt("fee_id"));
                fee.setStudentCourseId(rs.getInt("student_course_id"));
                fee.setPaidAmount(rs.getBigDecimal("paid_amount"));
                fee.setPendingAmount(rs.getBigDecimal("pending_amount"));
                fee.setTotalFee(rs.getBigDecimal("total_fee"));
                fee.setLastPaymentDate(rs.getDate("last_payment_date") != null ? 
                    rs.getDate("last_payment_date").toLocalDate() : null);
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
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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