package com.ems.exception;

public class InvalidPasswordFormatException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidPasswordFormatException(String s){
		super(s);
	}
}
