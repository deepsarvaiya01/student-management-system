package com.sms.main;

import java.util.Scanner;

import com.sms.controller.SubjectController;

public class SubjectMain {
    public void show() {
        Scanner scanner = new Scanner(System.in);
        SubjectController subjectController = new SubjectController();

        while (true) {
        	System.out.println("\n╔═══════════════════════════════════════════╗");
        	System.out.println("║            SUBJECT MANAGEMENT MENU        ║");
        	System.out.println("╠═══════════════════════════════════════════╣");
        	System.out.println("║ 1. Add Subject                            ║");
        	System.out.println("║ 2. View All Subjects                      ║");
        	System.out.println("║ 3. Update Subject                         ║");
        	System.out.println("║ 4. Delete Subject                         ║");
        	System.out.println("║ 0. Exit                                   ║");
        	System.out.println("╚═══════════════════════════════════════════╝");
        	System.out.print("👉 Enter your choice (0-4): ");


            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    subjectController.addSubject();;
                    break;
                case "2":
                    subjectController.viewSubjects();
                    break;
                case "3":
                    subjectController.updateSubject();
                    break;
                case "4":
                    subjectController.deleteSubject();
                    break;
                case "0":
                    System.out.println("Exiting Subject Management. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("❗ Invalid choice. Please try again.");
            }
        }
    }

}
