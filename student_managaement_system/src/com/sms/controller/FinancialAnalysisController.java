package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.service.FinancialAnalysisService;

public class FinancialAnalysisController {
	private FinancialAnalysisService analysisService;

	public FinancialAnalysisController() throws SQLException {
		this.analysisService = new FinancialAnalysisService();
	}

	// Revenue Analysis Methods
	public void showTotalRevenueCollected() {
		try {
			analysisService.displayTotalRevenueCollected();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying total revenue collected: " + e.getMessage());
		}
	}

	public void showRevenueByCourse() {
		try {
			analysisService.displayRevenueByCourse();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying revenue by course: " + e.getMessage());
		}
	}

	public void showAveragePaymentPerStudent() {
		try {
			analysisService.displayAveragePaymentPerStudent();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying average payment per student: " + e.getMessage());
		}
	}

	// Payment Analysis Methods
	public void showPaymentCompletionPercentage() {
		try {
			analysisService.displayPaymentCompletionPercentage();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying payment completion percentage: " + e.getMessage());
		}
	}

	public void showCourseWisePaymentStatus() {
		try {
			analysisService.displayCourseWisePaymentStatus();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying course-wise payment status: " + e.getMessage());
		}
	}

	public void showRecentPaymentTrends() {
		try {
			analysisService.displayRecentPaymentTrends();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying recent payment trends: " + e.getMessage());
		}
	}

	public void showOutstandingAmountByCourse() {
		try {
			analysisService.displayOutstandingAmountByCourse();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying outstanding amount by course: " + e.getMessage());
		}
	}

	// Financial Metrics Methods
	public void showCollectionEfficiency() {
		try {
			analysisService.displayCollectionEfficiency();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying collection efficiency: " + e.getMessage());
		}
	}

	public void showRevenuePerCourse() {
		try {
			analysisService.displayRevenuePerCourse();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying revenue per course: " + e.getMessage());
		}
	}

	public void showAverageFeeStructure() {
		try {
			analysisService.displayAverageFeeStructure();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying average fee structure: " + e.getMessage());
		}
	}

	public void showPaymentDelayAnalysis() {
		try {
			analysisService.displayPaymentDelayAnalysis();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying payment delay analysis: " + e.getMessage());
		}
	}
} 