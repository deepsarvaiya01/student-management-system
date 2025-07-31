package com.sms.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.Subject;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import com.sms.service.SubjectService;

public class CourseController {
	private StudentService studentService;
	private CourseService courseService;
	private SubjectService subjectService;
	final static Scanner scanner = new Scanner(System.in);

	public CourseController() throws SQLException {
		super();
		this.studentService = new StudentService();
		this.courseService = new CourseService();
		this.subjectService = new SubjectService();
	}

	public void viewAllCourses() {

		// TODO Auto-generated method stub
		List<Course> courses = studentService.getAllCourses();
		if (courses.isEmpty()) {
			System.out.println("No courses available.");
			return;
		}

		System.out.println("\nAvailable Courses:");
		printCourses(courses);
	}

	public void addNewCourse() {
		try {
			System.out.print("Enter course name: ");
			String name = scanner.nextLine();
			System.out.print("Enter number of semesters: ");
			int semesters = Integer.parseInt(scanner.nextLine());
			System.out.print("Enter total fee: ");
			BigDecimal fee = new BigDecimal(scanner.nextLine());

			Course course = new Course();
			course.setCourse_name(name);
			course.setNo_of_semester(semesters);
			course.setTotal_fee(fee);

			int courseId = courseService.addCourse(course);
			if (courseId == -1) {
				System.out.println("Failed to add course.");
				return;
			}

			int totalSubjects;
			while (true) {
				System.out.println("You must assign at least 5 subjects to the course.");
				System.out.println("1. Assign exactly 5 subjects");
				System.out.println("2. Assign more than 5 subjects");
				System.out.print("Enter choice: ");
				int assignChoice = Integer.parseInt(scanner.nextLine());

				if (assignChoice == 1) {
					totalSubjects = 5;
					break;
				} else if (assignChoice == 2) {
					System.out.print("Enter total number of subjects to assign (minimum 5): ");
					totalSubjects = Integer.parseInt(scanner.nextLine());
					if (totalSubjects >= 5)
						break;
					else
						System.out.println("❗ Please enter at least 5.");
				} else {
					System.out.println("❗ Invalid choice.");
				}
			}

			
			int existingCount = 0;
			while (true) {
				System.out.print(
						"How many subjects do you want to choose from existing ones (0 to " + totalSubjects + "): ");
				existingCount = Integer.parseInt(scanner.nextLine());
				if (existingCount >= 0 && existingCount <= totalSubjects)
					break;
				else
					System.out.println("❗ Invalid number. Must be between 0 and " + totalSubjects);
			}

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
						String[] ids = scanner.nextLine().split(",");
						if (ids.length != existingCount) {
							System.out.println("❗ You must enter exactly " + existingCount + " IDs.");
							continue;
						}

						boolean allValid = true;
						for (String idStr : ids) {
							try {
								int subjectId = Integer.parseInt(idStr.trim());
								if (!subjectService.subjectExists(subjectId)) {
									System.out.println("❗ Subject ID " + subjectId + " does not exist.");
									allValid = false;
									break;
								}
							} catch (NumberFormatException e) {
								System.out.println("❗ Invalid ID format: " + idStr);
								allValid = false;
								break;
							}
						}

						if (allValid) {
							for (String idStr : ids) {
								int subjectId = Integer.parseInt(idStr.trim());
								courseService.assignSubjectToCourse(courseId, subjectId);
							}
							break;
						}
					}
				}
			}

			int newSubjectCount = totalSubjects - existingCount;
			for (int i = 1; i <= newSubjectCount; i++) {
				System.out.print("Enter name for new subject " + i + ": ");
				String subjectName = scanner.nextLine();

				String subjectType;
				while (true) {
				    System.out.print("Enter subject type (Mandatory/Elective): ");
				    subjectType = scanner.nextLine().trim();
				    if (subjectType.equalsIgnoreCase("Mandatory") || subjectType.equalsIgnoreCase("Elective")) {
				        subjectType = subjectType.substring(0, 1).toUpperCase() + subjectType.substring(1).toLowerCase();
				        break;
				    } else {
				        System.out.println("❗ Invalid input. Please enter 'Mandatory' or 'Elective'.");
				    }
				}

				int subjectId = subjectService.addSubject(subjectName, subjectType);
				courseService.assignSubjectToCourse(courseId, subjectId);

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

	        System.out.print("Enter Course ID to add subjects to: ");
	        int courseId = Integer.parseInt(scanner.nextLine());
	        Course selectedCourse = courseService.getCourseById(courseId);

	        if (selectedCourse == null) {
	            System.out.println("❗ Course not found with ID: " + courseId);
	            return;
	        }

	        System.out.println("Choose subject addition method:");
	        System.out.println("1. Assign existing subjects");
	        System.out.println("2. Add new subjects and assign");
	        System.out.print("Enter choice: ");
	        int choice = Integer.parseInt(scanner.nextLine());

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

	        } else if (choice == 2) {
	            System.out.print("Enter number of new subjects to add: ");
	            int count = Integer.parseInt(scanner.nextLine());
	            for (int i = 1; i <= count; i++) {
	                System.out.print("Enter name for new subject " + i + ": ");
	                String subjectName = scanner.nextLine();

	                String subjectType;
	                while (true) {
	                    System.out.print("Enter type (mandatory/elective): ");
	                    subjectType = scanner.nextLine().trim().toLowerCase();
	                    if (subjectType.equals("mandatory") || subjectType.equals("elective")) {
	                        break;
	                    } else {
	                        System.out.println("❗ Invalid type. Must be 'mandatory' or 'elective'.");
	                    }
	                }

	                int newSubjectId = subjectService.addSubject(subjectName, subjectType);
	                if (newSubjectId != -1) {
	                    courseService.assignSubjectToCourse(courseId, newSubjectId);
	                    System.out.println("✅ Subject created and assigned.");
	                } else {
	                    System.out.println("❌ Failed to add subject.");
	                }
	            }
	        } else {
	            System.out.println("❗ Invalid choice.");
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
	        System.out.print("Enter choice: ");
	        int choice = Integer.parseInt(scanner.nextLine());

	        Course course = null;

	        if (choice == 1) {
	            System.out.print("Enter Course ID: ");
	            int id = Integer.parseInt(scanner.nextLine());
	            course = courseService.getCourseById(id);
	        } else if (choice == 2) {
	            System.out.print("Enter Course Name: ");
	            String name = scanner.nextLine();
	            course = courseService.getCourseByName(name);
	        } else {
	            System.out.println("Invalid choice.");
	            return;
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
	        System.out.println("Error while searching course: " + e.getMessage());
	    }
	}


	public void deleteCourse() {
	    try {
	        System.out.print("Enter Course ID to delete: ");
	        int courseId = Integer.parseInt(scanner.nextLine());

	        Course course = courseService.getCourseById(courseId);
	        if (course == null) {
	            System.out.println("❗ Course not found.");
	            return;
	        }

	        System.out.print("Are you sure you want to delete course '" + course.getCourse_name() + "'? (yes/no): ");
	        String confirm = scanner.nextLine().trim().toLowerCase();

	        if (confirm.equals("yes")) {
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
	        System.out.print("Enter Course ID to view its subjects: ");
	        int courseId = Integer.parseInt(scanner.nextLine());

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

	        System.out.println("\nSubjects for Course: " + course.getCourse_name() + " (ID: " + course.getCourse_id() + ")");
	        System.out.printf("%-10s %-30s\n", "Subject ID", "Subject Name");
	        System.out.println("----------------------------------------");
	        for (Subject subject : subjects) {
	            System.out.printf("%-10d %-30s\n", subject.getSubject_id(), subject.getSubject_name());
	        }

	    } catch (NumberFormatException e) {
	        System.out.println("❗ Invalid input. Please enter a numeric Course ID.");
	    } catch (Exception e) {
	        System.out.println("❗ Error while fetching subjects: " + e.getMessage());
	    }
	}



	private void printCourses(List<Course> courses) {
		System.out.printf("\n%-10s %-25s %-20s %-15s\n", "Course ID", "Course Name", "No. of Semesters", "Total Fee");
		System.out.println("-------------------------------------------------------------");
		for (Course c : courses) {
			String totalFee = (c.getTotal_fee() != null) ? "₹" + c.getTotal_fee() : "N/A";
			System.out.printf("%-10d %-25s %-20d %-15s\n", c.getCourse_id(), c.getCourse_name(), c.getNo_of_semester(),
					totalFee);
		}
	}

}
