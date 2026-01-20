package com.ems.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import com.ems.dao.RoleDao;
import com.ems.dao.UserDao;
import com.ems.dao.impl.RoleDaoImpl;
import com.ems.dao.impl.UserDaoImpl;
import com.ems.exception.AuthorizationException;
import com.ems.exception.AuthenticationException;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.util.InputValidationUtil;
import com.ems.util.PasswordUtil;

public class UserService {
	static Scanner scanner;
	private static UserDao userDao = new UserDaoImpl();
	
	public UserService(Scanner scanner){
		this.scanner = scanner;
	}
	
	public static User login(String emailId, String password) throws AuthorizationException, AuthenticationException{
		User user = userDao.findByEmail(emailId.toLowerCase());
		if(user == null) {
			throw new AuthorizationException("Invalid email address!");
		}
		try {
			if(!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
				throw new AuthenticationException("Invalid credentials");
			}else {
				System.out.println("Loggedin as: " + emailId);
				return user;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return  null;
		
	}
	
	public static void createAccount(int i) {
	    String fullName = InputValidationUtil.readNonEmptyString(scanner, "Enter Full Name: ");
	
	    String email = InputValidationUtil.readNonEmptyString(scanner, "Enter Email Address: ");
	    while (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
	        email = InputValidationUtil.readNonEmptyString(scanner, "Enter valid Email Address: ");
	    }
	
	    String phone = InputValidationUtil.readNonEmptyString(scanner, "Enter Phone Number: ");
	
	    String passwordPrompt = "Enter Password (Min 8 chars, 1 Digit, 1 Upper, 1 Lower, 1 Special [!@#$%^&*]): ";
	    String password = InputValidationUtil.readNonEmptyString(scanner, passwordPrompt);
	    while (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
	        password = InputValidationUtil.readNonEmptyString(scanner, "Weak password! " + passwordPrompt);
	    }
	
	    RoleDao roleDao = new RoleDaoImpl();
	    List<Role> roles = roleDao.getRoles();
	    
	    String targetRoleName = (i == 1) ? "ATTENDEE" : "ORGANIZER";
	    //String targetRoleName = "ADMIN"; 
	    Role selectedRole = roles.stream()
	            .filter(r -> r.getRoleName().equalsIgnoreCase(targetRoleName))
	            .findFirst()
	            .orElse(null);
	
	    if (selectedRole != null) {
	        try {
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
	                null
	            );
	
	            userDao.createUser(user);
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
