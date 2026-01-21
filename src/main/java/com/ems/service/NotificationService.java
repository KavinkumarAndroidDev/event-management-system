package com.ems.service;

import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.dao.impl.NotificationDaoImpl;
import com.ems.model.Notification;

public class NotificationService {
	private static NotificationDao notificationDao = new NotificationDaoImpl();
	
	public static void sendSystemWideNotification(String message, String notificationType) {
		notificationDao.sendSystemWideNotification(message, notificationType);
		System.out.println("The message has been sent to all users");
	}
	
	public static void displayUnreadNotifications(int userId) {
		List<Notification> notifications = notificationDao.getUnreadNotifications(userId);
		if(!notifications.isEmpty()) {
			System.out.println("\nYou have few unread notifications: ");
			notifications.forEach(n -> System.out.println(n));
			notifications.forEach(n -> notificationDao.markAsRead(n.getNotificationId()));
		}
		
	}
	public static void displayAllNotifications(int userId) {
		List<Notification> notifications = notificationDao.getAllNotifications(userId);
		if(!notifications.isEmpty()) {
			System.out.println("\nNotifications: ");
			notifications.forEach(n -> System.out.println(n));
			notifications.forEach(n -> notificationDao.markAllAsRead(userId));
		}else {
			System.out.println("\nNo notifications");
			return;
		}
	}
}
