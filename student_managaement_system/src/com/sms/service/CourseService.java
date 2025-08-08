package com.sms.service;

import java.sql.SQLException;
import java.util.List;

import com.sms.dao.CourseDAO;
import com.sms.model.Course;
import com.sms.model.Subject;

public class CourseService {
	private final CourseDAO courseDAO;

	public CourseService() throws SQLException {
		this.courseDAO = new CourseDAO();
	}

	public int addCourse(Course course) throws SQLException {
		return courseDAO.addCourse(course);
	}

	public void assignSubjectToCourse(int courseId, int subjectId) throws SQLException {
		courseDAO.assignSubjectToCourse(courseId, subjectId);
	}

	public Course getCourseById(int id) {
		return courseDAO.getCourseById(id);
	}

	public boolean deleteCourseById(int id) throws SQLException {
		return courseDAO.deleteCourse(id);
	}

	public List<Subject> getSubjectsForCourse(int courseId) {
		return courseDAO.getSubjectsByCourseId(courseId);
	}

	public List<Course> getAllCourses() {
		return courseDAO.getAllCourses();
	}

	public Course getCourseByName(String name) {
		return courseDAO.getCourseByName(name);
	}

	public List<Subject> getUnassignedSubjectsForCourse(int courseId) {
		return courseDAO.getUnassignedSubjectsForCourse(courseId);
	}

	public boolean updateCourseFees(Course course) {
		if (course == null || course.getCourse_id() <= 0 || course.getTotal_fee() == null) {
			return false;
		}
		return courseDAO.updateCourseFees(course.getCourse_id(), course.getTotal_fee());
	}

}
