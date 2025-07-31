package com.sms.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sms.dao.TeacherDao;
import com.sms.model.Teacher;

public class TeacherService {

	private final TeacherDao dao;

	public TeacherService() throws SQLException {
		this.dao = new TeacherDao();
	}

	public boolean addTeacher(Teacher t) {
		return dao.addTeacher(t);
	}

	public List<Teacher> fetchAllTeachers() {
		return dao.getAllTeachers();
	}

	public boolean deleteTeacher(int id) {
		if (!dao.isTeacherActive(id)) {
//			System.out.println("Teacher not found or already deleted.");
			return false;
		}
		return dao.softDeleteTeacher(id);
	}

	public boolean assignSubject(int teacherId, int subjectId) {
		if (!dao.isTeacherActive(teacherId)) {
			System.out.println("Inactive or invalid teacher.");
			return false;
		}
		Map<Integer, String> assigned = dao.getAssignedSubjects(teacherId);
		if (assigned.containsKey(subjectId)) {
			System.out.println("Subject already assigned.");
			return false;
		}
		if (assigned.size() >= 3) {
			System.out.println("Cannot assign more than 3 subjects.");
			return false;
		}
		return dao.assignSubject(teacherId, subjectId);
	}

	public boolean removeSubject(int teacherId, int subjectId) {
		if (!dao.isTeacherActive(teacherId)) {
			System.out.println("Inactive or invalid teacher.");
			return false;
		}
		Map<Integer, String> assigned = dao.getAssignedSubjects(teacherId);
		if (!assigned.containsKey(subjectId)) {
			System.out.println("Subject not assigned to this teacher.");
			return false;
		}
		return dao.removeSubject(teacherId, subjectId);
	}

	public Map<Integer, String> viewAssignedSubjects(int teacherId) {
		if (!dao.isTeacherActive(teacherId))
			return Map.of();
		return dao.getAssignedSubjects(teacherId);
	}

	public Map<Integer, String> getAvailableSubjects(int teacherId) {
		if (!dao.isTeacherActive(teacherId))
			return Map.of();
		return dao.fetchAvailableSubjectsForTeacher(teacherId);
	}

	public Teacher getTeacherById(int id) {
		return dao.getTeacherById(id);
	}

	public boolean isTeacherActive(int teacherId) {
		return dao.isTeacherActive(teacherId);
	}
	
	
}
