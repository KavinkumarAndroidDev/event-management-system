package com.ems.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ems.dao.*;
import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.exception.DataAccessException;
import com.ems.model.Category;
import com.ems.model.EventRegistrationReport;
import com.ems.model.User;
import com.ems.model.Venue;
import com.ems.service.AdminService;
import com.ems.service.NotificationService;
import com.ems.service.SystemLogService;

/*
 * Handles administrative business operations.
 *
 * Responsibilities:
 * - Manage users, categories, venues, and events
 * - Approve or cancel events
 * - Generate administrative reports
 * - Send system and targeted notifications
 */
public class AdminServiceImpl implements AdminService {

	private final UserDao userDao;
	private final EventDao eventDao;
	private final NotificationDao notificationDao;
	private final RegistrationDao registrationDao;
	private final NotificationService notificationService;
	private final CategoryDao categoryDao;
	private final VenueDao venueDao;
	private final SystemLogService systemLogService;

	/*
	 * Creates AdminService with required data access and notification dependencies.
	 */
	public AdminServiceImpl(UserDao userDao, EventDao eventDao, NotificationDao notificationDao,
			RegistrationDao registrationDao, CategoryDao categoryDao, VenueDao venueDao,
			NotificationService notificationService, SystemLogService systemLogService) {
		this.userDao = userDao;
		this.eventDao = eventDao;
		this.notificationDao = notificationDao;
		this.registrationDao = registrationDao;
		this.categoryDao = categoryDao;
		this.venueDao = venueDao;
		this.notificationService = notificationService;
		this.systemLogService = systemLogService;
	}

	/*
	 * Retrieves users filtered by role. Used by admin to review attendees or
	 * organizers.
	 */
	@Override
	public List<User> getUsersList(String userType) {
		List<User> users = new ArrayList<>();
		try {
			users = userDao.findAllUsers(userType);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

		if (users == null || users.isEmpty()) {
			System.out.println("No users available for the selected role");
		}
		return users;
	}

	/*
	 * Updates user account status.
	 *
	 * Rule: - Admin accounts cannot be modified
	 */
	@Override
	public boolean changeStatus(String status, int userId) {
		try {
			boolean updated = userDao.updateUserStatus(userId, status);
			if (updated) {
				systemLogService.log(
					null,                     
					"UPDATE_STATUS",
					"USER",
					userId,
					"User status changed to " + status
				);
			}
			return updated;
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	/*
	 * Sends a notification message to all users in the system.
	 */
	@Override
	public void sendSystemWideNotification(String message, String notificationType) {
		notificationService.sendSystemWideNotification(message, notificationType);
		systemLogService.log(
				null,
				"SEND_NOTIFICATION",
				"SYSTEM",
				null,
				"System-wide notification sent"
			);
	}

	/*
	 * Approves an event submitted by an organizer.
	 *
	 * Rule: - Organizer is notified after approval
	 */
	@Override
	public void approveEvents(int userId, int eventId) {
		try {
			boolean isApproved = eventDao.approveEvent(eventId, userId);
			if (isApproved) {
				notificationDao.sendNotification(eventDao.getOrganizerId(eventId),
						"Your event: " + eventId + " has been approved!", "EVENT");
				systemLogService.log(
						userId,
						"APPROVE",
						"EVENT",
						eventId,
						"Event approved by admin"
					);
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * Cancels an event.
	 *
	 * Rule: - Organizer is notified after cancellation
	 */
	@Override
	public void cancelEvents(int eventId) {
		try {
			boolean isCancelled = eventDao.cancelEvent(eventId);
			if (isCancelled) {
				notificationDao.sendNotification(eventDao.getOrganizerId(eventId),
						"Your event: " + eventId + " has been cancelled!", "EVENT");
				systemLogService.log(
						null,
						"CANCEL",
						"EVENT",
						eventId,
						"Event cancelled by admin"
					);
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Displays registration details for a specific event. Registrations are shown
	 * in reverse chronological order.
	 */
	@Override
	public void getEventWiseRegistrations(int eventId) {
		try {
			List<EventRegistrationReport> reports = registrationDao.getEventWiseRegistrations(eventId);

			if (reports.isEmpty()) {
				System.out.println("No registrations found for this event");
				return;
			}
			reports.sort(Comparator.comparing(EventRegistrationReport::getRegistrationDate).reversed());
			if (!reports.isEmpty()) {
				System.out.println("Event Wise Registration");
				reports.forEach(System.out::println);
			} else {
				System.out.println("No registrations for the given event!");
			}

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Displays revenue generated per event.
	 */
	@Override
	public void getRevenueReport() {
		try {
			Map<String, Double> revenueMap = eventDao.getEventWiseRevenue();

			if (revenueMap.isEmpty()) {
				System.out.println("No revenue data available.");
				return;
			}

			System.out.println("\nEvent Wise Revenue Report");
			System.out.println("-----------------------------------");

			revenueMap.forEach((event, revenue) -> {
				System.out.println("Event : " + event + " | Revenue : ₹" + revenue);
			});

			System.out.println("-----------------------------------");

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Displays organizer performance based on total events created.
	 */
	@Override
	public void getOrganizerWisePerformance() {
		try {
			Map<String, Integer> organizerStats = eventDao.getOrganizerWiseEventCount();

			if (organizerStats.isEmpty()) {
				System.out.println("No organizer data available.");
				return;
			}

			System.out.println("\nOrganizer Wise Performance");
			System.out.println("-----------------------------------");

			organizerStats.forEach((organizer, count) -> {
				System.out.println("Organizer : " + organizer + " | Total Events : " + count);
			});

			System.out.println("-----------------------------------");

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Sends a notification to all users of a specific role.
	 */
	@Override
	public void sendNotificationByRole(String message, NotificationType selectedType, UserRole role) {
		try {
			notificationDao.sendNotificationByRole(message, selectedType.toString(), role.toString());
			systemLogService.log(
					null,
					"SEND_NOTIFICATION",
					"ROLE",
					null,
					"Notification sent to role: " + role
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Sends a notification to a specific user.
	 */
	@Override
	public void sendNotificationToUser(String message, NotificationType selectedType, int userId) {
		try {
			notificationDao.sendNotification(userId, message, selectedType.toString());
			
			systemLogService.log(
					null,
					"SEND_NOTIFICATION",
					"USER",
					userId,
					"Notification sent to user"
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Retrieves all users sorted alphabetically.
	 */
	@Override
	public List<User> getAllUsers() {

		List<User> users = new ArrayList<>();
		try {
			users = userDao.findAllUsers();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

		if (users == null || users.isEmpty()) {
			System.out.println("No users found.");
		}

		// sorting
		users.sort(Comparator.comparing(User::getFullName));
		return users;

	}

	/*
	 * Category management operations.
	 */
	@Override
	public List<Category> getAllCategories() {
		try {
			return categoryDao.getAllCategories();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
			return List.of();
		}
	}

	@Override
	public void addCategory(String name) {
		try {
			categoryDao.addCategory(name);
			
			systemLogService.log(
					null,
					"CREATE",
					"CATEGORY",
					null,
					"Category created: " + name
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void updateCategory(int categoryId, String name) {
		try {
			categoryDao.updateCategoryName(categoryId, name);
			
			systemLogService.log(
					null,
					"UPDATE",
					"CATEGORY",
					categoryId,
					"Category name updated"
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void deleteCategory(int categoryId) {
		try {
			categoryDao.deactivateCategory(categoryId);
			
			systemLogService.log(
					null,
					"DELETE",
					"CATEGORY",
					categoryId,
					"Category deactivated"
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Marks completed events based on end time.
	 */
	@Override
	public void markCompletedEvents() {
		try {
			eventDao.completeEvents();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * Venue management operations.
	 */
	@Override
	public void addVenue(Venue venue) {
		try {
			venueDao.addVenue(venue);
			systemLogService.log(
					null,
					"CREATE",
					"VENUE",
					null,
					"Venue added: " + venue.getName()
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void updateVenue(Venue venue) {
		try {
			venueDao.updateVenue(venue);
			
			systemLogService.log(
					null,
					"UPDATE",
					"VENUE",
					venue.getVenueId(),
					"Venue updated"
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void removeVenue(int venueId) {
		try {
			venueDao.deactivateVenue(venueId);
			
			systemLogService.log(
					null,
					"DELETE",
					"VENUE",
					venueId,
					"Venue removed"
				);

		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

}
