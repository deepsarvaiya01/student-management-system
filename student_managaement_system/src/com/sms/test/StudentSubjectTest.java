package com.sms.test;

import java.sql.SQLException;
import java.util.List;
import com.sms.service.StudentService;
import com.sms.model.Student;
import com.sms.model.Subject;

public class StudentSubjectTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Enhanced Student Addition with Subject Selection...");
        
        try {
            StudentService studentService = new StudentService();
            
            // Test 1: Get subjects for a course
            System.out.println("1. Testing subject retrieval for Course ID 1 (BCA)...");
            List<Subject> subjects = studentService.getSubjectsByCourseId(1);
            System.out.println("Found " + subjects.size() + " subjects for BCA:");
            for (Subject subject : subjects) {
                System.out.printf("   ID: %d | %s | Type: %s\n", 
                    subject.getSubject_id(), subject.getSubject_name(), subject.getSubject_type());
            }
            
            // Test 2: Create a test student
            System.out.println("\n2. Testing student creation with subjects...");
            Student testStudent = new Student();
            testStudent.setName("Test Student");
            testStudent.setGr_number(9999);
            testStudent.setEmail("test.student@test.com");
            testStudent.setCity("Test City");
            testStudent.setMobile_no("1234567890");
            testStudent.setAge(20);
            
            // Select some subjects (assuming they exist in the database)
            List<Integer> selectedSubjectIds = List.of(1, 2, 3); // Programming, Business Comm, Data Structures
            
            String result = studentService.addStudentWithProfileAndCourseAndSubjects(testStudent, 1, selectedSubjectIds);
            System.out.println("Result: " + result);
            
            System.out.println("\n✓ Enhanced student addition test completed!");
            
        } catch (SQLException e) {
            System.err.println("✗ Test failed with SQLException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Test failed with Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 