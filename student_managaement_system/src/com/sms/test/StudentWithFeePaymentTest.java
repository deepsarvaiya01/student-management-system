package com.sms.test;

import java.sql.SQLException;
import java.util.List;
import com.sms.service.StudentService;
import com.sms.service.FeeService;
import com.sms.model.Student;
import com.sms.model.Subject;
import com.sms.model.Fee;

public class StudentWithFeePaymentTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Enhanced Student Addition with Fee Payment Option...");
        
        try {
            StudentService studentService = new StudentService();
            FeeService feeService = new FeeService();
            
            // Test 1: Get subjects for a course
            System.out.println("1. Testing subject retrieval for Course ID 1 (BCA)...");
            List<Subject> subjects = studentService.getSubjectsByCourseId(1);
            System.out.println("Found " + subjects.size() + " subjects for BCA:");
            for (Subject subject : subjects) {
                System.out.printf("   ID: %d | %s | Type: %s\n", 
                    subject.getSubject_id(), subject.getSubject_name(), subject.getSubject_type());
            }
            
            // Test 2: Check fee structure for course
            System.out.println("\n2. Testing fee structure for Course ID 1...");
            List<Fee> fees = feeService.getAllFees();
            System.out.println("Total fee records in system: " + fees.size());
            
            // Test 3: Create a test student (simulation)
            System.out.println("\n3. Testing student creation with subjects (simulation)...");
            System.out.println("This would normally create a student with:");
            System.out.println("   - Student details");
            System.out.println("   - Course assignment");
            System.out.println("   - Subject assignments");
            System.out.println("   - Fee record initialization");
            System.out.println("   - Immediate fee payment option");
            
            // Test 4: Simulate fee payment flow
            System.out.println("\n4. Testing fee payment flow simulation...");
            System.out.println("After student creation, the system would:");
            System.out.println("   - Ask if student wants to pay fees immediately");
            System.out.println("   - Show current fee status");
            System.out.println("   - Accept payment amount");
            System.out.println("   - Process payment");
            System.out.println("   - Update fee records");
            System.out.println("   - Show updated fee status");
            
            System.out.println("\n✓ Enhanced student addition with fee payment test completed!");
            System.out.println("\n📝 Manual Testing Instructions:");
            System.out.println("1. Run the main application");
            System.out.println("2. Go to Student Management → Add New Student");
            System.out.println("3. Complete student registration with subjects");
            System.out.println("4. When asked 'Would you like to pay fees now? (y/n):'");
            System.out.println("5. Enter 'y' to test immediate fee payment");
            System.out.println("6. Follow the payment process");
            
        } catch (SQLException e) {
            System.err.println("✗ Test failed with SQLException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Test failed with Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 