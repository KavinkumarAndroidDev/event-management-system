package com.ems.menu;

import com.ems.dao.impl.*;
import com.ems.dao.*;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class AdminMenu {
	User loggedInUser;
	public AdminMenu(User user){
		this.loggedInUser = user;
		this.start();
	}

	private void start() {
		NotificationDao notificationDao = new NotificationDaoImpl();
		while (true) {
		    System.out.println("\nAdmin Menu\n"
		    		+ "1. User Management\n"
		    		+ "2. Event Management\n"
		    		+ "3. Reports & Analytics\n"
		    		+ "4. Notifications\n"
		    		+ "5. Logout"
		    		+ "\n>");
		
		    int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
		
		    switch (choice) {
		        case 1 :
		        	userManagementMenu();
		        	break;
		        case 2 : 
		        	eventManagementMenu();
		        	break;
		        case 3 : 
		        	reportsMenu();
		        	break;
		        case 4 : 
		        	notificationMenu();
		        	break;
		        case 5 :
		            System.out.println("Logging out...");
		            return;
		        default :
		        	System.out.println("Invalid option");
		        	break;
		    }
		 }
	}
	private void userManagementMenu() {
	    while (true) {
	        System.out.println("\nUser Management\n"
	        		+ "1. View all users\r\n"
	        		+ "2. View organizers\n"
	        		+ "3. Activate user\n"
	        		+ "4. Suspend user\n"
	        		+ "5. Back"
	        		+ "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1 : 
	            	AdminService.getUsersList("Attendee");
	            	break;
	            case 2 : 
	            	AdminService.getUsersList("Organizer");
	            	break;
	            case 3 : 
	            	AdminService.changeStatus("ACTIVE");
	            	break;
	            case 4 : 
	            	AdminService.changeStatus("SUSPENDED");
	            	break;
	            case 5 : 
	            	return; 
	            default : 
	            	System.out.println("Invalid option");
	            	break;
	        }
	    }
	}
	private void eventManagementMenu() {
	    while (true) {
	        System.out.println("\nEvent Management\n"
	        		+ "1. View all events\n"
	        		+ "2. Approve event\n"
	        		+ "3. Cancel event\n"
	        		+ "4. Back"
	        		+ "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1 :
	            	EventService.printAllEvents();
	            	break;
	            case 2 :
					try {
						AdminService.approveEvents();
					} catch (Exception e) {
						System.out.println("Unexpected error occured: " + e.getMessage());
					}
					break;
	            case 3 :
					try {
						AdminService.cancelEvents();
					} catch (Exception e) {
						System.out.println("Unexpected error occured: " + e.getMessage());
					}
	            	break;
	            case 4 :
	            	return; 
	            default :
	            	System.out.println("Invalid option");
	        }
	    }
	}
	private void reportsMenu() {
	    while (true) {
	        System.out.println("\nReports & Analytics\n"
	        		+ "1. Event-wise registrations\n"
	        		+ "2. Organizer-wise performance\n"
	        		+ "3. Revenue report\n"
	        		+ "4. Back"
	        		+ "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1 :
	                int eventId = InputValidationUtil.readInt(
	                    ScannerUtil.getScanner(), "Enter event id: "
	                );
	                AdminService.getEventWiseRegistrations(eventId);
	                break;
	            case 2 :
	            	AdminService.getOrganizerWisePerformance();
	            	break;
	            case 3 :
	            	AdminService.getRevenueReport();
	            	break;
	            case 4 :
	            	return; 
	            default :
	            	System.out.println("Invalid option");
	            	break;
	        }
	    }
	}
	private void notificationMenu() {
	    while (true) {
	        System.out.println("\nNotifications\n"
	        		+ "1. Send system update\n"
	        		+ "2. Send promotional message\n"
	        		+ "3. Back"
	        		+ "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1 :
	                String msg = InputValidationUtil.readString(
	                    ScannerUtil.getScanner(), "Enter system message: "
	                );
	                AdminService.sendSystemWideNotification(msg, "SYSTEM");
	                break;
	            
	            case 2 :
	                String msg1 = InputValidationUtil.readString(
	                    ScannerUtil.getScanner(), "Enter promo message: "
	                );
	                AdminService.sendSystemWideNotification(msg1, "PROMOTION");
	                break;
	            
	            case 3 : 
	            	return; 
	            default : 
	            	System.out.println("Invalid option"); 
	            	break;
	        }
	    }
	}



}

