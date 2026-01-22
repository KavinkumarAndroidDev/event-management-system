package com.ems.service.impl;

import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.model.Notification;
import com.ems.service.NotificationService;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDao notificationDao;

    public NotificationServiceImpl(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }

    @Override
    public void sendSystemWideNotification(String message, String notificationType) {
        notificationDao.sendSystemWideNotification(message, notificationType);
        System.out.println("The message has been sent to all users");
    }

    @Override
    public void displayUnreadNotifications(int userId) {
        List<Notification> notifications =
                notificationDao.getUnreadNotifications(userId);

        if (!notifications.isEmpty()) {
            System.out.println("\nYou have few unread notifications: ");
            notifications.forEach(System.out::println);
            notifications.forEach(
                n -> notificationDao.markAsRead(n.getNotificationId())
            );
        }
    }

    @Override
    public void displayAllNotifications(int userId) {
        List<Notification> notifications =
                notificationDao.getAllNotifications(userId);

        if (!notifications.isEmpty()) {
            System.out.println("\nNotifications: ");
            notifications.forEach(System.out::println);
            notificationDao.markAllAsRead(userId);
        } else {
            System.out.println("\nNo notifications");
        }
    }
}
