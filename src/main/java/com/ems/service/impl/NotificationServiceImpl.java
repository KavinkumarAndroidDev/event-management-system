package com.ems.service.impl;

import java.util.Comparator;
import java.util.List;

import com.ems.dao.NotificationDao;
import com.ems.dao.RegistrationDao;
import com.ems.exception.DataAccessException;
import com.ems.model.Notification;
import com.ems.service.NotificationService;
import com.ems.service.SystemLogService;

/*
 * Handles notification related business operations.
 *
 * Responsibilities:
 * - Send notifications to users and groups
 * - Display user notifications
 * - Maintain read and unread notification states
 */
public class NotificationServiceImpl implements NotificationService {

	private final NotificationDao notificationDao;
	private final RegistrationDao registrationDao;
	private final SystemLogService systemLogService;

	/*
	 * Initializes notification service with required data access dependencies.
	 */
	public NotificationServiceImpl(NotificationDao notificationDao, RegistrationDao registrationDao, SystemLogService systemLogService) {
		this.notificationDao = notificationDao;
		this.registrationDao = registrationDao;
		this.systemLogService = systemLogService;
	}

	/*
	 * Sends a notification to all users in the system.
	 *
	 * Used for system level or promotional announcements.
	 */
	@Override
	public void sendSystemWideNotification(String message, String notificationType) {
		try {
			notificationDao.sendSystemWideNotification(message, notificationType);
			
			systemLogService.log(
				    null,
				    "SEND_NOTIFICATION",
				    "SYSTEM",
				    null,
				    "System-wide notification sent"
				);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("The message has been sent to all users");
	}

	/*
	 * Displays unread notifications for a user.
	 *
	 * Rule: - Notifications are automatically marked as read after display
	 */
	@Override
	public void displayUnreadNotifications(int userId) {
		try {
			List<Notification> notifications = notificationDao.getUnreadNotifications(userId);

			if (!notifications.isEmpty()) {
				System.out.println("\nYou have few unread notifications: ");
				notifications.sort(Comparator.comparing(Notification::getCreatedAt).reversed());
				notifications.forEach(System.out::println);
				// Mark notifications as read after viewing
				notifications.forEach(n -> {
					try {
						notificationDao.markAsRead(n.getNotificationId());
					} catch (DataAccessException e) {
						System.out.println(e.getMessage());
					}
				});
			} else {
				System.out.println("No unread notifications");
				return;
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Displays all notifications for a user.
	 *
	 * Rule: - All notifications are marked as read once displayed
	 */
	@Override
	public void displayAllNotifications(int userId) {
		try {
			List<Notification> notifications = notificationDao.getAllNotifications(userId);

			if (!notifications.isEmpty()) {
				System.out.println("\nNotifications: ");
				notifications.forEach(System.out::println);
				notificationDao.markAllAsRead(userId);
			} else {
				System.out.println("\nNo notifications");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Sends a notification to all users registered for a specific event.
	 *
	 * Used for event updates or schedule changes.
	 */
	@Override
	public void sendEventNotification(int eventId, String message, String type) {
		try {
			List<Integer> userIds = registrationDao.getRegisteredUserIdsByEvent(eventId);

			for (Integer userId : userIds) {
				notificationDao.sendNotification(userId, message, type);
			}
			systemLogService.log(
				    null,
				    "SEND_NOTIFICATION",
				    "EVENT",
				    eventId,
				    "Event notification sent to registered users"
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

}
