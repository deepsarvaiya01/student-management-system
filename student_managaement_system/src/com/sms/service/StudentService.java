package com.sms.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sms.dao.StudentDao;
import com.sms.model.Course;
import com.sms.model.Student;

public class StudentService {
	private StudentDao studentDao;

	public StudentService() throws SQLException {
		super();
		this.studentDao = new StudentDao();
	}

	public List<Student> readAllStudent() {
		return studentDao.readAllStudents();
	}

	public List<Course> readAllCourses(int studentId) {
		if (studentDao.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
			return new ArrayList<>();
		}
		return studentDao.readAllCourses(studentId);
	}

	public List<Course> getAllCourses() {
		return studentDao.getAllCourses();
	}

	public boolean assignCourseToStudent(int studentId, int courseId) {
		if (studentDao.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
			return false;
		}
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("No such course exists.");
			return false;
		}
		if (readAllCourses(studentId).stream().anyMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Course already assigned to student.");
			return false;
		}
		return studentDao.assignCourseToStudent(studentId, courseId);
	}

	public Student searchStudentById(int studentId) {
		return studentDao.searchStudentById(studentId);
	}

	public boolean deleteStudentById(int studentId) {
		if (studentDao.searchStudentById(studentId) == null) {
			System.out.println("No such student exists.");
			return false;
		}
		return studentDao.deleteStudentById(studentId);
	}

	public boolean addStudentWithProfileAndCourse(Student student, int courseId) {
		if (student == null || student.getName() == null || !student.getName().matches("[a-zA-Z ]{1,50}")) {
			System.out.println("Invalid name (letters/spaces, max 50 chars).");
			return false;
		}
		if (student.getGr_number() <= 0 || String.valueOf(student.getGr_number()).length() < 4
				|| String.valueOf(student.getGr_number()).length() > 10
				|| readAllStudent().stream().anyMatch(s -> s.getGr_number() == student.getGr_number())) {
			System.out.println("Invalid or duplicate GR number (4-10 digits).");
			return false;
		}
		if (student.getEmail() == null || !student.getEmail().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
				|| student.getEmail().length() > 100
				|| readAllStudent().stream().anyMatch(s -> s.getEmail().equals(student.getEmail()))) {
			System.out.println("Invalid or duplicate email.");
			return false;
		}
		if (student.getCity() == null || !student.getCity().matches("[a-zA-Z ]{1,50}")) {
			System.out.println("Invalid city (letters/spaces, max 50 chars).");
			return false;
		}
		if (student.getMobile_no() == null || !student.getMobile_no().matches("\\d{10}")
				|| student.getMobile_no().length() > 15) {
			System.out.println("Invalid mobile number (10 digits).");
			return false;
		}
		if (student.getAge() < 15 || student.getAge() > 100) {
			System.out.println("Invalid age (15-100).");
			return false;
		}
		if (courseId <= 0 || getAllCourses().stream().noneMatch(c -> c.getCourse_id() == courseId)) {
			System.out.println("Invalid or non-existent course ID: " + courseId);
			return false;
		}
		return studentDao.addStudentWithProfileAndCourse(student, courseId);
	}

	public boolean restoreStudentById(int studentId) {
		return studentDao.restoreStudentById(studentId);
	}
}
