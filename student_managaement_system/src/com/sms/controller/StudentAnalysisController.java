package com.sms.controller;

import java.sql.SQLException;

import com.sms.service.StudentAnalysisService;

public class StudentAnalysisController {
	private StudentAnalysisService analysisService;

	public StudentAnalysisController() throws SQLException {
		this.analysisService = new StudentAnalysisService();
	}

	// Enrollment Analysis Methods
	public void showTotalStudentsByCourse() {
		try {
			analysisService.displayTotalStudentsByCourse();
		} catch (SQLException e) {
			System.out.println("Error displaying total students by course: " + e.getMessage());
		}
	}

	public void showEnrollmentDateTrends() {
		try {
			analysisService.displayEnrollmentDateTrends();
		} catch (SQLException e) {
			System.out.println("Error displaying enrollment date trends: " + e.getMessage());
		}
	}

	public void showActiveVsInactiveStudents() {
		try {
			analysisService.displayActiveVsInactiveStudents();
		} catch (SQLException e) {
			System.out.println("Error displaying active vs inactive students: " + e.getMessage());
		}
	}

	public void showStudentDistributionByCity() {
		try {
			analysisService.displayStudentDistributionByCity();
		} catch (SQLException e) {
			System.out.println("Error displaying student distribution by city: " + e.getMessage());
		}
	}

	// Student Performance Methods
	public void showStudentsWithCompletePayment() {
		try {
			analysisService.displayStudentsWithCompletePayment();
		} catch (SQLException e) {
			System.out.println("Error displaying students with complete payment: " + e.getMessage());
		}
	}

	public void showStudentsWithPendingFees() {
		try {
			analysisService.displayStudentsWithPendingFees();
		} catch (SQLException e) {
			System.out.println("Error displaying students with pending fees: " + e.getMessage());
		}
	}

	public void showAverageFeePaymentPerStudent() {
		try {
			analysisService.displayAverageFeePaymentPerStudent();
		} catch (SQLException e) {
			System.out.println("Error displaying average fee payment per student: " + e.getMessage());
		}
	}

	public void showPaymentCompletionRate() {
		try {
			analysisService.displayPaymentCompletionRate();
		} catch (SQLException e) {
			System.out.println("Error displaying payment completion rate: " + e.getMessage());
		}
	}

	// Student Demographics Methods
	public void showAgeDistribution() {
		try {
			analysisService.displayAgeDistribution();
		} catch (SQLException e) {
			System.out.println("Error displaying age distribution: " + e.getMessage());
		}
	}

	public void showCityWiseStudentCount() {
		try {
			analysisService.displayCityWiseStudentCount();
		} catch (SQLException e) {
			System.out.println("Error displaying city-wise student count: " + e.getMessage());
		}
	}

	public void showCoursePreferenceByAge() {
		try {
			analysisService.displayCoursePreferenceByAge();
		} catch (SQLException e) {
			System.out.println("Error displaying course preference by age: " + e.getMessage());
		}
	}

	public void showGenderAnalysis() {
		try {
			analysisService.displayGenderAnalysis();
		} catch (SQLException e) {
			System.out.println("Error displaying gender analysis: " + e.getMessage());
		}
	}
} 