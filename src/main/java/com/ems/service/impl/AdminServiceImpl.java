package com.ems.service.impl;

import java.util.List;

import com.ems.dao.*;
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
        users.forEach(System.out::println);
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
    }

    @Override
    public void getOrganizerWisePerformance() {
    }

    @Override
    public void markCompletedEvents() {
        eventService.completeEvents();
    }
}
