package com.ems.dao;


import java.sql.SQLException;
import java.util.List;

import com.ems.model.User;

public interface UserDao {
	void createUser(User user) throws SQLException, Exception;
	User findByEmail(String email);
	void updateUserStatus(int userId, String status);
	List<User> findAllUsers(String userType);
	int getRole(User user);
}
//
//public interface UserDao {
//    User findByEmail(String email);
//    void save(User user);
//}
