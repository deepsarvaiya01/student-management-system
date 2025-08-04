// 📁 DashboardController.java
package com.sms.controller;

import com.sms.service.DashboardService;

public class DashboardController {
    private DashboardService dashboardService;

    public DashboardController() {
        dashboardService = new DashboardService();
    }

    public void showGeneralDashboard() {
        dashboardService.displayGeneralDashboard();
    }

    public void showCourseDashboard() {
        dashboardService.displayCourseWiseDashboard();
    }

    public void showSubjectDashboard() {
        dashboardService.displaySubjectDashboard();
    }

    public void showTeacherDashboard() {
        dashboardService.displayTeacherLoadDashboard();
    }
}
