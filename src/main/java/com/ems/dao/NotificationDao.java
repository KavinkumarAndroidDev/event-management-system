package com.ems.dao;

import java.util.List;

import com.ems.model.Notification;

public interface NotificationDao {

	List<Notification> getUnreadNotifications(int userId);
	void markAsRead(int notificationId);
	List<Notification> getAllNotifications(int userId);
	void markAllAsRead(int userId);
	void sendNotification(int userId, String message, String notificationType);
	void sendSystemWideNotification(String message, String notificationType);
	void sendNotificationByRole(String message, String notificationType, String role);
}
