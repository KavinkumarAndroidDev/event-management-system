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

public class AdminServiceImpl implements AdminService {

    private final UserDao userDao;
    private final EventDao eventDao;
    private final NotificationDao notificationDao;
    private final RegistrationDao registrationDao;
    private final NotificationService notificationService;
    private final CategoryDao categoryDao;
    private final VenueDao venueDao;

    // initializes admin service with required dependencies
    public AdminServiceImpl(
            UserDao userDao,
            EventDao eventDao,
            NotificationDao notificationDao,
            RegistrationDao registrationDao,
            CategoryDao categoryDao,
            VenueDao venueDao,
            NotificationService notificationService
    ) {
        this.userDao = userDao;
        this.eventDao = eventDao;
        this.notificationDao = notificationDao;
        this.registrationDao = registrationDao;
        this.categoryDao = categoryDao;
        this.venueDao = venueDao;
        this.notificationService = notificationService;
    }

    // shows users list based on role
    @Override
    public List<User> getUsersList(String userType) {
        List<User> users = new ArrayList<>();
		try {
			users = userDao.findAllUsers(userType);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
        }
        return users;
    }

    // updates user account status
    @Override
    public boolean changeStatus(String status, int userId) {
        try {
			return userDao.updateUserStatus(userId, status);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return false;
    }

    // sends system wide notification
    @Override
    public void sendSystemWideNotification(String message, String notificationType) {
        notificationService.sendSystemWideNotification(message, notificationType);
    }

    // approves pending events
    @Override
    public void approveEvents(int userId, int eventId){
        	try {
        		boolean isApproved = eventDao.approveEvent(eventId, userId);
                if (isApproved) {
                	notificationDao.sendNotification(eventDao.getOrganizerId(eventId),
                			"Your event: " + eventId + " has been approved!",
        			        "EVENT"
        			);
                }
        	}catch(DataAccessException e) {
        		System.out.println(e.getMessage());
        	}
        
    }

    // cancels selected events
    @Override
    public void cancelEvents(int eventId){
    	try {
	        boolean isCancelled = eventDao.cancelEvent(eventId);
	        if (isCancelled) {
	            notificationDao.sendNotification(
	                    eventDao.getOrganizerId(eventId),
	                    "Your event: " + eventId + " has been cancelled!",
	                    "EVENT"
	            );
	        }
    	}catch(DataAccessException e) {
    		System.out.println(e.getMessage());
    	}
    }

    // shows event wise registration details
    @Override
    public void getEventWiseRegistrations(int eventId) {
        try {
            List<EventRegistrationReport> reports =
                registrationDao.getEventWiseRegistrations(eventId);

            if (reports.isEmpty()) {
                System.out.println("No registrations found for this event");
                return;
            }
            //comparator usage
            reports.sort(
                Comparator.comparing(EventRegistrationReport::getRegistrationDate)
                          .reversed()
            );
            if(!reports.isEmpty()) {
            	System.out.println("Event Wise Registration");
            	reports.forEach(System.out::println);
            }else {
            	System.out.println("No registrations for the given event!");
            }

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    // revenue report placeholder
    @Override
    public void getRevenueReport() {
        try {
            Map<String, Double> revenueMap =
                eventDao.getEventWiseRevenue();

            if (revenueMap.isEmpty()) {
                System.out.println("No revenue data available.");
                return;
            }

            System.out.println("\nEvent Wise Revenue Report");
            System.out.println("-----------------------------------");

            revenueMap.forEach((event, revenue) -> {
                System.out.println(
                    "Event : " + event +
                    " | Revenue : ₹" + revenue
                );
            });

            System.out.println("-----------------------------------");

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }


    // organizer performance placeholder
    @Override
    public void getOrganizerWisePerformance() {
        try {
            Map<String, Integer> organizerStats =
                eventDao.getOrganizerWiseEventCount();

            if (organizerStats.isEmpty()) {
                System.out.println("No organizer data available.");
                return;
            }

            System.out.println("\nOrganizer Wise Performance");
            System.out.println("-----------------------------------");

            organizerStats.forEach((organizer, count) -> {
                System.out.println(
                    "Organizer : " + organizer +
                    " | Total Events : " + count
                );
            });

            System.out.println("-----------------------------------");

        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // sends notification based on role
	@Override
	public void sendNotificationByRole(String message, NotificationType selectedType,UserRole role) {
		try {
			notificationDao.sendNotificationByRole(message, selectedType.toString(), role.toString());
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // sends notification to a specific user
	@Override
	public void sendNotificationToUser(String message, NotificationType selectedType,int userId) {
		try {
			notificationDao.sendNotification(userId,message,selectedType.toString());
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // lists all users
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
        
        //sorting 
        users.sort(Comparator.comparing(User::getFullName));
        return users;
        
	}

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
	    } catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
	}

	@Override
	public void updateCategory(int categoryId, String name) {
	    try {
	        categoryDao.updateCategoryName(categoryId, name);
	    } catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
	}

	@Override
	public void deleteCategory(int categoryId) {
	    try {
	        categoryDao.deactivateCategory(categoryId);
	    } catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
	}

	@Override
	public void markCompletedEvents() {
		try {
			eventDao.completeEvents();
		} catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
		
	}

	@Override
	public void addVenue(Venue venue) {
	    try {
	        venueDao.addVenue(venue);
	    } catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
	}

	@Override
	public void updateVenue(Venue venue) {
	    try {
	        venueDao.updateVenue(venue);
	    } catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
	}

	@Override
	public void removeVenue(int venueId) {
	    try {
	        venueDao.deactivateVenue(venueId);
	    } catch (DataAccessException e) {
	        System.out.println(e.getMessage());
	    }
	}

}
