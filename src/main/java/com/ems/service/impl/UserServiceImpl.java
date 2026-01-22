package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import com.ems.dao.RoleDao;
import com.ems.dao.UserDao;
import com.ems.exception.AuthorizationException;
import com.ems.exception.DataAccessException;
import com.ems.exception.InvalidPasswordFormatException;
import com.ems.exception.AuthenticationException;
import com.ems.menu.AdminMenu;
import com.ems.menu.OrganizerMenu;
import com.ems.menu.UserMenu;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.service.EventService;
import com.ems.service.UserService;
import com.ems.util.InputValidationUtil;
import com.ems.util.PasswordUtil;
import com.ems.util.ScannerUtil;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final EventService eventService;

    private static final Logger logger =
            Logger.getLogger(UserServiceImpl.class.getName());

    public UserServiceImpl(
            UserDao userDao,
            RoleDao roleDao,
            EventService eventService
    ) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.eventService = eventService;
    }

    @Override
    public User login() throws AuthorizationException, AuthenticationException {

        String emailId =
            InputValidationUtil.readString(
                ScannerUtil.getScanner(),
                "Enter the email address: "
            );

        while (!emailId.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {

            emailId =
                InputValidationUtil.readString(
                    ScannerUtil.getScanner(),
                    "Enter the valid email address: "
                );
        }

        String password =
            InputValidationUtil.readString(
                ScannerUtil.getScanner(),
                "Enter the password: "
            );

        while (!password.matches(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {

            password =
                InputValidationUtil.readString(
                    ScannerUtil.getScanner(),
                    "Enter the password in the valid format: "
                );
        }
        try {
	        User user = userDao.findByEmail(emailId.toLowerCase());
	
	        if (user == null) {
	            throw new AuthorizationException("Invalid email address!");
	        }
            if (!PasswordUtil.verifyPassword(
                    password, user.getPasswordHash())) {

                throw new AuthenticationException("Invalid credentials");
            }else if(user.getStatus().toString().equalsIgnoreCase("suspended")) {
            	throw new AuthorizationException("\nYour account has been suspended!\ncontact admin@ems.com for more info");
            }

            System.out.println("Logged in as: " + emailId);

            int role = getRole(user);

            if (role == 1) {
                AdminMenu adminMenu = new AdminMenu(user);
                adminMenu.start();
            } else if (role == 2) {
                UserMenu userMenu = new UserMenu(user);
                userMenu.start();
            } else if (role == 3) {
                OrganizerMenu organizerMenu = new OrganizerMenu(user);
                organizerMenu.start();
            } else {
                logger.warning("Unexpected role");
            }

            return user;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public void createAccount(int i) {

        String fullName =
            InputValidationUtil.readNonEmptyString(
                ScannerUtil.getScanner(),
                "Enter Full Name: "
            );

        String email =
            InputValidationUtil.readNonEmptyString(
                ScannerUtil.getScanner(),
                "Enter Email Address: "
            );

        while (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {

            email =
                InputValidationUtil.readNonEmptyString(
                    ScannerUtil.getScanner(),
                    "Enter valid Email Address: "
                );
        }

        String phone =
            InputValidationUtil.readString(
                ScannerUtil.getScanner(),
                "Enter Phone Number: "
            );

        if (phone.trim().isEmpty()) {
            phone = null;
        }

        String passwordPrompt =
            "Enter Password (Min 8 chars, 1 Digit, 1 Upper, 1 Lower, 1 Special [!@#$%^&*]): ";

        String password =
            InputValidationUtil.readNonEmptyString(
                ScannerUtil.getScanner(),
                passwordPrompt
            );

        while (!password.matches(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {

            password =
                InputValidationUtil.readNonEmptyString(
                    ScannerUtil.getScanner(),
                    "Weak password! " + passwordPrompt
                );
        }

        int genderChoice;
        do {
            genderChoice =
                InputValidationUtil.readInt(
                    ScannerUtil.getScanner(),
                    "Enter your gender:\n1. Male\n2. Female\n3. Prefer not to say\n"
                );
        } while (genderChoice < 1 || genderChoice > 3);

        String gender =
            (genderChoice == 1)
                ? "Male"
                : (genderChoice == 2)
                    ? "Female"
                    : "Prefer not to say";
        try {
        List<Role> roles = roleDao.getRoles();
        roles.sort(
        	    (r1, r2) -> r1.getRoleName().compareToIgnoreCase(r2.getRoleName())
        	);
        String targetRoleName =
            (i == 1) ? "ATTENDEE" : "ORGANIZER";

        Role selectedRole =
            roles.stream()
                .filter(r ->
                    r.getRoleName().equalsIgnoreCase(targetRoleName))
                .findFirst()
                .orElse(null);

        if (selectedRole != null) {
            
                String hashedPassword =
                    PasswordUtil.hashPassword(password);

                userDao.createUser(
                    fullName,
                    email,
                    phone,
                    hashedPassword,
                    selectedRole.getRoleId(),
                    "ACTIVE",
                    LocalDateTime.now(),
                    null,
                    gender
                );

                System.out.println(
                    "Account created successfully as "
                    + selectedRole.getRoleName()
                    + " for: "
                    + fullName
                );
        } else {
            System.err.println(
                "Error: The role '" + targetRoleName + "' was not found in the database."
            );
        }
        }catch(DataAccessException e) {
        	System.out.println(e.getMessage());
        } catch (InvalidPasswordFormatException e) {
        	System.out.println(e.getMessage());
		}
    }

    @Override
    public int getRole(User user) {
        try {
			return userDao.getRole(user);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
        return 0;
    }

    @Override
    public void printAllAvailableEvents() {
        eventService.printAllAvailableEvents();
    }

    @Override
    public void viewTicketOptions() {
        eventService.viewTicketOptions();
    }

    @Override
    public void viewEventDetails() {
        eventService.viewEventDetails();
    }

    @Override
    public void registerForEvent(int userId) {
        eventService.registerForEvent(userId);
    }

    @Override
    public void viewUpcomingEvents(int userId) {
        eventService.viewUpcomingEvents(userId);
    }

    @Override
    public void viewPastEvents(int userId) {
        eventService.viewPastEvents(userId);
    }

    @Override
    public void viewBookingDetails(int userId) {
        eventService.viewBookingDetails(userId);
    }

    @Override
    public void submitRating(int userId) {
        eventService.submitRating(userId);
    }

    @Override
    public void submitReview(int userId) {
        eventService.submitRating(userId);
    }

    @Override
    public void searchEvents() {
        while (true) {
            System.out.println(
                "\nEnter your choice:\n"
              + "1. Search by category\r\n"
              + "2. Search by date\r\n"
              + "3. Search by date range\n"
              + "4. Search by city\r\n"
              + "5. Filter by price\r\n"
              + "6. Filter by availability\r\n"
              + "7. Exit to user menu\n"
            );

            int filterChoice =
                InputValidationUtil.readInt(
                    ScannerUtil.getScanner(), ""
                );

            switch (filterChoice) {
                case 1 :
                	eventService.searchBycategory();
                	break;
                case 2 :
                	eventService.searchByDate();
                	break;
                case 3 :
                	eventService.searchByDateRange();
                	break;
                case 4 :
                	eventService.searchByCity();
                	break;
                case 5 :
                	eventService.filterByPrice();
                	break;
                case 6 :
                	eventService.printAllAvailableEvents();
                	break;
                case 7 :
                	return;
                default :
                	System.out.println("Enter the valid option");
            }
        }
    }
}
