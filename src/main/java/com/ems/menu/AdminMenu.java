package com.ems.menu;

import java.util.List;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.model.Ticket;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.MenuHelper;
import com.ems.util.ScannerUtil;
import com.ems.util.ApplicationUtil;

public class AdminMenu extends BaseMenu {

	private final AdminService adminService;
	private final EventService eventService;
	private final NotificationService notificationService;

	public AdminMenu(User user) {
		super(user);
		this.notificationService = ApplicationUtil.notificationService();
		this.adminService = ApplicationUtil.adminService();
		this.eventService = ApplicationUtil.eventService();
	}

	public void start() {
		while (true) {
			adminService.markCompletedEvents();
			System.out.println("Admin Menu\n" + "1. User Management\n" + "2. Event Management\n"
					+ "3. Category Management\n" + "4. Venue Management\n" + "5. Ticket & Registration Management\n"
					+ "6. Payment & Revenue Management\n" + "7. Offer & Promotion Management\n"
					+ "8. Reports & Analytics\n" + "9. Notifications\n" + "10. Feedback Moderation\n"
					+ "11. Role Management\n" + "12. Logout\n" + ">");

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
				adminService.markCompletedEvents();
				if (confirmLogout()) {
					System.out.println("Logging out...");
					return;
				}
				break;
			default:
				System.out.println("Invalid option");
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
					System.out.println("No users registered");
				} else {
					MenuHelper.displayUsers(attendees);
				}
				break;
			}

			case 2: {
				List<User> organizers = adminService.getUsersList("Organizer");
				if (organizers.isEmpty()) {
					System.out.println("No organizers registered");
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
				System.out.println("Invalid option");
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

		adminService.changeStatus(status, selectedUser.getUserId());

		System.out.println("User status updated to " + status);
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
					System.out.println("No available events");
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
					System.out.println("No ticket types for this event");
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

				if (events.isEmpty()) {
					System.out.println("No events pending approval");
					break;
				}

				MenuHelper.printEventSummaries(events);

				int eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(),
						"Select an event to approve (1-" + events.size() + "): ");

				while (eventChoice < 1 || eventChoice > events.size()) {
					eventChoice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter a valid choice: ");
				}

				Event selectedEvent = events.get(eventChoice - 1);

				adminService.approveEvents(loggedInUser.getUserId(), selectedEvent.getEventId());

				System.out.println("Event approved successfully");
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

				adminService.cancelEvents(selectedEvent.getEventId());

				System.out.println("Event cancelled successfully");
				break;
			}

			case 6: {
				return;
			}

			default: {
				System.out.println("Invalid option");
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
			case 2:
				adminService.getOrganizerWisePerformance();
				break;
			case 3:
				adminService.getRevenueReport();
				break;
			case 4:
				return;
			default:
				System.out.println("Invalid option");
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
				System.out.println("Invalid option");
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
			System.out.println("Invalid notification type");
			return;
		}

		String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter message: ");

		adminService.sendNotificationToUser(message, type, selectedUser.getUserId());

		System.out.println("Notification sent successfully");
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
			System.out.println("Invalid role selection");
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
			System.out.println("Invalid notification type");
			return;
		}

		String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter message: ");

		adminService.sendNotificationByRole(message, type, role);

		System.out.println("Notification sent successfully");
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
					System.out.println("No categories available");
					return;
				}

				MenuHelper.displayCategories(categories);
				break;
			}

			case 2: {
				String name = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter category name: ");

				adminService.addCategory(name);
				System.out.println("Category added successfully");
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
					System.out.println("Delete aborted");
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
				System.out.println("Invalid option");
			}
			}
		}
		}
	
	private Category selectCategory() {

	    List<Category> categories = adminService.getAllCategories();

	    if (categories.isEmpty()) {
	        System.out.println("No categories available");
	        return null;
	    }

	    MenuHelper.displayCategories(categories);

	    int choice = InputValidationUtil.readInt(
	        ScannerUtil.getScanner(),
	        "Select a category (1-" + categories.size() + "): "
	    );

	    while (choice < 1 || choice > categories.size()) {
	        choice = InputValidationUtil.readInt(
	            ScannerUtil.getScanner(),
	            "Enter a valid choice: "
	        );
	    }

	    return categories.get(choice - 1);
	}



	private void venueManagementMenu() {
		while (true) {
			System.out.println(
					"Venue Management\n" + "1. View all venues\n" + "2. Add new venue\n" + "3. Update venue details\n"
							+ "4. Remove venue\n" + "5. View events at a venue\n" + "6. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			default:
				System.out.println("Invalid option");
			}
		}
	}

	private void ticketRegistrationManagementMenu() {
		while (true) {
			System.out.println("Ticket & Registration Management\n" + "1. View tickets by event\n"
					+ "2. View ticket availability summary\n" + "3. View registrations by event\n"
					+ "4. View registrations by user\n" + "5. Cancel a registration\n"
					+ "6. Restore cancelled registration\n" + "7. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			default:
				System.out.println("Invalid option");
			}
		}
	}

	private void paymentRevenueManagementMenu() {
		while (true) {
			System.out.println("Payment & Revenue Management\n" + "1. View payments by event\n"
					+ "2. View payments by user\n" + "3. View failed payments\n" + "4. View payment summary\n"
					+ "5. Initiate refund\n" + "6. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			default:
				System.out.println("Invalid option");
			}
		}
	}

	private void offerPromotionManagementMenu() {
		while (true) {
			System.out.println("Offer & Promotion Management\n" + "	1. View all offers\n" + "	2. Create new offer\n"
					+ "	3. Assign offer to event\n" + "	4. Activate or deactivate offer\n"
					+ "	5. View offer usage report\n" + "	6. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			default:
				System.out.println("Invalid option");
			}
		}
	}

	private void feedbackModerationMenu() {
		while (true) {
			System.out
					.println("Feedback Moderation\n" + "1. View feedback by event\n" + "2. View feedback by organizer\n"
							+ "3. Delete feedback\n" + "4. Flag feedback as reviewed\n" + "5. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			default:
				System.out.println("Invalid option");
			}
		}

	}

	private void roleManagementMenu() {
		while (true) {
			System.out.println("Role Management\n" + "1. View all roles\n" + "2. Create new role\n"
					+ "3. Assign role to user\n" + "4. Update role name\n" + "5. Delete role\n" + "6. Back\n" + ">");

			int choice = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

			switch (choice) {

			default:
				System.out.println("Invalid option");
			}
		}
	}

	private boolean confirmLogout() {
		char choice = InputValidationUtil.readChar(ScannerUtil.getScanner(), "Are you sure to logout (Y/N): ");
		return Character.toUpperCase(choice) == 'Y';
	}

	private Event selectAnyEvent() {

		List<Event> events = eventService.getAllEvents();

		if (events.isEmpty()) {
			System.out.println("No available events");
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
