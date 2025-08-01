package com.sms.main;

import java.sql.SQLException;

import com.sms.controller.DashboardController;

public class DashboardMain {

	public void show() throws SQLException {
		try {
			DashboardController controller = new DashboardController();
			controller.showDashboard();
		} catch (Exception e) {
			System.out.println("❌ Error initializing dashboard: " + e.getMessage());
		}
	}
}