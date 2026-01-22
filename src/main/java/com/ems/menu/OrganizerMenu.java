package com.ems.menu;

import com.ems.model.User;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.OrganizerService;
import com.ems.service.impl.OrganizerServiceImpl;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class OrganizerMenu extends BaseMenu {
	private final OrganizerService organizerService;
	private final NotificationService notificationService;
	private final EventService eventService;
	
	public OrganizerMenu(User user){
		super(user);
		this.organizerService = new OrganizerServiceImpl();
		this.notificationService = ApplicationUtil.notificationService();
	    this.eventService = ApplicationUtil.eventService();
	}
	public void start() {

	    notificationService.displayUnreadNotifications(loggedInUser.getUserId());
	
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
	                	System.out.println("Logging out...");
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
	            	eventService.createEvent();
	                break;
	            case 2:
	            	eventService.updateEventDetails();
	                break;
	            case 3:
	            	eventService.updateEventSchedule();
	                break;
	            case 4:
	            	eventService.updateEventCapacity();
	                break;
	            case 5:
	            	eventService.publishEvent();
	                break;
	            case 6:
	            	eventService.cancelEvent();
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
	            	eventService.createTicket();
	                break;
	            case 2:
	            	eventService.updateTicketPrice();
	                break;
	            case 3:
	            	eventService.updateTicketQuantity();
	                break;
	            case 4:
	            	eventService.viewTicketAvailability();
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
	                organizerService.viewEventRegistrations();
	                break;
	            case 2:
	                organizerService.viewRegisteredUsers();
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
	                organizerService.getEventWiseRegistrations();
	                break;
	            case 2:
	                organizerService.getTicketSales();
	                break;
	            case 3:
	                organizerService.getRevenueSummary();
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
	                organizerService.sendEventUpdate();
	                break;
	            case 2:
	                organizerService.sendScheduleChange();
	                break;
	            case 3:
	                notificationService.displayAllNotifications(
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


