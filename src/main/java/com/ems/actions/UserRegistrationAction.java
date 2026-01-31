package com.ems.actions;

import java.util.List;

import com.ems.model.BookingDetail;
import com.ems.model.UserEventRegistration;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;
import com.ems.util.MenuHelper;

public class UserRegistrationAction {
    private final EventService eventService;

    public UserRegistrationAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public void listUpcomingEvents(int userId) {
    	List<UserEventRegistration> upcoming =  eventService.viewUpcomingEvents(userId);
        if (upcoming.isEmpty()) {
	        System.out.println("\nYou have no upcoming events.");
	        return;
	    }

	    MenuHelper.printEventsList(upcoming);
    }

    public void listPastEvents(int userId) {
        List<UserEventRegistration> past =  eventService.viewPastEvents(userId);
        if (past.isEmpty()) {
	        System.out.println("\nYou have no past events.");
	        return;
	    }

	    MenuHelper.printEventsList(past);
    }

    public List<BookingDetail> getBookingDetails(int userId) {
        return eventService.viewBookingDetails(userId);
    }

	public void viewBookingDetails(int userId) {
		List<BookingDetail> bookingDetails = getBookingDetails(userId);
		if (bookingDetails.isEmpty()) {
            System.out.println("You have no bookings yet.");
            return;
        }
		MenuHelper.printBookingDetails(bookingDetails);
		
	}

	public List<UserEventRegistration> getUpcomingEvents(int userId) {
		return eventService.viewUpcomingEvents(userId);
	}

	public List<UserEventRegistration> getPastEvents(int userId) {
		// TODO Auto-generated method stub
		return eventService.viewPastEvents(userId);
	}
}