package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
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
		try {
			String name = InputValidator.getValidName(scanner, "Enter course name: ");
			int semesters = InputValidator.getValidIntegerInRange(scanner, "Enter number of semesters: ",
					"Number of Semesters", 1, 10);
			BigDecimal fee = InputValidator.getValidDecimal(scanner, "Enter total fee: ₹", "Total Fee");

			Course course = new Course();
			course.setCourse_name(name);
			course.setNo_of_semester(semesters);
			course.setTotal_fee(fee);

			int courseId = courseService.addCourse(course);
			if (courseId == -1) {
				System.out.println("❌ Failed to add course.");
				return;
			}

			int totalSubjects;
			while (true) {
				System.out.println("You must assign at least 5 subjects to the course.");
				System.out.println("1. Assign exactly 5 subjects");
				System.out.println("2. Assign more than 5 subjects");
				int assignChoice = InputValidator.getValidIntegerInRange(scanner, "Enter choice: ", "Choice", 1, 2);
				if (assignChoice == 1) {
					totalSubjects = 5;
					break;
				} else {
					totalSubjects = InputValidator.getValidIntegerInRange(scanner,
							"Enter total number of subjects to assign (minimum 5): ", "Number of Subjects", 5,
							Integer.MAX_VALUE);
					break;
				}
			}

			int existingCount = InputValidator.getValidIntegerInRange(scanner,
					"How many subjects do you want to choose from existing ones (0 to " + totalSubjects + "): ",
					"Number of Existing Subjects", 0, totalSubjects);

			if (existingCount > 0) {
				List<Subject> subjects = subjectService.getAllSubjects();
				if (subjects.isEmpty()) {
					System.out.println(
							"No existing subjects found. You'll need to create all " + totalSubjects + " subjects.");
					existingCount = 0;
				} else {
					System.out.println("Available Subjects:");
					for (Subject subject : subjects) {
						System.out.println("ID: " + subject.getSubject_id() + " - " + subject.getSubject_name());
					}

					while (true) {
						System.out.print("Enter " + existingCount + " subject IDs (comma-separated): ");
						String input = scanner.nextLine().trim();
						String[] ids = input.split(",");
						if (ids.length != existingCount) {
							System.out.println("❌ You must enter exactly " + existingCount + " IDs.");
							continue;
						}

						boolean allValid = true;
						int[] subjectIds = new int[existingCount];
						for (int i = 0; i < ids.length; i++) {
							try {
								subjectIds[i] = Integer.parseInt(ids[i].trim());
								if (!subjectService.subjectExists(subjectIds[i])) {
									System.out.println("❌ Subject ID " + subjectIds[i] + " does not exist.");
									allValid = false;
									break;
								}
							} catch (NumberFormatException e) {
								System.out.println("❌ Invalid ID format: " + ids[i]);
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

			int newSubjectCount = totalSubjects - existingCount;
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
						System.out.println("❌ Invalid input. Please enter 'Mandatory' or 'Elective'.");
					}
				}

				int subjectId = subjectService.addSubject(subjectName, subjectType);
				if (subjectId == -1) {
					System.out.println("❌ Failed to add subject: " + subjectName);
					continue;
				}
				courseService.assignSubjectToCourse(courseId, subjectId);

				List<Teacher> teachers = new TeacherService().fetchAllTeachers();
				if (teachers.isEmpty()) {
					System.out.println("❗ No teachers found. Skipping assignment.");
				} else {
					System.out.println("Available Teachers:");
					System.out.printf("%-5s %-20s %-20s %-10s%n", "ID", "Name", "Qualification", "Experience");
					System.out.println("------------------------------------------------------------");
					for (Teacher t : teachers) {
						System.out.printf("%-5d %-20s %-20s %-10.1f%n", t.getTeacherId(), t.getName(),
								t.getQualification(), t.getExperience());
					}

					int teacherId = InputValidator.getValidIntegerAllowZero(scanner,
							"Enter Teacher ID to assign to subject '" + subjectName + "' or 0 to skip: ", "Teacher ID");
					if (teacherId > 0) {
						boolean assigned = new TeacherService().assignSubject(teacherId, subjectId);
						if (assigned) {
							System.out.println("✅ Teacher assigned to subject.");
						} else {
							System.out.println("❌ Assignment failed. Possibly invalid ID or already assigned.");
						}
					} else {
						System.out.println("Skipped teacher assignment for this subject.");
					}
				}
			}

			System.out.println("✅ Course created and " + totalSubjects + " subjects assigned successfully.");
		} catch (Exception e) {
			System.out.println("❗ Error: " + e.getMessage());
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
			for (Course c : courses) {
				System.out.println(c.getCourse_id() + " - " + c.getCourse_name());
			}

			int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to add subjects to: ", "Course ID");
			Course selectedCourse = courseService.getCourseById(courseId);

			if (selectedCourse == null) {
				System.out.println("❗ Course not found with ID: " + courseId);
				return;
			}

			System.out.println("Choose subject addition method:");
			System.out.println("1. Assign existing subjects");
			System.out.println("2. Add new subjects and assign");
			int choice = InputValidator.getValidIntegerInRangeWithNewline(scanner, "Enter choice: ", "Choice", 1, 2);

			if (choice == 1) {
				List<Subject> existingSubjects = subjectService.getAllSubjects();
				if (existingSubjects.isEmpty()) {
					System.out.println("❗ No subjects found. Please create new subjects.");
					return;
				}

				System.out.println("Available Subjects:");
				for (Subject subject : existingSubjects) {
					System.out.println(subject.getSubject_id() + ": " + subject.getSubject_name());
				}

				System.out.print("Enter comma-separated subject IDs to assign: ");
				String[] ids = scanner.nextLine().split(",");
				for (String idStr : ids) {
					try {
						int subjectId = Integer.parseInt(idStr.trim());
						if (subjectService.subjectExists(subjectId)) {
							courseService.assignSubjectToCourse(courseId, subjectId);
							System.out.println("✅ Subject " + subjectId + " assigned.");
						} else {
							System.out.println("❗ Subject ID " + subjectId + " does not exist.");
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

							int teacherId = InputValidator.getValidIntegerAllowZero(scanner,
									"Enter Teacher ID to assign to subject '" + subjectName + "' or 0 to skip: ",
									"Teacher ID");
							if (teacherId > 0) {
								boolean assigned = new TeacherService().assignSubject(teacherId, newSubjectId);
								if (assigned) {
									System.out.println("✅ Teacher assigned to subject.");
								} else {
									System.out.println("❌ Assignment failed. Possibly invalid ID or already assigned.");
								}
							} else {
								System.out.println("Skipped teacher assignment.");
							}
						}
						System.out.println("✅ Subject created and assigned.");
					}
				}
			}
		} catch (Exception e) {
			System.out.println("❗ Error: " + e.getMessage());
		}
	}

	public void searchCourse() {
		try {
			System.out.println("Search course by:");
			System.out.println("1. Course ID");
			System.out.println("2. Course Name");
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
				System.out.println("Course Found:");
				System.out.println("ID: " + course.getCourse_id());
				System.out.println("Name: " + course.getCourse_name());
				System.out.println("Semesters: " + course.getNo_of_semester());
				System.out.println("Total Fee: " + course.getTotal_fee());
			} else {
				System.out.println("Course not found.");
			}
		} catch (Exception e) {
			System.out.println("❗ Error while searching course: " + e.getMessage());
		}
	}

	public void deleteCourse() {
		try {
			int courseId = InputValidator.getValidInteger(scanner, "Enter Course ID to delete: ", "Course ID");
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
			System.out.printf("%-10s %-30s\n", "Subject ID", "Subject Name");
			System.out.println("----------------------------------------");
			for (Subject subject : subjects) {
				System.out.printf("%-10d %-30s\n", subject.getSubject_id(), subject.getSubject_name());
			}
		} catch (Exception e) {
			System.out.println("❗ Error while fetching subjects: " + e.getMessage());
		}
	}


}