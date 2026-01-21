package com.ems.menu;

import com.ems.model.User;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class OrganizerMenu {
	User loggedInUser;
	
	public OrganizerMenu(User user){
		this.loggedInUser = user;
		this.start();
	}
	private void start() {

	    NotificationService.displayUnreadNotifications(loggedInUser.getUserId());
	
	    while (true) {
	        System.out.println(
	            "\nOrganizer Menu\n" +
	            "1. Event Management\n" +
	            "2. Ticket Management\n" +
	            "3. Registrations\n" +
	            "4. Reports\n" +
	            "5. Notifications\n" +
	            "6. Logout\n"
	            + "\n>"
	        );
	
	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );
	
	        switch (choice) {
	            case 1:
	                eventManagementMenu();
	                break;
	            case 2:
	                ticketManagementMenu();
	                break;
	            case 3:
	                registrationMenu();
	                break;
	            case 4:
	                reportMenu();
	                break;
	            case 5:
	                notificationMenu();
	                break;
	            case 6:
	                if (confirmLogout()) {
	                    return;
	                }
	                break;
	            default:
	                System.out.println("Invalid option");
	        }
	    }  
	}
	private void eventManagementMenu() {

	    while (true) {
	        System.out.println(
	            "\nEvent Management\n" +
	            "1. Create new event\n" +
	            "2. Update event details\n" +
	            "3. Update event schedule\n" +
	            "4. Update event capacity\n" +
	            "5. Publish event\n" +
	            "6. Cancel event\n" +
	            "7. Back\n"
	            + "\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	                EventService.createEvent();
	                break;
	            case 2:
	                EventService.updateEventDetails();
	                break;
	            case 3:
	                EventService.updateEventSchedule();
	                break;
	            case 4:
	                EventService.updateEventCapacity();
	                break;
	            case 5:
	                EventService.publishEvent();
	                break;
	            case 6:
	                EventService.cancelEvent();
	                break;
	            case 7:
	                return;
	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void ticketManagementMenu() {

	    while (true) {
	        System.out.println(
	            "\nTicket Management\n" +
	            "1. Create ticket type\n" +
	            "2. Update ticket price\n" +
	            "3. Update ticket quantity\n" +
	            "4. View ticket availability\n" +
	            "5. Back\n"
	            + "\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	                EventService.createTicket();
	                break;
	            case 2:
	            	EventService.updateTicketPrice();
	                break;
	            case 3:
	            	EventService.updateTicketQuantity();
	                break;
	            case 4:
	            	EventService.viewTicketAvailability();
	                break;
	            case 5:
	                return;
	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void registrationMenu() {

	    while (true) {
	        System.out.println(
	            "\nRegistrations\n" +
	            "1. View event registrations\n" +
	            "2. View registered users\n" +
	            "3. Back\n"
	            + "\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	                OrganizerService.viewEventRegistrations();
	                break;
	            case 2:
	                OrganizerService.viewRegisteredUsers();
	                break;
	            case 3:
	                return;
	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void reportMenu() {

	    while (true) {
	        System.out.println(
	            "\nReports\n" +
	            "1. View event registrations\n" +
	            "2. View ticket sales\n" +
	            "3. View revenue summary\n" +
	            "4. Back\n"
	            + "\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	                OrganizerService.getEventWiseRegistrations();
	                break;
	            case 2:
	                OrganizerService.getTicketSales();
	                break;
	            case 3:
	                OrganizerService.getRevenueSummary();
	                break;
	            case 4:
	                return;
	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void notificationMenu() {

	    while (true) {
	        System.out.println(
	            "\nNotifications\n" +
	            "1. Send event update\n" +
	            "2. Send schedule change\n" +
	            "3. View my notifications\n" +
	            "4. Back\n"
	            + "\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	                OrganizerService.sendEventUpdate();
	                break;
	            case 2:
	                OrganizerService.sendScheduleChange();
	                break;
	            case 3:
	                NotificationService.displayAllNotifications(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 4:
	                return;
	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}

	
	private boolean confirmLogout() {
	    char choice = InputValidationUtil.readChar(
	        ScannerUtil.getScanner(),
	        "Are you sure to logout (Y/N): "
	    );
	    return Character.toUpperCase(choice) == 'Y';
	}



}


