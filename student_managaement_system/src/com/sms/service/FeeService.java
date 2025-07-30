package com.sms.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.sms.dao.FeeDao;
import com.sms.model.Fee;

public class FeeService {
    private FeeDao feeDao;

    public FeeService() throws SQLException {
        this.feeDao = new FeeDao();
    }

    // View Total Paid Fees
    public BigDecimal getTotalPaidFees() {
        return feeDao.getTotalPaidFees();
    }

    // View Total Pending Fees
    public BigDecimal getTotalPendingFees() {
        return feeDao.getTotalPendingFees();
    }

    // View Fees By Student
    public List<Fee> getFeesByStudent(int studentId) {
        return feeDao.getFeesByStudent(studentId);
    }

    // View Fees By Course
    public List<Fee> getFeesByCourse(int courseId) {
        return feeDao.getFeesByCourse(courseId);
    }

    // Update Fees Of A Course
    public boolean updateCourseFees(int courseId, BigDecimal newTotalFee) {
        if (newTotalFee.compareTo(BigDecimal.ZERO) < 0) {
            return false; // Cannot set negative fees
        }
        return feeDao.updateCourseFees(courseId, newTotalFee);
    }

    // Get Total Earning
    public BigDecimal getTotalEarning() {
        return feeDao.getTotalEarning();
    }

    // Get all fees
    public List<Fee> getAllFees() {
        return feeDao.getAllFees();
    }

    // Get all courses for selection
    public List<com.sms.model.Course> getAllCourses() {
        return feeDao.getAllCourses();
    }

    // Get all students for selection
    public List<com.sms.model.Student> getAllStudents() {
        return feeDao.getAllStudents();
    }
} 