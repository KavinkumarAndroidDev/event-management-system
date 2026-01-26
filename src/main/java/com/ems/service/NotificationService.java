package com.ems.service;

public interface NotificationService {

    void sendSystemWideNotification(String message, String notificationType);

    void displayUnreadNotifications(int userId);

    void displayAllNotifications(int userId);

	void sendEventNotification(int eventId, String message, String string);
	

}
