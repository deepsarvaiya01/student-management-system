package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Subject;
import com.sms.service.SubjectService;

public class SubjectController {
    private final SubjectService subjectService = new SubjectService();
    private final Scanner scanner = new Scanner(System.in);


    public void addSubject() {
        System.out.print("Enter subject name: ");
        String name = scanner.nextLine();
        System.out.print("Enter subject type (Mandatory/Elective): ");
        String type = scanner.nextLine();

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
                System.out.println("No subjects found.");
                return;
            }

            System.out.printf("\n%-10s %-25s %-15s\n", "ID", "Subject Name", "Type");
            System.out.println("----------------------------------------------");
            for (Subject s : subjects) {
                System.out.printf("%-10d %-25s %-15s\n", s.getSubject_id(), s.getSubject_name(), s.getSubject_type());
            }
        } catch (SQLException e) {
            System.out.println("❗ Error retrieving subjects: " + e.getMessage());
        }
    }

    public void updateSubject() {
        System.out.print("Enter subject ID to update: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (!subjectService.subjectExists(id)) {
            System.out.println("❗ Subject not found.");
            return;
        }

        System.out.print("Enter new subject name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new subject type (Mandatory/Elective): ");
        String type = scanner.nextLine();

        if (subjectService.updateSubject(id, name, type)) {
            System.out.println("✅ Subject updated.");
        } else {
            System.out.println("❗ Failed to update subject.");
        }
    }

    public void deleteSubject() {
        System.out.print("Enter subject ID to delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (subjectService.deleteSubject(id)) {
            System.out.println("✅ Subject deleted (soft).");
        } else {
            System.out.println("❗ Failed to delete subject.");
        }
    }
}