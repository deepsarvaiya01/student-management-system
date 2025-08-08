package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Subject;
import com.sms.model.Teacher;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import com.sms.service.SubjectService;
import com.sms.service.TeacherService;
import com.sms.utils.HelperUtils;
import com.sms.utils.InputValidator;

public class CourseController {
	private StudentService studentService;
	private CourseService courseService;
	private SubjectService subjectService;
	private final Scanner scanner = new Scanner(System.in);

	public CourseController() throws SQLException {
		this.studentService = new StudentService();
		this.courseService = new CourseService();
		this.subjectService = new SubjectService();
	}

	public void viewAllCourses() {
		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available.");
			return;
		}
		System.out.println("\nAvailable Courses:");
		HelperUtils.printCourses(courses);
	}

	public void addNewCourse() {
		List<Integer> newlyCreatedSubjectIds = new ArrayList<>();
		int courseId = -1;

		try {
			Course course = createCourseFromInput();
			courseId = courseService.addCourse(course);
			if (courseId == -1) {
				System.out.println("Failed to add course.");
				return;
			}

			int totalSubjects = getTotalSubjectsFromUser();
			int existingCount = getExistingSubjectsCount(totalSubjects);

			if (existingCount > 0) {
				assignExistingSubjects(courseId, existingCount);
			}

			int newSubjectCount = totalSubjects - existingCount;
			boolean teacherAssignmentFailed = createNewSubjectsWithTeachers(courseId, newSubjectCount,
					newlyCreatedSubjectIds);

			if (teacherAssignmentFailed) {
				System.out.println("\nRolling back course creation due to teacher assignment failure...");
				performRollback(courseId, newlyCreatedSubjectIds);
				return;
			}

			System.out.println("\nCourse created and " + totalSubjects + " subjects assigned successfully.");

			// Display comprehensive course details
			displayCourseDetails(courseId);
			System.out.println("\n" + "╔" + "═".repeat(78) + "╗");
			System.out.println("║" + " ".repeat(30) + "COURSE CREATED SUCCESSFULLY" + " ".repeat(20) + "║");
			System.out.println("╚" + "═".repeat(78) + "╝");

		} catch (Exception e) {
			System.out.println("❗ Unexpected error: " + e.getMessage());
			if (courseId != -1) {
				System.out.println("\nRolling back due to unexpected error...");
				performRollback(courseId, newlyCreatedSubjectIds);
			}
		}
	}

	private Course createCourseFromInput() {
		String name = InputValidator.getValidCourseName(scanner, "Enter course name: ", courseService);
		int semesters = InputValidator.getValidIntegerInRange(scanner, "Enter number of semesters: ",
				"Number of Semesters", 1, 10);
		scanner.nextLine();

		BigDecimal fee = InputValidator.getValidDecimal(scanner, "Enter total fee: ₹", "Total Fee");
		scanner.nextLine();

		Course course = new Course();
		course.setCourse_name(name);
		course.setNo_of_semester(semesters);
		course.setTotal_fee(fee);
		return course;
	}

	private int getTotalSubjectsFromUser() {
		while (true) {
			System.out.println("\nYou must assign at least 5 subjects to the course:");
			System.out.println("+----+----------------------------------------+");
			System.out.println("| No | Option                                 |");
			System.out.println("+----+----------------------------------------+");
			System.out.println("| 1  | Assign exactly 5 subjects              |");
			System.out.println("| 2  | Assign more than 5 subjects            |");
			System.out.println("+----+----------------------------------------+");

			int assignChoice = InputValidator.getValidIntegerInRange(scanner, "Enter choice: ", "Choice", 1, 2);
			scanner.nextLine();

			if (assignChoice == 1) {
				return 5;
			} else {
				int totalSubjects = InputValidator.getValidIntegerInRange(scanner,
						"Enter total number of subjects to assign (minimum 5): ", "Number of Subjects", 5,
						Integer.MAX_VALUE);
				scanner.nextLine();
				return totalSubjects;
			}
		}
	}

	private int getExistingSubjectsCount(int totalSubjects) {
		int existingCount = InputValidator.getValidIntegerInRange(scanner,
				"How many subjects do you want to choose from existing ones (0 to " + totalSubjects + "): ",
				"Number of Existing Subjects", 0, totalSubjects);
		scanner.nextLine();
		return existingCount;
	}

	private void assignExistingSubjects(int courseId, int existingCount) throws SQLException {
		List<Subject> subjects = subjectService.getAllSubjects();
		if (subjects.isEmpty()) {
			System.out.println(" No existing subjects found. You must create all subjects.");
			return;
		}

		System.out.println("\nAvailable Subjects:");
		HelperUtils.viewSubjects(subjects);

		while (true) {
			System.out.print("Enter " + existingCount + " subject IDs (comma-separated): ");
			String input = scanner.nextLine().trim();
			String[] ids = input.split(",");

			if (ids.length != existingCount) {
				System.out.println("You must enter exactly " + existingCount + " IDs.");
				continue;
			}

			if (validateAndAssignSubjects(courseId, ids)) {
				break;
			}
		}
	}

	private boolean validateAndAssignSubjects(int courseId, String[] ids) throws SQLException {
		for (String idStr : ids) {
			try {
				int subjectId = Integer.parseInt(idStr.trim());
				if (!subjectService.subjectExists(subjectId)) {
					System.out.println(" Subject ID " + subjectId + " does not exist.");
					return false;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid ID format: " + idStr);
				return false;
			}
		}

		for (String idStr : ids) {
			int subjectId = Integer.parseInt(idStr.trim());
			courseService.assignSubjectToCourse(courseId, subjectId);
		}
		return true;
	}

	private boolean createNewSubjectsWithTeachers(int courseId, int newSubjectCount,
			List<Integer> newlyCreatedSubjectIds) throws SQLException {
		TeacherService teacherService = new TeacherService();

		for (int i = 1; i <= newSubjectCount; i++) {
			String subjectName = InputValidator.getValidName(scanner, "Enter name for new subject " + i + ": ");
			String subjectType = getSubjectTypeFromUser();

			int subjectId = subjectService.addSubject(subjectName, subjectType);
			if (subjectId == -1) {
				System.out.println("Failed to add subject: " + subjectName);
				continue;
			}

			newlyCreatedSubjectIds.add(subjectId);
			courseService.assignSubjectToCourse(courseId, subjectId);

			if (assignTeacherToSubject(teacherService, subjectId, subjectName)) {
				return true; // Teacher assignment failed
			}
		}
		return false;
	}

	private String getSubjectTypeFromUser() {
		while (true) {
			System.out.println("\nSelect Subject Type:");
			System.out.println("+----+------------+");
			System.out.println("| No | Type       |");
			System.out.println("+----+------------+");
			System.out.println("| 1  | Mandatory  |");
			System.out.println("| 2  | Elective   |");
			System.out.println("+----+------------+");

			System.out.print("Enter your choice (1 or 2): ");
			String choice = scanner.nextLine().trim();

			switch (choice) {
			case "1":
				return "mandatory";
			case "2":
				return "elective";
			default:
				System.out.println("Invalid choice. Please enter 1 for Mandatory or 2 for Elective.");
				break;
			}
		}
	}

	private boolean assignTeacherToSubject(TeacherService teacherService, int subjectId, String subjectName) {
		List<Teacher> teachers = teacherService.fetchAllTeachers();
		if (teachers.isEmpty()) {
			System.out.println("No teachers found. Skipping assignment.");
			return false;
		}

		displayTeachers(teachers);

		int attempts = 0;
		while (attempts < 3) {
			int teacherId = InputValidator.getValidInteger(scanner,
					"Enter Teacher ID to assign to subject '" + subjectName + "': ", "Teacher ID");
			scanner.nextLine();

			if (teacherId > 0) {
				boolean assigned = teacherService.assignSubject(teacherId, subjectId);
				if (assigned) {
					System.out.println("Teacher assigned to subject.");
					return false;
				} else {
					System.out.println("Assignment failed. Attempt " + (attempts + 1) + " of 3.");
					attempts++;
					if (attempts < 3) {
						System.out.println("Please try again with a different Teacher ID.");
					}
				}
			} else {
				System.out.println("Invalid Teacher ID. Attempt " + (attempts + 1) + " of 3.");
				attempts++;
			}
		}

		System.out.println("Failed to assign teacher after 3 attempts for subject: " + subjectName);
		return true; // Teacher assignment failed
	}

	private void displayTeachers(List<Teacher> teachers) {
		System.out.println("\nAvailable Teachers:");
		String separator = "+-----+--------------------+--------------------+----------+";
		String headerFormat = "| %-3s | %-18s | %-18s | %-8s |%n";
		String rowFormat = "| %-3d | %-18s | %-18s | %-8.1f |%n";

		System.out.println(separator);
		System.out.printf(headerFormat, "ID", "Name", "Qualification", "Experience");
		System.out.println(separator);

		for (Teacher t : teachers) {
			System.out.printf(rowFormat, t.getTeacherId(), t.getName(), t.getQualification(), t.getExperience());
		}
		System.out.println(separator);
	}

	private void performRollback(int courseId, List<Integer> newlyCreatedSubjectIds) {
		try {
			System.out.println("Deleting newly created subjects...");
			for (Integer subjectId : newlyCreatedSubjectIds) {
				boolean deleted = subjectService.deleteSubject(subjectId);
				System.out.println(
						deleted ? "Deleted subject ID: " + subjectId : "Failed to delete subject ID: " + subjectId);
			}

			System.out.println("Deleting course...");
			boolean courseDeleted = courseService.deleteCourseById(courseId);
			System.out.println(
					courseDeleted ? "Deleted course ID: " + courseId : "Failed to delete course ID: " + courseId);
			System.out.println("Rollback completed successfully.");

		} catch (Exception e) {
			System.out.println("Error during rollback: " + e.getMessage());
			System.out.println("Manual cleanup may be required.");
		}
	}

	public void addSubjectsToCourse() {
		try {
			List<Course> courses = courseService.getAllCourses();
			if (courses.isEmpty()) {
				System.out.println("❗ No courses available.");
				return;
			}

			System.out.println("Available Courses:");
			HelperUtils.printCourses(courses);

			int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to add subjects to: ", "Course ID");
			Course selectedCourse = courseService.getCourseById(courseId);

			if (selectedCourse == null) {
				System.out.println("❗ Course not found with ID: " + courseId);
				return;
			}

			displaySubjectAdditionMenu();
			int choice = InputValidator.getValidIntegerInRangeWithNewline(scanner, "Enter choice: ", "Choice", 1, 2);

			if (choice == 1) {
				assignExistingSubjectsToCourse(courseId);
			} else {
				addNewSubjectsToCourse(courseId);
			}
		} catch (Exception e) {
			handleSubjectAssignmentError(e);
		}
	}

	private void displaySubjectAdditionMenu() {
		System.out.println("Choose subject addition method:");
		System.out.println("+----+----------------------------------------+");
		System.out.println("| No | Option                                 |");
		System.out.println("+----+----------------------------------------+");
		System.out.println("| 1  | Assign Existing subjects               |");
		System.out.println("| 2  | Add new subject and Assign             |");
		System.out.println("+----+----------------------------------------+");
	}

	private void assignExistingSubjectsToCourse(int courseId) throws SQLException {
		List<Subject> unassignedSubjects = courseService.getUnassignedSubjectsForCourse(courseId);
		if (unassignedSubjects.isEmpty()) {
			System.out.println("❗ No unassigned subjects found. All subjects are already assigned to this course.");
			return;
		}

		System.out.println("Available Unassigned Subjects:");
		HelperUtils.viewSubjects(unassignedSubjects);

		System.out.print("Enter comma-separated subject IDs to assign: ");
		String[] ids = scanner.nextLine().split(",");
		for (String idStr : ids) {
			try {
				int subjectId = Integer.parseInt(idStr.trim());
				boolean isValidSubject = unassignedSubjects.stream().anyMatch(s -> s.getSubject_id() == subjectId);

				if (isValidSubject) {
					courseService.assignSubjectToCourse(courseId, subjectId);
					System.out.println("Subject " + subjectId + " assigned.");
				} else {
					System.out.println("❗ Subject ID " + subjectId
							+ " is not available for assignment (may be already assigned or doesn't exist).");
				}
			} catch (NumberFormatException e) {
				System.out.println("❗ Invalid input: " + idStr);
			}
		}
	}

	private void addNewSubjectsToCourse(int courseId) throws SQLException {
		int count = InputValidator.getValidInteger(scanner, "Enter number of new subjects to add: ",
				"Number of Subjects");
		for (int i = 1; i <= count; i++) {
			String subjectName = InputValidator.getValidName(scanner, "Enter name for new subject " + i + ": ");
			String subjectType = getSubjectTypeFromUser();

			int newSubjectId = subjectService.addSubject(subjectName, subjectType);
			if (newSubjectId != -1) {
				courseService.assignSubjectToCourse(courseId, newSubjectId);
				assignTeacherToNewSubject(newSubjectId, subjectName);
				System.out.println("✅ Subject created and assigned.");
			}
		}
	}

	private void assignTeacherToNewSubject(int newSubjectId, String subjectName) throws SQLException {
		List<Teacher> teachers = new TeacherService().fetchAllTeachers();
		if (teachers.isEmpty()) {
			System.out.println("❗ No teachers available to assign.");
			return;
		}

		System.out.println("Available Teachers:");
		String separator = "+-----+--------------------+--------------------+----------+";
		String headerFormat = "| %-3s | %-18s | %-18s | %-8s |%n";
		String rowFormat = "| %-3d | %-18s | %-18s | %-8.1f |%n";

		System.out.println(separator);
		System.out.printf(headerFormat, "ID", "Name", "Qualification", "Experience");
		System.out.println(separator);

		for (Teacher t : teachers) {
			System.out.printf(rowFormat, t.getTeacherId(), t.getName(), t.getQualification(), t.getExperience());
		}
		System.out.println(separator);

		int teacherId = InputValidator.getValidInteger(scanner,
				"Enter Teacher ID to assign to subject '" + subjectName + "': ", "Teacher ID");
		boolean assigned = new TeacherService().assignSubject(teacherId, newSubjectId);
		System.out.println(assigned ? "Teacher assigned to subject."
				: "Assignment failed. Possibly invalid ID or already assigned.");
	}

	private void handleSubjectAssignmentError(Exception e) {
		String message = e.getMessage();
		if (message != null && message.contains("Duplicate entry")) {
			System.out.println(" Subject is already assigned to this course. Please choose a different subject.");
		} else {
			System.out.println("An unexpected error occurred: " + message);
		}
	}

	public void searchCourse() {
		try {			
			List<Course> courses = courseService.getAllCourses();
	        System.out.println("\nAvailable Courses:");
	        System.out.println("+------------+---------------------------+");
	        System.out.printf("| %-10s | %-25s |\n", "Course ID", "Course Name");
	        System.out.println("+------------+---------------------------+");

	        for (Course course : courses) {
	            System.out.printf("| %-10d | %-25s |\n", course.getCourse_id(), course.getCourse_name());
	        }

	        System.out.println("+------------+---------------------------+");
			System.out.println("\nSearch course by:");
			System.out.println("+----------------+");
			System.out.println("| 1. Course ID   |");
			System.out.println("| 2. Course Name |");
			System.out.println("+----------------+");

			int choice = InputValidator.getValidIntegerInRange(scanner, "Enter choice: ", "Choice", 1, 2);

			Course course = null;
			if (choice == 1) {
				int id = InputValidator.getValidInteger(scanner, "Enter Course ID: ", "Course ID");
				course = courseService.getCourseById(id);
			} else {
				String name = InputValidator.getValidName(scanner, "Enter Course Name: ");
				course = courseService.getCourseByName(name);
			}

			displayCourseDetails(course.getCourse_id());
		} catch (Exception e) {
			System.out.println("❗ Error while searching course: " + e.getMessage());
		}
	}

	public void deleteCourse() {
		try {
			viewAllCourses();
			int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to delete: ", "Course ID");
			scanner.nextLine();
			Course course = courseService.getCourseById(courseId);
			if (course == null) {
				System.out.println("❗ Course not found.");
				return;
			}

			scanner.nextLine();
			boolean confirm = InputValidator.getValidConfirmation(scanner,
					"Are you sure you want to delete course '" + course.getCourse_name() + "'? (y/n): ");
			if (confirm) {
				boolean deleted = courseService.deleteCourseById(courseId);
				System.out.println(deleted ? "Course deleted successfully." : "❗ Failed to delete course.");
			} else {
				System.out.println("Deletion cancelled.");
			}
		} catch (Exception e) {
			System.out.println("❗ Error while deleting course: " + e.getMessage());
		}
	}

	public void viewSubjectsOfCourse() {
		try {
			viewAllCourses();
			int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to view its subjects: ",
					"Course ID");
			Course course = courseService.getCourseById(courseId);
			if (course == null) {
				System.out.println("❗ Course not found with ID: " + courseId);
				return;
			}

			List<Subject> subjects = courseService.getSubjectsForCourse(courseId);
			if (subjects == null || subjects.isEmpty()) {
				System.out.println("No subjects assigned to course: " + course.getCourse_name());
				return;
			}

			System.out.println(
					"\nSubjects for Course: " + course.getCourse_name() + " (ID: " + course.getCourse_id() + ")");
			HelperUtils.viewSubjects(subjects);
		} catch (Exception e) {
			System.out.println("❗ Error while fetching subjects: " + e.getMessage());
		}
	}

	private void displayCourseDetails(int courseId) {
		try {
			Course course = courseService.getCourseById(courseId);
			if (course == null) {
				System.out.println("Course not found.");
				return;
			}

			List<Subject> subjects = courseService.getSubjectsForCourse(courseId);

			System.out.println("\n" + "╔" + "═".repeat(78) + "╗");
			System.out.println("║" + " ".repeat(25) + "COURSE DETAILS" + " ".repeat(35) + "║");
			System.out.println("╚" + "═".repeat(78) + "╝");

			// Course information card
			System.out.println("\nCOURSE INFORMATION");
			System.out.println("┌" + "─".repeat(25) + "┬" + "─".repeat(50) + "┐");
			System.out.printf("│ %-23s │ %-48s │%n", "Course ID", course.getCourse_id());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48s │%n", "Course Name", course.getCourse_name());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ %-48d │%n", "Semesters", course.getNo_of_semester());
			System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(50) + "┤");
			System.out.printf("│ %-23s │ ₹%-46s │%n", "Total Fee",
					course.getTotal_fee() != null ? course.getTotal_fee().toString() : "N/A");
			System.out.println("└" + "─".repeat(25) + "┴" + "─".repeat(50) + "┘");

			// Subjects and teachers information
			if (subjects != null && !subjects.isEmpty()) {
				System.out.println("\nSUBJECTS & TEACHERS ASSIGNMENT");
				System.out.println("┌" + "─".repeat(10) + "┬" + "─".repeat(30) + "┬" + "─".repeat(15) + "┬"
						+ "─".repeat(17) + "┐");
				System.out.printf("│ %-8s │ %-28s │ %-13s │ %-15s │%n", "ID", "Subject Name", "Type", "Teacher");
				System.out.println("├" + "─".repeat(10) + "┼" + "─".repeat(30) + "┼" + "─".repeat(15) + "┼"
						+ "─".repeat(17) + "┤");

				TeacherService teacherService = new TeacherService();
				for (Subject subject : subjects) {
					Teacher teacher = teacherService.getTeacherBySubjectId(subject.getSubject_id());
					String teacherName = (teacher != null) ? teacher.getName() : "Not Assigned";

					String displayType = (subject.getSubject_type() != null)
							? subject.getSubject_type().substring(0, 1).toUpperCase()
									+ subject.getSubject_type().substring(1).toLowerCase()
							: "N/A";

					String truncatedSubjectName = subject.getSubject_name().length() > 28
							? subject.getSubject_name().substring(0, 25) + "..."
							: subject.getSubject_name();
					String truncatedTeacherName = teacherName.length() > 15 ? teacherName.substring(0, 12) + "..."
							: teacherName;

					System.out.printf("│ %-8d │ %-28s │ %-13s │ %-15s │%n", subject.getSubject_id(),
							truncatedSubjectName, displayType, truncatedTeacherName);
				}
				System.out.println("└" + "─".repeat(10) + "┴" + "─".repeat(30) + "┴" + "─".repeat(15) + "┴"
						+ "─".repeat(17) + "┘");

				long mandatoryCount = subjects.stream().filter(s -> "mandatory".equalsIgnoreCase(s.getSubject_type()))
						.count();
				long electiveCount = subjects.stream().filter(s -> "elective".equalsIgnoreCase(s.getSubject_type()))
						.count();
				long assignedTeachersCount = subjects.stream()
						.mapToInt(s -> teacherService.getTeacherBySubjectId(s.getSubject_id()) != null ? 1 : 0).sum();

				System.out.println("\nSUMMARY STATISTICS");
				System.out.println("┌" + "─".repeat(25) + "┬" + "─".repeat(15) + "┐");
				System.out.printf("│ %-23s │ %-13s │%n", "Metric", "Count");
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Total Subjects", subjects.size());
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Mandatory Subjects", mandatoryCount);
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Elective Subjects", electiveCount);
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Teachers Assigned", assignedTeachersCount);
				System.out.println("├" + "─".repeat(25) + "┼" + "─".repeat(15) + "┤");
				System.out.printf("│ %-23s │ %-13d │%n", "Unassigned Subjects",
						subjects.size() - assignedTeachersCount);
				System.out.println("└" + "─".repeat(25) + "┴" + "─".repeat(15) + "┘");

			} else {
				System.out.println("\nNo subjects assigned to this course yet.");
			}

		} catch (Exception e) {
			System.out.println("Error displaying course details: " + e.getMessage());
		}
	}
}