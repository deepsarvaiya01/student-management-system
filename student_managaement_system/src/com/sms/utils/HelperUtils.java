package com.sms.utils;

import java.util.List;

import com.sms.model.Course;
import com.sms.model.Student;

public class HelperUtils {

    public static void printStudents(List<Student> students) {
        System.out.printf("\n%-10s %-20s %-25s %-10s\n", "Student ID", "Name", "Email", "GR Number");
        System.out.println("-------------------------------------------------------------");
        for (Student s : students) {
            System.out.printf("%-10d %-20s %-25s %-10d\n", s.getStudent_id(), s.getName(), s.getEmail(), s.getGr_number());
        }
    }

    public static void printCourses(List<Course> courses) {
        System.out.printf("\n%-10s %-25s %-20s %-15s\n", "Course ID", "Course Name", "No. of Semesters", "Total Fee");
        System.out.println("-------------------------------------------------------------");
        for (Course c : courses) {
            String totalFee = (c.getTotal_fee() != null) ? "₹" + c.getTotal_fee() : "N/A";
            System.out.printf("%-10d %-25s %-20d %-15s\n", c.getCourse_id(), c.getCourse_name(), c.getNo_of_semester(), totalFee);
        }
    }
    
    
    public static String validateStudentData(Student student, int courseId) {
        if (student == null || student.getName() == null || !student.getName().matches("[a-zA-Z ]{1,50}")) {
            return "Invalid name (letters/spaces, max 50 chars).";
        }
        if (student.getGr_number() <= 0 || String.valueOf(student.getGr_number()).length() < 4
                || String.valueOf(student.getGr_number()).length() > 10) {
            return "Invalid GR number (4-10 digits).";
        }
        if (student.getEmail() == null || !student.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
                || student.getEmail().length() > 100) {
            return "Invalid email format or too long.";
        }
        if (student.getCity() == null || !student.getCity().matches("[a-zA-Z ]{1,50}")) {
            return "Invalid city (letters/spaces, max 50 chars).";
        }
        if (student.getMobile_no() == null || !student.getMobile_no().matches("\\d{10}")
                || student.getMobile_no().length() > 15) {
            return "Invalid mobile number (10 digits).";
        }
        if (student.getAge() < 15 || student.getAge() > 100) {
            return "Invalid age (15-100).";
        }
        return "VALID";
    }
}
