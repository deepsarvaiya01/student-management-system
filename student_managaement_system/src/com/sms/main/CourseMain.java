package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.CourseController;

public class CourseMain {

    public void show() throws SQLException {
        CourseController controller = new CourseController();
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
        	System.out.println("\n╔═══════════════════════════════════════════╗");
        	System.out.println("║           COURSE MANAGEMENT MENU          ║");
        	System.out.println("╠═══════════════════════════════════════════╣");
        	System.out.println("║ 1. View All Courses                       ║");
        	System.out.println("║ 2. Add New Course                         ║");
        	System.out.println("║ 3. Add Subjects to a Course               ║");
        	System.out.println("║ 4. View Subjects of a Course              ║");
        	System.out.println("║ 5. Search a Course                        ║");
        	System.out.println("║ 6. Delete a Course                        ║");
        	System.out.println("║ 7. Manage Subjects                        ║");
        	System.out.println("║ 0. Exit                                   ║");
        	System.out.println("╚═══════════════════════════════════════════╝");
        	System.out.print("👉 Enter your choice (0-7): ");

            if (!scanner.hasNextInt()) {
                System.out.println("❗ Please enter a valid number.");
                scanner.next(); 
                continue;
            }

            choice = scanner.nextInt();

            switch (choice) {
                case 1 -> controller.viewAllCourses();
                case 2 -> controller.addNewCourse();
                case 3 -> controller.addSubjectsToCourse();
                case 4 -> controller.viewSubjectsOfCourse();
                case 5 -> controller.searchCourse();
                case 6 -> controller.deleteCourse();
                case 7 -> {SubjectMain subjectMain = new SubjectMain();
                		  subjectMain.show();}
                case 0 -> {
                    System.out.println("Exiting Course Management... Thank you!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("❗ Invalid choice! Please enter a number between 0 and 6.");
            }
        }
    }
}
