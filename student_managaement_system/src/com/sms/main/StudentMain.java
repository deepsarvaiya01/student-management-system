package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.StudentController;
import com.sms.utils.InputValidator;

public class StudentMain {
    public void show() throws SQLException {
        StudentController controller = new StudentController();
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║          STUDENT MANAGEMENT MENU                         ║");
            System.out.println("╠══════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. View All Students                                     ║");
            System.out.println("║ 2. Add New Student                                       ║");
            System.out.println("║ 3. Assign a Course by Student ID                         ║");
            System.out.println("║ 4. View All Courses by Student ID                        ║");
            System.out.println("║ 5. Search a Student by Student ID                        ║");
            System.out.println("║ 6. Delete a Student by Student ID                        ║");
            System.out.println("║ 7. Manage Fee Notifier                                   ║");
            System.out.println("║ 8. Pay Fees                                              ║");
            System.out.println("║ 9. Restore a Deleted Student                             ║");
            System.out.println("║ 0. Back                                                  ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");
            
            choice = InputValidator.getValidMenuChoice(scanner, "Enter your choice (0-9): ", 9);
            
            switch (choice) {
                case 1 -> controller.viewAllStudents();
                case 2 -> controller.addNewStudent();
                case 3 -> controller.assignCourse();
                case 4 -> controller.viewAllCourses();
                case 5 -> controller.searchStudent();
                case 6 -> controller.deleteStudent();
                case 7 -> controller.manageFeeNotifierPreferences();
                case 8 -> controller.payFees();
                case 9 -> controller.restoreStudent();
                case 0 -> System.out.println("Going back to Student Main Menu...");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 0);
    }
}