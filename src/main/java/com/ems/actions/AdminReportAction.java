package com.ems.actions;

import java.util.List;

import com.ems.model.OrganizerEventSummary;
import com.ems.service.AdminService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

public class AdminReportAction {
    private final AdminService adminService;
    private final OrganizerService organizerService;

    public AdminReportAction() {
        this.adminService = ApplicationUtil.adminService();
        this.organizerService = ApplicationUtil.organizerService();
    }

    public void getEventWiseRegistrations(int eventId) {
        adminService.getEventWiseRegistrations(eventId);
    }

    public List<OrganizerEventSummary> getOrganizerEventSummary(int organizerId) {
        return organizerService.getOrganizerEventSummary(organizerId);
    }

    public void getRevenueReport() {
        adminService.getRevenueReport();
    }
}