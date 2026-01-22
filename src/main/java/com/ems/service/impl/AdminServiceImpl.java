package com.ems.service.impl;

import java.util.List;

import com.ems.dao.*;
import com.ems.enums.NotificationType;
import com.ems.model.Event;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class AdminServiceImpl implements AdminService {

    private final UserDao userDao;
    private final EventDao eventDao;
    private final NotificationDao notificationDao;
    private final RegistrationDao registrationDao;
    private final NotificationService notificationService;
    private final EventService eventService;

    public AdminServiceImpl(
            UserDao userDao,
            EventDao eventDao,
            NotificationDao notificationDao,
            RegistrationDao registrationDao,
            NotificationService notificationService,
            EventService eventService
    ) {
        this.userDao = userDao;
        this.eventDao = eventDao;
        this.notificationDao = notificationDao;
        this.registrationDao = registrationDao;
        this.notificationService = notificationService;
        this.eventService = eventService;
    }

    @Override
    public void getUsersList(String userType) {
        List<User> users = userDao.findAllUsers(userType);

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n==============================================================");
        System.out.printf(
            "%-5s %-20s %-10s %-25s %-15s %-10s%n",
            "ID", "Name", "Gender", "Email", "Phone", "Status"
        );
        System.out.println("==============================================================");

        users.forEach(user -> {
            System.out.printf(
                "%-5d %-20s %-10s %-25s %-15s %-10s%n",
                user.getUserId(),
                user.getFullName(),
                user.getGender(),
                user.getEmail(),
                user.getPhone() == null ? "-" : user.getPhone(),
                user.getStatus()
            );
        });

        System.out.println("==============================================================");
    }


    @Override
    public void changeStatus(String status) {
        int userId = InputValidationUtil.readInt(
                ScannerUtil.getScanner(), "Enter the user id: ");
        userDao.updateUserStatus(userId, status);
    }

    @Override
    public void sendSystemWideNotification(String message, String notificationType) {
        notificationService.sendSystemWideNotification(message, notificationType);
    }

    @Override
    public void approveEvents(int userId) throws Exception {
        List<Event> events = eventDao.listEventsYetToApprove();
        if (events.isEmpty()) {
            throw new Exception("There are no events yet to be approved!");
        }

        eventService.printEventSummaries(events);

        int eventId = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "\n(Note: Future events only can be approved!)\nEnter the event id of the event to be approved: "
        );

        boolean isApproved = eventDao.approveEvent(eventId, userId);
        if (isApproved) {
            notificationDao.sendNotification(
                    eventDao.getOrganizerId(eventId),
                    "Your event: " + eventId + " has been approved!",
                    "EVENT"
            );
        }
    }

    @Override
    public void cancelEvents() throws Exception {
        List<Event> events = eventDao.listAvailableAndDraftEvents();
        if (events.isEmpty()) {
            throw new Exception("There are no approved events!");
        }

        eventService.printEventSummaries(events);

        int eventId = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "\n(Note: Future events only can be cancelled!)\nEnter the event id of the event to be cancelled: "
        );

        boolean isCancelled = eventDao.cancelEvent(eventId);
        if (isCancelled) {
            notificationDao.sendNotification(
                    eventDao.getOrganizerId(eventId),
                    "Your event: " + eventId + " has been cancelled!",
                    "EVENT"
            );
        }
    }

    @Override
    public void getEventWiseRegistrations(int eventId) {
        registrationDao.getEventWiseRegistrations(eventId);
    }

    @Override
    public void getRevenueReport() {
    	System.out.println("Yet to be done!");
    }

    @Override
    public void getOrganizerWisePerformance() {
    	System.out.println("Yet to be done!");
    }

    @Override
    public void markCompletedEvents() {
        eventService.completeEvents();
    }

	@Override
	public void sendNotificationByRole() {
		System.out.println("\nAvailable roles:\n1. Users,\n2. Organizers,\n3. Admins");
		int roleId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the role id: ");
		while(roleId <1 || roleId > 3) {
			roleId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid role id: ");
		}
		String role;
		if(roleId == 1) role = "ATTENDEE";
		else if(roleId == 2) role ="ORGANIZER";
		else role ="ADMIN";
		System.out.println("available payment method:");
		NotificationType[] methods = NotificationType.values();

        for (int i = 0; i < methods.length; i++) {
            System.out.println(
                (i + 1) + ". " + methods[i].name().replace("_", " ")
            );
        }

        int choice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter choice (1-" + methods.length + "): "
        );

        while (choice < 1 || choice > methods.length) {
            System.out.println("Invalid notification type selected");
            choice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter choice (1-" + methods.length + "): "
            );
        }

        NotificationType selectedType = methods[choice - 1];
        
        String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the notification message: ");
        notificationDao.sendNotificationByRole(message, selectedType.toString(), role);
	}

	@Override
	public void sendNotificationToUser() {
		int userId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the user id: ");
		NotificationType[] methods = NotificationType.values();

        for (int i = 0; i < methods.length; i++) {
            System.out.println(
                (i + 1) + ". " + methods[i].name().replace("_", " ")
            );
        }

        int choice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter choice (1-" + methods.length + "): "
        );

        while (choice < 1 || choice > methods.length) {
            System.out.println("Invalid notification type selected");
            choice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter choice (1-" + methods.length + "): "
            );
        }

        NotificationType selectedType = methods[choice - 1];
        
        String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the notification message: ");
        notificationDao.sendNotification(userId,message,selectedType.toString());
	}

	@Override
	public void getAllUsers() {
		List<User> users = userDao.findAllUsers();

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("\n==============================================================");
        System.out.printf(
            "%-5s %-20s %-10s %-25s %-15s %-10s%n",
            "ID", "Name", "Gender", "Email", "Phone", "Status"
        );
        System.out.println("==============================================================");

        users.forEach(user -> {
            System.out.printf(
                "%-5d %-20s %-10s %-25s %-15s %-10s%n",
                user.getUserId(),
                user.getFullName(),
                user.getGender(),
                user.getEmail(),
                user.getPhone() == null ? "-" : user.getPhone(),
                user.getStatus()
            );
        });

        System.out.println("==============================================================");
		
	}
}
