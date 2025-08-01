package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Subject;
import com.sms.service.SubjectService;

public class SubjectController {
    private final SubjectService subjectService;
    private final Scanner scanner = new Scanner(System.in);

    public SubjectController() throws SQLException {
        this.subjectService = new SubjectService();
    }

    public void addSubject() {
        System.out.print("Enter subject name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❗ Subject name cannot be empty.");
            return;
        }

        String type = getSubjectTypeFromUser();
        if (type == null) {
            System.out.println("❗ Invalid choice. Subject not added.");
            return;
        }

        int id = subjectService.addSubject(name, type);
        if (id != -1) {
            System.out.println("✅ Subject added with ID: " + id);
        } else {
            System.out.println("❗ Failed to add subject.");
        }
    }

    public void viewSubjects() {
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            if (subjects.isEmpty()) {
                System.out.println("⚠️ No subjects found.");
                return;
            }

            System.out.println("\n╔═════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                             SUBJECT LIST                                    ║");
            System.out.println("╚═════════════════════════════════════════════════════════════════════════════╝");

            String format = "| %-6s | %-6s | %-35s | %-20s |\n";
            System.out.println("+--------+--------+-------------------------------------+----------------------+");
            System.out.printf("| %-6s | %-6s | %-35s | %-20s |\n", "SrNo", "ID", "Subject Name", "Type");
            System.out.println("+--------+--------+-------------------------------------+----------------------+");


            int srNo = 1;
            for (Subject s : subjects) {
                System.out.printf(format, srNo++, s.getSubject_id(), s.getSubject_name(), s.getSubject_type().toLowerCase());
            }

            System.out.println("+--------+---------+-------------------------------------+---------------------+");
            System.out.println("✅ Subjects listed successfully!");

        } catch (SQLException e) {
            System.out.println("❗ Error retrieving subjects: " + e.getMessage());
        }
    }



    public void updateSubject() {
        System.out.print("Enter subject ID to update: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❗ Invalid subject ID.");
            return;
        }

        if (!subjectService.subjectExists(id)) {
            System.out.println("❗ Subject not found.");
            return;
        }

        System.out.print("Enter new subject name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("❗ Subject name cannot be empty.");
            return;
        }

        String type = getSubjectTypeFromUser();
        if (type == null) {
            System.out.println("❗ Invalid choice. Update cancelled.");
            return;
        }

        if (subjectService.updateSubject(id, name, type)) {
            System.out.println("✅ Subject updated.");
        } else {
            System.out.println("❗ Failed to update subject.");
        }
    }

//    public void deleteSubject() {
//        System.out.print("Enter subject ID to delete: ");
//        int id = null;
//        try {
//            id = Integer.parseInt(scanner.nextLine());
//        } catch (NumberFormatException e) {
//            System.out.println("❗ Invalid subject ID.");
//            return;
//        }
//
////        if (subjectService.deleteSubject(id)) {
////            System.out.println("✅ Subject deleted (soft).");
////        } else {
////            System.out.println("❗ Failed to delete subject.");
////        }
//    }

    // Reusable menu-driven method to get subject type
    private String getSubjectTypeFromUser() {
        while (true) {
        	System.out.println("\n╔═══════════════════════════════╗");
            System.out.println("║      SELECT SUBJECT TYPE      ║");
            System.out.println("╠═══════════════════════════════╣");
            System.out.println("║ 1. Mandatory                  ║");
            System.out.println("║ 2. Elective                   ║");
            System.out.println("╚═══════════════════════════════╝");
            System.out.print("👉 Enter your choice (1-2): ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return "Mandatory";
                case "2":
                    return "Elective";
                default:
                    System.out.println("❗ Invalid choice. Please select 1 or 2.");
            }
        }
    }

}
