package com.sms.dto;

import java.sql.SQLException;
import java.util.List;

import com.sms.dao.TeacherDao;
import com.sms.model.Teacher;

public class TeacherDto {
    private final TeacherDao dao;

    public TeacherDto() throws SQLException {
        this.dao = new TeacherDao();
    }

    public boolean add(Teacher t) {
        if (t.getName().isEmpty() || t.getQualification().isEmpty() || t.getExperience() < 0)
            return false;
        return dao.addTeacher(t);
    }

    public List<Teacher> getAll() {
        return dao.getAllTeachers();
    }

    public boolean delete(int id) {
        return dao.softDeleteTeacher(id);
    }

    public boolean assign(int teacherId, int subjectId) {
        return dao.assignSubject(teacherId, subjectId);
    }

    public boolean remove(int teacherId, int subjectId) {
        return dao.removeSubject(teacherId, subjectId);
    }

    public List<String> getSubjects(int teacherId) {
        return dao.getAssignedSubjects(teacherId);
    }
}
