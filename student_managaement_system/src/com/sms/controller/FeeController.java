package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.service.FeeService;

public class FeeController {

    private FeeService feeService;
    private Scanner scanner = new Scanner(System.in);

    public FeeController() throws SQLException {
        this.feeService = new FeeService();
    }

    // View Total Paid Fees
    public void viewTotalPaidFees() {
        BigDecimal totalPaid = feeService.getTotalPaidFees();
        System.out.println("\n💰 Total Paid Fees: ₹" + totalPaid);
        System.out.println("=====================================");
    }

    // View Total Pending Fees
    public void viewTotalPendingFees() {
        BigDecimal totalPending = feeService.getTotalPendingFees();
        System.out.println("\n⏳ Total Pending Fees: ₹" + totalPending);
        System.out.println("=====================================");
    }

    // View Fees By Student
    public void viewFeesByStudent() {
        System.out.println("\n📚 Available Students:");
        List<Student> students = feeService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        printStudents(students);

        System.out.print("\nEnter Student ID to view fees: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❗ Please enter a valid Student ID.");
            scanner.next(); // clear invalid input
            return;
        }

        int studentId = scanner.nextInt();
        List<Fee> fees = feeService.getFeesByStudent(studentId);

        if (fees.isEmpty()) {
            System.out.println("No fees found for Student ID: " + studentId);
        } else {
            System.out.println("\nFees for Student ID " + studentId + ":");
            Fee.printHeader();
            for (Fee fee : fees) {
                System.out.println(fee);
            }
        }
    }

    // View Fees By Course
    public void viewFeesByCourse() {
        System.out.println("\nAvailable Courses:");
        List<Course> courses = feeService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        printCourses(courses);

        System.out.print("\nEnter Course ID to view fees: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❗ Please enter a valid Course ID.");
            scanner.next(); // clear invalid input
            return;
        }

        int courseId = scanner.nextInt();
        List<Fee> fees = feeService.getFeesByCourse(courseId);

        if (fees.isEmpty()) {
            System.out.println("No fees found for Course ID: " + courseId);
        } else {
            System.out.println("\nFees for Course ID " + courseId + ":");
            Fee.printHeader();
            for (Fee fee : fees) {
                System.out.println(fee);
            }
        }
    }

    // Update Fees Of A Course
    public void updateFeesOfCourse() {
        System.out.println("\nAvailable Courses:");
        List<Course> courses = feeService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        printCourses(courses);

        System.out.print("\nEnter Course ID to update fees: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❗ Please enter a valid Course ID.");
            scanner.next(); // clear invalid input
            return;
        }

        int courseId = scanner.nextInt();

        System.out.print("Enter new total fee amount: ₹");
        if (!scanner.hasNextBigDecimal()) {
            System.out.println("Please enter a valid fee amount.");
            scanner.next(); // clear invalid input
            return;
        }

        BigDecimal newTotalFee = scanner.nextBigDecimal();

        if (newTotalFee.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Fee amount cannot be negative.");
            return;
        }

        boolean success = feeService.updateCourseFees(courseId, newTotalFee);
        if (success) {
            System.out.println("Course fees updated successfully to ₹" + newTotalFee);
        } else {
            System.out.println("Failed to update course fees. Please check the Course ID.");
        }
    }

    // View Total Earning
    public void viewTotalEarning() {
        BigDecimal totalEarning = feeService.getTotalEarning();
        System.out.println("\nTotal Earning: ₹" + totalEarning);
        System.out.println("=====================================");
    }

    // Helper: Print students in tabular format
    private void printStudents(List<Student> students) {
        System.out.printf("\n%-10s %-20s %-25s %-10s\n", "Student ID", "Name", "Email", "GR Number");
        System.out.println("-------------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-10d %-20s %-25s %-10d\n", 
                s.getStudent_id(), s.getName(), s.getEmail(), s.getGr_number());
        }
    }

    // Helper: Print courses in tabular format
    private void printCourses(List<Course> courses) {
        System.out.printf("\n%-10s %-25s %-20s %-15s\n", "Course ID", "Course Name", "No. of Semesters", "Total Fee");
        System.out.println("-------------------------------------------------------------");
        for (Course c : courses) {
            String totalFee = (c.getTotal_fee() != null) ? "₹" + c.getTotal_fee() : "N/A";
            System.out.printf("%-10d %-25s %-20d %-15s\n", 
                c.getCourse_id(), c.getCourse_name(), c.getNo_of_semester(), totalFee);
        }
    }
} 