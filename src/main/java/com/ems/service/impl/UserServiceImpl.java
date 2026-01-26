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
import com.ems.service.UserService;
import com.ems.util.PasswordUtil;

public class UserServiceImpl implements UserService {

	private final UserDao userDao;
	private final RoleDao roleDao;

	public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
		this.userDao = userDao;
		this.roleDao = roleDao;
	}

	// login functionality
	@Override
	public User login(String emailId, String password) throws AuthorizationException, AuthenticationException {

		try {
			User user = userDao.findByEmail(emailId.toLowerCase());

			if (user == null) {
				throw new AuthorizationException("Invalid email address!");
			}
			if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {

				throw new AuthenticationException("Invalid credentials");
			} else if (user.getStatus().toString().equalsIgnoreCase("suspended")) {
				throw new AuthorizationException(
						"\nYour account has been suspended!\ncontact admin@ems.com for more info");
			}

			System.out.println("Logged in as: " + emailId);

			return user;

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

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
				System.out
						.println("Error: The role '" + role.toString() + "' was not found in the database.");
			}
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		} catch (InvalidPasswordFormatException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public UserRole getRole(User user) {
		try {
			return userDao.getRole(user);
		} catch (DataAccessException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

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
