package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.dao.FinancialAnalysisDAO;

public class FinancialAnalysisService {
	private FinancialAnalysisDAO analysisDAO;

	public FinancialAnalysisService() throws SQLException {
		this.analysisDAO = new FinancialAnalysisDAO();
	}

	// Revenue Analysis Methods
	public void displayTotalRevenueCollected() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                💰 TOTAL REVENUE COLLECTED                ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> revenueStats = analysisDAO.getTotalRevenueCollected();
		
		if (revenueStats.isEmpty()) {
			System.out.println("❌ No revenue data available for analysis.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                               │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");
		System.out.printf("│ Total Revenue Collected │ ₹%-35.2f │%n", revenueStats.get("total_collected"));
		System.out.printf("│ Total Pending Amount    │ ₹%-35.2f │%n", revenueStats.get("total_pending"));
		System.out.printf("│ Total Expected Amount   │ ₹%-35.2f │%n", revenueStats.get("total_expected"));
		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");

		// Calculate and display percentage
		double totalCollected = ((Number) revenueStats.get("total_collected")).doubleValue();
		double totalExpected = ((Number) revenueStats.get("total_expected")).doubleValue();
		double collectionPercentage = totalExpected > 0 ? (totalCollected * 100.0) / totalExpected : 0;
		
		System.out.printf("\n📊 Collection Rate: %.1f%%%n", collectionPercentage);
	}

	public void displayRevenueByCourse() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                📈 REVENUE BY COURSE                      ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Double> courseRevenue = analysisDAO.getRevenueByCourse();
		
		if (courseRevenue.isEmpty()) {
			System.out.println("❌ No course revenue data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Course Name             │ Revenue         │ Percentage      │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		double totalRevenue = courseRevenue.values().stream().mapToDouble(Double::doubleValue).sum();

		for (Map.Entry<String, Double> entry : courseRevenue.entrySet()) {
			String courseName = entry.getKey();
			double revenue = entry.getValue();
			double percentage = totalRevenue > 0 ? (revenue * 100.0) / totalRevenue : 0;

			System.out.printf("│ %-23s │ ₹%-14.2f │ %-15.1f%% │%n", 
				courseName, revenue, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
		System.out.printf("\n💰 Total Revenue: ₹%.2f%n", totalRevenue);
	}

	public void displayAveragePaymentPerStudent() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            💰 AVERAGE PAYMENT PER STUDENT                ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		double avgPayment = analysisDAO.getAveragePaymentPerStudent();
		
		if (avgPayment == 0.0) {
			System.out.println("❌ No payment data available for analysis.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                               │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");
		System.out.printf("│ Average Payment         │ ₹%-35.2f │%n", avgPayment);
		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");
	}

	// Payment Analysis Methods
	public void displayPaymentCompletionPercentage() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            📊 PAYMENT COMPLETION PERCENTAGE              ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> completionStats = analysisDAO.getPaymentCompletionPercentage();
		
		if (completionStats.isEmpty()) {
			System.out.println("❌ No payment completion data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                               │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");
		System.out.printf("│ Total Students          │ %-35d │%n", completionStats.get("total_students"));
		System.out.printf("│ Completed Payments      │ %-35d │%n", completionStats.get("completed_payments"));
		System.out.printf("│ Pending Payments        │ %-35d │%n", completionStats.get("pending_payments"));
		System.out.printf("│ Completion Percentage   │ %-35.1f%% │%n", completionStats.get("completion_percentage"));
		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");

		// Visual progress bar
		double completionPercentage = (Double) completionStats.get("completion_percentage");
		System.out.println("\n📈 Payment Completion Progress:");
		System.out.print("│");
		int barLength = 50;
		int filledLength = (int) (completionPercentage * barLength / 100);
		
		for (int i = 0; i < barLength; i++) {
			if (i < filledLength) {
				System.out.print("█");
			} else {
				System.out.print("░");
			}
		}
		System.out.printf("│ %.1f%%%n", completionPercentage);
	}

	public void displayCourseWisePaymentStatus() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            📊 COURSE-WISE PAYMENT STATUS                 ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Map<String, Object>> coursePaymentStatus = analysisDAO.getCourseWisePaymentStatus();
		
		if (coursePaymentStatus.isEmpty()) {
			System.out.println("❌ No course payment status data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Course Name             │ Total Students  │ Completed       │ Pending         │ Completion %    │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┼─────────────────┤");

		for (Map.Entry<String, Map<String, Object>> entry : coursePaymentStatus.entrySet()) {
			String courseName = entry.getKey();
			Map<String, Object> status = entry.getValue();
			
			int totalStudents = (Integer) status.get("total_students");
			int completedPayments = (Integer) status.get("completed_payments");
			int pendingPayments = (Integer) status.get("pending_payments");
			double completionPercentage = (Double) status.get("completion_percentage");

			System.out.printf("│ %-23s │ %-15d │ %-15d │ %-15d │ %-15.1f%% │%n", 
				courseName, totalStudents, completedPayments, pendingPayments, completionPercentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘");
	}

	public void displayRecentPaymentTrends() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            📈 RECENT PAYMENT TRENDS                      ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> paymentTrends = analysisDAO.getRecentPaymentTrends();
		
		if (paymentTrends.isEmpty()) {
			System.out.println("❌ No recent payment trends data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Month-Year              │ Payment Count   │ Visual Chart    │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		int maxPayments = paymentTrends.values().stream().mapToInt(Integer::intValue).max().orElse(0);

		for (Map.Entry<String, Integer> entry : paymentTrends.entrySet()) {
			String monthYear = entry.getKey();
			int paymentCount = entry.getValue();
			String chart = generateBarChart(paymentCount, maxPayments, 20);

			System.out.printf("│ %-23s │ %-15d │ %-15s │%n", 
				monthYear, paymentCount, chart);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
	}

	public void displayOutstandingAmountByCourse() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            ⚠️ OUTSTANDING AMOUNT BY COURSE               ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Double> outstandingAmounts = analysisDAO.getOutstandingAmountByCourse();
		
		if (outstandingAmounts.isEmpty()) {
			System.out.println("✅ No outstanding amounts found!");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Course Name             │ Outstanding     │ Percentage      │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		double totalOutstanding = outstandingAmounts.values().stream().mapToDouble(Double::doubleValue).sum();

		for (Map.Entry<String, Double> entry : outstandingAmounts.entrySet()) {
			String courseName = entry.getKey();
			double outstandingAmount = entry.getValue();
			double percentage = totalOutstanding > 0 ? (outstandingAmount * 100.0) / totalOutstanding : 0;

			System.out.printf("│ %-23s │ ₹%-14.2f │ %-15.1f%% │%n", 
				courseName, outstandingAmount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
		System.out.printf("\n⚠️ Total Outstanding Amount: ₹%.2f%n", totalOutstanding);
	}

	// Financial Metrics Methods
	public void displayCollectionEfficiency() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            📊 COLLECTION EFFICIENCY                      ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> efficiencyStats = analysisDAO.getCollectionEfficiency();
		
		if (efficiencyStats.isEmpty()) {
			System.out.println("❌ No collection efficiency data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                               │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");
		System.out.printf("│ Total Expected Amount   │ ₹%-35.2f │%n", efficiencyStats.get("total_expected"));
		System.out.printf("│ Total Collected Amount  │ ₹%-35.2f │%n", efficiencyStats.get("total_collected"));
		System.out.printf("│ Total Pending Amount    │ ₹%-35.2f │%n", efficiencyStats.get("total_pending"));
		System.out.printf("│ Collection Efficiency   │ %-35.1f%% │%n", efficiencyStats.get("collection_efficiency"));
		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");

		// Visual efficiency bar
		double efficiency = (Double) efficiencyStats.get("collection_efficiency");
		System.out.println("\n📈 Collection Efficiency Progress:");
		System.out.print("│");
		int barLength = 50;
		int filledLength = (int) (efficiency * barLength / 100);
		
		for (int i = 0; i < barLength; i++) {
			if (i < filledLength) {
				System.out.print("█");
			} else {
				System.out.print("░");
			}
		}
		System.out.printf("│ %.1f%%%n", efficiency);
	}

	public void displayRevenuePerCourse() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            💰 REVENUE PER COURSE                         ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Double> revenuePerCourse = analysisDAO.getRevenuePerCourse();
		
		if (revenuePerCourse.isEmpty()) {
			System.out.println("❌ No revenue per course data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Course Name             │ Revenue per Student                 │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");

		for (Map.Entry<String, Double> entry : revenuePerCourse.entrySet()) {
			String courseName = entry.getKey();
			double revenuePerStudent = entry.getValue();

			System.out.printf("│ %-23s │ ₹%-35.2f │%n", 
				courseName, revenuePerStudent);
		}

		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");
	}

	public void displayAverageFeeStructure() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            📊 AVERAGE FEE STRUCTURE                      ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> feeStructure = analysisDAO.getAverageFeeStructure();
		
		if (feeStructure.isEmpty()) {
			System.out.println("❌ No fee structure data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                               │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");
		System.out.printf("│ Average Total Fee       │ ₹%-35.2f │%n", feeStructure.get("avg_total_fee"));
		System.out.printf("│ Minimum Fee             │ ₹%-35.2f │%n", feeStructure.get("min_fee"));
		System.out.printf("│ Maximum Fee             │ ₹%-35.2f │%n", feeStructure.get("max_fee"));
		System.out.printf("│ Unique Fee Structures   │ %-35d │%n", feeStructure.get("unique_fee_structures"));
		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");
	}

	public void displayPaymentDelayAnalysis() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║            ⏰ PAYMENT DELAY ANALYSIS                     ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> delayAnalysis = analysisDAO.getPaymentDelayAnalysis();
		
		if (delayAnalysis.isEmpty()) {
			System.out.println("❌ No payment delay data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────┐");
		System.out.println("│ Metric                  │ Value                               │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────┤");
		System.out.printf("│ Total Students          │ %-35d │%n", delayAnalysis.get("total_students"));
		System.out.printf("│ Students with Pending   │ %-35d │%n", delayAnalysis.get("students_with_pending"));
		System.out.printf("│ Students without Pending│ %-35d │%n", delayAnalysis.get("students_without_pending"));
		System.out.printf("│ Average Pending Amount  │ ₹%-35.2f │%n", delayAnalysis.get("avg_pending_amount"));
		System.out.println("└─────────────────────────┴─────────────────────────────────────┘");

		// Calculate and display delay percentage
		int totalStudents = (Integer) delayAnalysis.get("total_students");
		int studentsWithPending = (Integer) delayAnalysis.get("students_with_pending");
		double delayPercentage = totalStudents > 0 ? (studentsWithPending * 100.0) / totalStudents : 0;
		
		System.out.printf("\n⚠️ Students with Pending Payments: %.1f%%%n", delayPercentage);
	}

	// Helper method to generate bar charts
	private String generateBarChart(int value, int maxValue, int maxLength) {
		if (maxValue == 0) return "";
		
		int barLength = (int) ((value * maxLength) / maxValue);
		StringBuilder bar = new StringBuilder();
		
		for (int i = 0; i < barLength; i++) {
			bar.append("█");
		}
		
		return bar.toString();
	}
} 