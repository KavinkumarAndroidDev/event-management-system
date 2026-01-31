package com.ems.actions;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.service.AdminService;
import com.ems.util.ApplicationUtil;

public class AdminNotificationManagementAction {
    private final AdminService adminService;

    public AdminNotificationManagementAction() {
        this.adminService = ApplicationUtil.adminService();
    }

    public void sendSystemWideNotification(String message, String type) {
        adminService.sendSystemWideNotification(message, type);
    }

    public void sendNotificationByRole(String message, NotificationType type, UserRole role) {
        adminService.sendNotificationByRole(message, type, role);
    }

    public void sendNotificationToUser(String message, NotificationType type, int userId) {
        adminService.sendNotificationToUser(message, type, userId);
    }
}