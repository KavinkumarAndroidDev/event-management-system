package com.ems.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.ems.dao.RoleDao;
import com.ems.dao.UserDao;
import com.ems.enums.UserRole;
import com.ems.exception.AuthorizationException;
import com.ems.exception.DataAccessException;
import com.ems.exception.InvalidPasswordFormatException;
import com.ems.exception.AuthenticationException;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.service.SystemLogService;
import com.ems.service.UserService;
import com.ems.util.PasswordUtil;

/*
 * Handles user authentication and account management.
 *
 * Responsibilities:
 * - Authenticate users during login
 * - Create new user accounts with role assignment
 * - Validate user credentials and account status
 * - Provide user role and existence checks
 */
public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final RoleDao roleDao;
	private final SystemLogService systemLogService;

	public UserServiceImpl(UserDao userDao, RoleDao roleDao, SystemLogService systemLogService) {
		this.userDao = userDao;
		this.roleDao = roleDao;
		this.systemLogService = systemLogService;
	}

	/*
	 * Authenticates a user using email and password.
	 *
	 * Rules: - Email must exist - Password must match stored hash - Suspended
	 * accounts are not allowed to log in
	 */
	@Override
	public User login(String emailId, String password)
	        throws AuthorizationException, AuthenticationException {

	    try {
	        User user = userDao.findByEmail(emailId.toLowerCase());

	        if (user == null) {
	            throw new AuthorizationException("Invalid email address!");
	        }

	        if ("SUSPENDED".equalsIgnoreCase(user.getStatus())) {
	        	systemLogService.log(
                	    user.getUserId(),
                	    "LOGIN_BLOCKED",
                	    "USER",
                	    user.getUserId(),
                	    "Login attempt blocked. Account is suspended"
                	);
	            throw new AuthorizationException(
	                "\nYour account has been suspended!\ncontact admin@ems.com for more info"
	            );
	        }

	        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
	        	systemLogService.log(
	        		    user.getUserId(),
	        		    "LOGIN_FAILED",
	        		    "USER",
	        		    user.getUserId(),
	        		    "Invalid password attempt"
	        		);

	            userDao.incrementFailedAttempts(user.getUserId());

	            if (user.getFailedAttempts() + 1 >= 3) {
	                userDao.updateUserStatus(user.getUserId(), "SUSPENDED");
	                systemLogService.log(
	                	    user.getUserId(),
	                	    "ACCOUNT_SUSPENDED",
	                	    "USER",
	                	    user.getUserId(),
	                	    "Account suspended due to multiple failed login attempts"
	                	);


	                throw new AuthorizationException(
	                    "Account suspended due to multiple failed login attempts\ncontact admin@ems.com for more info"
	                );
	            }

	            throw new AuthenticationException("Invalid credentials");
	        }

	        userDao.resetFailedAttempts(user.getUserId());

	        System.out.println("\nWelcome, " + user.getFullName());
	        return user;

	    } catch (DataAccessException e) {
	        throw new AuthenticationException("Login failed");
	    }
	}


	/*
	 * Creates a new user account with the specified role.
	 *
	 * Rules: - Role must exist in the system - Password must meet security
	 * requirements before hashing
	 */
	@Override
	public void createAccount(String fullName, String email, String phone, String password, String gender,
			UserRole role) {

		try {
			List<Role> roles = roleDao.getRoles();
			Role selectedRole = roles.stream().filter(r -> r.getRoleName().equalsIgnoreCase(role.toString()))
					.findFirst().orElse(null);

			if (selectedRole != null) {

				String hashedPassword = PasswordUtil.hashPassword(password);

				userDao.createUser(fullName, email, phone, hashedPassword, selectedRole.getRoleId(), "ACTIVE",
						LocalDateTime.now(), null, gender);

			} else {
				System.out.println("Error: The role '" + role.toString() + "' was not found in the database.");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		} catch (InvalidPasswordFormatException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Retrieves the role associated with the given user.
	 */
	@Override
	public UserRole getRole(User user) {
		try {
			return userDao.getRole(user);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/*
	 * Checks whether a user already exists based on email.
	 */
	@Override
	public boolean checkUserExists(String email) {
		try {
			return userDao.checkUserExists(email);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

}
