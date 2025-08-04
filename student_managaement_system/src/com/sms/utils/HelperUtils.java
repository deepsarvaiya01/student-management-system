package com.sms.utils;

import java.util.List;

import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.model.Subject;

public class HelperUtils {

	public static void printStudents(List<Student> students) {
		String line = "+------------+----------------------+---------------------------+------------+";
		String format = "| %-10s | %-20s | %-25s | %-10s |%n";

		System.out.println("\n📚 List of Students:");
		System.out.println(line);
		System.out.printf(format, "Student ID", "Name", "Email", "GR Number");
		System.out.println(line);

		for (Student s : students) {
			System.out.printf(format, s.getStudent_id(), s.getName(), s.getEmail(), s.getGr_number());
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

		System.out.println("\n🎓 List of Courses");
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

		String line = "+------+----------------------+";
		String format = "| %-4s | %-20s |%n";

		System.out.println("\n📚 List of Subjects");
		System.out.println(line);
		System.out.printf(format, "ID", "Subject Name");
		System.out.println(line);

		for (Subject subject : subjects) {
			System.out.printf(format, subject.getSubject_id(), subject.getSubject_name());
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
		return "VALID";
	}
}
