package com.ems.menu;

import java.util.List;

import com.ems.enums.UserRole;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.service.EventService;
import com.ems.service.UserService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

/*
 * Handles guest user console interactions.
 *
 * Responsibilities:
 * - Display guest accessible menus
 * - Allow browsing events and ticket information
 * - Guide guests through account registration
 */
public class GuestMenu extends BaseMenu {

	private final EventService eventService;
	private final UserService userService;
	public GuestMenu() {
		super(null);
		this.eventService = ApplicationUtil.eventService();
		this.userService = ApplicationUtil.userService();
	}
	public void start() {
		while(true) {
			System.out.println("\nGuest menu"
					+ "\n\nPlease select an option\n"
					+ "1. Browse Events\n" 
			        + "2. Register account\n"
			        + "3. Exit Guest Mode\n"
			        + "\nNote: Guest users have limited access.\n"
			        + "Register or log in to unlock all features.\n"
			        + "\n>");
			int input = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch(input) {
			case 1:
                browseEventsMenu();
                break;
			case 2:
				createAccount(UserRole.ATTENDEE);
				break;
			case 3:
			    System.out.println("Exiting guest mode. Returning to main menu...\n");
			    return;   
			default:
				System.out.println("Please select a valid option from the menu.");
				break;
			}
			
		}
	}
	private void createAccount(UserRole role) {
		String fullName =
	            InputValidationUtil.readNonEmptyString(
	                ScannerUtil.getScanner(),
	                "Enter Full Name: "
	            );
		String email = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the email address: ");
		while (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
			System.out.println("Invalid email format.\n"
					+ "Example: name@example.com\n");
            email =
                InputValidationUtil.readNonEmptyString(
                    ScannerUtil.getScanner(),
                    "Enter valid Email Address: "
                );
        }
		while(userService.checkUserExists(email)) {
			System.out.println("This email is already registered.\n"
					+ "Please try a different email.\n");
			email =
	                InputValidationUtil.readNonEmptyString(
	                    ScannerUtil.getScanner(),
	                    "Enter valid Email Address: "
	                );
		}
		String phone =
	            InputValidationUtil.readString(
	                ScannerUtil.getScanner(),
	                "Enter phone number (optional):\n"
	            );
	        
	        if (phone.trim().isEmpty()) {
	            phone = null;
	        }else {
	        	phone = phone.replaceAll("\\D", ""); 
	            while (phone.length() != 10) {
	            	phone =InputValidationUtil.readString(ScannerUtil.getScanner(),"Enter valid phone Number: ");
	            }
	        }
		String passwordPrompt =
	            "Create a password:\n"
	            + "Minimum 8 characters\n"
	            + "At least 1 uppercase, 1 lowercase, 1 number, 1 special character\n";

	        String password =
	            InputValidationUtil.readNonEmptyString(
	                ScannerUtil.getScanner(),
	                passwordPrompt
	            );

	        while (!password.matches(
	                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
	        	password =
	    	            InputValidationUtil.readNonEmptyString(
	    	                ScannerUtil.getScanner(),
	    	                "Enter the valid password: "
	    	            );
	        }
	        int genderChoice;
	        do {
	            genderChoice =
	                InputValidationUtil.readInt(
	                    ScannerUtil.getScanner(),
	                    "Enter your gender:\n1. Male\n2. Female\n3. Prefer not to say\n"
	                );
	        } while (genderChoice < 1 || genderChoice > 3);

	        String gender =
	            (genderChoice == 1)
	                ? "Male"
	                : (genderChoice == 2)
	                    ? "Female"
	                    : "Opt-out"; 
	        userService.createAccount(fullName, email, phone, password, gender, role);
		
	}
	private void browseEventsMenu() {

		while (true) {
			System.out.println("\nBrowse Events\n" + "1. View all available events\n" + "2. View event details\n"
					+ "3. View ticket options\n" + "4. Back" + "\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				printAllAvailableEvents();
				break;
			case 2:
				viewEventDetails();
				break;
			case 3:
				viewTicketOptions();
				break;
			case 4:
				return;
			default:
				System.out.println("Invalid option");
			}
		}
	}
	private void printAllAvailableEvents() {
		List<Event> filteredEvents = eventService.getAllEvents();
		if(filteredEvents.isEmpty()) {
			System.out.println("No events are available at the moment.\n");
			return;
		}
		MenuHelper.printEventSummaries(filteredEvents);
	}
	
	private void viewEventDetails() {
    	List<Event> events = eventService.listAvailableEvents();
    	if (events.isEmpty()) {
		    System.out.println("No events are available at the moment.\n");
		    return;
		}
		
    	MenuHelper.printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event (1-" + events.size() + "): "
    	);
    	while (choice < 1 || choice > events.size()) {
    	    choice = InputValidationUtil.readInt(
    	        ScannerUtil.getScanner(),
    	        "Enter a valid choice: "
    	    );
    	}
    	Event selectedEvent = events.get(choice - 1);
    	MenuHelper.printEventDetails(selectedEvent);
		
	}
	
	private void viewTicketOptions() {
		List<Event> events = eventService.listAvailableEvents();
		if (events.isEmpty()) {
		    System.out.println("No events are available at the moment.\n");
		    return;
		}
		
    	MenuHelper.printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event (1-" + events.size() + "): "
    	);
    	while (choice < 1 || choice > events.size()) {
    	    choice = InputValidationUtil.readInt(
    	        ScannerUtil.getScanner(),
    	        "Enter a valid choice: "
    	    );
    	}
    	Event selectedEvent = events.get(choice - 1);
		int eventId = selectedEvent.getEventId();

		List<Ticket> tickets = eventService.getTicketTypes(eventId);
		
		if(!tickets.isEmpty()) {
			System.out.println("\nAvailable ticket types: ");			
			
			int displayIndex = 1;
	        for (Ticket ticket: tickets) {
	        	System.out.println(
	                    displayIndex + " | " +
	                    ticket.getTicketType() + " | ₹" +
	                    ticket.getPrice() + " | " +
	                    "Tickets: " + ticket.getAvailableQuantity() +"/" + ticket.getTotalQuantity()
	                );

	                displayIndex++;
	        }
		}else {
			System.out.println("No ticket types for the given event id");
			return;
		}
		
	}

}