package com.ems.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ems.dao.*;
import com.ems.enums.NotificationType;
import com.ems.exception.DataAccessException;
import com.ems.model.Event;
import com.ems.model.EventRegistrationReport;
import com.ems.model.User;
import com.ems.service.AdminService;
import com.ems.service.EventService;
import com.ems.service.NotificationService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class AdminServiceImpl implements AdminService {

    private final UserDao userDao;
    private final EventDao eventDao;
    private final NotificationDao notificationDao;
    private final RegistrationDao registrationDao;
    private final NotificationService notificationService;
    private final EventService eventService;

    // initializes admin service with required dependencies
    public AdminServiceImpl(
            UserDao userDao,
            EventDao eventDao,
            NotificationDao notificationDao,
            RegistrationDao registrationDao,
            NotificationService notificationService,
            EventService eventService
    ) {
        this.userDao = userDao;
        this.eventDao = eventDao;
        this.notificationDao = notificationDao;
        this.registrationDao = registrationDao;
        this.notificationService = notificationService;
        this.eventService = eventService;
    }

    // shows users list based on role
    @Override
    public void getUsersList(String userType) {
        List<User> users = new ArrayList<>();
		try {
			users = userDao.findAllUsers(userType);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        users.sort(Comparator.comparing(User::getFullName));
        System.out.println("\n==============================================================");
        System.out.printf(
            "%-5s %-5s %-20s %-10s %-25s %-15s %-10s%n",
            "NO" ,"ID", "Name", "Gender", "Email", "Phone", "Status"
        );
        System.out.println("==============================================================");

        int displayIndex = 1;
        for(User user: users) {
            System.out.printf(
                "%-5d %-5d %-20s %-10s %-25s %-15s %-10s%n",
                displayIndex,
                user.getUserId(),
                user.getFullName(),
                user.getGender(),
                user.getEmail(),
                user.getPhone() == null ? "-" : user.getPhone(),
                user.getStatus()
            );
            displayIndex++;
        }

        System.out.println("==============================================================");
    }

    // updates user account status
    @Override
    public void changeStatus(String status) {
        int userId = InputValidationUtil.readInt(
                ScannerUtil.getScanner(), "Enter the user id: ");
        try {
			userDao.updateUserStatus(userId, status);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
    }

    // sends system wide notification
    @Override
    public void sendSystemWideNotification(String message, String notificationType) {
        notificationService.sendSystemWideNotification(message, notificationType);
    }

    // approves pending events
    @Override
    public void approveEvents(int userId){
        List<Event> events = new ArrayList<>();
		try {
			events = eventDao.listEventsYetToApprove();
		}catch(DataAccessException e) {
    		System.out.println(e.getMessage());
    	}
        if (events.isEmpty()) {
            System.out.println("There are no events yet to be approved!");
            return;
        }

        eventService.printEventSummaries(events);

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
    public void cancelEvents(){
    	try {
	        List<Event> events = eventDao.listAvailableAndDraftEvents();
	        if (events.isEmpty()) {
	        	System.out.println("There is no event to in the draft or published state");
	        	return;
	        }
	
	        eventService.printEventSummaries(events);
	
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
    public void getEventWiseRegistrations() {
        try {
        	List<Event> events = eventDao.listAllEvents();
        	eventService.printEventSummaries(events);
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
            List<EventRegistrationReport> reports =
                registrationDao.getEventWiseRegistrations(selectedEvent.getEventId());

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


    // marks completed events
    @Override
    public void markCompletedEvents() {
        eventService.completeEvents();
    }

    // sends notification based on role
	@Override
	public void sendNotificationByRole() {
		System.out.println("\nAvailable roles:\n1. Users,\n2. Organizers,\n3. Admins");
		int roleId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the role id: ");
		while(roleId <1 || roleId > 3) {
			roleId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the valid role id: ");
		}
		String role;
		if(roleId == 1) role = "ATTENDEE";
		else if(roleId == 2) role ="ORGANIZER";
		else role ="ADMIN";
		System.out.println("available payment method:");
		NotificationType[] methods = NotificationType.values();

        for (int i = 0; i < methods.length; i++) {
            System.out.println(
                (i + 1) + ". " + methods[i].name().replace("_", " ")
            );
        }

        int choice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter choice (1-" + methods.length + "): "
        );

        while (choice < 1 || choice > methods.length) {
            System.out.println("Invalid notification type selected");
            choice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter choice (1-" + methods.length + "): "
            );
        }

        NotificationType selectedType = methods[choice - 1];
        
        String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the notification message: ");
        try {
			notificationDao.sendNotificationByRole(message, selectedType.toString(), role);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // sends notification to a specific user
	@Override
	public void sendNotificationToUser() {
		int userId = InputValidationUtil.readInt(ScannerUtil.getScanner(), "Enter the user id: ");
		NotificationType[] methods = NotificationType.values();

        for (int i = 0; i < methods.length; i++) {
            System.out.println(
                (i + 1) + ". " + methods[i].name().replace("_", " ")
            );
        }

        int choice = InputValidationUtil.readInt(
            ScannerUtil.getScanner(),
            "Enter choice (1-" + methods.length + "): "
        );

        while (choice < 1 || choice > methods.length) {
            System.out.println("Invalid notification type selected");
            choice = InputValidationUtil.readInt(
                ScannerUtil.getScanner(),
                "Enter choice (1-" + methods.length + "): "
            );
        }

        NotificationType selectedType = methods[choice - 1];
        
        String message = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the notification message: ");
        try {
			notificationDao.sendNotification(userId,message,selectedType.toString());
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
	}

    // lists all users
	@Override
	public void getAllUsers() {
		
		List<User> users = new ArrayList<>();
		try {
			users = userDao.findAllUsers();
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        //sorting 
        users.sort(Comparator.comparing(User::getFullName));
        System.out.println("\n==============================================================");
        System.out.printf(
            "%-5s %-5s %-20s %-10s %-25s %-15s %-10s%n",
            "NO", "ID", "Name", "Gender", "Email", "Phone", "Status"
        );
        System.out.println("==============================================================");
        int displayIndex = 1;
        for(User user: users) {
            System.out.printf(
                "%-5d %-5d %-20s %-10s %-25s %-15s %-10s%n",
                displayIndex,
                user.getUserId(),
                user.getFullName(),
                user.getGender(),
                user.getEmail(),
                user.getPhone() == null ? "-" : user.getPhone(),
                user.getStatus()
            );
            displayIndex++;
        }

        System.out.println("==============================================================");
	}

	@Override
	public void printAllEvents() {
		List<Event> events = eventService.getAllEvents();
		if(events != null || !events.isEmpty()) {
			eventService.printEventSummaries(events);
		}
		
	}
}
