package com.ems.menu;

import com.ems.actions.UserAction;
import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.model.User;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

/*
 * Handles the main application entry menu.
 *
 * Responsibilities:
 * - Display initial application options
 * - Handle user login and registration flows
 * - Route users to role specific menus or guest access
 */
public class MainMenu {
	private final UserAction userAction = new UserAction();
    
	public void start() {
		while(true) {
			System.out.println("\nMain Menu"
					+ "\n\nEnter your choice:"
			        + "\n1. Login"
			        + "\n2. Register as User"
			        + "\n3. Register as Organizer"
			        + "\n4. Continue as Guest"
			        + "\n5. Exit the app"
			        + "\n>");
			int input = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch(input) {
				case 1:
					handleLogin();
					break;
				case 2:
					createAccount(UserRole.ATTENDEE);
					break;
				case 3:
					createAccount(UserRole.ORGANIZER);
					
					break;
				case 4:
					GuestMenu guestMenu = new GuestMenu();
					guestMenu.start();
					break;
				case 5:
					if (confirmLogout()) {
						System.out.println("Exiting the app...");
	                    return;
	                }
	                break;
	            default:
	            	System.out.println("Enter the valid option (1 - 5)");
			}
			
		}
		
	}
	private void handleLogin() {

	    try {
	        User user = userAction.login();
	        if (user == null) {
	        	return;
	        }

	        UserRole role = userAction.getUserRole(user);

	        switch (role) {
	            case ADMIN:
	                new AdminMenu(user).start();
	                break;
	
	            case ATTENDEE:
	                new UserMenu(user).start();
	                break;
	
	            case ORGANIZER:
	                new OrganizerMenu(user).start();
	                break;
	
	            default:
	                System.out.println("Unknown user role.");
	        }

	    } catch (AuthorizationException | AuthenticationException e) {
	        System.out.println(e.getMessage());
	    }
	}

	
	private void createAccount(UserRole role) {
        userAction.createAccount(role);
    }
	
	private boolean confirmLogout() {
	    char choice = InputValidationUtil.readChar(
	        ScannerUtil.getScanner(),
	        "Are you sure to leave the application (Y/N): "
	    );
	    return Character.toUpperCase(choice) == 'Y';
	}

}
