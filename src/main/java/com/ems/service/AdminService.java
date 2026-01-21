package com.ems.service;

import java.util.List;

import com.ems.dao.*;
import com.ems.dao.impl.*;
import com.ems.model.Event;
import com.ems.model.User;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class AdminService {
	
	private static UserDao userDao = new UserDaoImpl();
	private static EventDao eventDao = new EventDaoImpl();
	private static NotificationDao notificationDao = new NotificationDaoImpl();
	private static RegistrationDao registrationDao = new RegistrationDaoImpl();
	
	public static void getUsersList(String userType) {
		List<User> users = userDao.findAllUsers(userType);
		users.stream().forEach(u -> System.out.println(u));
	}
	public static void changeStatus(String status) {
		int userId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the user id: ");
		userDao.updateUserStatus(userId, status);
	}
	
	public static void sendSystemWideNotification(String message, String notificationType) {
		NotificationService.sendSystemWideNotification(message, notificationType);
	}
	public static void approveEvents() throws Exception {
		List<Event> events = eventDao.listEventsYetToApprove();
		if(events.isEmpty()) {
			throw new Exception("There are no events yet to be approved!");
		}
		EventService.printEventSummaries(events);
		int eventId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "/n(Note: Future events only can be approved!)\nEnter the event id of the event to be approved: ");
		eventDao.approveEvent(eventId);
		notificationDao.sendNotification(eventDao.getOrganizerId(eventId), "Your event: " + eventId + " has been approved!", "EVENT");
	}
	public static void cancelEvents() throws Exception {
		List<Event> events = eventDao.listAvailableAndDraftEvents();
		if(events.isEmpty()) {
			throw new Exception("There are no approved events!");
		}
		EventService.printEventSummaries(events);
		int eventId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "/n(Note: Future events only can be cancelled!)\\nEnter the event id of the event to be cancelled: ");
		eventDao.cancelEvent(eventId);
		notificationDao.sendNotification(eventDao.getOrganizerId(eventId), "Your event: " + eventId + " has been cancelled!", "EVENT");

		//TODO: cancellation must have refunds if any tickets have been booked!
	}
	public static void getEventWiseRegistrations(int eventId) {
		registrationDao.getEventWiseRegistrations(eventId);
	}
	public static void getRevenueReport() {
		// TODO Auto-generated method stub
		
	}
	public static void getOrganizerWisePerformance() {
		// TODO Auto-generated method stub
		
	}
	
}
