package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.dao.SubjectAnalysisDAO;
import com.sms.utils.HelperUtils;

public class SubjectAnalysisService {
	private SubjectAnalysisDAO analysisDAO;

	public SubjectAnalysisService() throws SQLException {
		this.analysisDAO = new SubjectAnalysisDAO();
	}

	// Subject Distribution Methods
	public void displayMandatorySubjectsCount() throws SQLException {
		System.out.println("\n╔════════════════════════════════════════════════╗");
		System.out.println("║             MANDATORY SUBJECTS COUNT           ║");
		System.out.println("╚════════════════════════════════════════════════╝");

		Map<String, Integer> mandatorySubjects = analysisDAO.getMandatorySubjectsCount();

		if (mandatorySubjects.isEmpty()) {
			System.out.println("No mandatory subjects found.");
			return;
		}

		System.out.println("\n┌────────────────────────────────────┐");
		System.out.println("│ Mandatory Subjects                 │");
		System.out.println("├────────────────────────────────────┤");

		for (String subjectName : mandatorySubjects.keySet()) {
			System.out.printf("│ • %-32s │%n", HelperUtils.truncate(subjectName, 30));
		}

		System.out.println("└────────────────────────────────────┘");
		System.out.printf(" Total Mandatory Subjects: %d%n", mandatorySubjects.size());
	}

	public void displayElectiveSubjectsCount() throws SQLException {
		System.out.println("\n╔════════════════════════════════════════════════╗");
		System.out.println("║              ELECTIVE SUBJECTS COUNT           ║");
		System.out.println("╚════════════════════════════════════════════════╝");

		Map<String, Integer> electiveSubjects = analysisDAO.getElectiveSubjectsCount();

		if (electiveSubjects.isEmpty()) {
			System.out.println("No elective subjects found.");
			return;
		}

		System.out.println("\n┌────────────────────────────────────┐");
		System.out.println("│ Elective Subjects                  │");
		System.out.println("├────────────────────────────────────┤");

		for (String subjectName : electiveSubjects.keySet()) {
			System.out.printf("│ • %-32s │%n", HelperUtils.truncate(subjectName, 30));
		}

		System.out.println("└────────────────────────────────────┘");
		System.out.printf(" Total Elective Subjects: %d%n", electiveSubjects.size());
	}

	public void displayMostAssignedSubjects() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║               MOST ASSIGNED SUBJECTS                     ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> assignedSubjects = analysisDAO.getMostAssignedSubjects();

		if (assignedSubjects.isEmpty()) {
			System.out.println(" No assigned subjects found.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Subject Name            │ Assignment Count│ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalAssignments = assignedSubjects.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : assignedSubjects.entrySet()) {
			String subjectName = HelperUtils.truncate(entry.getKey(), 23);
			int assignmentCount = entry.getValue();
			double percentage = totalAssignments > 0 ? (assignmentCount * 100.0) / totalAssignments : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", subjectName, assignmentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n Total Assignments: %d%n", totalAssignments);
	}

	public void displayUnassignedSubjects() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║               UNASSIGNED SUBJECTS                        ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		List<String> unassignedSubjects = analysisDAO.getUnassignedSubjects();

		if (unassignedSubjects.isEmpty()) {
			System.out.println(" All subjects are assigned to courses.");
			return;
		}

		System.out.println("\n┌────────────────────────────────────┐");
		System.out.println("│ Unassigned Subjects                │");
		System.out.println("├────────────────────────────────────┤");

		for (String subject : unassignedSubjects) {
			System.out.printf("│ • %-32s │%n", HelperUtils.truncate(subject, 30));
		}

		System.out.println("└────────────────────────────────────┘");
		System.out.printf("Total Unassigned Subjects: %d%n", unassignedSubjects.size());
	}

	// Teacher Assignment Methods
	public void displaySubjectsWithTeachers() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                SUBJECTS WITH TEACHERS                    ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, String> subjectsWithTeachers = analysisDAO.getSubjectsWithTeachers();

		if (subjectsWithTeachers.isEmpty()) {
			System.out.println(" No subjects with teachers found.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────────────────────────────────────┐");
		System.out.println("│ Subject Name            │ Assigned Teacher                                │");
		System.out.println("├─────────────────────────┼─────────────────────────────────────────────────┤");

		for (Map.Entry<String, String> entry : subjectsWithTeachers.entrySet()) {
			String subjectInfo = entry.getKey();
			String teacherName = entry.getValue();

			// Truncate if too long
			if (subjectInfo.length() > 23) {
				subjectInfo = subjectInfo.substring(0, 20) + "...";
			}
			if (teacherName.length() > 47) {
				teacherName = teacherName.substring(0, 44) + "...";
			}

			System.out.printf("│ %-23s │ %-47s │%n", subjectInfo, teacherName);
		}

		System.out.println("└─────────────────────────┴─────────────────────────────────────────────────┘");
		System.out.printf("\n Total Subjects with Teachers: %d%n", subjectsWithTeachers.size());
	}

	public void displaySubjectsWithoutTeachers() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                SUBJECTS WITHOUT TEACHERS                 ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		List<String> subjectsWithoutTeachers = analysisDAO.getSubjectsWithoutTeachers();

		if (subjectsWithoutTeachers.isEmpty()) {
			System.out.println("All subjects have teachers assigned.");
			return;
		}

		System.out.println("\n┌─────────────────────────────────────────────────────────────────────────────┐");
		System.out.println("│ Subjects Without Teachers                                                   │");
		System.out.println("├─────────────────────────────────────────────────────────────────────────────┤");

		for (String subject : subjectsWithoutTeachers) {
			System.out.printf("│ • %-65s │%n", subject);
		}

		System.out.println("└─────────────────────────────────────────────────────────────────────────────┘");
		System.out.printf("Total Subjects Without Teachers: %d%n", subjectsWithoutTeachers.size());
	}

	public void displayTeacherWorkloadDistribution() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                TEACHER WORKLOAD DISTRIBUTION             ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> teacherWorkload = analysisDAO.getTeacherWorkloadDistribution();

		if (teacherWorkload.isEmpty()) {
			System.out.println("No teacher workload data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Teacher Name            │ Subject Count   │ Workload Status │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		for (Map.Entry<String, Integer> entry : teacherWorkload.entrySet()) {
			String teacherName = entry.getKey();
			int subjectCount = entry.getValue();
			String workloadStatus = subjectCount >= 3 ? "High" : subjectCount >= 2 ? "Medium" : "Low";

			System.out.printf("│ %-23s │ %-15d │ %-15s │%n", teacherName, subjectCount, workloadStatus);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
	}

	public void displayAssignmentEfficiency() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                ASSIGNMENT EFFICIENCY                     ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> assignmentEfficiency = analysisDAO.getAssignmentEfficiency();

		if (assignmentEfficiency.isEmpty()) {
			System.out.println(" No assignment efficiency data available.");
			return;
		}

		int totalSubjects = (Integer) assignmentEfficiency.get("total_subjects");
		int assignedSubjects = (Integer) assignmentEfficiency.get("assigned_subjects");
		int unassignedSubjects = (Integer) assignmentEfficiency.get("unassigned_subjects");
		double subjectAssignmentRate = (Double) assignmentEfficiency.get("subject_assignment_rate");
		int totalTeachers = (Integer) assignmentEfficiency.get("total_teachers");
		int teachersWithSubjects = (Integer) assignmentEfficiency.get("teachers_with_subjects");
		double teacherUtilizationRate = (Double) assignmentEfficiency.get("teacher_utilization_rate");

		System.out.println("\n┌────────────────────────────────────────────────────────────┐");
		System.out.println("│ Assignment Efficiency Overview                             │");
		System.out.println("├────────────────────────────────────────────────────────────┤");
		System.out.printf("│ Total Subjects:              %-29d │%n", totalSubjects);
		System.out.printf("│ Assigned Subjects:            %-28d │%n", assignedSubjects);
		System.out.printf("│ Unassigned Subjects:          %-28d │%n", unassignedSubjects);
		System.out.printf("│ Subject Assignment Rate:      %-27.1f%% │%n", subjectAssignmentRate);
		System.out.println("├────────────────────────────────────────────────────────────┤");
		System.out.printf("│ Total Teachers:               %-28d │%n", totalTeachers);
		System.out.printf("│ Teachers with Subjects:       %-28d │%n", teachersWithSubjects);
		System.out.printf("│ Teacher Utilization Rate:     %-27.1f%% │%n", teacherUtilizationRate);
		System.out.println("└────────────────────────────────────────────────────────────┘");
	}

	// Subject Performance Methods
	public void displaySubjectEnrollmentByCourse() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              SUBJECT ENROLLMENT BY COURSE                ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Map<String, Integer>> subjectEnrollmentByCourse = analysisDAO.getSubjectEnrollmentByCourse();

		if (subjectEnrollmentByCourse.isEmpty()) {
			System.out.println(" No subject enrollment data available.");
			return;
		}

		System.out
				.println("\n┌─────────────────────────┬─────────────────┬─────────────────────────┬─────────────────┐");
		System.out.println("│ Course Name             │ Total Subjects  │ Subject Name            │ Enrollment Count│");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────────────┼─────────────────┤");

		for (Map.Entry<String, Map<String, Integer>> courseEntry : subjectEnrollmentByCourse.entrySet()) {
			String courseName = courseEntry.getKey();
			Map<String, Integer> subjects = courseEntry.getValue();

			int totalSubjects = subjects.size();
			boolean firstRow = true;

			for (Map.Entry<String, Integer> subjectEntry : subjects.entrySet()) {
				String subjectName = subjectEntry.getKey();
				int enrollmentCount = subjectEntry.getValue();

				// Truncate if too long
				String courseDisplay = courseName.length() > 23 ? courseName.substring(0, 20) + "..." : courseName;
				String subjectDisplay = subjectName.length() > 23 ? subjectName.substring(0, 20) + "..." : subjectName;

				if (firstRow) {
					System.out.printf("│ %-23s │ %-15d │ %-23s │ %-15d │%n", courseDisplay, totalSubjects,
							subjectDisplay, enrollmentCount);
					firstRow = false;
				} else {
					System.out.printf("│ %-23s │ %-15s │ %-23s │ %-15d │%n", "", "", subjectDisplay, enrollmentCount);
				}
			}

			// Separator between courses
			System.out.println(
					"├─────────────────────────┼─────────────────┼─────────────────────────┼─────────────────┤");
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────────────┴─────────────────┘");

	}

	public void displaySubjectPopularityTrends() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║               SUBJECT POPULARITY TRENDS                  ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> subjectPopularity = analysisDAO.getSubjectPopularityTrends();

		if (subjectPopularity.isEmpty()) {
			System.out.println(" No subject popularity data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Subject Name            │ Enrollment Count│ Percentage      │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		int totalEnrollments = subjectPopularity.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : subjectPopularity.entrySet()) {
			String subjectName = entry.getKey();
			int enrollmentCount = entry.getValue();
			double percentage = totalEnrollments > 0 ? (enrollmentCount * 100.0) / totalEnrollments : 0;

			// Truncate if longer than 23 chars
			String displayName = subjectName.length() > 23 ? subjectName.substring(0, 20) + "..." : subjectName;

			System.out.printf("│ %-23s │ %-15d │ %-14.1f%% │%n", displayName, enrollmentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
		System.out.printf("\n🎯 Total Enrollments: %d%n", totalEnrollments);
	}

	public void displaySubjectWiseStudentCount() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              SUBJECT-WISE STUDENT COUNT                  ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> subjectStudentCount = analysisDAO.getSubjectWiseStudentCount();

		if (subjectStudentCount.isEmpty()) {
			System.out.println("No subject student count data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Subject Name            │ Student Count   │ Percentage      │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		int totalStudents = subjectStudentCount.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : subjectStudentCount.entrySet()) {
			String subjectName = entry.getKey();
			int studentCount = entry.getValue();
			double percentage = totalStudents > 0 ? (studentCount * 100.0) / totalStudents : 0;

			// Truncate subject names longer than 23 characters
			String displayName = subjectName.length() > 23 ? subjectName.substring(0, 20) + "..." : subjectName;

			System.out.printf("│ %-23s │ %-15d │ %-14.1f%% │%n", displayName, studentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
		System.out.printf("\nTotal Students: %d%n", totalStudents);

	}

	public void displaySubjectCompletionRates() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║              SUBJECT COMPLETION RATES                    ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> subjectCompletionRates = analysisDAO.getSubjectCompletionRates();

		if (subjectCompletionRates.isEmpty()) {
			System.out.println("No subject completion data available.");
			return;
		}

		System.out.println(
				"\n┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┬─────────────────┐");
		System.out.println(
				"│ Subject Name            │ Total Enrolled  │ Completed       │ In Progress     │ Completion %    │");
		System.out.println(
				"├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┼─────────────────┤");

		for (Map.Entry<String, Object> entry : subjectCompletionRates.entrySet()) {
			String subjectName = entry.getKey();
			@SuppressWarnings("unchecked")
			Map<String, Object> completionData = (Map<String, Object>) entry.getValue();

			int totalEnrolled = (Integer) completionData.get("total_enrolled");
			int completed = (Integer) completionData.get("completed");
			int inProgress = (Integer) completionData.get("in_progress");
			double completionRate = (Double) completionData.get("completion_rate");

			// Truncate subject name if longer than 23 characters
			String displayName = subjectName.length() > 23 ? subjectName.substring(0, 20) + "..." : subjectName;

			System.out.printf("│ %-23s │ %-15d │ %-15d │ %-15d │ %-14.1f%% │%n", displayName, totalEnrolled, completed,
					inProgress, completionRate);
		}

		System.out.println(
				"└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┴─────────────────┘");

	}
}