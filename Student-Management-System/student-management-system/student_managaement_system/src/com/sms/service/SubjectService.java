package com.sms.service;

import java.sql.SQLException;
import java.util.List;

import com.sms.dao.SubjectDAO;
import com.sms.model.Subject;

public class SubjectService {
    private final SubjectDAO subjectDAO;

    public SubjectService() throws SQLException {
        this.subjectDAO = new SubjectDAO();
    }

    public List<Subject> getAllSubjects() throws SQLException {
        return subjectDAO.getAllSubjects();
    }

    public int addSubject(String subjectName, String subjectType) {
        Subject subject = new Subject();
        subject.setSubject_name(subjectName);
        subject.setSubject_type(subjectType);
        subject.setActive(1); 
        return subjectDAO.addSubject(subject);
    }

    public boolean subjectExists(int id) {
        try {
            return subjectDAO.getSubjectById(id) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateSubject(int id, String name, String type) {
        Subject subject = new Subject();
        subject.setSubject_id(id);
        subject.setSubject_name(name);
        subject.setSubject_type(type);
        return subjectDAO.updateSubject(subject);
    }

    public boolean deleteSubject(int id) {
        return subjectDAO.softDeleteSubject(id);
    }
}
