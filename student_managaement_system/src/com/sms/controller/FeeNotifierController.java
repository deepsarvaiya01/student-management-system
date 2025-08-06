package com.sms.controller;

import java.util.List;
import java.util.Scanner;

import com.sms.model.Course;
import com.sms.model.FeeNotifier;
import com.sms.model.Student;
import com.sms.service.FeeNotifierService;
import com.sms.utils.HelperUtils;

public class FeeNotifierController {

	private FeeNotifierService service;
	Scanner scanner = new Scanner(System.in);

	public FeeNotifierController() {
		this.service = new FeeNotifierService();
	}

	public FeeNotifier getOrCreatePreferences(int studentId) {
		FeeNotifier prefs = service.getPreferences(studentId);

		if (prefs == null) {
			System.out.println("⚠️ Preferences not found. Creating default preferences...");
			boolean created = service.createDefaultPreferences(studentId);
			if (!created) {
				System.out.println("❌ Student ID may not exist. Aborting.");
				return null;
			}
			prefs = service.getPreferences(studentId);
		}
		return prefs;
	}

	public boolean updatePreferences(FeeNotifier prefs) {
		return service.updatePreferences(prefs);
	}

	public void showStudents() {
		List<Student> students = service.getAllStudents();
		if (students.isEmpty()) {
			System.out.println("No students available.");
			return;
		}

		HelperUtils.printStudents(students);
	}
}