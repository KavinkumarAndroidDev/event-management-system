package com.ems.menu;

import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.service.UserService;
import com.ems.util.ApplicationUtil;
import com.ems.util.InputValidationUtil;
import com.ems.util.ScannerUtil;

public class MainMenu {
	private final UserService userService;

	public MainMenu() {
	    this.userService = ApplicationUtil.userService();
	}
	public void start() {
		while(true) {
			System.out.println("\nMain Menu"
					+ "\n\nEnter your choice:"
			        + "\n1. Login"
			        + "\n2. Register as User"
			        + "\n3. Register as Organizer"
			        + "\n4. Continue as Guest"
			        + "\nAny other number to Exit Application"
			        + "\n>");
			int input = InputValidationUtil.readInt(ScannerUtil.getScanner(), "");
			switch(input) {
				case 1:
					try {
						userService.login();
					} catch (AuthorizationException e) {
						System.out.println(e.getMessage());
					} catch (AuthenticationException e) {
						System.out.println(e.getMessage());
					}
					break;
				case 2:
					userService.createAccount(1);
					break;
				case 3:
					userService.createAccount(2);
					break;
				case 4:
					GuestMenu guestMenu = new GuestMenu();
					guestMenu.start();
					break;
				default:
					if (confirmLogout()) {
						System.out.println("Exiting the app...");
	                    return;
	                }
	                break;
			}
			
		}
		
	}
	private boolean confirmLogout() {
	    char choice = InputValidationUtil.readChar(
	        ScannerUtil.getScanner(),
	        "Are you sure to leave the application (Y/N): "
	    );
	    return Character.toUpperCase(choice) == 'Y';
	}

}
