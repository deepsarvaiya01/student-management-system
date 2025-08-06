package com.sms.main;

import java.sql.SQLException;
import java.util.Scanner;

import com.sms.controller.StudentAnalysisController;
import com.sms.controller.FinancialAnalysisController;
import com.sms.controller.CourseAnalysisController;
import com.sms.controller.SubjectAnalysisController;
import com.sms.utils.InputValidator;

public class AnalysisMain {

	public void show() throws SQLException {
		Scanner scanner = new Scanner(System.in);
		StudentAnalysisController studentController = new StudentAnalysisController();
		FinancialAnalysisController financialController = new FinancialAnalysisController();
		CourseAnalysisController courseController = new CourseAnalysisController();
		SubjectAnalysisController subjectController = new SubjectAnalysisController();
		int choice;

		do {
			System.out.println("\n╔══════════════════════════════════════════════════════╗");
			System.out.println("║                   ANALYSIS DASHBOARD                 ║");
			System.out.println("╠══════════════════════════════════════════════════════╣");
			System.out.println("║ 1. Student Analysis                                  ║");
			System.out.println("║ 2. Financial Analysis                                ║");
			System.out.println("║ 3. Course Analysis                                   ║");
			System.out.println("║ 4. Subject Analysis                                  ║");
			System.out.println("║ 0. Back                                              ║");
			System.out.println("╚══════════════════════════════════════════════════════╝");

			choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-4): ", 4);

			switch (choice) {
			case 1 -> showStudentAnalysisMenu(scanner, studentController);
			case 2 -> showFinancialAnalysisMenu(scanner, financialController);
			case 3 -> showCourseAnalysisMenu(scanner, courseController);
			case 4 -> showSubjectAnalysisMenu(scanner, subjectController);
			case 0 -> System.out.println("Going back to Main Menu...");
			default -> System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 0);
	}

	private void showStudentAnalysisMenu(Scanner scanner, StudentAnalysisController controller) throws SQLException {
		int choice;
		do {
			System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
			System.out.println("║                           ‍    STUDENT ANALYSIS MENU                          ║");
			System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
			System.out.println("║    1. Enrollment Analysis                                                    ║");
			System.out.println("║    ├─ Total students by course                                               ║");
			System.out.println("║    ├─ Enrollment date trends                                                 ║");
			System.out.println("║    ├─ Active vs inactive students                                            ║");
			System.out.println("║    └─ Student distribution by city                                           ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║    2. Student Performance                                                    ║");
			System.out.println("║    ├─ Students with complete fee payment                                     ║");
			System.out.println("║    ├─ Students with pending fees                                             ║");
			System.out.println("║    ├─ Average fee payment per student                                        ║");
			System.out.println("║    └─ Payment completion rate                                                ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║    3. Student Demographics                                                   ║");
			System.out.println("║    ├─ Age distribution                                                       ║");
			System.out.println("║    ├─ City-wise student count                                                ║");
			System.out.println("║    ├─ Course preference by age                                               ║");
			System.out.println("║    └─ Gender analysis                                                        ║");
			System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");


			choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-3): ", 3);

			switch (choice) {
			case 1 -> {
				System.out.println("\n Showing All Enrollment Analysis Results...");
				controller.showTotalStudentsByCourse();
				controller.showEnrollmentDateTrends();
				controller.showActiveVsInactiveStudents();
				controller.showStudentDistributionByCity();
			}
			case 2 -> {
				System.out.println("\n Showing All Student Performance Results...");
				controller.showStudentsWithCompletePayment();
				controller.showStudentsWithPendingFees();
				controller.showAverageFeePaymentPerStudent();
				controller.showPaymentCompletionRate();
			}
			case 3 -> {
				System.out.println("\n Showing All Student Demographics Results...");
				controller.showAgeDistribution();
				controller.showCityWiseStudentCount();
				controller.showCoursePreferenceByAge();
				controller.showGenderAnalysis();
			}
			case 0 -> System.out.println("Going back to Analysis Dashboard...");
			default -> System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 0);
	}

	private void showFinancialAnalysisMenu(Scanner scanner, FinancialAnalysisController controller) throws SQLException {
		int choice;
		do {
			System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
			System.out.println("║                              FINANCIAL ANALYSIS MENU                         ║");
			System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
			System.out.println("║    1. Revenue Analysis                                                       ║");
			System.out.println("║    ├─ Total revenue collected                                                ║");
			System.out.println("║    ├─ Revenue by course                                                      ║");
			System.out.println("║    └─ Average payment per student                                            ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║    2. Payment Analysis                                                       ║");
			System.out.println("║    ├─ Payment completion percentage                                          ║");
			System.out.println("║    ├─ Course-wise payment status                                             ║");
			System.out.println("║    ├─ Recent payment trends                                                  ║");
			System.out.println("║    └─ Outstanding amount by course                                           ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║    3. Financial Metrics                                                      ║");
			System.out.println("║    ├─ Collection efficiency                                                  ║");
			System.out.println("║    ├─ Revenue per course                                                     ║");
			System.out.println("║    ├─ Average fee structure                                                  ║");
			System.out.println("║    └─ Payment delay analysis                                                 ║");
			System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
		

			choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-3): ", 3);

			switch (choice) {
			case 1 -> {
				System.out.println("\n Showing All Revenue Analysis Results...");
				controller.showTotalRevenueCollected();
				controller.showRevenueByCourse();
				controller.showAveragePaymentPerStudent();
			}
			case 2 -> {
				System.out.println("\n Showing All Payment Analysis Results...");
				controller.showPaymentCompletionPercentage();
				controller.showCourseWisePaymentStatus();
				controller.showRecentPaymentTrends();
				controller.showOutstandingAmountByCourse();
			}
			case 3 -> {
				System.out.println("\n Showing All Financial Metrics Results...");
				controller.showCollectionEfficiency();
				controller.showRevenuePerCourse();
				controller.showAverageFeeStructure();
				controller.showPaymentDelayAnalysis();
			}
			case 0 -> System.out.println("Going back to Analysis Dashboard...");
			default -> System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 0);
	}

	private void showCourseAnalysisMenu(Scanner scanner, CourseAnalysisController controller) throws SQLException {
		int choice;
		do {
			System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
			System.out.println("║                              COURSE ANALYSIS MENU                            ║");
			System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
			System.out.println("║    1. Course Popularity                                                      ║");
			System.out.println("║    ├─ Most enrolled courses                                                  ║");
			System.out.println("║    ├─ Course-wise student count                                              ║");
			System.out.println("║    ├─ Subject distribution per course                                        ║");
			System.out.println("║    └─ Course completion status                                               ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║     2. Teacher Analysis                                                      ║");
			System.out.println("║    ├─ Teacher workload (subjects assigned)                                   ║");
			System.out.println("║    ├─ Teachers with max subjects (3)                                         ║");
			System.out.println("║    ├─ Available teachers for assignment                                      ║");
			System.out.println("║    └─ Teacher-subject distribution                                           ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║    3. Academic Structure                                                     ║");
			System.out.println("║    ├─ Mandatory vs elective subjects                                         ║");
			System.out.println("║    ├─ Subject popularity                                                     ║");
			System.out.println("║    ├─ Course fee analysis                                                    ║");
			System.out.println("║    └─ Semester-wise structure                                                ║");
			System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
			

			choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-3): ", 3);

			switch (choice) {
			case 1 -> {
				System.out.println("\n Showing All Course Popularity Results...");
				controller.showMostEnrolledCourses();
				controller.showCourseWiseStudentCount();
				controller.showSubjectDistributionPerCourse();
				controller.showCourseCompletionStatus();
			}
			case 2 -> {
				System.out.println("\n Showing All Teacher Analysis Results...");
				controller.showTeacherWorkload();
				controller.showTeachersWithMaxSubjects();
				controller.showAvailableTeachersForAssignment();
				controller.showTeacherSubjectDistribution();
			}
			case 3 -> {
				System.out.println("\n Showing All Academic Structure Results...");
				controller.showMandatoryVsElectiveSubjects();
				controller.showSubjectPopularity();
				controller.showCourseFeeAnalysis();
				controller.showSemesterWiseStructure();
			}
			case 0 -> System.out.println("Going back to Analysis Dashboard...");
			default -> System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 0);
	}

	private void showSubjectAnalysisMenu(Scanner scanner, SubjectAnalysisController controller) throws SQLException {
		int choice;
		do {
			System.out.println("\n╔══════════════════════════════════════════════════════════════════════════════╗");
			System.out.println("║                              SUBJECT ANALYSIS MENU                           ║");
			System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
			System.out.println("║    1. Subject Distribution                                                   ║");
			System.out.println("║    ├─ Mandatory subjects count                                               ║");
			System.out.println("║    ├─ Elective subjects count                                                ║");
			System.out.println("║    ├─ Most assigned subjects                                                 ║");
			System.out.println("║    └─ Unassigned subjects                                                    ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║     2. Teacher Assignment                                                    ║");
			System.out.println("║    ├─ Subjects with teachers                                                 ║");
			System.out.println("║    ├─ Subjects without teachers                                              ║");
			System.out.println("║    ├─ Teacher workload distribution                                          ║");
			System.out.println("║    └─ Assignment efficiency                                                  ║");
			System.out.println("╠──────────────────────────────────────────────────────────────────────────────╣");
			System.out.println("║    3. Subject Performance                                                    ║");
			System.out.println("║    ├─ Subject enrollment by course                                           ║");
			System.out.println("║    ├─ Subject popularity trends                                              ║");
			System.out.println("║    ├─ Subject-wise student count                                             ║");
			System.out.println("║    └─ Subject completion rates                                               ║");
			System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
		

			choice = InputValidator.getValidMenuChoice(scanner, "👉 Enter your choice (0-3): ", 3);

			switch (choice) {
			case 1 -> {
				System.out.println("\n Showing All Subject Distribution Results...");
				controller.showMandatorySubjectsCount();
				controller.showElectiveSubjectsCount();
				controller.showMostAssignedSubjects();
				controller.showUnassignedSubjects();
			}
			case 2 -> {
				System.out.println("\n Showing All Teacher Assignment Results...");
				controller.showSubjectsWithTeachers();
				controller.showSubjectsWithoutTeachers();
				controller.showTeacherWorkloadDistribution();
				controller.showAssignmentEfficiency();
			}
			case 3 -> {
				System.out.println("\n Showing All Subject Performance Results...");
				controller.showSubjectEnrollmentByCourse();
				controller.showSubjectPopularityTrends();
				controller.showSubjectWiseStudentCount();
				controller.showSubjectCompletionRates();
			}
			case 0 -> System.out.println("Going back to Analysis Dashboard...");
			default -> System.out.println("Invalid choice. Try again.");
			}
		} while (choice != 0);
	}
}
