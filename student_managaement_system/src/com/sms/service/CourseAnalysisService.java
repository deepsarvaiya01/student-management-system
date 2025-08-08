package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.dao.CourseAnalysisDAO;
import com.sms.utils.HelperUtils;

public class CourseAnalysisService {
	private CourseAnalysisDAO analysisDAO;

	public CourseAnalysisService() throws SQLException {
		this.analysisDAO = new CourseAnalysisDAO();
	}

	// Course Popularity Methods
	public void displayMostEnrolledCourses() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   MOST ENROLLED COURSES                  ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> enrolledCourses = analysisDAO.getMostEnrolledCourses();
		
		if (enrolledCourses.isEmpty()) {
			System.out.println(" No course enrollment data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Course Name             │ Enrollment Count│ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalEnrollments = enrolledCourses.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : enrolledCourses.entrySet()) {
			String courseName = entry.getKey();
			int enrollmentCount = entry.getValue();
			double percentage = totalEnrollments > 0 ? (enrollmentCount * 100.0) / totalEnrollments : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", 
				courseName, enrollmentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n Total Enrollments: %d%n", totalEnrollments);
	}

	public void displayCourseWiseStudentCount() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   COURSE-WISE STUDENT COUNT              ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> courseStudentCount = analysisDAO.getCourseWiseStudentCount();
		
		if (courseStudentCount.isEmpty()) {
			System.out.println(" No course student count data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Course Name             │ Student Count   │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalStudents = courseStudentCount.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : courseStudentCount.entrySet()) {
			String courseName = entry.getKey();
			int studentCount = entry.getValue();
			double percentage = totalStudents > 0 ? (studentCount * 100.0) / totalStudents : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", 
				courseName, studentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n Total Students: %d%n", totalStudents);
	}

public void displaySubjectDistributionPerCourse() throws SQLException {
    System.out.println("\n╔══════════════════════════════════════════════════════════╗");
    System.out.println("║               SUBJECT DISTRIBUTION PER COURSE            ║");
    System.out.println("╚══════════════════════════════════════════════════════════╝");

    Map<String, Map<String, Integer>> subjectDistribution = analysisDAO.getSubjectDistributionPerCourse();
    
    if (subjectDistribution.isEmpty()) {
        System.out.println(" No subject distribution data available.");
        return;
    }

    for (Map.Entry<String, Map<String, Integer>> courseEntry : subjectDistribution.entrySet()) {
        String courseName = courseEntry.getKey();
        Map<String, Integer> subjects = courseEntry.getValue();
        
        System.out.println("\n┌───────────────────────────────────────────────────────────────┐");
        System.out.printf("│ Course: %-53s │%n", HelperUtils.truncate(courseName, 54));
        System.out.println("├─────────────────────────┬─────────────────────────────────────┤");
        System.out.println("│ Subject Name            │ Type                                │");
        System.out.println("├─────────────────────────┼─────────────────────────────────────┤");

        for (Map.Entry<String, Integer> subjectEntry : subjects.entrySet()) {
            String subjectInfo = subjectEntry.getKey();
            
            // Parse subject name and type from the combined string
            String subjectName = subjectInfo.contains(" (") 
                ? subjectInfo.substring(0, subjectInfo.indexOf(" (")) 
                : subjectInfo;
            String subjectType = subjectInfo.contains(" (") 
                ? subjectInfo.substring(subjectInfo.indexOf("(") + 1, subjectInfo.indexOf(")")) 
                : "N/A";

            // Truncate both fields for table alignment
            subjectName = HelperUtils.truncate(subjectName, 23);
            subjectType = HelperUtils.truncate(subjectType, 35);

            System.out.printf("│ %-23s │ %-35s │%n", subjectName, subjectType);
        }

        System.out.println("└─────────────────────────┴─────────────────────────────────────┘");
        System.out.printf(" Total Subjects: %d%n", subjects.size());
    }
}

	@SuppressWarnings("unchecked")
	public void displayCourseCompletionStatus() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   COURSE COMPLETION STATUS               ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> completionStatus = analysisDAO.getCourseCompletionStatus();
		
		if (completionStatus.isEmpty()) {
			System.out.println(" No course completion data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Course Name             │ Total Enrolled  │ Completed       │ In Progress     │ Completion %     │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼─────────────────┼──────────────────┤");

		for (Map.Entry<String, Object> entry : completionStatus.entrySet()) {
			String courseName = entry.getKey();
			Map<String, Object> status = (Map<String, Object>) entry.getValue();
			
			int totalEnrolled = (Integer) status.get("total_enrolled");
			int completed = (Integer) status.get("completed");
			int inProgress = (Integer) status.get("in_progress");
			double completionPercentage = (Double) status.get("completion_percentage");

			System.out.printf("│ %-23s │ %-15d │ %-15d │ %-15d │ %-15.1f%% │%n", 
				courseName, totalEnrolled, completed, inProgress, completionPercentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴─────────────────┴──────────────────┘");
	}

	// Teacher Analysis Methods
	public void displayTeacherWorkload() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                    TEACHER WORKLOAD                      ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> teacherWorkload = analysisDAO.getTeacherWorkload();
		
		if (teacherWorkload.isEmpty()) {
			System.out.println(" No teacher workload data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┐");
		System.out.println("│ Teacher Name            │ Subject Count   │ Workload Status │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┤");

		for (Map.Entry<String, Integer> entry : teacherWorkload.entrySet()) {
			String teacherName = entry.getKey();
			int subjectCount = entry.getValue();
			String workloadStatus = subjectCount >= 3 ? "High" : subjectCount >= 2 ? "Medium" : "Low";

			System.out.printf("│ %-23s │ %-15d │ %-15s │%n", 
				teacherName, subjectCount, workloadStatus);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┘");
	}

	public void displayTeachersWithMaxSubjects() throws SQLException {
	    System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
	    System.out.println("║                     TEACHERS WITH MAX SUBJECTS                       ║");
	    System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

	    List<String> maxSubjectTeachers = analysisDAO.getTeachersWithMaxSubjects();
	    
	    if (maxSubjectTeachers.isEmpty()) {
	        System.out.println(" No teachers have 3 or more subjects assigned.");
	        return;
	    }

	    System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
	    System.out.println("│ Teachers with 3+ Subjects Assigned                                 │");
	    System.out.println("├────────────────────────────────────────────────────────────────────┤");

	    for (String teacher : maxSubjectTeachers) {
	        String name = HelperUtils.truncate(teacher, 66);
	        System.out.printf("│ %-66s │%n", name);
	    }

	    System.out.println("└────────────────────────────────────────────────────────────────────┘");
	    System.out.printf("‍ Total Teachers with Max Subjects: %d%n", maxSubjectTeachers.size());
	}

	public void displayAvailableTeachersForAssignment() throws SQLException {
	    System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
	    System.out.println("║               AVAILABLE TEACHERS FOR ASSIGNMENT                      ║");
	    System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

	    List<String> availableTeachers = analysisDAO.getAvailableTeachersForAssignment();
	    
	    if (availableTeachers.isEmpty()) {
	        System.out.println(" No available teachers for assignment.");
	        return;
	    }

	    System.out.println("\n┌────────────────────────────────────────────────────────────────────┐");
	    System.out.println("│ Available Teachers (Less than 3 subjects)                          │");
	    System.out.println("├────────────────────────────────────────────────────────────────────┤");

	    for (String teacher : availableTeachers) {
	        String name = HelperUtils.truncate(teacher, 66);
	        System.out.printf("│ %-66s │%n", name);
	    }

	    System.out.println("└────────────────────────────────────────────────────────────────────┘");
	    System.out.printf(" Total Available Teachers: %d%n", availableTeachers.size());
	}

public void displayTeacherSubjectDistribution() throws SQLException {
    System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════════╗");
    System.out.println("║                          TEACHER-SUBJECT DISTRIBUTION                              ║");
    System.out.println("╚════════════════════════════════════════════════════════════════════════════════════╝");

    Map<String, List<String>> teacherSubjectDistribution = analysisDAO.getTeacherSubjectDistribution();
    
    if (teacherSubjectDistribution.isEmpty()) {
        System.out.println(" No teacher-subject distribution data available.");
        return;
    }

    // Table Header
    System.out.println("\n┌──────────────────────────┬──────────────────────────────┬────────────────────┬───────────────┐");
    System.out.println("│ Teacher Name             │ Subject Name                 │ Subject Type       │ Total Count   │");
    System.out.println("├──────────────────────────┼──────────────────────────────┼────────────────────┼───────────────┤");

    boolean firstTeacher = true;

    for (Map.Entry<String, List<String>> entry : teacherSubjectDistribution.entrySet()) {
        String teacherName = HelperUtils.truncate(entry.getKey(), 24);
        List<String> subjects = entry.getValue();
        int totalCount = subjects.size();

        // Add underscore separator before new teacher (except first)
        if (!firstTeacher) {
            System.out.println("│__________________________│______________________________│____________________│_______________│");
        }
        firstTeacher = false;

        if (subjects.isEmpty()) {
            System.out.printf("│ %-24s │ %-28s │ %-16s │ %-13d │%n",
                    teacherName, "No subjects assigned", "N/A", totalCount);
        } else {
            boolean firstRow = true;
            for (String subjectInfo : subjects) {
                String subjectName = subjectInfo.contains(" (")
                        ? subjectInfo.substring(0, subjectInfo.indexOf(" ("))
                        : subjectInfo;
                String subjectType = subjectInfo.contains(" (")
                        ? subjectInfo.substring(subjectInfo.indexOf("(") + 1, subjectInfo.indexOf(")"))
                        : "N/A";

                String truncatedSubject = HelperUtils.truncate(subjectName, 28);
                String truncatedType = HelperUtils.truncate(subjectType, 20);

                if (firstRow) {
                    System.out.printf("│ %-24s │ %-28s │ %-18s │ %-13d │%n",
                            teacherName, truncatedSubject, truncatedType, totalCount);
                    firstRow = false;
                } else {
                    System.out.printf("│ %-24s │ %-28s │ %-18s │ %-13s │%n",
                            "", truncatedSubject, truncatedType, "");
                }

            }
        }
    }

    System.out.println("└──────────────────────────┴──────────────────────────────┴────────────────────┴───────────────┘");
}


	// Academic Structure Methods
	public void displayMandatoryVsElectiveSubjects() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║               MANDATORY VS ELECTIVE SUBJECTS             ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> subjectTypeCount = analysisDAO.getMandatoryVsElectiveSubjects();
		
		if (subjectTypeCount.isEmpty()) {
			System.out.println(" No subject type data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Subject Type            │ Count           │ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalSubjects = subjectTypeCount.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : subjectTypeCount.entrySet()) {
			String subjectType = entry.getKey();
			int count = entry.getValue();
			double percentage = totalSubjects > 0 ? (count * 100.0) / totalSubjects : 0;

			System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", 
				subjectType, count, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\n📚 Total Subjects: %d%n", totalSubjects);
	}

	public void displaySubjectPopularity() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   SUBJECT POPULARITY                     ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Integer> subjectPopularity = analysisDAO.getSubjectPopularity();
		
		if (subjectPopularity.isEmpty()) {
			System.out.println(" No subject popularity data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬──────────────────┐");
		System.out.println("│ Subject Name            │ Enrollment Count│ Percentage       │");
		System.out.println("├─────────────────────────┼─────────────────┼──────────────────┤");

		int totalEnrollments = subjectPopularity.values().stream().mapToInt(Integer::intValue).sum();

		for (Map.Entry<String, Integer> entry : subjectPopularity.entrySet()) {
		    String subjectName = HelperUtils.truncate(entry.getKey(), 23); // limit to column width
		    int enrollmentCount = entry.getValue();
		    double percentage = totalEnrollments > 0 ? (enrollmentCount * 100.0) / totalEnrollments : 0;

		    System.out.printf("│ %-23s │ %-15d │ %-15.1f%% │%n", 
		        subjectName, enrollmentCount, percentage);
		}

		System.out.println("└─────────────────────────┴─────────────────┴──────────────────┘");
		System.out.printf("\nTotal Enrollments: %d%n", totalEnrollments);
	}

	@SuppressWarnings("unchecked")
	public void displayCourseFeeAnalysis() throws SQLException {
		System.out.println("\n╔══════════════════════════════════════════════════════════╗");
		System.out.println("║                   COURSE FEE ANALYSIS                    ║");
		System.out.println("╚══════════════════════════════════════════════════════════╝");

		Map<String, Object> feeAnalysis = analysisDAO.getCourseFeeAnalysis();
		
		if (feeAnalysis.isEmpty()) {
			System.out.println(" No course fee data available.");
			return;
		}

		System.out.println("\n┌─────────────────────────┬─────────────────┬─────────────────┬──────────────────────┐");
		System.out.println("│ Course Name             │ Total Fee       │ Avg Paid        │ Fee Range            │");
		System.out.println("├─────────────────────────┼─────────────────┼─────────────────┼──────────────────────┤");

		for (Map.Entry<String, Object> entry : feeAnalysis.entrySet()) {
			String courseName = entry.getKey();
			Map<String, Object> feeInfo = (Map<String, Object>) entry.getValue();
			
			double totalFee = ((Number) feeInfo.get("total_fee")).doubleValue();
			double avgPaid = feeInfo.get("avg_paid") != null ? ((Number) feeInfo.get("avg_paid")).doubleValue() : 0.0;
			double minFee = feeInfo.get("min_fee") != null ? ((Number) feeInfo.get("min_fee")).doubleValue() : 0.0;
			double maxFee = feeInfo.get("max_fee") != null ? ((Number) feeInfo.get("max_fee")).doubleValue() : 0.0;
			
			String feeRange = String.format("₹%.0f - ₹%.0f", minFee, maxFee);

			System.out.printf("│ %-23s │ ₹%-14.2f │ ₹%-14.2f │ %-20s │%n", 
				courseName, totalFee, avgPaid, feeRange);
		}

		System.out.println("└─────────────────────────┴─────────────────┴─────────────────┴──────────────────────┘");
	}

	public void displaySemesterWiseStructure() throws SQLException {
	    System.out.println("\n╔══════════════════════════════════════════════════════════╗");
	    System.out.println("║                    SEMESTER-WISE STRUCTURE               ║");
	    System.out.println("╚══════════════════════════════════════════════════════════╝");

	    Map<String, Integer> semesterStructure = analysisDAO.getSemesterWiseStructure();

	    if (semesterStructure.isEmpty()) {
	        System.out.println(" No semester structure data available.");
	        return;
	    }

	    // Header
	    System.out.println("\n┌──────────────────────────────────────────────────────────┬─────────────────┐");
	    System.out.println("│ Course Name (Semesters)                                  │ Student Count   │");
	    System.out.println("├──────────────────────────────────────────────────────────┼─────────────────┤");

	    for (Map.Entry<String, Integer> entry : semesterStructure.entrySet()) {
	        String courseInfo = HelperUtils.truncate(entry.getKey(), 58); // keep in column
	        int studentCount = entry.getValue();

	        System.out.printf("│ %-56s │ %-15d │%n", courseInfo, studentCount);
	    }

	    // Footer
	    System.out.println("└──────────────────────────────────────────────────────────┴─────────────────┘");

	    int totalStudents = semesterStructure.values().stream().mapToInt(Integer::intValue).sum();
	    System.out.printf("\nTotal Students: %d%n", totalStudents);
	}

} 