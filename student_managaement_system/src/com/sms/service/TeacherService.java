package com.sms.service;

import java.sql.SQLException;
import java.util.List;

import com.sms.dto.TeacherDto;
import com.sms.model.Teacher;

public class TeacherService {
	private final TeacherDto dto;

	public TeacherService() throws SQLException {
		this.dto = new TeacherDto();
	}

	public boolean addTeacher(Teacher teacher) {
		return dto.add(teacher);
	}

	public List<Teacher> fetchAllTeachers() {
		return dto.getAll();
	}

	public boolean deleteTeacher(int id) {
		return dto.delete(id);
	}

	public boolean assignSubject(int teacherId, int subjectId) {
		return dto.assign(teacherId, subjectId);
	}

	public boolean removeSubject(int teacherId, int subjectId) {
		return dto.remove(teacherId, subjectId);
	}

	public List<String> viewAssignedSubjects(int teacherId) {
		return dto.getSubjects(teacherId);
	}
}
