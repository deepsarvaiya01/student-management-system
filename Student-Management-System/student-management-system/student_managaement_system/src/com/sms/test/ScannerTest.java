package com.sms.test;

import java.util.Scanner;
import com.sms.utils.InputValidator;

public class ScannerTest {
    
    public static void main(String[] args) {
        System.out.println("Testing Scanner Input Handling...");
        
        Scanner scanner = new Scanner(System.in);
        
        try {
            // Test 1: Integer input followed by string input
            System.out.println("1. Testing integer input followed by string input...");
            int number = InputValidator.getValidIntegerWithNewline(scanner, "Enter a number: ", "Number");
            System.out.println("You entered: " + number);
            
            System.out.print("Enter a string: ");
            String text = scanner.nextLine().trim();
            System.out.println("You entered: " + text);
            
            // Test 2: Integer range input followed by string input
            System.out.println("\n2. Testing integer range input followed by string input...");
            int choice = InputValidator.getValidIntegerInRangeWithNewline(scanner, "Enter choice (1-5): ", "Choice", 1, 5);
            System.out.println("You entered: " + choice);
            
            System.out.print("Enter another string: ");
            String text2 = scanner.nextLine().trim();
            System.out.println("You entered: " + text2);
            
            // Test 3: Simulate elective subject selection
            System.out.println("\n3. Testing elective subject selection simulation...");
            int courseId = InputValidator.getValidIntegerWithNewline(scanner, "Enter Course ID: ", "Course ID");
            System.out.println("Course ID: " + courseId);
            
            System.out.print("Enter elective subject IDs (comma-separated, or press Enter to skip): ");
            String electiveInput = scanner.nextLine().trim();
            if (electiveInput.isEmpty()) {
                System.out.println("No elective subjects selected.");
            } else {
                System.out.println("Elective subjects: " + electiveInput);
            }
            
            System.out.println("\n✓ Scanner input handling test completed successfully!");
            
        } catch (Exception e) {
            System.err.println("✗ Test failed with Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
} 