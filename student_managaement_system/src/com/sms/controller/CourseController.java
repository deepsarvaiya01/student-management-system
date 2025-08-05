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
		// Track newly created entities for rollback
		List<Integer> newlyCreatedSubjectIds = new ArrayList<>();
		int courseId = -1;
		
		try {
			String name = InputValidator.getValidName(scanner, "Enter course name: ");
			int semesters = InputValidator.getValidIntegerInRange(scanner, "Enter number of semesters: ",
					"Number of Semesters", 1, 10);
			scanner.nextLine(); // Clear buffer

			BigDecimal fee = InputValidator.getValidDecimal(scanner, "Enter total fee: ₹", "Total Fee");
			scanner.nextLine(); // Clear buffer

			Course course = new Course();
			course.setCourse_name(name);
			course.setNo_of_semester(semesters);
			course.setTotal_fee(fee);

			courseId = courseService.addCourse(course);
			if (courseId == -1) {
				System.out.println("Failed to add course.");
				return;
			}

			int totalSubjects;
			while (true) {
				System.out.println("\n📌 You must assign at least 5 subjects to the course:");
				System.out.println("+----+----------------------------------------+");
				System.out.println("| No | Option                                 |");
				System.out.println("+----+----------------------------------------+");
				System.out.println("| 1  | Assign exactly 5 subjects              |");
				System.out.println("| 2  | Assign more than 5 subjects            |");
				System.out.println("+----+----------------------------------------+");

				int assignChoice = InputValidator.getValidIntegerInRange(scanner, "Enter choice: ", "Choice", 1, 2);
				scanner.nextLine(); // Clear buffer

				if (assignChoice == 1) {
					totalSubjects = 5;
					break;
				} else {
					totalSubjects = InputValidator.getValidIntegerInRange(scanner,
							"Enter total number of subjects to assign (minimum 5): ", "Number of Subjects", 5,
							Integer.MAX_VALUE);
					scanner.nextLine(); // Clear buffer
					break;
				}
			}

			int existingCount = InputValidator.getValidIntegerInRange(scanner,
					"How many subjects do you want to choose from existing ones (0 to " + totalSubjects + "): ",
					"Number of Existing Subjects", 0, totalSubjects);
			scanner.nextLine(); // Clear buffer

			if (existingCount > 0) {
				List<Subject> subjects = subjectService.getAllSubjects();
				if (subjects.isEmpty()) {
					System.out.println(
							" No existing subjects found. You must create all " + totalSubjects + " subjects.");
					existingCount = 0;
				} else {
					System.out.println("\nAvailable Subjects:");
					HelperUtils.viewSubjects(subjects); // Already a formatted viewer

					while (true) {
						System.out.print("Enter " + existingCount + " subject IDs (comma-separated): ");
						String input = scanner.nextLine().trim();
						String[] ids = input.split(",");
						if (ids.length != existingCount) {
							System.out.println("You must enter exactly " + existingCount + " IDs.");
							continue;
						}

						boolean allValid = true;
						int[] subjectIds = new int[existingCount];
						for (int i = 0; i < ids.length; i++) {
							try {
								subjectIds[i] = Integer.parseInt(ids[i].trim());
								if (!subjectService.subjectExists(subjectIds[i])) {
									System.out.println(" Subject ID " + subjectIds[i] + " does not exist.");
									allValid = false;
									break;
								}
							} catch (NumberFormatException e) {
								System.out.println("Invalid ID format: " + ids[i]);
								allValid = false;
								break;
							}
						}

						if (allValid) {
							for (int subjectId : subjectIds) {
								courseService.assignSubjectToCourse(courseId, subjectId);
							}
							break;
						}
					}
				}
			}

			// Add new subjects if required
			int newSubjectCount = totalSubjects - existingCount;
			TeacherService teacherService = new TeacherService(); // Reuse instance
			boolean teacherAssignmentFailed = false;
			
			for (int i = 1; i <= newSubjectCount; i++) {
				String subjectName = InputValidator.getValidName(scanner, "Enter name for new subject " + i + ": ");

				String subjectType;
				while (true) {
					System.out.print("Enter subject type (Mandatory/Elective): ");
					subjectType = scanner.nextLine().trim();
					if (subjectType.equalsIgnoreCase("Mandatory") || subjectType.equalsIgnoreCase("Elective")) {
						subjectType = subjectType.substring(0, 1).toUpperCase()
								+ subjectType.substring(1).toLowerCase();
						break;
					} else {
						System.out.println("Invalid input. Please enter 'Mandatory' or 'Elective'.");
					}
				}

				int subjectId = subjectService.addSubject(subjectName, subjectType);
				if (subjectId == -1) {
					System.out.println("Failed to add subject: " + subjectName);
					continue;
				}
				
				// Track newly created subject for potential rollback
				newlyCreatedSubjectIds.add(subjectId);
				courseService.assignSubjectToCourse(courseId, subjectId);

				List<Teacher> teachers = teacherService.fetchAllTeachers();
				if (teachers.isEmpty()) {
					System.out.println("⚠ No teachers found. Skipping assignment.");
				} else {
					System.out.println("\n👩‍🏫 Available Teachers:");
					System.out.printf("%-5s %-20s %-20s %-10s%n", "ID", "Name", "Qualification", "Experience");
					System.out.println("------------------------------------------------------------");
					for (Teacher t : teachers) {
						System.out.printf("%-5d %-20s %-20s %-10.1f%n", t.getTeacherId(), t.getName(),
								t.getQualification(), t.getExperience());
					}

					// Try teacher assignment with 3 attempts
					int attempts = 0;
					boolean assigned = false;
					
					while (attempts < 3 && !assigned) {
						int teacherId = InputValidator.getValidInteger(scanner,
								"Enter Teacher ID to assign to subject '" + subjectName + "': ", "Teacher ID");
						scanner.nextLine(); // Clear buffer

						if (teacherId > 0) {
							assigned = teacherService.assignSubject(teacherId, subjectId);
							if (assigned) {
								System.out.println("✅ Teacher assigned to subject.");
							} else {
								System.out.println("❌ Assignment failed. Attempt " + (attempts + 1) + " of 3.");
								attempts++;
								if (attempts < 3) {
									System.out.println("Please try again with a different Teacher ID.");
								}
							}
						} else {
							System.out.println("❌ Invalid Teacher ID. Attempt " + (attempts + 1) + " of 3.");
							attempts++;
						}
					}
					
					// If teacher assignment failed after 3 attempts, mark for rollback
					if (!assigned && attempts >= 3) {
						System.out.println("❌ Failed to assign teacher after 3 attempts for subject: " + subjectName);
						teacherAssignmentFailed = true;
						break;
					}
				}
			}

			// Check if we need to rollback due to teacher assignment failure
			if (teacherAssignmentFailed) {
				System.out.println("\n🔄 Rolling back course creation due to teacher assignment failure...");
				performRollback(courseId, newlyCreatedSubjectIds);
				return;
			}

			System.out.println("\n✅ Course created and " + totalSubjects + " subjects assigned successfully.");

		} catch (Exception e) {
			System.out.println("❗ Unexpected error: " + e.getMessage());
			// If there's an exception, also perform rollback
			if (courseId != -1) {
				System.out.println("\n🔄 Rolling back due to unexpected error...");
				performRollback(courseId, newlyCreatedSubjectIds);
			}
		}
	}
	
	/**
	 * Performs rollback by deleting the course and all newly created subjects
	 * @param courseId The ID of the course to delete
	 * @param newlyCreatedSubjectIds List of subject IDs that were newly created
	 */
	private void performRollback(int courseId, List<Integer> newlyCreatedSubjectIds) {
		try {
			System.out.println("🗑️ Deleting newly created subjects...");
			
			// Delete all newly created subjects
			for (Integer subjectId : newlyCreatedSubjectIds) {
				boolean deleted = subjectService.deleteSubject(subjectId);
				if (deleted) {
					System.out.println("✅ Deleted subject ID: " + subjectId);
				} else {
					System.out.println("❌ Failed to delete subject ID: " + subjectId);
				}
			}
			
			System.out.println("🗑️ Deleting course...");
			
			// Delete the course
			boolean courseDeleted = courseService.deleteCourseById(courseId);
			if (courseDeleted) {
				System.out.println("✅ Deleted course ID: " + courseId);
			} else {
				System.out.println("❌ Failed to delete course ID: " + courseId);
			}
			
			System.out.println("🔄 Rollback completed successfully.");
			
		} catch (Exception e) {
			System.out.println("❌ Error during rollback: " + e.getMessage());
			System.out.println("⚠️ Manual cleanup may be required.");
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

			System.out.println("Choose subject addition method:");
			System.out.println("+----+----------------------------------------+");
			System.out.println("| No | Option                                 |");
			System.out.println("+----+----------------------------------------+");
			System.out.println("| 1  | Assign Existing subjects               |");
			System.out.println("| 2  | Add new subject and Assign             |");
			System.out.println("+----+----------------------------------------+");

			int choice = InputValidator.getValidIntegerInRangeWithNewline(scanner, "Enter choice: ", "Choice", 1, 2);

			if (choice == 1) {
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
						// Check if the subject is in the unassigned list
						boolean isValidSubject = unassignedSubjects.stream()
								.anyMatch(s -> s.getSubject_id() == subjectId);
						
						if (isValidSubject) {
							courseService.assignSubjectToCourse(courseId, subjectId);
							System.out.println("✅ Subject " + subjectId + " assigned.");
						} else {
							System.out.println("❗ Subject ID " + subjectId + " is not available for assignment (may be already assigned or doesn't exist).");
						}
					} catch (NumberFormatException e) {
						System.out.println("❗ Invalid input: " + idStr);
					}
				}
			} else {
				int count = InputValidator.getValidInteger(scanner, "Enter number of new subjects to add: ",
						"Number of Subjects");
				for (int i = 1; i <= count; i++) {
					String subjectName = InputValidator.getValidName(scanner, "Enter name for new subject " + i + ": ");
					String subjectType;
					while (true) {
						System.out.print("Enter type (Mandatory/Elective): ");
						subjectType = scanner.nextLine().trim();
						if (subjectType.equalsIgnoreCase("Mandatory") || subjectType.equalsIgnoreCase("Elective")) {
							subjectType = subjectType.substring(0, 1).toUpperCase()
									+ subjectType.substring(1).toLowerCase();
							break;
						} else {
							System.out.println("❗ Invalid type. Must be 'Mandatory' or 'Elective'.");
						}
					}

					int newSubjectId = subjectService.addSubject(subjectName, subjectType);
					if (newSubjectId != -1) {
						courseService.assignSubjectToCourse(courseId, newSubjectId);

						List<Teacher> teachers = new TeacherService().fetchAllTeachers();
						if (teachers.isEmpty()) {
							System.out.println("❗ No teachers available to assign.");
						} else {
							System.out.println("Available Teachers:");
							System.out.printf("%-5s %-20s %-20s %-10s%n", "ID", "Name", "Qualification", "Experience");
							System.out.println("------------------------------------------------------------");
							for (Teacher t : teachers) {
								System.out.printf("%-5d %-20s %-20s %-10.1f%n", t.getTeacherId(), t.getName(),
										t.getQualification(), t.getExperience());
							}

							int teacherId = InputValidator.getValidInteger(scanner,
									"Enter Teacher ID to assign to subject '" + subjectName + "': ",
									"Teacher ID");
							boolean assigned = new TeacherService().assignSubject(teacherId, newSubjectId);
							if (assigned) {
								System.out.println("✅ Teacher assigned to subject.");
							} else {
								System.out.println("❌ Assignment failed. Possibly invalid ID or already assigned.");
							}
						}
						System.out.println("✅ Subject created and assigned.");
					}
				}
			}
		} catch (Exception e) {
			String message = e.getMessage();

			if (message != null && message.contains("Duplicate entry")) {
				System.out.println(" Subject is already assigned to this course. Please choose a different subject.");
			} else {
				System.out.println("An unexpected error occurred: " + message);
			}
		}

	}

	public void searchCourse() {
		try {
			System.out.println("\n🔍 Search course by:");
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

			if (course != null) {
				System.out.println("\n📘 Course Found:");
				System.out.println("+-----------+---------------------------+---------------------+--------------+");
				System.out.printf("| %-9s | %-25s | %-19s | %-12s |\n", "Course ID", "Course Name", "No. of Semesters",
						"Total Fee");
				System.out.println("+-----------+---------------------------+---------------------+--------------+");
				System.out.printf("| %-9d | %-25s | %-19d | ₹%-11s |\n", course.getCourse_id(), course.getCourse_name(),
						course.getNo_of_semester(),
						course.getTotal_fee() != null ? course.getTotal_fee().toString() : "N/A");
				System.out.println("+-----------+---------------------------+---------------------+--------------+");

			} else {
				System.out.println("❌ Course not found.");
			}
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
				if (deleted) {
					System.out.println("✅ Course deleted successfully.");
				} else {
					System.out.println("❗ Failed to delete course.");
				}
			} else {
				System.out.println("❌ Deletion cancelled.");
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
				System.out.println("ℹ️ No subjects assigned to course: " + course.getCourse_name());
				return;
			}

			System.out.println(
					"\nSubjects for Course: " + course.getCourse_name() + " (ID: " + course.getCourse_id() + ")");
			HelperUtils.viewSubjects(subjects);
		} catch (Exception e) {
			System.out.println("❗ Error while fetching subjects: " + e.getMessage());
		}
	}

}