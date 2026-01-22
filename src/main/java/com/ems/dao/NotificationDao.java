package com.ems.dao;

import java.util.List;

import com.ems.exception.DataAccessException;
import com.ems.model.Notification;

public interface NotificationDao {

	List<Notification> getUnreadNotifications(int userId) throws DataAccessException;
	
	void markAsRead(int notificationId) throws DataAccessException;
	
	List<Notification> getAllNotifications(int userId) throws DataAccessException;
	
	void markAllAsRead(int userId) throws DataAccessException;
	
	void sendNotification(int userId, String message, String notificationType) throws DataAccessException;
	
	void sendSystemWideNotification(String message, String notificationType) throws DataAccessException;
	
	void sendNotificationByRole(String message, String notificationType, String role) throws DataAccessException;
}
