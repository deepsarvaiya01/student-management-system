package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.sms.dao.StudentDao;
import com.sms.model.Course;
import com.sms.model.Student;
import com.sms.model.Subject;

public class StudentService {
	private StudentDao studentDao;

	public StudentService() throws SQLException {
		super();
		this.studentDao = new StudentDao();
	}

	public List<Student> readAllStudent() {
		return studentDao.readAllStudents();
	}

	public String readAllCourses(int studentId) {
		if (studentId <= 0) {
			return "Invalid student ID.";
		}
		if (studentDao.searchStudentById(studentId) == null) {
			return "No such student exists.";
		}
		List<Course> courses = studentDao.readAllCourses(studentId);
		if (courses.isEmpty()) {
			return "No courses assigned to student ID: " + studentId;
		}
		return "SUCCESS"; // Special return value to indicate success
	}

	public List<Course> getAllCourses() {
		return studentDao.getAllCourses();
	}

	public String assignCourseToStudent(int studentId, int courseId, List<Integer> subjectIds) {
		if (studentId <= 0) {
			return "Invalid student ID.";
		}
		if (studentDao.searchStudentById(studentId) == null) {
			return "No such student exists.";
		}
		if (courseId <= 0) {
			return "Invalid course ID.";
		}
		if (getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			return "No such course exists.";
		}
		List<Course> existingCourses = studentDao.readAllCourses(studentId);
		if (existingCourses.stream().anyMatch(c -> c.getCourse_id() == courseId)) {
			return "Course already assigned to student.";
		}
		if (subjectIds == null || subjectIds.isEmpty()) {
			return "At least one subject must be selected.";
		}
		List<Subject> availableSubjects = getSubjectsByCourseId(courseId);
		List<Integer> availableSubjectIds = availableSubjects.stream().map(Subject::getSubject_id)
				.collect(Collectors.toList());
		for (Integer subjectId : subjectIds) {
			if (!availableSubjectIds.contains(subjectId)) {
				return "Invalid subject ID " + subjectId + " for course ID " + courseId;
			}
		}
		boolean success = studentDao.assignCourseAndSubjectsToStudent(studentId, courseId, subjectIds);
		if (!success) {
			return "Failed to assign course and subjects. Database error.";
		}
		return "Course ID " + courseId + " with " + subjectIds.size() + " subjects assigned to student ID " + studentId
				+ " successfully.";
	}

	public String searchStudentById(int studentId) {
		if (studentId <= 0) {
			return "Invalid student ID.";
		}
		Student student = studentDao.searchStudentById(studentId);
		if (student == null) {
			return "No such student exists.";
		}
		return "SUCCESS"; // Special return value to indicate success
	}

	public String deleteStudentById(int studentId) {
		if (studentId <= 0) {
			return "Invalid student ID.";
		}
		if (studentDao.searchStudentById(studentId) == null) {
			return "No such student exists.";
		}
		boolean success = studentDao.deleteStudentById(studentId);
		if (!success) {
			return "Failed to mark student ID " + studentId + " as inactive. Check database.";
		}
		return "Student ID " + studentId + " marked as inactive successfully.";
	}

	public String addStudentWithProfileAndCourse(Student student, int courseId) {
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
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			return "Invalid or non-existent course ID: " + courseId;
		}
		// Check for duplicate GR number and email in DB
		boolean success = studentDao.addStudentWithProfileAndCourse(student, courseId);
		if (!success) {
			return "Duplicate GR number or email, or invalid course.";
		}
		return "Student added and course assigned successfully.";
	}

	public String restoreStudentById(int studentId) {
		if (studentId <= 0) {
			return "Invalid student ID.";
		}
		boolean success = studentDao.restoreStudentById(studentId);
		if (!success) {
			return "No such student exists or is already active.";
		}
		return "Student ID " + studentId + " restored successfully.";
	}

	// Helper method to get student object for display purposes
	public Student getStudentById(int studentId) {
		return studentDao.searchStudentById(studentId);
	}

	// Helper method to get courses for display purposes
	public List<Course> getCoursesByStudentId(int studentId) {
		return studentDao.readAllCourses(studentId);
	}

	// Get subjects by course ID
	public List<Subject> getSubjectsByCourseId(int courseId) {
		return studentDao.getSubjectsByCourseId(courseId);
	}

	// Add student with profile, course, and subjects
	public String addStudentWithProfileAndCourseAndSubjects(Student student, int courseId, List<Integer> subjectIds) {
		// First validate the student data
		String validationResult = validateStudentData(student, courseId);
		if (!validationResult.equals("VALID")) {
			return validationResult;
		}

		// Validate subject IDs
		if (subjectIds == null || subjectIds.isEmpty()) {
			return "At least one subject must be selected.";
		}

		// Check if all subject IDs are valid for the course
		List<Subject> availableSubjects = getSubjectsByCourseId(courseId);
		List<Integer> availableSubjectIds = availableSubjects.stream().map(Subject::getSubject_id)
				.collect(Collectors.toList());

		for (Integer subjectId : subjectIds) {
			if (!availableSubjectIds.contains(subjectId)) {
				return "Invalid subject ID " + subjectId + " for course ID " + courseId;
			}
		}

		// Add student with subjects
		boolean success = studentDao.addStudentWithProfileAndCourseAndSubjects(student, courseId, subjectIds);
		if (!success) {
			return "Failed to add student. Check for duplicate GR number or email.";
		}
		return "Student added successfully with " + subjectIds.size() + " subjects assigned.";
	}

	// Helper method to validate student data
	private String validateStudentData(Student student, int courseId) {
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
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			return "Invalid or non-existent course ID: " + courseId;
		}
		return "VALID";
	}
}
