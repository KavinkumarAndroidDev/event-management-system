package com.ems.service.impl;

import com.ems.service.EventService;
import com.ems.service.GuestService;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class GuestServiceImpl implements GuestService {

    private final UserService userService;
    private final EventService eventService;

    public GuestServiceImpl(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public void createAccount() {
        userService.createAccount(1);
    }

    @Override
    public void printAllAvailableEvents() {
        eventService.printAllAvailableEvents();
    }

    @Override
    public void viewEventDetails() {
        eventService.viewEventDetails();
    }

    @Override
    public void viewTicketOptions() {
        eventService.viewTicketOptions();
    }

    @Override
    public void searchEvents() {
        while (true) {
            System.out.println(
                "\nEnter your choice:\n"
              + "1. Search by category\r\n"
              + "2. Search by date\r\n"
              + "3. Filter by availability\r\n"
              + "4. Exit to user menu\n"
            );

            int filterChoice =
                InputValidationUtil.readInt(ScannerUtil.getScanner(), "");

            switch (filterChoice) {
                case 1:
                    eventService.searchBycategory();
                    break;
                case 2:
                    eventService.searchByDate();
                    break;
                case 3:
                    eventService.printAllAvailableEvents();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Enter the valid option");
            }
        }
    }
}
