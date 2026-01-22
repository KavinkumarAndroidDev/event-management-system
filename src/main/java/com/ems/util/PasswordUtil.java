package com.ems.util;

import org.mindrot.jbcrypt.BCrypt;

import com.ems.exception.InvalidPasswordFormatException;

public final class PasswordUtil {
	private PasswordUtil(){
		
	}
	public static String hashPassword(String plainPassword)  throws InvalidPasswordFormatException{
		if(!plainPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$")) {
			throw new InvalidPasswordFormatException("Invalid password format");
		}
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}