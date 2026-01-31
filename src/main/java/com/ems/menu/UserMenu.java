package com.ems.menu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ems.actions.EventBrowsingAction;
import com.ems.actions.EventRegistrationAction;
import com.ems.actions.EventSearchAction;
import com.ems.actions.FeedbackAction;
import com.ems.actions.NotificationAction;
import com.ems.actions.UserRegistrationAction;
import com.ems.enums.PaymentMethod;
import com.ems.model.BookingDetail;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.model.UserEventRegistration;
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

	private final NotificationAction notificationAction;
	private final EventBrowsingAction eventBrowsingAction;
	private final EventRegistrationAction eventRegistrationAction;
	private final UserRegistrationAction userRegistrationAction;
	private final EventSearchAction eventSearchAction;
	private final FeedbackAction feedbackAction;

	public UserMenu(User user) {
		super(user);
		this.notificationAction = new NotificationAction();
		this.eventBrowsingAction = new EventBrowsingAction();
		this.eventRegistrationAction = new EventRegistrationAction();
		this.userRegistrationAction = new UserRegistrationAction();
		this.eventSearchAction = new EventSearchAction();
		this.feedbackAction = new FeedbackAction();
	}

	public void start() {

		notificationAction.displayUnreadNotifications(loggedInUser.getUserId());

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
				notificationAction.displayAllNotifications(loggedInUser.getUserId());
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
				eventBrowsingAction.printAllAvailableEvents();
				break;
			case 2:
				eventBrowsingAction.viewEventDetails();
				break;
			case 3:
				eventBrowsingAction.viewTicketOptions();
				break;
			case 4:
				eventRegistrationAction.registerForEvent(loggedInUser.getUserId());
				break;
			case 5:
				return;
			default:
				System.out.println("Invalid option. Please select from the menu.");
			}
		}
	}


	private void registrationMenu() {
		while (true) {
			System.out.println("\nMy Registrations\n\n" + "1. View upcoming events\n" + "2. View past events\n"
					+ "3. View booking details\n" + "4. Cancel registration\n" + "5. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1: 
				userRegistrationAction.listUpcomingEvents(loggedInUser.getUserId());
				break;
			case 2:
				userRegistrationAction.listPastEvents(loggedInUser.getUserId());
				break;
			case 3:
				userRegistrationAction.viewBookingDetails(loggedInUser.getUserId());
				break;
			case 4:
				cancelRegistration();
				break;
			case 5:
				return;
			default:
				System.out.println("Invalid option. Please select from the menu.");
			}
		}
	}

	private void cancelRegistration() {
		List<UserEventRegistration> upcoming = userRegistrationAction.getUpcomingEvents(loggedInUser.getUserId());
		if (upcoming.isEmpty()) {
            System.out.println("You have no upcoming events.");
            return;
        }

        MenuHelper.printEventsList(upcoming);
        
        int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
    		    "Select a registration number (1-" + upcoming.size() + "): ");
        while(choice < 1 || choice > upcoming.size()) {
        	choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
        		    "Select a registration number (1-" + upcoming.size() + "): ");
        }
        UserEventRegistration registration = upcoming.get(choice-1);
        char cancelChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(), "Enter Y to confirm cancellation");
		if(cancelChoice == 'Y' || cancelChoice == 'y'){
			eventRegistrationAction.cancelRegistration(loggedInUser.getUserId(), registration.getRegistrationId());
		}else{
			System.out.println("Registration cancellation cancelled.");
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
		List<UserEventRegistration> past = userRegistrationAction.getPastEvents(loggedInUser.getUserId());
		
		if (past.isEmpty()) {
            System.out.println("No past events!");
            return;
        }
        
        MenuHelper.printEventsList(past);

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
		feedbackAction.submitRating(loggedInUser.getUserId(), eventId, rating, comments);
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
				eventBrowsingAction.printAllAvailableEvents();
				break;
			case 7:
				return;
			default:
				System.out.println("Please enter a valid choice.");
			}
		}
	}

	private void searchBycategory() {
		List<Category> categories = eventSearchAction.getAllCategories();
		if(categories.isEmpty()) {
			System.out.println("No categories available.");
			return;
		}
		MenuHelper.displayCategories(categories);
		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select category number: ");
		while (choice < 1 || choice > categories.size()) {
		    choice = InputValidationUtil.readInt(
		        ScannerUtil.getScanner(),
		        "Enter a valid choice: "
		    );
		}
		Category selectedCategory = categories.get(choice - 1);
		int categoryId = selectedCategory.getCategoryId();
		List<Event> events = eventSearchAction.searchByCategory(categoryId);
		if(events.isEmpty()) {
			System.out.println("No events found in this category.: " + selectedCategory.getName());
			return;
		}
		MenuHelper.printEventSummaries(events);
	}

	private void searchByDateRange() {
		String startDateInput = InputValidationUtil.readString(ScannerUtil.getScanner(),"Enter start date (dd-mm-yyyy): " );
		LocalDate startDate = DateTimeUtil.parseLocalDate(startDateInput);
		String endDateInput = InputValidationUtil.readString(ScannerUtil.getScanner(),"Enter end date (dd-mm-yyyy): " );
		LocalDate endDate = DateTimeUtil.parseLocalDate(endDateInput);
	    List<Event> filteredEvents = eventSearchAction.searchByDateRange(startDate, endDate);
	    if(filteredEvents.isEmpty()) {
			System.out.println("No events found in the selected date range.");
			return;
		}
		MenuHelper.printEventSummaries(filteredEvents);
	}

	private void searchByCity() {
		Map<Integer, String> cities = eventSearchAction.getAllCities();
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

		List<Event> filteredEvents = eventSearchAction.searchByCity(selectedCityId);
		if(filteredEvents.isEmpty()) {
			System.out.println("No events found in the selected city.");
			return;
		}
		MenuHelper.printEventDetails(filteredEvents);
	}
	
	private void searchByDate() {
		String dateInput = InputValidationUtil.readString(ScannerUtil.getScanner(),"Enter the date to get available event from the given date (dd-mm-yyyy): " );
		LocalDate localDate = DateTimeUtil.parseLocalDate(dateInput);
		List<Event> filteredEvents = eventSearchAction.searchByDate(localDate);
		if(filteredEvents.isEmpty()) {
			System.out.println("There is no available events in or after selected date!");
			return;
		}
		MenuHelper.printEventDetails(filteredEvents);
	}
	
	private void filterByPrice() {
		double minPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter the minimum price: ");
		double maxPrice = InputValidationUtil.readDouble(ScannerUtil.getScanner(), "Enter the maximum price: ");
		List<Event> filteredEvents = eventSearchAction.filterByPrice(minPrice, maxPrice);
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