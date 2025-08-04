// 📁 DashboardService.java
package com.sms.service;

import java.util.List;
import com.sms.dao.DashboardDao;
import com.sms.model.DashboardModel;

public class DashboardService {
    private DashboardDao dao = new DashboardDao();

    public void displayGeneralDashboard() {
        List<DashboardModel> list = dao.getGeneralDashboardData();
        printTable(list, "GENERAL STUDENT DASHBOARD");
    }

    public void displayCourseWiseDashboard() {
        List<DashboardModel> list = dao.getCourseWiseDashboardData();
        printTable(list, "COURSE-WISE STUDENT DASHBOARD");
    }

    public void displaySubjectDashboard() {
        List<DashboardModel> list = dao.getSubjectDashboardData();
        printTable(list, "SUBJECT MANAGEMENT DASHBOARD");
    }

    public void displayTeacherLoadDashboard() {
        List<DashboardModel> list = dao.getTeacherLoadDashboardData();
        printTable(list, "TEACHER LOAD DASHBOARD");
    }

    private void printTable(List<DashboardModel> dashboard, String title) {
        int subjectsWidth = 45;
        int teachersWidth = 45;

        String format = "| %-4s | %-4s | %-20s | %-10s | %-10s | %-10s | %-10s | %-" + subjectsWidth + "s | %-" + teachersWidth + "s |%n";
        String topLine = "\n╔" + "═".repeat(183) + "╗";
        String titleLine = "║" + centerText(title, 183) + "║";
        String bottomLine = "╚" + "═".repeat(183) + "╝";

        String headerFooterLine = "+" +
                "------+------+----------------------+------------+------------+------------+------------+" +
                "-".repeat(subjectsWidth + 2) + "+" +
                "-".repeat(teachersWidth + 2) + "+";

        System.out.println(topLine);
        System.out.println(titleLine);
        System.out.println(bottomLine);

        if (dashboard.isEmpty()) {
            System.out.println("⚠ No data found for: " + title);
            return;
        }

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

        System.out.println(headerFooterLine);
        System.out.println("\n✅ " + title + " displayed successfully!");
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
