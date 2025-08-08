package com.sms.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.model.Subject;

public class HelperUtils {
	
	// Student Performance Methods
	public static String truncate(String text, int maxLength) {
		if (text == null)
			return "";
		return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
	}

	public static void printStudents(List<Student> students) {
	    String line = "+------------+----------------------+------------------------------------------+------------+";
	    String format = "| %-10s | %-20s | %-40s | %-10s |%n";

	    System.out.println("\nList of Students:");
	    System.out.println(line);
	    System.out.printf(format, "Student ID", "Name", "Email", "GR Number");
	    System.out.println(line);

	    for (Student s : students) {
	        String email = s.getEmail();
	        if (email.length() > 40) {
	            email = email.substring(0, 37) + "...";
	        }
	        System.out.printf(format, s.getStudent_id(), s.getName(), email, s.getGr_number());
	    }

	    System.out.println(line);
	}


	public static void printCourses(List<Course> courses) {
		if (courses == null || courses.isEmpty()) {
			System.out.println("❗ No courses found.");
			return;
		}

		String headerFormat = "| %-10s | %-25s | %-18s | %-15s |%n";
		String rowFormat = "| %-10d | %-25s | %-18d | %-15s |%n";
		String separator = "+------------+---------------------------+--------------------+-----------------+";

		System.out.println("\nList of Courses");
		System.out.println(separator);
		System.out.printf(headerFormat, "Course ID", "Course Name", "No. of Semesters", "Total Fee");
		System.out.println(separator);

		for (Course c : courses) {
			String totalFee = (c.getTotal_fee() != null) ? "₹" + c.getTotal_fee() : "N/A";
			System.out.printf(rowFormat, c.getCourse_id(), c.getCourse_name(), c.getNo_of_semester(), totalFee);
		}

		System.out.println(separator);
	}

	public static void viewSubjects(List<Subject> subjects) {
	    if (subjects == null || subjects.isEmpty()) {
	        System.out.println("❗ No subjects found.");
	        return;
	    }

	    String line = "+--------+------------------------------------------+";
	    String format = "| %-6s | %-40s |%n";

	    System.out.println("\nList of Subjects");
	    System.out.println(line);
	    System.out.printf(format, "ID", "Subject Name");
	    System.out.println(line);

	    for (Subject subject : subjects) {
	        String name = subject.getSubject_name();
	        if (name.length() > 40) {
	            name = name.substring(0, 37) + "...";
	        }
	        System.out.printf(format, subject.getSubject_id(), name);
	    }

	    System.out.println(line);
	}


	public static String validateStudentData(Student student, int courseId) {
		if (student == null || student.getName() == null || !student.getName().matches("[a-zA-Z ]{1,50}")) {
			return "Invalid name (letters/spaces, max 50 chars).";
		}
		if (student.getGr_number() <= 0 || String.valueOf(student.getGr_number()).length() < 4
				|| String.valueOf(student.getGr_number()).length() > 10) {
			return "Invalid GR number (4-10 digits).";
		}
		if (student.getEmail() == null || !student.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
				|| student.getEmail().length() > 100) {
			return "Invalid email format or too long.";
		}
		if (student.getCity() == null || !student.getCity().matches("[a-zA-Z ]{1,50}")) {
			return "Invalid city (letters/spaces, max 50 chars).";
		}
		if (student.getMobile_no() == null || !student.getMobile_no().matches("\\d{10}")
				|| student.getMobile_no().length() > 15) {
			return "Invalid mobile number (10 digits).";
		}
		if (student.getAge() < 15 || student.getAge() > 100) {
			return "Invalid age (15-100).";
		}
		if (student.getGender() == null) {
            return "Invalid gender. Must be M for Male, F for Female, or O for Other.";
        }
		return "VALID";
	}

	public static List<Integer> selectSubjectsForCourse(int courseId, List<Subject> availableSubjects,
			Scanner scanner) {
		List<Integer> selectedSubjectIds = new ArrayList<>();

		List<Subject> mandatorySubjects = availableSubjects.stream()
				.filter(s -> "mandatory".equalsIgnoreCase(s.getSubject_type())).collect(Collectors.toList());

		List<Subject> electiveSubjects = availableSubjects.stream()
				.filter(s -> "elective".equalsIgnoreCase(s.getSubject_type())).collect(Collectors.toList());

		System.out.println("\nAvailable Subjects for Course ID " + courseId + ":");
		System.out.println("=".repeat(80));

		if (!mandatorySubjects.isEmpty()) {
			System.out.println("MANDATORY SUBJECTS (Must select all):");
			for (Subject subject : mandatorySubjects) {
				System.out.printf("ID: %-3d | %-40s | Type: %s\n", subject.getSubject_id(),
						subject.getSubject_name(), subject.getSubject_type());
			}
		}

		if (!electiveSubjects.isEmpty()) {
			System.out.println("\nELECTIVE SUBJECTS (Optional):");
			for (Subject subject : electiveSubjects) {
				System.out.printf("ID: %-3d | %-40s | Type: %s\n", subject.getSubject_id(),
						subject.getSubject_name(), subject.getSubject_type());
			}
		}

		// Auto-select mandatory subjects
		for (Subject subject : mandatorySubjects) {
			selectedSubjectIds.add(subject.getSubject_id());
			System.out.println("✓ Auto-selected: " + subject.getSubject_name());
		}

		// User selects elective subjects
		if (!electiveSubjects.isEmpty()) {
			System.out.print("\nEnter elective subject IDs (comma-separated, or press Enter to skip): ");
			String electiveInput = scanner.nextLine().trim();

			if (!electiveInput.isEmpty()) {
				try {
					String[] electiveIds = electiveInput.split(",");
					for (String idStr : electiveIds) {
						int electiveId = Integer.parseInt(idStr.trim());
						boolean isValid = electiveSubjects.stream().anyMatch(s -> s.getSubject_id() == electiveId);
						if (isValid) {
							selectedSubjectIds.add(electiveId);
							Subject selectedSubject = electiveSubjects.stream()
									.filter(s -> s.getSubject_id() == electiveId).findFirst().orElse(null);
							if (selectedSubject != null) {
								System.out.println("✓ Selected: " + selectedSubject.getSubject_name());
							}
						} else {
							System.out.println("Invalid elective subject ID: " + electiveId);
						}
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid input format. No elective subjects selected.");
				}
			} else {
				System.out.println("No elective subjects selected.");
			}
		}

		return selectedSubjectIds;
	}

}
