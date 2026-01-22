package com.ems.menu;

import com.ems.model.User;
import com.ems.service.NotificationService;
import com.ems.service.UserService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class UserMenu extends BaseMenu {
	private final NotificationService notificationService;
	private final UserService userService;
	public UserMenu( User user) {
		super(user);
		this.notificationService = ApplicationUtil.notificationService();
	    this.userService = ApplicationUtil.userService();
	}
	public void start() {

		notificationService.displayUnreadNotifications(
	        loggedInUser.getUserId()
	    );

	    while (true) {
	        System.out.println(
	            "\nUser Menu\n" +
	            "1. Browse Events\n" +
	            "2. Search & Filter Events\n" +
	            "3. My Registrations\n" +
	            "4. Notifications\n" +
	            "5. Feedback\n" +
	            "6. Logout\n"
	            + ">"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	                browseEventsMenu();
	                break;
	            case 2:
	                userService.searchEvents();
	                break;
	            case 3:
	                registrationMenu();
	                break;
	            case 4:
	            	notificationService.displayAllNotifications(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 5:
	                feedbackMenu();
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
	
	private void browseEventsMenu() {

	    while (true) {
	        System.out.println(
	            "\nBrowse Events\n" +
	            "1. View all available events\n" +
	            "2. View event details\n" +
	            "3. View ticket options\n" +
	            "4. Register for event\n" +
	            "5. Back"
	            + "\n>"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	            	userService.printAllAvailableEvents();
	                break;
	            case 2:
	            	userService.printAllAvailableEvents();
	            	userService.viewEventDetails();
	                break;
	            case 3:
	            	userService.viewTicketOptions();
	                break;
	            case 4:
	            	userService.registerForEvent(
	                    loggedInUser.getUserId()
	                );
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
	            "\nMy Registrations\n" +
	            "1. View upcoming events\n" +
	            "2. View past events\n" +
	            "3. View booking details\n" +
	            "4. Back\n"
	            + ">"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	            	userService.viewUpcomingEvents(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 2:
	            	userService.viewPastEvents(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 3:
	            	userService.viewBookingDetails(
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
	
	private void feedbackMenu() {

	    while (true) {
	        System.out.println(
	            "\nFeedback\n" +
	            "1. Submit rating\n" +
	            "2. Back\n"
	            + ">"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	            	userService.submitRating(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 2:
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

//
//Payment payment = new Payment();
//payment.setRegistrationId(regId);
//payment.setAmount(amount);
//payment.setPaymentMethod(method);
//
//paymentService.processPayment(payment);
//System.out.println("Payment successful");
