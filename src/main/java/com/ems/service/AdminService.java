package com.ems.service;

public interface AdminService {

    void getUsersList(String userType);
    
    void getAllUsers();

    void changeStatus(String status);

    void sendSystemWideNotification(String message, String notificationType);

    void approveEvents(int userId);

    void cancelEvents();

    void getEventWiseRegistrations();

    void getRevenueReport();

    void getOrganizerWisePerformance();

    void markCompletedEvents();

	void sendNotificationByRole();

	void sendNotificationToUser();

	void printAllEvents();
}
