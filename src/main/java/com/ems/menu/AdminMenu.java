package com.ems.menu;

import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;
import com.ems.util.ApplicationUtil;

public class AdminMenu extends BaseMenu {

	private final AdminService adminService;
	private final EventService eventService;

	public AdminMenu(User user) {
	    super(user);
	    this.adminService = ApplicationUtil.adminService();
	    this.eventService = ApplicationUtil.eventService();
	}


	public void start() {
		while (true) {
			adminService.markCompletedEvents();
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
		        	adminService.markCompletedEvents();
		            if (confirmLogout()) {
		            	System.out.println("Logging out...");
	                    return;
	                }
	                break;
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
	            	adminService.getUsersList("Attendee");
	            	break;
	            case 2 : 
	            	adminService.getUsersList("Organizer");
	            	break;
	            case 3 : 
	            	adminService.changeStatus("ACTIVE");
	            	break;
	            case 4 : 
	            	adminService.changeStatus("SUSPENDED");
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
	            	eventService.printAllEvents();
	            	break;
	            case 2 :
					try {
						adminService.approveEvents(loggedInUser.getUserId());
					} catch (Exception e) {
						System.out.println("Unexpected error occured: " + e.getMessage());
					}
					break;
	            case 3 :
					try {
						adminService.cancelEvents();
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
	                adminService.getEventWiseRegistrations(eventId);
	                break;
	            case 2 :
	            	adminService.getOrganizerWisePerformance();
	            	break;
	            case 3 :
	            	adminService.getRevenueReport();
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
	                adminService.sendSystemWideNotification(msg, "SYSTEM");
	                break;
	            
	            case 2 :
	                String msg1 = InputValidationUtil.readString(
	                    ScannerUtil.getScanner(), "Enter promo message: "
	                );
	                adminService.sendSystemWideNotification(msg1, "PROMOTION");
	                break;
	            
	            case 3 : 
	            	return; 
	            default : 
	            	System.out.println("Invalid option"); 
	            	break;
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

