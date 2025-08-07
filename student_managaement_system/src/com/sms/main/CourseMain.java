package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.CourseController;
import com.sms.utils.InputValidator;

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
            System.out.println("║ 3. Assign Subjects to a Course            ║");
            System.out.println("║ 4. View Subjects of a Course              ║");
            System.out.println("║ 5. Search a Course With Full Details      ║");
            System.out.println("║ 6. Delete a Course                        ║");
            System.out.println("║ 7. Manage Subjects                        ║");
            System.out.println("║ 0. Back                                   ║");
            System.out.println("╚═══════════════════════════════════════════╝");
            
            choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-7): ", 7);

            switch (choice) {
                case 1 -> controller.viewAllCourses();
                case 2 -> controller.addNewCourse();
                case 3 -> controller.addSubjectsToCourse();
                case 4 -> controller.viewSubjectsOfCourse();
                case 5 -> controller.searchCourse();
                case 6 -> controller.deleteCourse();
                case 7 -> {
                    SubjectMain subjectMain = new SubjectMain();
                    subjectMain.show();
                }
                case 0 -> {
                    System.out.println("Going back to Student Main Menu...");
                    return;
                }
            }
        }
    }
}