package com.ems.menu;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.model.Venue;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.OfferService;
import com.ems.service.OrganizerService;
import com.ems.service.SystemLogService;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;
import com.ems.util.ApplicationUtil;
import com.ems.util.DateTimeUtil;

/*
 * Handles administrator related console interactions.
 *
 * Responsibilities:
 * - Display admin menus and navigation flows
 * - Collect and validate user input
 * - Delegate administrative operations to services
 */
public class AdminMenu extends BaseMenu {

	private final AdminService adminService;
	private final EventService eventService;
	private final NotificationService notificationService;
	private final OfferService offerService;
	private final OrganizerService organizerService;
	private final SystemLogService systemLogService;

	public AdminMenu(User user) {
		super(user);
		this.notificationService = ApplicationUtil.notificationService();
		this.adminService = ApplicationUtil.adminService();
		this.eventService = ApplicationUtil.eventService();
		this.offerService = ApplicationUtil.offerService();
		this.organizerService = ApplicationUtil.organizerService();
		this.systemLogService = ApplicationUtil.systemLogService();
	}

	public void start() {
		while (true) {
			adminService.markCompletedEvents();
			System.out.println("\nAdmin Menu\n" + "1. User Management\n" + "2. Event Management\n"
					+ "3. Category Management\n" + "4. Venue Management\n" + "5. Ticket & Registration Management\n"
					+ "6. Payment & Revenue Management\n" + "7. Offer & Promotion Management\n"
					+ "8. Reports & Analytics\n" + "9. Notifications\n" + "10. Feedback Moderation\n"
					+ "11. Role Management\n" + "12. View system logs\n" + "13. Logout\n>" );

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				userManagementMenu();
				break;
			case 2:
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
			case 8:
				reportsMenu();
				break;
			case 9:
				notificationMenu();
				break;
			case 10:
				feedbackModerationMenu();
				break;
			case 11:
				roleManagementMenu();
				break;
			case 12:
				systemLogService.printAllLogs();
				break;
			case 13:
				adminService.markCompletedEvents();
				if (confirmLogout()) {
					System.out.println("Logging out...");
					return;
				}
				break;
			default:
				System.out.println("Invalid option. Please select a valid menu number.");
				break;
			}
		}
	}

	private void userManagementMenu() {
		while (true) {
			System.out.println("\nUser Management\n" + "1. View all users\n" + "2. View organizers\n"
					+ "3. Activate user\n" + "4. Suspend user\n" + "5. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<User> attendees = adminService.getUsersList("Attendee");
				if (attendees.isEmpty()) {
					System.out.println("No users found at the moment.");
				} else {
					MenuHelper.displayUsers(attendees);
				}
				break;
			}

			case 2: {
				List<User> organizers = adminService.getUsersList("Organizer");
				if (organizers.isEmpty()) {
					System.out.println("No organizers found.");
				} else {
					MenuHelper.displayUsers(organizers);
				}
				break;
			}

			case 3: {
				changeUserStatus("ACTIVE");
				break;
			}

			case 4: {
				changeUserStatus("SUSPENDED");
				break;
			}

			case 5: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private void changeUserStatus(String status) {

		List<User> users = adminService.getAllUsers();

		if (users.isEmpty()) {
			System.out.println("No users available");
			return;
		}

		MenuHelper.displayUsers(users);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select a user (1-" + users.size() + "): ");

		while (choice < 1 || choice > users.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		User selectedUser = users.get(choice - 1);
		char approveChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Do you want to change the status to " + status + " for user " + selectedUser.getFullName() + " (Y/N)\n");
		if (approveChoice == 'Y' || approveChoice == 'y') {
			boolean isSuccess = adminService.changeStatus(status, selectedUser.getUserId());

			if (isSuccess) {
				System.out.println("User status updated successfully.");
			} else {
				System.out.println("Unable to update user status. Please try again.");
			}
		} else {
			System.out.println("Action cancelled by user.");
		}
	}

	private void eventManagementMenu() {

		while (true) {
			System.out.println("\nEvent Management\n" + "1. View all events\n" + "2. View event details\n"
					+ "3. View ticket options\n" + "4. Approve event\n" + "5. Cancel event\n" + "6. Back\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<Event> events = eventService.getAllEvents();
				if (events.isEmpty()) {
					System.out.println("No events available at the moment.");
				} else {
					MenuHelper.printEventSummaries(events);
				}
				break;
			}

			case 2: {
				Event selectedEvent = selectAnyEvent();
				if (selectedEvent == null)
					break;

				MenuHelper.printEventDetails(selectedEvent);
				break;
			}

			case 3: {
				Event selectedEvent = selectAnyEvent();
				if (selectedEvent == null)
					break;

				List<Ticket> tickets = eventService.getTicketTypes(selectedEvent.getEventId());

				if (tickets.isEmpty()) {
					System.out.println("No ticket options are available for this event.");
					break;
				}

				System.out.println("\nAvailable ticket types:");

				int index = 1;
				for (Ticket ticket : tickets) {
					System.out.println(index + ". " + ticket.getTicketType() + " | ₹" + ticket.getPrice() + " | "
							+ "Tickets: " + ticket.getAvailableQuantity() + "/" + ticket.getTotalQuantity());
					index++;
				}
				break;
			}

			case 4: {
				List<Event> events = eventService.listEventsYetToApprove();

				if (events == null || events.isEmpty()) {
					System.out.println("There are no events waiting for approval.");
					break;
				}

				MenuHelper.printEventSummaries(events);

				int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event to approve (1-" + events.size() + "): ");

				while (eventChoice < 1 || eventChoice > events.size()) {
					eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}

				Event selectedEvent = events.get(eventChoice - 1);
				char approveChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
						"Approve this event? (Y/N)\n");
				if (approveChoice == 'Y' || approveChoice == 'y') {
					adminService.approveEvents(loggedInUser.getUserId(), selectedEvent.getEventId());
					System.out.println("Event approved successfully and the organizer has been notified.");
				} else {
					System.out.println("Action cancelled by user.");
				}
				break;
			}

			case 5: {
				List<Event> events = eventService.listAvailableAndDraftEvents();

				if (events.isEmpty()) {
					System.out.println("No events available to cancel");
					break;
				}

				MenuHelper.printEventSummaries(events);

				int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event to cancel (1-" + events.size() + "): ");

				while (eventChoice < 1 || eventChoice > events.size()) {
					eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}

				Event selectedEvent = events.get(eventChoice - 1);

				char cancelChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
						"Cancel this event? (Y/N)\n");
				if (cancelChoice == 'Y' || cancelChoice == 'y') {
					adminService.cancelEvents(selectedEvent.getEventId());

					System.out.println("Event cancelled successfully.");
				} else {
					System.out.println("Action cancelled by user.");
				}
				break;
			}

			case 6: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private void reportsMenu() {
		while (true) {
			System.out.println("\nReports & Analytics\n" + "1. Event-wise registrations\n"
					+ "2. Organizer-wise performance\n" + "3. Revenue report\n" + "4. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				Event selectedEvent = selectAnyEvent();
				if (selectedEvent == null)
					break;

				adminService.getEventWiseRegistrations(selectedEvent.getEventId());
				break;
			case 2:{
					List<User> user = adminService.getUsersList(UserRole.ORGANIZER.toString());
					MenuHelper.displayUsers(user);
					int organizerChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid choice (1 - " + user.size() +")");
					while(organizerChoice < 1 || organizerChoice > user.size()) {
						organizerChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid choice (1 - " + user.size() +")");
					}
					List<OrganizerEventSummary> list =
				            organizerService.getOrganizerEventSummary(user.get(organizerChoice).getUserId());

				    if (list.isEmpty()) {
				        System.out.println("No event conducted by the organizer!");
				        return;
				    }

				    System.out.println("\nOrganizer Events Summary");

				    String currentStatus = "";

				    for (OrganizerEventSummary s : list) {
				    	
				        if (!s.getStatus().equals(currentStatus)) {
				            currentStatus = s.getStatus();
				            System.out.println("\n[" + currentStatus + "]");
				        }

				        System.out.println(
				        	    s.getTitle()
				        	    + " | Tickets Booked: " + s.getBookedTickets()
				        	    + " out of " + s.getTotalTickets()
				        	);

				    }
				    break;
				}
			case 3:
				adminService.getRevenueReport();
				break;
			case 4:
				return;
			default:
				System.out.println("Invalid option. Please select a valid menu number.");
				break;
			}
		}
	}

	private void notificationMenu() {

		while (true) {
			System.out.println("\nNotifications\n" + "1. Send system update (all users)\n"
					+ "2. Send promotional message (all users)\n" + "3. Send notification to user role\n"
					+ "4. Send notification to specific user\n" + "5. View my notifications\n" + "6. Back\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				String msg = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter system message: ");

				adminService.sendSystemWideNotification(msg, NotificationType.SYSTEM.name());
				break;
			}

			case 2: {
				String msg = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(),
						"Enter promotional message: ");

				adminService.sendSystemWideNotification(msg, NotificationType.EVENT.name());
				break;
			}

			case 3: {
				sendNotificationByRole();
				break;
			}

			case 4: {
				sendNotificationToSpecificUser();
				break;
			}

			case 5: {
				notificationService.displayAllNotifications(loggedInUser.getUserId());
				break;
			}

			case 6: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private void sendNotificationToSpecificUser() {

		List<User> users = adminService.getAllUsers();

		if (users.isEmpty()) {
			System.out.println("No users available");
			return;
		}

		MenuHelper.displayUsers(users);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select a user (1-" + users.size() + "): ");

		while (choice < 1 || choice > users.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		User selectedUser = users.get(choice - 1);

		System.out.println("\nSelect notification type\n" + "1. SYSTEM\n" + "2. EVENT\n" + "3. PAYMENT\n>");

		int typeChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

		NotificationType type;

		if (typeChoice == 1) {
			type = NotificationType.SYSTEM;
		} else if (typeChoice == 2) {
			type = NotificationType.EVENT;
		} else if (typeChoice == 3) {
			type = NotificationType.PAYMENT;
		} else {
			System.out.println("Invalid notification type selected.");
			return;
		}

		String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter message: ");

		adminService.sendNotificationToUser(message, type, selectedUser.getUserId());

		System.out.println("Notification sent successfully.");
	}

	private void sendNotificationByRole() {

		System.out.println("\nSelect user role\n" + "1. Attendee\n" + "2. Organizer\n>");

		int roleChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

		UserRole role;

		if (roleChoice == 1) {
			role = UserRole.ATTENDEE;
		} else if (roleChoice == 2) {
			role = UserRole.ORGANIZER;
		} else {
			System.out.println("Invalid role selected. Please try again.");
			return;
		}

		System.out.println("\nSelect notification type\n" + "1. SYSTEM\n" + "2. EVENT\n" + "3. PAYMENT\n>");

		int typeChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

		NotificationType type;

		if (typeChoice == 1) {
			type = NotificationType.SYSTEM;
		} else if (typeChoice == 2) {
			type = NotificationType.EVENT;
		} else if (typeChoice == 3) {
			type = NotificationType.PAYMENT;
		} else {
			System.out.println("Invalid notification type selected.");
			return;
		}

		String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter message: ");

		adminService.sendNotificationByRole(message, type, role);

		System.out.println("Notification sent successfully.");
	}

	private void categoryManagementMenu() {

		while (true) {
			System.out.println("\nCategory Management\n" + "1. View all categories\n" + "2. Add new category\n"
					+ "3. Update category name\n" + "4. Delete category\n" + "5. Back\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<Category> categories = adminService.getAllCategories();

				if (categories.isEmpty()) {
					System.out.println("No categories found.");
					return;
				}

				MenuHelper.displayCategories(categories);
				break;
			}

			case 2: {
				String name = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter category name: ");

				adminService.addCategory(name);
				System.out.println("Category added successfully.");
				break;
			}

			case 3: {
				Category selectedCategory = selectCategory();
				if (selectedCategory == null)
					return;

				String newName = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(),
						"Enter new category name: ");

				adminService.updateCategory(selectedCategory.getCategoryId(), newName);

				System.out.println("Category updated successfully");
				break;
			}

			case 4: {
				Category selectedCategory = selectCategory();
				if (selectedCategory == null)
					return;

				char confirm = InputValidationUtil.readChar(ScannerUtil.getScanner(),
						"Are you sure you want to delete this category (Y/N): ");

				if (Character.toUpperCase(confirm) != 'Y') {
					System.out.println("Category deletion cancelled.");
					return;
				}

				adminService.deleteCategory(selectedCategory.getCategoryId());

				System.out.println("Category deleted successfully");
				break;
			}

			case 5: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private Category selectCategory() {

		List<Category> categories = adminService.getAllCategories();

		if (categories.isEmpty()) {
			System.out.println("No categories found.");
			return null;
		}

		MenuHelper.displayCategories(categories);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select a category (1-" + categories.size() + "): ");

		while (choice < 1 || choice > categories.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		return categories.get(choice - 1);
	}

	private void venueManagementMenu() {

		while (true) {
			System.out.println(
					"\nVenue Management\n" + "1. View all venues\n" + "2. Add new venue\n" + "3. Update venue details\n"
							+ "4. Remove venue\n" + "5. View events at a venue\n" + "6. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<Venue> venues = eventService.getAllVenues();

				if (venues.isEmpty()) {
					System.out.println("No venues found.");
				} else {
					MenuHelper.displayVenues(venues);
				}
				break;
			}

			case 2: {
				Venue venue = new Venue();

				venue.setName(
						InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the venue name: "));
				venue.setStreet(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the street: "));
				venue.setCity(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the city: "));
				venue.setState(InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the state: "));
				venue.setPincode(
						InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the pincode: "));
				venue.setMaxCapacity(
						InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the maximum capacity: "));

				adminService.addVenue(venue);
				System.out.println("Venue added successfully.");
				break;
			}

			case 3: {
				Venue selectedVenue = selectVenue();
				if (selectedVenue == null)
					break;

				System.out.println("Press Enter to keep the current value");

				String name = InputValidationUtil.readString(ScannerUtil.getScanner(),
						"Venue name (" + selectedVenue.getName() + "): ");
				if (!name.isBlank()) {
					selectedVenue.setName(name);
				}

				String street = InputValidationUtil.readString(ScannerUtil.getScanner(),
						"Street (" + selectedVenue.getStreet() + "): ");
				if (!street.isBlank()) {
					selectedVenue.setStreet(street);
				}

				String city = InputValidationUtil.readString(ScannerUtil.getScanner(),
						"City (" + selectedVenue.getCity() + "): ");
				if (!city.isBlank()) {
					selectedVenue.setCity(city);
				}

				String state = InputValidationUtil.readString(ScannerUtil.getScanner(),
						"State (" + selectedVenue.getState() + "): ");
				if (!state.isBlank()) {
					selectedVenue.setState(state);
				}

				String pincode = InputValidationUtil.readString(ScannerUtil.getScanner(),
						"Pincode (" + selectedVenue.getPincode() + "): ");
				if (!pincode.isBlank()) {
					selectedVenue.setPincode(pincode);
				}

				int capacity = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Maximum capacity (" + selectedVenue.getMaxCapacity() + ") enter 0 to skip: ");
				if (capacity > 0) {
					selectedVenue.setMaxCapacity(capacity);
				}

				adminService.updateVenue(selectedVenue);
				System.out.println("Venue updated successfully");
				break;
			}

			case 4: {
				Venue selectedVenue = selectVenue();
				if (selectedVenue == null)
					break;

				char confirm = InputValidationUtil.readChar(ScannerUtil.getScanner(),
						"Are you sure you want to remove this venue (Y/N): ");

				if (Character.toUpperCase(confirm) != 'Y') {
					System.out.println("Venue removal cancelled.");
					break;
				}

				adminService.removeVenue(selectedVenue.getVenueId());
				System.out.println("Venue removed successfully.");
				break;
			}

			case 5: {
				Venue selectedVenue = selectVenue();
				if (selectedVenue == null)
					break;

				List<Event> events = eventService.searchByCity(selectedVenue.getVenueId());

				if (events.isEmpty()) {
					System.out.println("No events for this venue");
				} else {
					MenuHelper.printEventSummaries(events);
				}
				break;
			}

			case 6: {
				return;
			}

			default: {
				System.out.println("Invalid option. Please select a valid menu number.");
			}
			}
		}
	}

	private Venue selectVenue() {

		List<Venue> venues = eventService.getAllVenues();

		if (venues.isEmpty()) {
			System.out.println("No venues found.");
			return null;
		}

		MenuHelper.displayVenues(venues);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select a venue (1-" + venues.size() + "): ");

		while (choice < 1 || choice > venues.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		return venues.get(choice - 1);
	}

	private void ticketRegistrationManagementMenu() {
		while (true) {
			System.out.println("\nTicket & Registration Management\n" + "1. View tickets by event\n"
					+ "2. View ticket availability summary\n" + "3. View registrations by event\n"
					+ "4. View registrations by user\n" + "5. Cancel a registration\n"
					+ "6. Restore cancelled registration\n" + "7. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<Event> events = eventService.listAvailableEvents();
				if (events.isEmpty()) {
					System.out.println("No events available at the moment.");
					break;
				}

				MenuHelper.printEventSummaries(events);

				int eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");
				while (eChoice < 1 || eChoice > events.size()) {
					eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}

				Event selectedEvent = events.get(eChoice - 1);
				List<Ticket> tickets = eventService.getTicketTypes(selectedEvent.getEventId());

				if (tickets.isEmpty()) {
					System.out.println("No tickets found for this event");
					break;
				}

				int index = 1;
				for (Ticket t : tickets) {
					System.out.println(index + ". " + t.getTicketType() + " | Price: ₹" + t.getPrice()
							+ " | Available: " + t.getAvailableQuantity() + "/" + t.getTotalQuantity());
					index++;
				}
				break;
			}

			case 2: {
				List<Event> events = eventService.listAvailableEvents();
				if (events.isEmpty()) {
					System.out.println("No events available at the moment.");
					break;
				}

				MenuHelper.printEventSummaries(events);

				int eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");
				while (eChoice < 1 || eChoice > events.size()) {
					eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}

				Event selectedEvent = events.get(eChoice - 1);
				List<Ticket> tickets = eventService.getTicketTypes(selectedEvent.getEventId());

				int total = 0;
				int available = 0;

				for (Ticket t : tickets) {
					total += t.getTotalQuantity();
					available += t.getAvailableQuantity();
				}

				System.out.println("Event Capacity Summary\n" + "Total Tickets: " + total + "\n" + "Available Tickets: "
						+ available);
				break;
			}

			case 3: {
				List<Event> events = eventService.listAvailableEvents();
				if (events.isEmpty()) {
					System.out.println("No events available at the moment.");
					break;
				}

				MenuHelper.printEventSummaries(events);

				int eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event (1-" + events.size() + "): ");
				while (eChoice < 1 || eChoice > events.size()) {
					eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}

				Event selectedEvent = events.get(eChoice - 1);
				adminService.getEventWiseRegistrations(selectedEvent.getEventId());
				break;
			}

			case 4:
				System.out.println("Feature coming soon");
				break;

			case 5:
				System.out.println("Cancellation flow will be added soon");
				break;

			case 6:
				System.out.println("Restore flow will be added soon");
				break;

			case 7:
				return;

			default:
				System.out.println("Invalid option. Please select a valid menu number.");
			}
		}
	}

	private void paymentRevenueManagementMenu() {
		while (true) {
			System.out.println("\nPayment & Revenue Management\n" + "1. View payments by event\n"
					+ "2. View payments by user\n" + "3. View failed payments\n" + "4. View payment summary\n"
					+ "5. Initiate refund\n" + "6. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 6:
				return;

			default:
				System.out.println("This feature is under development and will be available soon.");
			}
		}
	}

	private void offerPromotionManagementMenu() {
		while (true) {
			System.out.println("\nOffer & Promotion Management\n" + "1. View all offers\n" + "2. Create new offer\n"
					+ "3. Activate or deactivate offer\n" + "4. View offer usage report\n" + "5. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 1:
				List<Offer> offers = offerService.getAllOffers();
				if (offers.isEmpty()) {
					System.out.println("No offers found.");
				} else {
					MenuHelper.displayOffers(offers);
				}
				break;

			case 2:
				createOffer();
				break;
			case 3:
				toggleOfferStatus();
				break;

			case 4:
				Map<String, Integer> report = offerService.getOfferUsageReport();
				report.forEach((code, count) -> System.out.println(code + " | Used: " + count));
				break;

			case 5:
				return;

			default:
				System.out.println("Invalid option. Please select a valid menu number.");
			}
		}
	}

	private void createOffer() {

		List<Event> events = eventService.listAvailableEvents();

		if (events.isEmpty()) {
			System.out.println("No events available");
			return;
		}

		MenuHelper.printEventSummaries(events);

		int eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Select event (1-" + events.size() + "): ");

		while (eChoice < 1 || eChoice > events.size()) {
			eChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Event event = events.get(eChoice - 1);

		String code = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the offer code: ");

		int discount = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the discount percentage: ");

		/*
		 * Offer start date must: - Not be in the past - Not exceed the event start time
		 * 
		 * This ensures offers are only active before the event begins.
		 */
		LocalDateTime from = DateTimeUtil.getLocalDateTime("Enter the valid from (dd-MM-yyyy HH:mm): ");

		while (from.isBefore(LocalDateTime.now()) || from.isAfter(event.getStartDateTime())) {

			from = DateTimeUtil.getLocalDateTime("Enter the valid from (dd-MM-yyyy HH:mm): ");
		}

		/*
		 * Offer end date must: - Not be in the past - Be after the offer start date -
		 * Not exceed the event start time
		 * 
		 * This prevents invalid or overlapping offer periods.
		 */
		LocalDateTime to = DateTimeUtil.getLocalDateTime("Enter the valid to (dd-MM-yyyy HH:mm): ");

		while (to.isBefore(LocalDateTime.now()) || to.isBefore(from) || to.isAfter(event.getStartDateTime())) {

			to = DateTimeUtil.getLocalDateTime("Enter the valid to (dd-MM-yyyy HH:mm): ");
		}

		int offerId = offerService.createOffer(event.getEventId(), code, discount, from, to);

		System.out.println("Offer created successfully. Offer ID: " + offerId);
	}

	private void toggleOfferStatus() {

		System.out.println("\n1. Activate offer\n" + "2. Deactivate offer\n" + "3. Back\n" + ">");

		int option = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

		if (option == 3) {
			return;
		}

		LocalDateTime now = LocalDateTime.now();

		List<Offer> offers = offerService.getAllOffers();

		if (offers.isEmpty()) {
			System.out.println("No offers found.");
			return;
		}

		List<Offer> filtered;
		if (option == 1) {
			filtered = offers.stream()
					.filter(o -> o.getEventId() != 0 && o.getValidTo() != null && o.getValidTo().isBefore(now))
					.toList();
		} else if (option == 2) {
			filtered = offers.stream().filter(o -> o.getValidTo() != null && o.getValidTo().isAfter(now)).toList();
		} else {
			System.out.println("Invalid option. Please select a valid menu number.");
			return;
		}

		if (filtered.isEmpty()) {
			System.out.println("No applicable offers found");
			return;
		}

		MenuHelper.displayOffers(filtered);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select offer (1-" + filtered.size() + "): ");

		while (choice < 1 || choice > filtered.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		Offer selectedOffer = filtered.get(choice - 1);

		LocalDateTime newValidTo;

		if (option == 1) {
			newValidTo = DateTimeUtil.getLocalDateTime("Activate until (dd-MM-yyyy HH:mm): ");
		} else {
			newValidTo = now;
		}
		Event event = eventService.getEventById(selectedOffer.getEventId());
		if (event == null) {
			System.out.println("No event found for the offer!");
			return;
		}
		if (newValidTo.isAfter(event.getStartDateTime())) {
			System.out.println("Offer validity must end before the event starts.");
			return;
		}

		char updateChoice = InputValidationUtil.readChar(ScannerUtil.getScanner(),
				"Are you sure you want to update offer status (Y/N)\n");
		if (updateChoice == 'Y' || updateChoice == 'y') {
			offerService.toggleOfferStatus(selectedOffer.getOfferId(), newValidTo);

			System.out.println(option == 1 ? "Offer activated successfully." : "Offer deactivated successfully.");
		} else {
			System.out.println("Process aborted!");
		}

	}

	private void feedbackModerationMenu() {
		while (true) {
			System.out.println(
					"\nFeedback Moderation\n" + "1. View feedback by event\n" + "2. View feedback by organizer\n"
							+ "3. Delete feedback\n" + "4. Flag feedback as reviewed\n" + "5. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 5:
				return;
			default:
				System.out.println("This feature is under development and will be available soon.");
			}
		}

	}

	private void roleManagementMenu() {
		while (true) {
			System.out.println("\nRole Management\n" + "1. View all roles\n" + "2. Create new role\n"
					+ "3. Assign role to user\n" + "4. Update role name\n" + "5. Delete role\n" + "6. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
			case 6:
				return;
			default:
				System.out.println("This feature is under development and will be available soon.");
			}
		}
	}

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(ScannerUtil.getScanner(), "Are you sure you want to log out? (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}

	private Event selectAnyEvent() {

		List<Event> events = eventService.getAllEvents();

		if (events.isEmpty()) {
			System.out.println("No events available at the moment.");
			return null;
		}

		MenuHelper.printEventSummaries(events);

		int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
				"Select an event (1-" + events.size() + "): ");

		while (choice < 1 || choice > events.size()) {
			choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
		}

		return events.get(choice - 1);
	}

}
