package com.ems.service;

public interface AdminService {

    void getUsersList(String userType);
    
    void getAllUsers();

    void changeStatus(String status);

    void sendSystemWideNotification(String message, String notificationType);

    void approveEvents(int userId) throws Exception;

    void cancelEvents() throws Exception;

    void getEventWiseRegistrations(int eventId);

    void getRevenueReport();

    void getOrganizerWisePerformance();

    void markCompletedEvents();

	void sendNotificationByRole();

	void sendNotificationToUser();
}
