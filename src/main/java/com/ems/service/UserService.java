package com.ems.service;

import com.ems.exception.AuthorizationException;
import com.ems.exception.AuthenticationException;
import com.ems.model.User;

public interface UserService {

    User login() throws AuthorizationException, AuthenticationException;

    void createAccount(int type);

    int getRole(User user);

    void printAllAvailableEvents();

    void viewTicketOptions();

    void viewEventDetails();

    void registerForEvent(int userId);

    void viewUpcomingEvents(int userId);

    void viewPastEvents(int userId);

    void viewBookingDetails(int userId);

    void submitRating(int userId);

    void submitReview(int userId);

    void searchEvents();
}
