package com.ems.service.impl;

import java.util.Comparator;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;
import com.ems.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;

    public NotificationServiceImpl(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    @Override
    public void sendSystemWideNotification(String message, String notificationType) {
        try {
			notificationDao.sendSystemWideNotification(message, notificationType);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        System.out.println("The message has been sent to all users");
    }

    @Override
    public void displayUnreadNotifications(int userId) {
    	try {
	        List<Notification> notifications =
	                notificationDao.getUnreadNotifications(userId);
	
	        if (!notifications.isEmpty()) {
	            System.out.println("\nYou have few unread notifications: ");
	            notifications.sort(
	            	    Comparator.comparing(Notification::getCreatedAt).reversed()
	            	);
	            notifications.forEach(System.out::println);
	            notifications.forEach(
	                n -> {
						try {
							notificationDao.markAsRead(n.getNotificationId());
						} catch (DataAccessException e) {
							System.out.println(e.getMessage());
						}
					}
	            );
	        }else {
	        	System.out.println("No unread notifications");
	            return;
	        }
    	}catch(DataAccessException e) {
    		System.out.println(e.getMessage());
    	}
    }

    @Override
    public void displayAllNotifications(int userId) {
    	try {
    		List<Notification> notifications =notificationDao.getAllNotifications(userId);

	        if (!notifications.isEmpty()) {
	            System.out.println("\nNotifications: ");
	            notifications.forEach(System.out::println);
	            notificationDao.markAllAsRead(userId);
	        } else {
	            System.out.println("\nNo notifications");
	        }
	    }catch(DataAccessException e) {
	    	System.out.println(e.getMessage());
	    }
    }
}
