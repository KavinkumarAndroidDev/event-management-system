package com.ems.dao;


import java.time.LocalDateTime;
import java.util.List;

import com.ems.enums.UserRole;
import com.ems.exception.DataAccessException;
import com.ems.model.User;

public interface UserDao {
	void createUser(String fullName,String email,String phone,String passwordHash,int roleId,String status,
            LocalDateTime createdAt,LocalDateTime updatedAt,String gender)  throws DataAccessException;
	
	User findByEmail(String email)  throws DataAccessException;
	
	boolean updateUserStatus(int userId, String status)  throws DataAccessException;
	
	List<User> findAllUsers(String userType)  throws DataAccessException;
	
	List<User> findAllUsers()  throws DataAccessException;
	
	UserRole getRole(User user)  throws DataAccessException;

	boolean checkUserExists(String email) throws DataAccessException;
	
	void incrementFailedAttempts(int userId) throws DataAccessException;
	
	void resetFailedAttempts(int userId) throws DataAccessException;

}
