package com.ems.menu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.enums.PaymentMethod;
import com.ems.model.BookingDetail;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

/*
 * Handles authenticated user console interactions.
 *
 * Responsibilities:
 * - Display user menus and navigation flows
 * - Allow event browsing, searching, and registration
 * - Manage user registrations, notifications, and feedback
 */
public class UserMenu extends BaseMenu {

	private final NotificationService notificationService;
	private final EventService eventService;

	public UserMenu(User user) {
		super(user);
		this.notificationService = ApplicationUtil.notificationService();
		this.eventService = ApplicationUtil.eventService();
	}

	public void start() {

		notificationService.displayUnreadNotifications(loggedInUser.getUserId());

		while (true) {
			System.out.println("\nUser Menu\n\n" + "1. Browse Events\n" + "2. Search & Filter Events\n"
					+ "3. My Registrations\n" + "4. Notifications\n" + "5. Feedback\n" + "6. Logout\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				browseEventsMenu();
				break;
			case 2:
				searchEvents();
				break;
			case 3:
				registrationMenu();
				break;
			case 4:
				notificationService.displayAllNotifications(loggedInUser.getUserId());
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
				System.out.println("Invalid option. Please select from the menu.");
			}
		}
	}

	private void browseEventsMenu() {

		while (true) {
			System.out.println("\nBrowse Events\n\n" + "1. View all available events\n" + "2. View event details\n"
					+ "3. View ticket options\n" + "4. Register for event\n" + "5. Back" + "\n>");

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
				registerForEvent();
				break;
			case 5:
				return;
			default:
				System.out.println("Invalid option. Please select from the menu.");
			}
		}
	}

	private void registerForEvent() {
		List<Event> events = eventService.listAvailableEvents();
        if (events == null || events.isEmpty()) {
            System.out.println("No events available at the moment.");
            return;
        }

        MenuHelper.printEventSummaries(events);
        int choice = InputValidationUtil.readInt(
        		ScannerUtil.getScanner(),
        		"Select an event number (1-" + events.size() + "): "
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
        if (tickets == null ||tickets.isEmpty()) {
            System.out.println("No ticket types available for this event.");
            return;
        }

        System.out.println("\nAvailable Ticket Types:");
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
        int ticketChoice = InputValidationUtil.readInt(
        		ScannerUtil.getScanner(),
        		"Select a ticket (1-" + tickets.size() + "): "
        );
        while (ticketChoice < 1 || ticketChoice > tickets.size()) {
        	ticketChoice = InputValidationUtil.readInt(
        			ScannerUtil.getScanner(),
        	        "Enter a valid choice: "
        	    );
        }
        Ticket selectedTicket = tickets.get(ticketChoice - 1);
        int ticketId = selectedTicket.getTicketId();

        int quantity = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter number of tickets: ");
        while (quantity <= 0 || quantity > selectedTicket.getAvailableQuantity()) {
            quantity = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter quantity (1-" + selectedTicket.getAvailableQuantity() + "): "
            );
        }

        
        
        System.out.println("\nAvailable payment methods:");

        PaymentMethod[] methods = PaymentMethod.values();

        for (int i = 0; i < methods.length; i++) {
            System.out.println(
                (i + 1) + ". " + methods[i].name().replace("_", " ")
            );
        }

        int paymentChoice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter choice (1-" + methods.length + "): "
        );

        while (paymentChoice < 1 || paymentChoice > methods.length) {
            System.out.println("Please select a valid payment method.");
            paymentChoice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter choice (1-" + methods.length + "): "
            );
        }

        PaymentMethod selectedMethod = methods[paymentChoice - 1];
        
        String offerCode = InputValidationUtil.readString(
        	    ScannerUtil.getScanner(),
        	    "Enter offer code (press Enter to skip): "
        	);

        	if (offerCode.isBlank()) {
        	    offerCode = null;
        	}else {
        		offerCode = offerCode.trim().toUpperCase();
        	}

        
        boolean success = eventService.registerForEvent(loggedInUser.getUserId(), eventId, ticketId, quantity, selectedTicket.getPrice(), selectedMethod, offerCode);
        if (success) {
            System.out.println("Registration successful. Your tickets are confirmed.");
        } else {
            System.out.println("Registration failed. Please check availability or try again.\n");
        }
	}


	private void viewTicketOptions() {
		List<Event> events = eventService.listAvailableEvents();
		if (events.isEmpty()) {
		    System.out.println("No events available at the moment.");
		    return;
		}
		
    	MenuHelper.printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event number (1-" + events.size() + "): "
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
			System.out.println("No ticket types available for this event.\n");
			return;
		}
		
	}

	private void viewEventDetails() {
    	List<Event> events = eventService.listAvailableEvents();
    	if (events.isEmpty()) {
		    System.out.println("No events available at the moment.");
		    return;
		}
		
    	MenuHelper.printEventSummaries(events);
    	int choice = InputValidationUtil.readInt(
	    	    ScannerUtil.getScanner(),
	    	    "Select an event number (1-" + events.size() + "): "
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

	private void registrationMenu() {
		while (true) {
			System.out.println("\nMy Registrations\n\n" + "1. View upcoming events\n" + "2. View past events\n"
					+ "3. View booking details\n" + "4. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				viewUpcomingEvents();
				break;
			case 2:
				viewPastEvents();
				break;
			case 3:
				viewBookingDetails();
				break;
			case 4:
				return;
			default:
				System.out.println("Invalid option. Please select from the menu.");
			}
		}
	}

	private void viewBookingDetails() {
		List<BookingDetail> bookingDetails = eventService.viewBookingDetails(loggedInUser.getUserId());
		if (bookingDetails.isEmpty()) {
            System.out.println("You have no bookings yet.");
            return;
        }
		System.out.println("Booking Details\n");

        for (BookingDetail b : bookingDetails) {
            System.out.println("------------------------------------------");
            System.out.println("Event  : " + b.getEventName());
            System.out.println("Venue : " + b.getVenueName() + " (" + b.getCity() + ")");
            System.out.println("Tickets: " + b.getTicketType() + " x" + b.getQuantity());
            System.out.println("Total : ₹" + b.getTotalCost());
            System.out.println("------------------------------------------");
        }
		
	}

	private void viewUpcomingEvents() {
		List<UserEventRegistration> upcoming = eventService.viewUpcomingEvents(loggedInUser.getUserId());
		if (upcoming.isEmpty()) {
            System.out.println("You have no upcoming events.");
            return;
        }

        System.out.println("--- Upcoming Events: " + upcoming.size() + " ---");
        int displayIndex = 1;
        for (UserEventRegistration r : upcoming) {
        	System.out.println(
                    displayIndex + " | " +
                    r.getTitle() + " | " +
                    r.getCategory() + " | " +
                    DateTimeUtil.formatDateTime(r.getStartDateTime()) +
                    " | Tickets booked: " + r.getTicketsPurchased() +
                    " | Status: " + r.getRegistrationStatus()
                );

                displayIndex++;
        }
	}

	private void viewPastEvents() {
		List<UserEventRegistration> past = eventService.viewPastEvents(loggedInUser.getUserId());
		if (past.isEmpty()) {
            System.out.println("You have no past events.");
            return;
        }

        System.out.println("--- Past Events: " + past.size() + " ---");
        int displayIndex = 1;
        for (UserEventRegistration r : past) {
        	System.out.println(
                    displayIndex + " | " +
                    r.getTitle() + " | " +
                    r.getCategory() + " | " +
                    DateTimeUtil.formatDateTime(r.getStartDateTime()) +
                    " | Tickets booked: " + r.getTicketsPurchased() +
                    " | Status: " + r.getRegistrationStatus()
                );
        	displayIndex++;
        }
	}

	private void feedbackMenu() {

		while (true) {
			System.out.println("\nFeedback\n\n" + "1. Submit rating\n" + "2. Back\n"+ ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				submitRating();
				break;
			case 2:
				return;
			default:
				System.out.println("Invalid option. Please select from the menu.");
			}
		}
	}

	private void submitRating() {
		List<UserEventRegistration> past = eventService.viewPastEvents(loggedInUser.getUserId());
		
		if (past.isEmpty()) {
            System.out.println("No past events!");
            return;
        }
        System.out.println("--- Past Events: " + past.size() + " ---");
        int displayIndex = 1;
        for (UserEventRegistration r : past) {
            try {
                System.out.println(
                    displayIndex + " | " +
                    r.getTitle() + " | " +
                    r.getCategory() + " | " +
                    DateTimeUtil.formatDateTime(r.getStartDateTime()) +
                    " | Tickets booked: " + r.getTicketsPurchased() +
                    " | Status: " + r.getRegistrationStatus() +
                    " | Attended at: " + r.getStartDateTime()
                );

                displayIndex++;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

		int choice = InputValidationUtil.readInt(
		    ScannerUtil.getScanner(),
		    "Select an event number (1-" + past.size() + "): "
		);

		while (choice < 1 || choice > past.size()) {
		    choice = InputValidationUtil.readInt(
		        ScannerUtil.getScanner(),
		        "Enter a valid choice: "
		    );
		}

		int eventId = past.get(choice - 1).getEventId();

		int rating = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Rate the event (1-5): ");
		while(rating >5 || rating <1) {
			rating = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Rate the event (1-5): ");
		}
		String comments = InputValidationUtil.readString(
				ScannerUtil.getScanner(),
				"Enter feedback (optional, press Enter to skip):\n"
		);
		if(comments.trim().isBlank()) {
			comments = null;
		}
		eventService.submitRating(loggedInUser.getUserId(), eventId, rating, comments);
		System.out.println("Thank you for your feedback.\n");
	}

	public void searchEvents() {
		while (true) {
			System.out.println("\nEnter your choice:\n" + "1. Search by category\r\n" + "2. Search by date\r\n"
					+ "3. Search by date range\n" + "4. Search by city\r\n" + "5. Filter by price\r\n"
					+ "6. Filter by availability\r\n" + "7. Exit to user menu\n" + "\n>");

			int filterChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (filterChoice) {
			case 1:
				searchBycategory();
				break;
			case 2:
				searchByDate();
				break;
			case 3:
				searchByDateRange();
				break;
			case 4:
				searchByCity();
				break;
			case 5:
				filterByPrice();
				break;
			case 6:
				printAllAvailableEvents();
				break;
			case 7:
				return;
			default:
				System.out.println("Please enter a valid choice.");
			}
		}
	}

	private void searchBycategory() {
		List<Category> categories = eventService.getAllCategory();
		if(categories.isEmpty()) {
			System.out.println("No categories available.");
			return;
		}
		int defaultIndex = 1;
		for(Category category: categories) {
			System.out.println(defaultIndex +". " +category.getName());
			defaultIndex++;
		}
		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select category number: ");
		while (choice < 1 || choice > categories.size()) {
		    choice = InputValidationUtil.readInt(
		        ScannerUtil.getScanner(),
		        "Enter a valid choice: "
		    );
		}
		Category selectedCategory = categories.get(choice - 1);
		int categoryId = selectedCategory.getCategoryId();
		List<Event> events = eventService.searchBycategory(categoryId);
		if(events.isEmpty()) {
			System.out.println("No events found in this category.: " + selectedCategory.getName());
			return;
		}
		MenuHelper.printEventSummaries(events);
	}

	private void printAllAvailableEvents() {
		List<Event> filteredEvents = eventService.listAvailableEvents();
		if(filteredEvents.isEmpty()) {
			System.out.println("There is no available events!");
			return;
		}
		MenuHelper.printEventSummaries(filteredEvents);
	}

	private void searchByDateRange() {
		LocalDate startDate = DateTimeUtil.getLocalDate("Enter start date (dd-mm-yyyy):");
	    LocalDate endDate = DateTimeUtil.getLocalDate("Enter end date (dd-mm-yyyy):");
	    List<Event> filteredEvents = eventService.searchByDateRange(startDate, endDate);
	    if(filteredEvents.isEmpty()) {
			System.out.println("No events found in the selected date range.");
			return;
		}
		MenuHelper.printEventSummaries(filteredEvents);
	}

	private void searchByCity() {
		Map<Integer, String> cities = eventService.getAllCities();
		if(cities.isEmpty()) {
			System.out.println("No cities available.");
			return;
		}
		List<Integer> dbKeys = new ArrayList<>(cities.keySet());
		for (int i = 0; i < dbKeys.size(); i++) {
	        int internalDbId = dbKeys.get(i);
	        String cityName = cities.get(internalDbId);
	        System.out.println((i + 1) + ". " + cityName);
	    }
		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the city (1 - " + dbKeys.size() + "):");
	    while (choice < 1 || choice > dbKeys.size()) {
	        choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid city (1 - " + dbKeys.size() + "):");
	    }

	    final int selectedCityId = dbKeys.get(choice - 1);

		List<Event> filteredEvents = eventService.searchByCity(selectedCityId);
		if(filteredEvents.isEmpty()) {
			System.out.println("No events found in the selected city.");
			return;
		}
		MenuHelper.printEventDetails(filteredEvents);
	}
	private void searchByDate() {
		LocalDate localDate = DateTimeUtil.getLocalDate("Enter the date to get available event from the given date:");
		List<Event> filteredEvents = eventService.searchByDate(localDate);
		if(filteredEvents.isEmpty()) {
			System.out.println("There is no available events in selected date!");
			return;
		}
		MenuHelper.printEventDetails(filteredEvents);
	}
	private void filterByPrice() {
		double minPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter the minimum price: ");
		double maxPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter the maximum price: ");
		List<Event> filteredEvents = eventService.filterByPrice(minPrice, maxPrice);
		if(filteredEvents.isEmpty()) {
			System.out.println("No events found in the selected price range.");
			return;
		}
		MenuHelper.printEventDetails(filteredEvents);
	}

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(ScannerUtil.getScanner(), "Are you sure you want to logout? (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}
}