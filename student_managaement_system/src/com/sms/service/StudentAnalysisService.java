package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.dao.StudentAnalysisDAO;
import com.sms.utils.HelperUtils;

public class StudentAnalysisService {
	private StudentAnalysisDAO analysisDAO;

	public StudentAnalysisService() throws SQLException {
		this.analysisDAO = new StudentAnalysisDAO();
	}

	// Enrollment Analysis Methods
	public void displayTotalStudentsByCourse() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                  TOTAL STUDENTS BY COURSE                ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> courseStats = analysisDAO.getTotalStudentsByCourse();

		if (courseStats.isEmpty()) {
			System.out.println(" No data available for analysis.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Course Name             │ Student Count   │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalStudents = courseStats.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : courseStats.entrySet()) {
			String courseName = entry.getKey();
			int studentCount = entry.getValue();
			double percentage = totalStudents > 0 ? (studentCount * 100.0) / totalStudents : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", courseName, studentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n📈 Total Students: %d%n", totalStudents);
	}

	public void displayEnrollmentDateTrends() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                  ENROLLMENT DATE TRENDS                  ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> enrollmentTrends = analysisDAO.getEnrollmentDateTrends();

		if (enrollmentTrends.isEmpty()) {
			System.out.println(" No enrollment data available for analysis.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Month-Year              │ Enrollments     │ Visual Chart    │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		int maxEnrollments = enrollmentTrends.values().stream().mapToInt(Integer::intValue).max().orElse(0);

		for (Map.Entry<String, Integer> entry : enrollmentTrends.entrySet()) {
			String monthYear = entry.getKey();
			int enrollments = entry.getValue();
			String chart = generateBarChart(enrollments, maxEnrollments, 20);

			System.out.printf("│ %-23s │ %-15d │ %-20s %n", monthYear, enrollments, chart);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
	}

	public void displayActiveVsInactiveStudents() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║             ACTIVE VS INACTIVE STUDENTS                  ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> statusStats = analysisDAO.getActiveVsInactiveStudents();

		if (statusStats.isEmpty()) {
			System.out.println(" No student status data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Status                  │ Count           │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalStudents = statusStats.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : statusStats.entrySet()) {
			String status = entry.getKey();
			int count = entry.getValue();
			double percentage = totalStudents > 0 ? (count * 100.0) / totalStudents : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", status, count, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n📊 Total Students: %d%n", totalStudents);
	}

	public void displayStudentDistributionByCity() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   STUDENT DISTRIBUTION BY CITY           ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> cityStats = analysisDAO.getStudentDistributionByCity();

		if (cityStats.isEmpty()) {
			System.out.println(" No city distribution data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ City                    │ Student Count   │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalStudents = cityStats.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : cityStats.entrySet()) {
			String city = entry.getKey();
			int count = entry.getValue();
			double percentage = totalStudents > 0 ? (count * 100.0) / totalStudents : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", city, count, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n🌍 Total Cities: %d%n", cityStats.size());
	}



	public void displayStudentsWithCompletePayment() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              STUDENTS WITH COMPLETE PAYMENT              ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		List<Map<String, Object>> completePayments = analysisDAO.getStudentsWithCompletePayment();

		if (completePayments.isEmpty()) {
			System.out.println(" No students with complete payment found.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Student Name            │ Course          │ Total Paid      │ Payment Date    │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┤");

		for (Map<String, Object> student : completePayments) {
			String name = HelperUtils.truncate((String) student.get("name"), 23);
			String course = HelperUtils.truncate((String) student.get("course_name"), 15);
			String paidAmount = student.get("paid_amount").toString();
			String paymentDate = student.get("last_payment_date").toString();

			System.out.printf("│ %-23s │ %-15s │ %-15s │ %-15s │%n", name, course, "₹" + paidAmount, paymentDate);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┘");
		System.out.printf("\n🎉 Total Students with Complete Payment: %d%n", completePayments.size());
	}

	public void displayStudentsWithPendingFees() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              STUDENTS WITH PENDING FEES                  ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		List<Map<String, Object>> pendingFees = analysisDAO.getStudentsWithPendingFees();

		if (pendingFees.isEmpty()) {
			System.out.println("✅ All students have completed their payments!");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Student Name            │ Course          │ Pending Amount  │ Completion %    │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┤");

		for (Map<String, Object> student : pendingFees) {
			String name = HelperUtils.truncate((String) student.get("name"), 23);
			String course = HelperUtils.truncate((String) student.get("course_name"), 15);
			String pendingAmount = student.get("pending_amount").toString();
			double completionPercentage = (Double) student.get("completion_percentage");

			System.out.printf("│ %-23s │ %-15s │ %-15s │ %-14.1f%% │%n", name, course, "₹" + pendingAmount,
					completionPercentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┘");
		System.out.printf("\n⚠️ Students with Pending Fees: %d%n", pendingFees.size());
	}

	public void displayAverageFeePaymentPerStudent() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║               AVERAGE FEE PAYMENT PER STUDENT            ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Double> averagePayments = analysisDAO.getAverageFeePaymentPerStudent();

		if (averagePayments.isEmpty()) {
			System.out.println(" No payment data available for analysis.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┐");
		System.out.println("│ Course                  │ Avg. Payment    │");
		System.out.println("├─────────────────────────┼─────────────────│");

		for (Map.Entry<String, Double> entry : averagePayments.entrySet()) {
			String course = entry.getKey();
			double avgPayment = entry.getValue();

			System.out.printf("│ %-23s │ ₹%-14.2f │%n", course, avgPayment);
		}

		System.out.println("└─────────────────────────┴─────────────────┘");
	}

	public void displayPaymentCompletionRate() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              PAYMENT COMPLETION RATE                     ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> completionStats = analysisDAO.getPaymentCompletionRate();

		if (completionStats.isEmpty()) {
			System.out.println(" No payment data available for analysis.");
			return;
		}

		double totalExpected = (Double) completionStats.get("total_expected");
		double totalCollected = (Double) completionStats.get("total_collected");
		double completionRate = (Double) completionStats.get("completion_rate");

		System.out.println("\n┌─────────────────────────┬──────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                                │");
		System.out.println("├─────────────────────────┼──────────────────────────────────────┤");
		System.out.printf("│ Total Expected Amount   │ ₹%-35.2f │%n", totalExpected);
		System.out.printf("│ Total Collected Amount  │ ₹%-35.2f │%n", totalCollected);
		System.out.printf("│ Completion Rate         │ %-35.1f%% │%n", completionRate);
		System.out.println("└─────────────────────────┴──────────────────────────────────────┘");

		// Visual progress bar
		System.out.println("\n📈 Payment Completion Progress:");
		System.out.print("│");
		int barLength = 50;
		int filledLength = (int) (completionRate * barLength / 100);

		for (int i = 0; i < barLength; i++) {
			if (i < filledLength) {
				System.out.print("█");
			} else {
				System.out.print("░");
			}
		}
		System.out.printf("│ %.1f%%%n", completionRate);
	}

	// Student Demographics Methods
	public void displayAgeDistribution() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                  AGE DISTRIBUTION                        ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> ageStats = analysisDAO.getAgeDistribution();

		if (ageStats.isEmpty()) {
			System.out.println(" No age data available for analysis.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Age Group               │ Student Count   │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalStudents = ageStats.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : ageStats.entrySet()) {
			String ageGroup = entry.getKey();
			int count = entry.getValue();
			double percentage = totalStudents > 0 ? (count * 100.0) / totalStudents : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", ageGroup, count, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
	}

	public void displayCityWiseStudentCount() throws SQLException {
		// This is same as displayStudentDistributionByCity()
		displayStudentDistributionByCity();
	}

	public void displayCoursePreferenceByAge() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              COURSE PREFERENCE BY AGE                    ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Map<String, Integer>> ageCourseStats = analysisDAO.getCoursePreferenceByAge();

		if (ageCourseStats.isEmpty()) {
			System.out.println(" No age-course preference data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Age Group               │ Course          │ Student Count   │ Percentage      │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┤");

		for (Map.Entry<String, Map<String, Integer>> ageEntry : ageCourseStats.entrySet()) {
			String ageGroup = HelperUtils.truncate(ageEntry.getKey(), 23);
			Map<String, Integer> courseStats = ageEntry.getValue();

			int totalInAgeGroup = courseStats.values().stream().mapToInt(Integer::intValue).sum();

			for (Map.Entry<String, Integer> courseEntry : courseStats.entrySet()) {
				String course = HelperUtils.truncate(courseEntry.getKey(), 15);
				int count = courseEntry.getValue();
				double percentage = totalInAgeGroup > 0 ? (count * 100.0) / totalInAgeGroup : 0;

				System.out.printf("│ %-23s │ %-15s │ %-15d │ %-14.1f%% │%n", ageGroup, course, count, percentage);
			}
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┘");
	}

	public void displayGenderAnalysis() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   GENDER ANALYSIS                        ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		// Display overall gender distribution
		Map<String, Integer> genderStats = analysisDAO.getGenderDistribution();

		if (genderStats.isEmpty()) {
			System.out.println(" No gender data available for analysis.");
			return;
		}

		System.out.println("\n📊 Overall Gender Distribution:");
		System.out.println("┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Gender                  │ Student Count   │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalStudents = genderStats.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : genderStats.entrySet()) {
			String gender = entry.getKey();
			int count = entry.getValue();
			double percentage = totalStudents > 0 ? (count * 100.0) / totalStudents : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", gender, count, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("👥 Total Students: %d%n", totalStudents);

		// Display gender distribution by course
		System.out.println("\n📚 Gender Distribution by Course:");
		Map<String, Map<String, Integer>> genderCourseStats = analysisDAO.getGenderDistributionByCourse();

		if (!genderCourseStats.isEmpty()) {
		    System.out.println("┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┐");
		    System.out.println("│ Gender                  │ Course          │ Student Count   │ Percentage      │");
		    System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┤");

		    for (Map.Entry<String, Map<String, Integer>> genderEntry : genderCourseStats.entrySet()) {
		        String gender = HelperUtils.truncate(genderEntry.getKey(), 23);
		        Map<String, Integer> courseStats = genderEntry.getValue();

		        int totalInGender = courseStats.values().stream().mapToInt(Integer::intValue).sum();

		        for (Map.Entry<String, Integer> courseEntry : courseStats.entrySet()) {
		            String course = HelperUtils.truncate(courseEntry.getKey(), 15);
		            int count = courseEntry.getValue();
		            double percentage = totalInGender > 0 ? (count * 100.0) / totalInGender : 0;

		            System.out.printf("│ %-23s │ %-15s │ %-15d │ %-14.1f%% │%n", 
		                gender, course, count, percentage);
		        }
		    }

		    System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┘");
		}

		// Display gender distribution by city
		System.out.println("\n🏙️ Gender Distribution by City:");
		Map<String, Map<String, Integer>> genderCityStats = analysisDAO.getGenderDistributionByCity();

		if (!genderCityStats.isEmpty()) {
			System.out.println("┌─────────────────────────┬─────────────────┬─────────────────┬──────────────────┐");
			System.out.println("│ Gender                  │ City            │ Student Count   │ Percentage       │");
			System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼──────────────────┤");

			for (Map.Entry<String, Map<String, Integer>> genderEntry : genderCityStats.entrySet()) {
				String gender = genderEntry.getKey();
				Map<String, Integer> cityStats = genderEntry.getValue();

				int totalInGender = cityStats.values().stream().mapToInt(Integer::intValue).sum();

				for (Map.Entry<String, Integer> cityEntry : cityStats.entrySet()) {
					String city = cityEntry.getKey();
					int count = cityEntry.getValue();
					double percentage = totalInGender > 0 ? (count * 100.0) / totalInGender : 0;

					System.out.printf("│ %-23s │ %-15s │ %-15d │ %-15.1f%% │%n", gender, city, count, percentage);
				}
			}

			System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴──────────────────┘");
		}
	}

	// Helper method to generate bar charts
	private String generateBarChart(int value, int maxValue, int maxLength) {
		if (maxValue == 0)
			return "";

		int barLength = (int) ((value * maxLength) / maxValue);
		StringBuilder bar = new StringBuilder();

		for (int i = 0; i < barLength; i++) {
			bar.append("█");
		}

		return bar.toString();
	}
}