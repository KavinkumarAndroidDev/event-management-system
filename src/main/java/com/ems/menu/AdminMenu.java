package com.ems.menu;

import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;
import com.ems.util.ApplicationUtil;

public class AdminMenu extends BaseMenu {

	private final AdminService adminService;
	private final EventService eventService;
	private final NotificationService notificationService;
	private final UserService userService;
	
		
	public AdminMenu(User user) {
	    super(user);
	    this.notificationService = ApplicationUtil.notificationService();
	    this.adminService = ApplicationUtil.adminService();
	    this.eventService = ApplicationUtil.eventService();
	    this.userService = ApplicationUtil.userService();
	}


	public void start() {
		while (true) {
			adminService.markCompletedEvents();
		    System.out.println("Admin Menu\n"
		    		+ "1. User Management\n"
		    		+ "2. Event Management\n"
		    		+ "3. Category Management\n"
		    		+ "4. Venue Management\n"
		    		+ "5. Ticket & Registration Management\n"
		    		+ "6. Payment & Revenue Management\n"
		    		+ "7. Offer & Promotion Management\n"
		    		+ "8. Reports & Analytics\n"
		    		+ "9. Notifications\n"
		    		+ "10. Feedback Moderation\n"
		    		+ "11. Role Management\n"
		    		+ "12. Logout\n"
		    		+ "\n>");
		
		    int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
		
		    switch (choice) {
		        case 1 :
		        	userManagementMenu();
		        	break;
		        case 2 : 
		        	eventManagementMenu();
		        	break;
		        case 3:
		        	categoryManagementMenu();
		        	break;
		        case 4:
		        	venueManagementMenu();
		        	break;
		        case 5:
		        	ticketRegistrationManagementMenu();
		        	break;
		        case 6:
		        	paymentRevenueManagementMenu();
		        	break;
		        case 7:
		        	offerPromotionManagementMenu();
		        	break;
		        case 8 : 
		        	reportsMenu();
		        	break;
		        case 9 : 
		        	notificationMenu();
		        	break;
		        case 10:
		        	feedbackModerationMenu();
		        	break;
		        case 11:
		        	roleManagementMenu();
		        	break;
		        case 12 :
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
	        		+ "1. View all users\n"
	        		+ "2. View organizers\n"
	        		+ "3. Activate user\n"
	        		+ "4. Suspend user\n"
	        		+ "5. Back\n"
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
	        		+ "2. View event details\n" 
		            + "3. View ticket options\n" 
	        		+ "4. Approve event\n"
	        		+ "5. Cancel event\n"
	        		+ "6. Back\n"
	        		+ "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1 :
	            	adminService.printAllEvents();
	            	break;
	            case 2:
	            	userService.viewEventDetails();
	                break;
	            case 3:
	            	userService.viewTicketOptions();
	                break;
	            case 4 :
	            	adminService.approveEvents(loggedInUser.getUserId());
					break;
	            case 5 :
	            	adminService.cancelEvents();
	            	break;
	            case 6 :
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
	        		+ "4. Back\n"
	        		+ "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1 :
	                adminService.getEventWiseRegistrations();
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
	                + "1. Send system update (all users)\n"
	                + "2. Send promotional message (all users)\n"
	                + "3. Send notification to user role\n"
	                + "4. Send notification to specific user\n"
	                + "5. View my notifications\n"
	                + "6. Back"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            case 1:
	                String msg = InputValidationUtil.readString(
	                        ScannerUtil.getScanner(), "Enter system message: "
	                );
	                adminService.sendSystemWideNotification(msg, "SYSTEM");
	                break;

	            case 2:
	                String msg1 = InputValidationUtil.readString(
	                        ScannerUtil.getScanner(), "Enter promo message: "
	                );
	                adminService.sendSystemWideNotification(msg1, "PROMOTION");
	                break;

	            case 3:
	            	adminService.sendNotificationByRole();
	                break;

	            case 4:
	            	adminService.getAllUsers();
	            	adminService.sendNotificationToUser();
	                break;

	            case 5:
	            	notificationService.displayAllNotifications(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 6:
	                return;

	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	private void categoryManagementMenu() {
	    while (true) {
	        System.out.println("Category Management\n"
	        		+ "1. View all categories\n"
	        		+ "2. Add new category\n"
	        		+ "3. Update category name\n"
	        		+ "4. Delete category\n"
	        		+ "5. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	        	case 1:
	        		//adminService.listAllCategories();
	        		break;
	        	case 2:
	        		//adminService.addNewCategory();
	        		break;
	        	case 3:
	        		//adminService.updateCategory();
	        		break;
	        	case 4:
	        		break;

	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void venueManagementMenu() {
	    while (true) {
	        System.out.println("Venue Management\n"
	        		+ "1. View all venues\n"
	        		+ "2. Add new venue\n"
	        		+ "3. Update venue details\n"
	        		+ "4. Remove venue\n"
	        		+ "5. View events at a venue\n"
	        		+ "6. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            

	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void ticketRegistrationManagementMenu() {
	    while (true) {
	        System.out.println("Ticket & Registration Management\n"
	        		+ "1. View tickets by event\n"
	        		+ "2. View ticket availability summary\n"
	        		+ "3. View registrations by event\n"
	        		+ "4. View registrations by user\n"
	        		+ "5. Cancel a registration\n"
	        		+ "6. Restore cancelled registration\n"
	        		+ "7. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            

	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	
	private void paymentRevenueManagementMenu() {
	    while (true) {
	        System.out.println("Payment & Revenue Management\n"
	        		+ "1. View payments by event\n"
	        		+ "2. View payments by user\n"
	        		+ "3. View failed payments\n"
	        		+ "4. View payment summary\n"
	        		+ "5. Initiate refund\n"
	        		+ "6. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            

	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	private void offerPromotionManagementMenu() {
	    while (true) {
	        System.out.println("Offer & Promotion Management\n"
	        		+ "	1. View all offers\n"
	        		+ "	2. Create new offer\n"
	        		+ "	3. Assign offer to event\n"
	        		+ "	4. Activate or deactivate offer\n"
	        		+ "	5. View offer usage report\n"
	        		+ "	6. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            

	            default:
	                System.out.println("Invalid option");
	        }
	    }
	}
	private void feedbackModerationMenu() {
	    while (true) {
	        System.out.println("Feedback Moderation\n"
	        		+ "1. View feedback by event\n"
	        		+ "2. View feedback by organizer\n"
	        		+ "3. Delete feedback\n"
	        		+ "4. Flag feedback as reviewed\n"
	        		+ "5. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            

	            default:
	                System.out.println("Invalid option");
	        }
	    }
		
	}
	
	private void roleManagementMenu() {
		while (true) {
	        System.out.println("Role Management\n"
	        		+ "1. View all roles\n"
	        		+ "2. Create new role\n"
	        		+ "3. Assign role to user\n"
	        		+ "4. Update role name\n"
	        		+ "5. Delete role\n"
	        		+ "6. Back\n"
	                + "\n>");

	        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

	        switch (choice) {
	            

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

