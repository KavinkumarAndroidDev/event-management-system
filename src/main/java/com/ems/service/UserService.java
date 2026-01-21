package com.ems.service;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import com.ems.dao.RoleDao;
import com.ems.dao.UserDao;
import com.ems.dao.impl.RoleDaoImpl;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.exception.AuthorizationException;
import com.ems.menu.AdminMenu;
import com.ems.menu.OrganizerMenu;
import com.ems.menu.UserMenu;
import com.ems.exception.AuthenticationException;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.util.InputValidationUtil;
import com.ems.util.PasswordUtil;
import com.ems.util.ScannerUtil;

public class UserService {
	private static UserDao userDao = new UserDaoImpl();
    private static final Logger logger = Logger.getLogger(UserService.class.getName());

	
	public static User login() throws AuthorizationException, AuthenticationException{
		//check that the email address is valid
		String emailId = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter the email address: ");
		while(!emailId.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
			emailId = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter the valid email address: ");
		}
		//Password must follow the rule: (min 8 character: must contain 1 lowercase, 1 upper case, 1 special char)
		String password = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter the password: ");
		while(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")){
			password = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter the password in the valid format: ");
		}
		//gets the user details with the email address
		User user = userDao.findByEmail(emailId.toLowerCase());
		if(user == null) {
			//if the result is null means that there is no user in the given email address
			throw new AuthorizationException("Invalid email address!");
		}
		try {
			//util class to hash the user typed password and compare to hashed password in db
			if(!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
				throw new AuthenticationException("Invalid credentials");
			}else {
				System.out.println("Logged in as: " + emailId);
				//gets the user role inorder to open the menu respective to the user role
				int role = UserService.getRole(user);
				if(role == 1) {
					new AdminMenu(user);
				}else if(role == 2) {
					new UserMenu(user);
				}else if(role == 3) {
					new OrganizerMenu(user);
				}else{
					logger.warning("Unexpected role");
				}
				return user;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return  null;
		
	}
	
	
	
	public static void createAccount(int i) {
	    String fullName = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter Full Name: ");
	    //email validation
	    String email = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter Email Address: ");
	    while (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
	        email = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Enter valid Email Address: ");
	    }
	    //phone number is not a mandatory feild
	    String phone = InputValidationUtil.readString(ScannerUtil.getScanner(), "Enter Phone Number: ");
	    if(phone.trim().isEmpty()) phone = null;
	    
	    // password rule: (Min 8 chars, 1 Digit, 1 Upper, 1 Lower, 1 Special [!@#$%^&*])
	    String passwordPrompt = "Enter Password (Min 8 chars, 1 Digit, 1 Upper, 1 Lower, 1 Special [!@#$%^&*]): ";
	    String password = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), passwordPrompt);
	    while (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
	        password = InputValidationUtil.readNonEmptyString(ScannerUtil.getScanner(), "Weak password! " + passwordPrompt);
	    }
	    
	    int genderChoice;
	    do {
	        genderChoice = InputValidationUtil.readInt(
	        		ScannerUtil.getScanner(),
	            "Enter your gender:\n1. Male\n2. Female\n3. Prefer not to say\n"
	        );
	    } while (genderChoice < 1 || genderChoice > 3);

	    String gender;

	    switch (genderChoice) {
	        case 1:
	            gender = "Male";
	            break;
	        case 2:
	            gender = "Female";
	            break;
	        default:
	            gender = "Prefer not to say";
	    }
	    //used to get role_id of attendee/organizer
	    RoleDao roleDao = new RoleDaoImpl();
	    List<Role> roles = roleDao.getRoles();
	    // if the input i is 1, then the role is attendee, or else organizer
	    String targetRoleName = (i == 1) ? "ATTENDEE" : "ORGANIZER";
	    //String targetRoleName = "ADMIN"; 
	    Role selectedRole = roles.stream()
	            .filter(r -> r.getRoleName().equalsIgnoreCase(targetRoleName))
	            .findFirst()
	            .orElse(null);
	
	    if (selectedRole != null) {
	        try {
	        	//hash the password using bcrypt
	            String hashedPassword = PasswordUtil.hashPassword(password);
	            LocalDateTime now = LocalDateTime.now();
	
	            User user = new User(
	                fullName, 
	                email, 
	                phone, 
	                hashedPassword, 
	                selectedRole.getRoleId(), 
	                "ACTIVE", 
	                now, 
	                null,
	                gender
	            );
	
	            userDao.createUser(user);
	            //
	            System.out.println("Account created successfully as " + selectedRole.getRoleName() + " for: " + fullName);
	        } catch (Exception e) {
	            System.err.println("Database Error: " + e.getMessage());
	        }
	    } else {
	        System.err.println("Error: The role '" + targetRoleName + "' was not found in the database.");
	    }
	}

	public static int getRole(User user) {
		return userDao.getRole(user);
	}
	
}
