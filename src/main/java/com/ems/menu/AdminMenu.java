package com.ems.menu;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.ems.actions.AdminCategoryManagementAction;
import com.ems.actions.AdminEventManagementAction;
import com.ems.actions.AdminNotificationManagementAction;
import com.ems.actions.AdminOfferManagementAction;
import com.ems.actions.AdminReportAction;
import com.ems.actions.AdminTicketManagementAction;
import com.ems.actions.AdminUserManagementAction;
import com.ems.actions.AdminVenueManagementAction;
import com.ems.actions.NotificationAction;
import com.ems.actions.SystemLogAction;
import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.model.Event;
import com.ems.model.Offer;
import com.ems.model.OrganizerEventSummary;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.util.AdminMenuHelper;
import com.ems.util.DateTimeUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;

/*
 * Handles administrator related console interactions.
 *
 * Responsibilities:
 * - Display admin menus and navigation flows
 * - Collect and validate user input
 * - Delegate administrative operations to services
 */
public class AdminMenu extends BaseMenu {

	private final AdminUserManagementAction userManagementAction;
	private final AdminEventManagementAction eventManagementAction;
	private final AdminCategoryManagementAction categoryManagementAction;
	private final AdminVenueManagementAction venueManagementAction;
	private final AdminNotificationManagementAction notificationManagementAction;
	private final AdminReportAction reportAction;
	private final AdminOfferManagementAction offerManagementAction;
	private final AdminTicketManagementAction ticketManagementAction;
	private final NotificationAction notificationAction;
	private final SystemLogAction systemLogAction;

	public AdminMenu(User user) {
		super(user);
		this.userManagementAction = new AdminUserManagementAction();
		this.eventManagementAction = new AdminEventManagementAction();
		this.categoryManagementAction = new AdminCategoryManagementAction();
		this.venueManagementAction = new AdminVenueManagementAction();
		this.notificationManagementAction = new AdminNotificationManagementAction();
		this.reportAction = new AdminReportAction();
		this.offerManagementAction = new AdminOfferManagementAction();
		this.ticketManagementAction = new AdminTicketManagementAction();
		this.notificationAction = new NotificationAction();
		this.systemLogAction = new SystemLogAction();
	}

	public void start() {
		while (true) {
			eventManagementAction.markCompletedEvents();
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
				systemLogAction.printAllLogs();
				break;
			case 13:
				eventManagementAction.markCompletedEvents();
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
				userManagementAction.listUsersByRole("Attendee");
				break;
			}

			case 2: {
				userManagementAction.listUsersByRole("Organizer");
				break;
			}

			case 3: {
				userManagementAction.changeUserStatus("ACTIVE");
				break;
			}

			case 4: {
				userManagementAction.changeUserStatus("SUSPENDED");
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


	private void eventManagementMenu() {

		while (true) {
			System.out.println("\nEvent Management\n" + "1. View all events\n" + "2. View event details\n"
					+ "3. View ticket options\n" + "4. Approve event\n" + "5. Cancel event\n" + "6. Back\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				eventManagementAction.listAllEvents();
				break;
			}

			case 2: {
				eventManagementAction.printEventDetails();
				break;
			}

			case 3: {
				eventManagementAction.listTicketsForEvent();
				break;
			}

			case 4: {
				eventManagementAction.approveEvent(loggedInUser.getUserId());
				break;
			}

			case 5: {
				eventManagementAction.cancelEvent();
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
				Event selectedEvent = eventManagementAction.selectAnyEvent();
				if (selectedEvent == null)
					break;

				reportAction.getEventWiseRegistrations(selectedEvent.getEventId());
				break;
			case 2:{
					List<User> user = userManagementAction.getUsersByRole(UserRole.ORGANIZER.toString());
					MenuHelper.displayUsers(user);
					int organizerChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid choice (1 - " + user.size() +")");
					while(organizerChoice < 1 || organizerChoice > user.size()) {
						organizerChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid choice (1 - " + user.size() +")");
					}
					List<OrganizerEventSummary> list =
				            reportAction.getOrganizerEventSummary(user.get(organizerChoice - 1).getUserId());

				    if (list.isEmpty()) {
				        System.out.println("No event conducted by the organizer!");
				        return;
				    }

				    AdminMenuHelper.printOrganizerEventSummary(list);
				    break;
				}
			case 3:
				reportAction.getRevenueReport();
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

				notificationManagementAction.sendSystemWideNotification(msg, NotificationType.SYSTEM.name());
				break;
			}

			case 2: {
				String msg = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(),
						"Enter promotional message: ");

				notificationManagementAction.sendSystemWideNotification(msg, NotificationType.EVENT.name());
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
				notificationAction.displayAllNotifications(loggedInUser.getUserId());
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

		List<User> users = userManagementAction.getAllUsers();

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

		notificationManagementAction.sendNotificationToUser(message, type, selectedUser.getUserId());

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

		notificationManagementAction.sendNotificationByRole(message, type, role);

		System.out.println("Notification sent successfully.");
	}

	private void categoryManagementMenu() {

		while (true) {
			System.out.println("\nCategory Management\n" + "1. View all categories\n" + "2. Add new category\n"
					+ "3. Update category name\n" + "4. Delete category\n" + "5. Back\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				categoryManagementAction.listAllCategories();
				break;
			}

			case 2: {
				categoryManagementAction.addCategory();
				break;
			}

			case 3: {
				categoryManagementAction.updateCategory();
				break;
			}

			case 4: {
				categoryManagementAction.deleteCategory();
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

	

	private void venueManagementMenu() {

		while (true) {
			System.out.println(
					"\nVenue Management\n" + "1. View all venues\n" + "2. Add new venue\n" + "3. Update venue details\n"
							+ "4. Remove venue\n" + "5. View events at a venue\n" + "6. Back\n\n>");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {
				case 1: {
					venueManagementAction.listAllVenues();
					break;
				}
	
				case 2: {
					venueManagementAction.addVenue();
					break;
				}
				case 3: {
					venueManagementAction.updateVenue();
					break;
				}
	
				case 4: {
					venueManagementAction.removeVenue();
					break;
				}
	
				case 5: {
					venueManagementAction.listEventsByCity();
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

	private void ticketRegistrationManagementMenu() {
		while (true) {
			System.out.println("\nTicket & Registration Management\n" + "1. View tickets by event\n"
					+ "2. View ticket availability summary\n" + "3. View registrations by event\n"
					+ "4. View registrations by user\n" + "5. Cancel a registration\n"
					+ "6. Restore cancelled registration\n" + "7. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			case 1: {
				List<Event> events = ticketManagementAction.getAvailableEvents();
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
				List<Ticket> tickets = ticketManagementAction.getTicketsForEvent(selectedEvent.getEventId());

				if (tickets.isEmpty()) {
					System.out.println("No tickets found for this event");
					break;
				}

				AdminMenuHelper.printTicketDetails(tickets);
				break;
			}

			case 2: {
				List<Event> events = ticketManagementAction.getAvailableEvents();
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
				List<Ticket> tickets = ticketManagementAction.getTicketsForEvent(selectedEvent.getEventId());

				AdminMenuHelper.printTicketCapacitySummary(tickets);
				break;
			}

			case 3: {
				List<Event> events = ticketManagementAction.getAvailableEvents();
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
				ticketManagementAction.getEventWiseRegistrations(selectedEvent.getEventId());
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
				List<Offer> offers = offerManagementAction.getAllOffers();
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
				Map<String, Integer> report = offerManagementAction.getOfferUsageReport();
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

		List<Event> events = ticketManagementAction.getAvailableEvents();

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
		while (discount < 0 || discount > 100) {
			discount = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the discount percentage (1 - 100): ");
		}
		/*
		 * Offer start date must: - Not be in the past - Not exceed the event start time
		 * 
		 * This ensures offers are only active before the event begins.
		 */
		LocalDateTime from = null;

		while (from == null) {
		    String input = InputValidationUtil.readString(
		            ScannerUtil.getScanner(),
		            "Enter the valid from (dd-MM-yyyy HH:mm): "
		    );

		    from = DateTimeUtil.parseLocalDateTime(input);

		    if (from == null
		            || from.isBefore(LocalDateTime.now())
		            || from.isAfter(event.getStartDateTime())) {

		        System.out.println("Invalid 'from' date time. Please try again.");
		        from = null;
		    }
		}


		/*
		 * Offer end date must: - Not be in the past - Be after the offer start date -
		 * Not exceed the event start time
		 * 
		 * This prevents invalid or overlapping offer periods.
		 */
		LocalDateTime to = null;

		while (to == null) {
		    String input = InputValidationUtil.readString(
		            ScannerUtil.getScanner(),
		            "Enter the valid to (dd-MM-yyyy HH:mm): "
		    );

		    to = DateTimeUtil.parseLocalDateTime(input);

		    if (to == null
		            || to.isBefore(LocalDateTime.now())
		            || to.isBefore(from)
		            || to.isAfter(event.getStartDateTime())) {

		        System.out.println("Invalid 'to' date time. Please try again.");
		        to = null;
		    }
		}

		int offerId = offerManagementAction.createOffer(event.getEventId(), code, discount, from, to);

		System.out.println("Offer created successfully. Offer ID: " + offerId);
	}

	private void toggleOfferStatus() {

		System.out.println("\n1. Activate offer\n" + "2. Deactivate offer\n" + "3. Back\n" + ">");

		int option = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

		if (option == 3) {
			return;
		}

		List<Offer> offers = offerManagementAction.getAllOffers();

		if (offers.isEmpty()) {
			System.out.println("No offers found.");
			return;
		}

		List<Offer> filtered;
		if (option == 1) {
			filtered = AdminMenuHelper.filterExpiredOffers(offers);
		} else if (option == 2) {
			filtered = AdminMenuHelper.filterActiveOffers(offers);
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
			String dateInput = InputValidationUtil.readString(ScannerUtil.getScanner(),"Activate until (dd-MM-yyyy HH:mm): ");
			newValidTo = DateTimeUtil.parseLocalDateTime(dateInput);
		} else {
			newValidTo = LocalDateTime.now();
		}
		Event event = eventManagementAction.getEventById(selectedOffer.getEventId());
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
			offerManagementAction.toggleOfferStatus(selectedOffer.getOfferId(), newValidTo);

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
}