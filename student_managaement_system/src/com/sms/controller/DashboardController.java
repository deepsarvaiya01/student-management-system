package com.sms.controller;

import java.util.List;
import com.sms.model.DashboardModel;
import com.sms.service.DashboardService;

public class DashboardController {

    public void showDashboard() {
        DashboardService service = new DashboardService();
        List<DashboardModel> dashboard = service.getDashboardView();

        int subjectsWidth = 45;
        int teachersWidth = 45;

        String format = "| %-4s | %-4s | %-20s | %-10s | %-10s | %-10s | %-10s | %-" + subjectsWidth + "s | %-" + teachersWidth + "s |%n";

        String topLine = "╔" + "═".repeat(183) + "╗";
        String titleLine = "║" + centerText("STUDENT MANAGEMENT DASHBOARD", 183) + "║";
        String bottomLine = "╚" + "═".repeat(183) + "╝";

        String headerFooterLine = "+" +
                "------+------+----------------------+------------+------------+------------+------------+" +
                "-".repeat(subjectsWidth + 2) + "+" +
                "-".repeat(teachersWidth + 2) + "+";

        // Print Top Title Box
        System.out.println("\n" + topLine);
        System.out.println(titleLine);
        System.out.println(bottomLine);

        System.out.println("📌 Active Students Overview\n");

        if (dashboard.isEmpty()) {
            System.out.println("⚠ No data found. Please add student records first.");
            return;
        }

        // Table Header
        System.out.println(headerFooterLine);
        System.out.printf(format, "SrNo", "ID", "Name", "Course", "Paid", "Pending", "Total", "Subjects", "Teachers");
        System.out.println(headerFooterLine);

        for (DashboardModel d : dashboard) {
            String subjects = clean(d.getSubjects(), subjectsWidth);
            String teachers = clean(d.getTeachers(), teachersWidth);

            System.out.printf(format,
                    d.getSrNo(),
                    d.getStudentId(),
                    d.getName(),
                    d.getCourse(),
                    String.format("%.2f", d.getPaidFee()),
                    String.format("%.2f", d.getPendingFee()),
                    String.format("%.2f", d.getTotalFee()),
                    subjects,
                    teachers
            );
        }

        // Table Footer
        System.out.println(headerFooterLine);

        // Dashboard Footer
        System.out.println("\n✅ Dashboard displayed successfully!");
        System.out.println("📘 Thank you for using the Student Management System.");
        System.out.println("↩ Return to the main menu to explore more options.\n");
    }

    private String clean(String value, int maxLength) {
        if (value == null || value.equalsIgnoreCase("null") || value.trim().isEmpty()) {
            return "--";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(padding, 0)) + text + " ".repeat(Math.max(width - text.length() - padding, 0));
    }
}
