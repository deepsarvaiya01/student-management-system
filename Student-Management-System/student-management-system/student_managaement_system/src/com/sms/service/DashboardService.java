package com.sms.service;

import java.util.List;
import java.util.Scanner;

import com.sms.dao.DashboardDao;
import com.sms.model.DashboardModel;
import com.sms.utils.InputValidator;

public class DashboardService {
	private DashboardDao dashboardDao = new DashboardDao();
	private Scanner scanner = new Scanner(System.in);

	public void displayGeneralDashboard() {
		List<DashboardModel> list = dashboardDao.getGeneralDashboardData();
		printTable(list, "GENERAL STUDENT DASHBOARD");
	}

	public void displayCourseWiseDashboard() {
		List<DashboardModel> list = dashboardDao.getCourseWiseDashboardData();
		printTable(list, "COURSE - WISE DASHBOARD");
	}

	public void displaySubjectDashboard() {
		boolean showAll = InputValidator.getValidConfirmation(scanner, "Show subjects with no students? (y/n): ");
		List<DashboardModel> list = dashboardDao.getSubjectDashboardData(showAll);
		printTable(list, "SUBJECT - WISE DASHBOARD");
	}

	public void displayTeacherLoadDashboard() {
		List<DashboardModel> list = dashboardDao.getTeacherLoadDashboardData();
		printTable(list, "TEACHER - WISE DASHBOARD");
	}

	private void printTable(List<DashboardModel> dashboard, String title) {
		int maxWidth = 120;
		String headerFooterLine = "";
		String format = "";

		String topLine = "\n╔" + "═".repeat(maxWidth - 2) + "╗";
		String titleLine = "║" + centerText(title, maxWidth - 2) + "║";
		String bottomLine = "╚" + "═".repeat(maxWidth - 2) + "╝";

		System.out.println(topLine);
		System.out.println(titleLine);
		System.out.println(bottomLine);

		if (dashboard.isEmpty()) {
			System.out.println("⚠ No data found for: " + title);
			return;
		}

		boolean isGeneral = title.contains("GENERAL");
		boolean isCourse = title.contains("COURSE - WISE");
		boolean isSubject = title.contains("SUBJECT - WISE");
		boolean isTeacher = title.contains("TEACHER - WISE");

		if (isGeneral) {
			headerFooterLine = "+------+------+-----------------+-------------+-------------+---------------+--------------------------+---------------------------+";
			format = "| %-4s | %-4s | %-15s | %-11s | %-11s | %-13s | %-24s | %-24s |%n";
			System.out.println(headerFooterLine);
			System.out.printf(format, "SrNo", "ID", "Name", "Courses", "Paid", "Pending", "Subjects", "Teachers");
			System.out.println(headerFooterLine);

			for (DashboardModel d : dashboard) {
				String status = d.getPendingFee() == 0 ? "✅" : "⚠";
				System.out.printf(format, d.getSrNo(), d.getStudentId(), clean(d.getName(), 15),
						clean(d.getCourse(), 11), String.format("%.2f", d.getPaidFee()),
						String.format("%.2f %s", d.getPendingFee(), status), clean(d.getSubjects(), 24),
						clean(d.getTeachers(), 24));
			}
		} else if (isCourse) {
			headerFooterLine = "+------+---------------+----------+--------------------------+----------------+----------------+-----------------+--------------------------+--------------------------+";
			format = "| %-4s | %-13s | %-8s | %-24s | %-14s | %-14s | %-14s | %-24s | %-24s |%n";
			System.out.println(headerFooterLine);
			System.out.printf(format, "SrNo", "Course", "Students", "Student Names", "Total Fee", "Paid", "Pending",
					"Subjects", "Teachers");
			System.out.println(headerFooterLine);

			for (DashboardModel d : dashboard) {
				String status = d.getPendingFee() == 0 ? "✅" : "⚠";
				System.out.printf(format, d.getSrNo(), clean(d.getCourse(), 13), d.getTotalStudents(),
						clean(d.getStudentList(), 24), String.format("%.2f", d.getTotalFee()),
						String.format("%.2f", d.getPaidFee()), String.format("%.2f %s", d.getPendingFee(), status),
						clean(d.getSubjects(), 24), clean(d.getTeachers(), 24));
			}
		} else if (isSubject) {
			headerFooterLine = "+------+---------------+---------------+---------------+----------+--------------------------+";
			format = "| %-4s | %-13s | %-13s | %-13s | %-8s | %-24s |%n";
			System.out.println(headerFooterLine);
			System.out.printf(format, "SrNo", "Subject", "Type", "Course", "Students", "Teachers");
			System.out.println(headerFooterLine);

			for (DashboardModel d : dashboard) {
				System.out.printf(format, d.getSrNo(), clean(d.getSubjects(), 13), clean(d.getSubjectType(), 13),
						clean(d.getCourse(), 13), d.getTotalStudents(), clean(d.getTeachers(), 24));
			}
		} else if (isTeacher) {
			headerFooterLine = "+------+------+-----------------+----------+----------+--------------------------+--------------------------+";
			format = "| %-4s | %-4s | %-15s | %-8s | %-8s | %-24s | %-24s |%n";
			System.out.println(headerFooterLine);
			System.out.printf(format, "SrNo", "ID", "Teacher Name", "Subjects", "Students", "Subject Names", "Courses");
			System.out.println(headerFooterLine);

			for (DashboardModel d : dashboard) {
				System.out.printf(format, d.getSrNo(), d.getTeacherId(), clean(d.getTeacherName(), 15),
						d.getTotalSubjects(), d.getTotalStudents(), clean(d.getSubjects(), 24),
						clean(d.getCourse(), 24));
			}
		} else {
			// Fallback case for unexpected title
			System.out.println("⚠ Error: Invalid dashboard title: " + title);
			return;
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