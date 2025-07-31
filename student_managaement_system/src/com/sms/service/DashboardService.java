package com.sms.service;

import java.util.List;

import com.sms.dao.DashboardDao;
import com.sms.model.DashboardModel;

public class DashboardService {

	private DashboardDao dao = new DashboardDao();

    public List<DashboardModel> getDashboardView() {
        return dao.getDashboardData();
    }

}
