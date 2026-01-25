package com.ems.service;

import com.ems.exception.AuthorizationException;
import com.ems.enums.UserRole;
import com.ems.exception.AuthenticationException;
import com.ems.model.User;

public interface UserService {

	 // authentication
    User login(String emailId, String password)
            throws AuthorizationException, AuthenticationException;

    // account creation
    void createAccount(
            String fullName,
            String email,
            String phone,
            String password,
            String gender,
            UserRole role
    );
    
    boolean checkUserExists(String email);
    
    // role resolution
    UserRole getRole(User user);
}
