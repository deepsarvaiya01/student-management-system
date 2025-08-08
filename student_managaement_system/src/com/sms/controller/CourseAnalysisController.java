package com.sms.controller;

import java.sql.SQLException;

import com.sms.service.CourseAnalysisService;

public class CourseAnalysisController {
	private CourseAnalysisService analysisService;

	public CourseAnalysisController() throws SQLException {
		this.analysisService = new CourseAnalysisService();
	}

	// Course Popularity Methods
	public void showMostEnrolledCourses() {
		try {
			analysisService.displayMostEnrolledCourses();
		} catch (SQLException e) {
			System.out.println("Error displaying most enrolled courses: " + e.getMessage());
		}
	}

	public void showCourseWiseStudentCount() {
		try {
			analysisService.displayCourseWiseStudentCount();
		} catch (SQLException e) {
			System.out.println("Error displaying course-wise student count: " + e.getMessage());
		}
	}

	public void showSubjectDistributionPerCourse() {
		try {
			analysisService.displaySubjectDistributionPerCourse();
		} catch (SQLException e) {
			System.out.println("Error displaying subject distribution per course: " + e.getMessage());
		}
	}

	public void showCourseCompletionStatus() {
		try {
			analysisService.displayCourseCompletionStatus();
		} catch (SQLException e) {
			System.out.println("Error displaying course completion status: " + e.getMessage());
		}
	}

	// Teacher Analysis Methods
	public void showTeacherWorkload() {
		try {
			analysisService.displayTeacherWorkload();
		} catch (SQLException e) {
			System.out.println("❌ Error displaying teacher workload: " + e.getMessage());
		}
	}

	public void showTeachersWithMaxSubjects() {
		try {
			analysisService.displayTeachersWithMaxSubjects();
		} catch (SQLException e) {
			System.out.println("Error displaying teachers with max subjects: " + e.getMessage());
		}
	}

	public void showAvailableTeachersForAssignment() {
		try {
			analysisService.displayAvailableTeachersForAssignment();
		} catch (SQLException e) {
			System.out.println("Error displaying available teachers for assignment: " + e.getMessage());
		}
	}

	public void showTeacherSubjectDistribution() {
		try {
			analysisService.displayTeacherSubjectDistribution();
		} catch (SQLException e) {
			System.out.println("Error displaying teacher-subject distribution: " + e.getMessage());
		}
	}

	// Academic Structure Methods
	public void showMandatoryVsElectiveSubjects() {
		try {
			analysisService.displayMandatoryVsElectiveSubjects();
		} catch (SQLException e) {
			System.out.println("Error displaying mandatory vs elective subjects: " + e.getMessage());
		}
	}

	public void showSubjectPopularity() {
		try {
			analysisService.displaySubjectPopularity();
		} catch (SQLException e) {
			System.out.println("Error displaying subject popularity: " + e.getMessage());
		}
	}

	public void showCourseFeeAnalysis() {
		try {
			analysisService.displayCourseFeeAnalysis();
		} catch (SQLException e) {
			System.out.println("Error displaying course fee analysis: " + e.getMessage());
		}
	}

	public void showSemesterWiseStructure() {
		try {
			analysisService.displaySemesterWiseStructure();
		} catch (SQLException e) {
			System.out.println("Error displaying semester-wise structure: " + e.getMessage());
		}
	}
} 