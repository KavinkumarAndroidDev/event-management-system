package com.ems.service;

import java.util.List;

import com.ems.enums.NotificationType;
import com.ems.enums.UserRole;
import com.ems.model.Category;
import com.ems.model.User;
import com.ems.model.Venue;

public interface AdminService {

	// user management
	List<User> getUsersList(String userType);

	List<User> getAllUsers();

	boolean changeStatus(String status, int userId);

	// notification management
	void sendSystemWideNotification(String message, String notificationType);

	void sendNotificationByRole(String message, NotificationType selectedType, UserRole role);

	void sendNotificationToUser(String message, NotificationType selectedType, int userId);

	// event management
	void approveEvents(int userId, int eventId);

	void cancelEvent(int eventId);

	void markCompletedEvents();

	// reports & analytics 
	void getEventWiseRegistrations(int eventId);

	void getRevenueReport();

	void getOrganizerWisePerformance();

	// category management
	List<Category> getAllCategories();

	void addCategory(String name);

	void updateCategory(int categoryId, String name);
	
	void updateCategory(int categoryId);

	void deleteCategory(int categoryId);

	// Venue management
	void addVenue(Venue venue);

	void updateVenue(Venue selectedVenue);

	void removeVenue(int venueId);
}