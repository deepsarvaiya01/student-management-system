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
		
		// Display the course-wise student count chart
		displayCourseWiseStudentChart();
	}

	public void displayCourseWiseDashboard() {
		List<DashboardModel> list = dashboardDao.getCourseWiseDashboardData();
		printTable(list, "COURSE - WISE DASHBOARD");
		
		// Display the course-wise subject count chart
		displayCourseWiseSubjectChart();
	}

	public void displaySubjectDashboard() {
		boolean showAll = InputValidator.getValidConfirmation(scanner, "Show subjects with no students? (y/n): ");
		List<DashboardModel> list = dashboardDao.getSubjectDashboardData(showAll);
		printTable(list, "SUBJECT - WISE DASHBOARD");
		
		// Display the subject-wise student count chart
		displaySubjectWiseStudentChart();
	}

	public void displayTeacherLoadDashboard() {
		List<DashboardModel> list = dashboardDao.getTeacherLoadDashboardData();
		printTable(list, "TEACHER - WISE DASHBOARD");
		
		// Display the teacher-wise subject count chart
		displayTeacherWiseSubjectChart();
	}

	/**
	 * Display a horizontal bar chart showing number of students per course
	 */
	private void displayCourseWiseStudentChart() {
		List<DashboardModel> courseCounts = dashboardDao.getCourseWiseStudentCounts();
		
		if (courseCounts.isEmpty()) {
			System.out.println("\n⚠ No course data available for chart visualization.");
			return;
		}

		System.out.println("\n" + "═".repeat(80));
		System.out.println("📊 CHART: Number of Students per Course");
		System.out.println("═".repeat(80));
		
		// Find the maximum student count for scaling
		int maxStudents = courseCounts.stream()
				.mapToInt(DashboardModel::getTotalStudents)
				.max()
				.orElse(1);
		
		// Define colors for the bars (using simple color codes that work in most terminals)
		String[] colors = {
			"\033[94m",  // Blue
			"\033[92m",  // Green
			"\033[93m",  // Yellow
			"\033[95m",  // Magenta
			"\033[96m",  // Cyan
			"\033[91m",  // Red
			"\033[90m",  // Dark Gray
			"\033[97m",  // White
			"\033[33m",  // Orange
			"\033[35m"   // Purple
		};
		String resetColor = "\033[0m";
		
		// Create a map to assign colors based on student count
		java.util.Map<Integer, String> studentCountToColor = new java.util.HashMap<>();
		java.util.Set<Integer> uniqueStudentCounts = new java.util.TreeSet<>();
		
		// Collect unique student counts
		for (DashboardModel course : courseCounts) {
			uniqueStudentCounts.add(course.getTotalStudents());
		}
		
		// Assign colors to unique student counts
		int colorIndex = 0;
		for (Integer studentCount : uniqueStudentCounts) {
			studentCountToColor.put(studentCount, colors[colorIndex % colors.length]);
			colorIndex++;
		}
		
		// Chart title and axis labels
		System.out.println("Y-axis: Number of students");
		System.out.println("X-axis: Course names");
		System.out.println();
		
		// Display the chart
		for (int i = 0; i < courseCounts.size(); i++) {
			DashboardModel course = courseCounts.get(i);
			String courseName = course.getCourse() != null ? course.getCourse() : "Unknown Course";
			int studentCount = course.getTotalStudents();
			
			// Calculate bar length (max 40 characters for the bar to fit nicely)
			int barLength = maxStudents > 0 ? (studentCount * 40) / maxStudents : 0;
			if (barLength == 0 && studentCount > 0) barLength = 1; // Minimum bar for at least 1 student
			
			// Get color for this student count
			String color = studentCountToColor.get(studentCount);
			
			// Create the bar with different characters for variety
			String barChar = "█";
			if (i % 3 == 0) barChar = "█";
			else if (i % 3 == 1) barChar = "▓";
			else barChar = "▒";
			
			String bar = barChar.repeat(barLength);
			
			// Format the output with proper alignment
			System.out.printf("%-25s │ %s%s%s %d\n", 
				courseName, 
				color, 
				bar, 
				resetColor, 
				studentCount);
		}
		
		// Display color legend
		System.out.println("\n" + "─".repeat(80));
		System.out.println("🎨 COLOR LEGEND:");
		for (Integer studentCount : uniqueStudentCounts) {
			String color = studentCountToColor.get(studentCount);
			System.out.printf("%s██%s = %d student%s\n", 
				color, 
				resetColor, 
				studentCount, 
				studentCount == 1 ? "" : "s");
		}
		
		// Chart footer with summary
		System.out.println("\n" + "─".repeat(80));
		int totalStudents = courseCounts.stream().mapToInt(DashboardModel::getTotalStudents).sum();
		System.out.println("💡 Chart shows course popularity based on student enrollment");
		System.out.println("📈 Total students across all courses: " + totalStudents);
		if (!courseCounts.isEmpty()) {
			DashboardModel mostPopular = courseCounts.get(0);
			System.out.println("🎯 Most popular course: " + mostPopular.getCourse() + 
				" (" + mostPopular.getTotalStudents() + " students)");
		}
		System.out.println("═".repeat(80));
	}

	/**
	 * Display a horizontal bar chart showing number of subjects per course
	 */
	private void displayCourseWiseSubjectChart() {
		List<DashboardModel> courseSubjectCounts = dashboardDao.getCourseWiseSubjectCounts();
		
		if (courseSubjectCounts.isEmpty()) {
			System.out.println("\n⚠ No course-subject data available for chart visualization.");
			return;
		}

		System.out.println("\n" + "═".repeat(80));
		System.out.println("📊 CHART: Number of Subjects per Course");
		System.out.println("═".repeat(80));
		
		// Find the maximum subject count for scaling
		int maxSubjects = courseSubjectCounts.stream()
				.mapToInt(DashboardModel::getTotalSubjects)
				.max()
				.orElse(1);
		
		// Define colors for the bars
		String[] colors = {
			"\033[94m",  // Blue
			"\033[92m",  // Green
			"\033[93m",  // Yellow
			"\033[95m",  // Magenta
			"\033[96m",  // Cyan
			"\033[91m",  // Red
			"\033[90m",  // Dark Gray
			"\033[97m",  // White
			"\033[33m",  // Orange
			"\033[35m"   // Purple
		};
		String resetColor = "\033[0m";
		
		// Create a map to assign colors based on subject count
		java.util.Map<Integer, String> subjectCountToColor = new java.util.HashMap<>();
		java.util.Set<Integer> uniqueSubjectCounts = new java.util.TreeSet<>();
		
		// Collect unique subject counts
		for (DashboardModel course : courseSubjectCounts) {
			uniqueSubjectCounts.add(course.getTotalSubjects());
		}
		
		// Assign colors to unique subject counts
		int colorIndex = 0;
		for (Integer subjectCount : uniqueSubjectCounts) {
			subjectCountToColor.put(subjectCount, colors[colorIndex % colors.length]);
			colorIndex++;
		}
		
		// Chart title and axis labels
		System.out.println("Y-axis: Number of subjects");
		System.out.println("X-axis: Course names");
		System.out.println();
		
		// Display the chart
		for (int i = 0; i < courseSubjectCounts.size(); i++) {
			DashboardModel course = courseSubjectCounts.get(i);
			String courseName = course.getCourse() != null ? course.getCourse() : "Unknown Course";
			int subjectCount = course.getTotalSubjects();
			
			// Calculate bar length (max 40 characters for the bar to fit nicely)
			int barLength = maxSubjects > 0 ? (subjectCount * 40) / maxSubjects : 0;
			if (barLength == 0 && subjectCount > 0) barLength = 1; // Minimum bar for at least 1 subject
			
			// Get color for this subject count
			String color = subjectCountToColor.get(subjectCount);
			
			// Create the bar with different characters for variety
			String barChar = "█";
			if (i % 3 == 0) barChar = "█";
			else if (i % 3 == 1) barChar = "▓";
			else barChar = "▒";
			
			String bar = barChar.repeat(barLength);
			
			// Format the output with proper alignment
			System.out.printf("%-25s │ %s%s%s %d\n", 
				courseName, 
				color, 
				bar, 
				resetColor, 
				subjectCount);
		}
		
		// Display color legend
		System.out.println("\n" + "─".repeat(80));
		System.out.println("🎨 COLOR LEGEND:");
		for (Integer subjectCount : uniqueSubjectCounts) {
			String color = subjectCountToColor.get(subjectCount);
			System.out.printf("%s██%s = %d subject%s\n", 
				color, 
				resetColor, 
				subjectCount, 
				subjectCount == 1 ? "" : "s");
		}
		
		// Chart footer with summary
		System.out.println("\n" + "─".repeat(80));
		int totalSubjects = courseSubjectCounts.stream().mapToInt(DashboardModel::getTotalSubjects).sum();
		System.out.println("💡 Chart shows course curriculum load based on subject count");
		System.out.println("📈 Total subjects across all courses: " + totalSubjects);
		if (!courseSubjectCounts.isEmpty()) {
			DashboardModel mostLoaded = courseSubjectCounts.get(0);
			System.out.println("🎯 Most loaded course: " + mostLoaded.getCourse() + 
				" (" + mostLoaded.getTotalSubjects() + " subjects)");
		}
		System.out.println("═".repeat(80));
	}

	/**
	 * Display a horizontal bar chart showing number of subjects per teacher
	 */
	private void displayTeacherWiseSubjectChart() {
		List<DashboardModel> teacherSubjectCounts = dashboardDao.getTeacherWiseSubjectCounts();
		
		if (teacherSubjectCounts.isEmpty()) {
			System.out.println("\n⚠ No teacher-subject data available for chart visualization.");
			return;
		}

		System.out.println("\n" + "═".repeat(80));
		System.out.println("📊 CHART: Number of Subjects per Teacher");
		System.out.println("═".repeat(80));
		
		// Find the maximum subject count for scaling
		int maxSubjects = teacherSubjectCounts.stream()
				.mapToInt(DashboardModel::getTotalSubjects)
				.max()
				.orElse(1);
		
		// Define colors for the bars
		String[] colors = {
			"\033[94m",  // Blue
			"\033[92m",  // Green
			"\033[93m",  // Yellow
			"\033[95m",  // Magenta
			"\033[96m",  // Cyan
			"\033[91m",  // Red
			"\033[90m",  // Dark Gray
			"\033[97m",  // White
			"\033[33m",  // Orange
			"\033[35m"   // Purple
		};
		String resetColor = "\033[0m";
		
		// Create a map to assign colors based on subject count
		java.util.Map<Integer, String> subjectCountToColor = new java.util.HashMap<>();
		java.util.Set<Integer> uniqueSubjectCounts = new java.util.TreeSet<>();
		
		// Collect unique subject counts
		for (DashboardModel teacher : teacherSubjectCounts) {
			uniqueSubjectCounts.add(teacher.getTotalSubjects());
		}
		
		// Assign colors to unique subject counts
		int colorIndex = 0;
		for (Integer subjectCount : uniqueSubjectCounts) {
			subjectCountToColor.put(subjectCount, colors[colorIndex % colors.length]);
			colorIndex++;
		}
		
		// Chart title and axis labels
		System.out.println("Y-axis: Number of subjects");
		System.out.println("X-axis: Teacher names");
		System.out.println();
		
		// Display the chart
		for (int i = 0; i < teacherSubjectCounts.size(); i++) {
			DashboardModel teacher = teacherSubjectCounts.get(i);
			String teacherName = teacher.getTeacherName() != null ? teacher.getTeacherName() : "Unknown Teacher";
			int subjectCount = teacher.getTotalSubjects();
			
			// Calculate bar length (max 40 characters for the bar to fit nicely)
			int barLength = maxSubjects > 0 ? (subjectCount * 40) / maxSubjects : 0;
			if (barLength == 0 && subjectCount > 0) barLength = 1; // Minimum bar for at least 1 subject
			
			// Get color for this subject count
			String color = subjectCountToColor.get(subjectCount);
			
			// Create the bar with different characters for variety
			String barChar = "█";
			if (i % 3 == 0) barChar = "█";
			else if (i % 3 == 1) barChar = "▓";
			else barChar = "▒";
			
			String bar = barChar.repeat(barLength);
			
			// Format the output with proper alignment
			System.out.printf("%-25s │ %s%s%s %d\n", 
				teacherName, 
				color, 
				bar, 
				resetColor, 
				subjectCount);
		}
		
		// Display color legend
		System.out.println("\n" + "─".repeat(80));
		System.out.println("🎨 COLOR LEGEND:");
		for (Integer subjectCount : uniqueSubjectCounts) {
			String color = subjectCountToColor.get(subjectCount);
			System.out.printf("%s██%s = %d subject%s\n", 
				color, 
				resetColor, 
				subjectCount, 
				subjectCount == 1 ? "" : "s");
		}
		
		// Chart footer with summary
		System.out.println("\n" + "─".repeat(80));
		int totalSubjects = teacherSubjectCounts.stream().mapToInt(DashboardModel::getTotalSubjects).sum();
		System.out.println("💡 Chart shows teacher workload distribution based on subject count");
		System.out.println("📈 Total subjects across all teachers: " + totalSubjects);
		if (!teacherSubjectCounts.isEmpty()) {
			DashboardModel mostLoaded = teacherSubjectCounts.get(0);
			System.out.println("🎯 Most loaded teacher: " + mostLoaded.getTeacherName() + 
				" (" + mostLoaded.getTotalSubjects() + " subjects)");
		}
		System.out.println("═".repeat(80));
	}

	/**
	 * Display a horizontal bar chart showing number of students per subject
	 */
	private void displaySubjectWiseStudentChart() {
		List<DashboardModel> subjectStudentCounts = dashboardDao.getSubjectWiseStudentCounts();
		
		if (subjectStudentCounts.isEmpty()) {
			System.out.println("\n⚠ No subject-student data available for chart visualization.");
			return;
		}

		System.out.println("\n" + "═".repeat(80));
		System.out.println("📊 CHART: Number of Students per Subject");
		System.out.println("═".repeat(80));
		
		// Find the maximum student count for scaling
		int maxStudents = subjectStudentCounts.stream()
				.mapToInt(DashboardModel::getTotalStudents)
				.max()
				.orElse(1);
		
		// Define colors for the bars
		String[] colors = {
			"\033[94m",  // Blue
			"\033[92m",  // Green
			"\033[93m",  // Yellow
			"\033[95m",  // Magenta
			"\033[96m",  // Cyan
			"\033[91m",  // Red
			"\033[90m",  // Dark Gray
			"\033[97m",  // White
			"\033[33m",  // Orange
			"\033[35m"   // Purple
		};
		String resetColor = "\033[0m";
		
		// Create a map to assign colors based on student count
		java.util.Map<Integer, String> studentCountToColor = new java.util.HashMap<>();
		java.util.Set<Integer> uniqueStudentCounts = new java.util.TreeSet<>();
		
		// Collect unique student counts
		for (DashboardModel subject : subjectStudentCounts) {
			uniqueStudentCounts.add(subject.getTotalStudents());
		}
		
		// Assign colors to unique student counts
		int colorIndex = 0;
		for (Integer studentCount : uniqueStudentCounts) {
			studentCountToColor.put(studentCount, colors[colorIndex % colors.length]);
			colorIndex++;
		}
		
		// Chart title and axis labels
		System.out.println("Y-axis: Number of students");
		System.out.println("X-axis: Subject names");
		System.out.println();
		
		// Display the chart
		for (int i = 0; i < subjectStudentCounts.size(); i++) {
			DashboardModel subject = subjectStudentCounts.get(i);
			String subjectName = subject.getSubjects() != null ? subject.getSubjects() : "Unknown Subject";
			int studentCount = subject.getTotalStudents();
			
			// Calculate bar length (max 40 characters for the bar to fit nicely)
			int barLength = maxStudents > 0 ? (studentCount * 40) / maxStudents : 0;
			if (barLength == 0 && studentCount > 0) barLength = 1; // Minimum bar for at least 1 student
			
			// Get color for this student count
			String color = studentCountToColor.get(studentCount);
			
			// Create the bar with different characters for variety
			String barChar = "█";
			if (i % 3 == 0) barChar = "█";
			else if (i % 3 == 1) barChar = "▓";
			else barChar = "▒";
			
			String bar = barChar.repeat(barLength);
			
			// Format the output with proper alignment
			System.out.printf("%-25s │ %s%s%s %d\n", 
				subjectName, 
				color, 
				bar, 
				resetColor, 
				studentCount);
		}
		
		// Display color legend
		System.out.println("\n" + "─".repeat(80));
		System.out.println("🎨 COLOR LEGEND:");
		for (Integer studentCount : uniqueStudentCounts) {
			String color = studentCountToColor.get(studentCount);
			System.out.printf("%s██%s = %d student%s\n", 
				color, 
				resetColor, 
				studentCount, 
				studentCount == 1 ? "" : "s");
		}
		
		// Chart footer with summary
		System.out.println("\n" + "─".repeat(80));
		int totalStudents = subjectStudentCounts.stream().mapToInt(DashboardModel::getTotalStudents).sum();
		System.out.println("💡 Chart shows subject popularity based on student enrollment");
		System.out.println("📈 Total students across all subjects: " + totalStudents);
		if (!subjectStudentCounts.isEmpty()) {
			DashboardModel mostPopular = subjectStudentCounts.get(0);
			System.out.println("🎯 Most popular subject: " + mostPopular.getSubjects() + 
				" (" + mostPopular.getTotalStudents() + " students)");
		}
		System.out.println("═".repeat(80));
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