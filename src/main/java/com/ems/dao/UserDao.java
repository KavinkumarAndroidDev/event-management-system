package com.ems.dao;


import java.util.List;

import com.ems.model.User;

public interface UserDao {
	void createUser(User user);
	User findByEmail(String email);
	void updateUserStatus(int userId, String status);
	List<User> findAllUsers();
	int getRole(User user);
	List<User> findAllOrganizers();
}
//
//public interface UserDao {
//    User findByEmail(String email);
//    void save(User user);
//}
