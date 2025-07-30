package com.sms.service;

import java.sql.SQLException;
import java.util.List;

import com.sms.dao.TeacherDao;
import com.sms.model.Teacher;

public class TeacherService {
	private final TeacherDao dao;

	public TeacherService() throws SQLException {
		this.dao = new TeacherDao();
	}

	public boolean addTeacher(Teacher teacher) {
		if (teacher.getName().isEmpty() || teacher.getQualification().isEmpty() || teacher.getExperience() < 0)
			return false;
		return dao.addTeacher(teacher);
	}

	public List<Teacher> fetchAllTeachers() {
		return dao.getAllTeachers();
	}

	public boolean deleteTeacher(int id) {
		return dao.softDeleteTeacher(id);
	}

	public boolean assignSubject(int teacherId, int subjectId) {
		return dao.assignSubject(teacherId, subjectId);
	}

	public boolean removeSubject(int teacherId, int subjectId) {
		return dao.removeSubject(teacherId, subjectId);
	}

	public List<String> viewAssignedSubjects(int teacherId) {
		return dao.getAssignedSubjects(teacherId);
	}

	public List<String> getAssignedSubjectsForTeacher(int teacherId) {
		return dao.getAssignedSubjects(teacherId);
	}

	public Teacher getTeacherById(int id) {
		return dao.getTeacherById(id);
	}

}
