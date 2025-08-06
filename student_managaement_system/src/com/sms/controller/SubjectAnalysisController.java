package com.sms.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.service.SubjectAnalysisService;

public class SubjectAnalysisController {
	private SubjectAnalysisService analysisService;

	public SubjectAnalysisController() throws SQLException {
		this.analysisService = new SubjectAnalysisService();
	}

	// Subject Distribution Methods
	public void showMandatorySubjectsCount() {
		try {
			analysisService.displayMandatorySubjectsCount();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying mandatory subjects count: " + e.getMessage());
		}
	}

	public void showElectiveSubjectsCount() {
		try {
			analysisService.displayElectiveSubjectsCount();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying elective subjects count: " + e.getMessage());
		}
	}

	public void showMostAssignedSubjects() {
		try {
			analysisService.displayMostAssignedSubjects();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying most assigned subjects: " + e.getMessage());
		}
	}

	public void showUnassignedSubjects() {
		try {
			analysisService.displayUnassignedSubjects();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying unassigned subjects: " + e.getMessage());
		}
	}

	// Teacher Assignment Methods
	public void showSubjectsWithTeachers() {
		try {
			analysisService.displaySubjectsWithTeachers();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying subjects with teachers: " + e.getMessage());
		}
	}

	public void showSubjectsWithoutTeachers() {
		try {
			analysisService.displaySubjectsWithoutTeachers();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying subjects without teachers: " + e.getMessage());
		}
	}

	public void showTeacherWorkloadDistribution() {
		try {
			analysisService.displayTeacherWorkloadDistribution();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying teacher workload distribution: " + e.getMessage());
		}
	}

	public void showAssignmentEfficiency() {
		try {
			analysisService.displayAssignmentEfficiency();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying assignment efficiency: " + e.getMessage());
		}
	}

	// Subject Performance Methods
	public void showSubjectEnrollmentByCourse() {
		try {
			analysisService.displaySubjectEnrollmentByCourse();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying subject enrollment by course: " + e.getMessage());
		}
	}

	public void showSubjectPopularityTrends() {
		try {
			analysisService.displaySubjectPopularityTrends();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying subject popularity trends: " + e.getMessage());
		}
	}

	public void showSubjectWiseStudentCount() {
		try {
			analysisService.displaySubjectWiseStudentCount();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying subject-wise student count: " + e.getMessage());
		}
	}

	public void showSubjectCompletionRates() {
		try {
			analysisService.displaySubjectCompletionRates();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying subject completion rates: " + e.getMessage());
		}
	}
} 