package com.ems.menu;


import com.ems.model.User;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class UserMenu {
	User loggedInUser;
	
	public UserMenu( User user) {
		this.loggedInUser = user;
		this.start();
	}

	private void start() {

	    NotificationService.displayUnreadNotifications(
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
	                EventService.searchEvents();
	                break;
	            case 3:
	                registrationMenu();
	                break;
	            case 4:
	                NotificationService.displayAllNotifications(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 5:
	                feedbackMenu();
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
	                EventService.printAllAvailableEvents();
	                break;
	            case 2:
	                EventService.viewEventDetails();
	                break;
	            case 3:
	                EventService.viewTicketOptions();
	                break;
	            case 4:
	                EventService.registerForEvent(
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
	            	EventService.viewUpcomingEvents(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 2:
	            	EventService.viewPastEvents(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 3:
	            	EventService.viewBookingDetails(
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
	            "2. Submit review\n" +
	            "3. Back\n"
	            + ">"
	        );

	        int choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(), ""
	        );

	        switch (choice) {
	            case 1:
	            	EventService.submitRating(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 2:
	            	EventService.submitReview(
	                    loggedInUser.getUserId()
	                );
	                break;
	            case 3:
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
