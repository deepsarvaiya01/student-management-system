package com.sms.service;

import java.util.List;

import com.sms.dao.FeeNotifierDao;
import com.sms.model.FeeNotifier;

public class FeeNotifierService {

	private FeeNotifierDao feeNotifierDao;

	public FeeNotifierService() {
		this.feeNotifierDao = new FeeNotifierDao();
	}

	public FeeNotifier getPreferences(int studentId) {
		return feeNotifierDao.getPreferences(studentId);
	}

	public boolean updatePreferences(FeeNotifier prefs) {
		return feeNotifierDao.updatePreferences(prefs);
	}

	public boolean createDefaultPreferences(int studentId) {
		return feeNotifierDao.insertDefaultPreferences(studentId);
	}
	
	public List<com.sms.model.Student> getAllStudents() {
		return feeNotifierDao.getAllStudents();
	}

}
