package com.ems.actions;

import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.util.ApplicationUtil;

/**
 * Action class for notification operations.
 * Delegates business logic to appropriate services.
 * This action is shared by both Admin and Organizer menus.
 */
public class NotificationAction {

    private final NotificationService notificationService;
    private final OrganizerService organizerService;

    public NotificationAction() {
        this.notificationService = ApplicationUtil.notificationService();
        this.organizerService = ApplicationUtil.organizerService();
    }

    /**
     * Displays unread notifications for a user.
     * 
     * @param userId the ID of the user
     */
    public void displayUnreadNotifications(int userId) {
        notificationService.displayUnreadNotifications(userId);
    }

    /**
     * Displays all notifications for a user.
     * 
     * @param userId the ID of the user
     */
    public void displayAllNotifications(int userId) {
        notificationService.displayAllNotifications(userId);
    }

    /**
     * Sends an event update notification to all registered attendees.
     * 
     * @param eventId the ID of the event
     * @param message the message to send
     */
    public void sendEventUpdate(int eventId, String message) {
        organizerService.sendEventUpdate(eventId, message);
    }

    /**
     * Sends a schedule change notification to all registered attendees.
     * 
     * @param eventId the ID of the event
     * @param message the message to send
     */
    public void sendScheduleChange(int eventId, String message) {
        organizerService.sendScheduleChange(eventId, message);
    }
}
