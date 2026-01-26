package com.ems.menu;

import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.exception.AuthorizationException;
import com.ems.model.User;
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
					login();
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
				default:
					if (confirmLogout()) {
						System.out.println("Exiting the app...");
	                    return;
	                }
	                break;
			}
			
		}
		
	}
	private void createAccount(UserRole role) {
		String fullName =
	            InputValidationUtil.readNonEmptyString(
	                ScannerUtil.getScanner(),
	                "Enter Full Name: "
	            );
		String email = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the email address: ");
		while (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            email =
                InputValidationUtil.readNonEmptyString(
                    ScannerUtil.getScanner(),
                    "Enter valid Email Address: "
                );
        }
		while(userService.checkUserExists(email)) {
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
	        }else {
	        	phone = phone.replaceAll("\\D", ""); 
	            while (phone.length() != 10) {
	            	phone =InputValidationUtil.readString(ScannerUtil.getScanner(),"Enter valid phone Number: ");
	            }
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
	    	                "Enter the valid password: "
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
	                    : "Opt-out"; 
	        userService.createAccount(fullName, email, phone, password, gender, role);
		
	}
	private void login() {
		String email = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter the email address: ");
		while (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            email =
                InputValidationUtil.readNonEmptyString(
                    ScannerUtil.getScanner(),
                    "Enter valid Email Address: "
                );
        }
		
		String password =
	            InputValidationUtil.readNonEmptyString(
	                ScannerUtil.getScanner(),
	                "Enter the password: "
	            );
		try {
			User user = userService.login(email, password);
			if(user == null) {
				return;
			}
			UserRole role = userService.getRole(user);
			if(role == UserRole.ADMIN) {
				AdminMenu adminMenu = new AdminMenu(user);
				adminMenu.start();
			}else if(role == UserRole.ATTENDEE) {
				UserMenu userMenu = new UserMenu(user);
				userMenu.start();
			}else if(role == UserRole.ORGANIZER) {
				OrganizerMenu organizerMenu = new OrganizerMenu(user);
				organizerMenu.start();
			}
		} catch (AuthorizationException e) {
			System.out.println(e);
		} catch (AuthenticationException e) {
			System.out.println(e);
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
