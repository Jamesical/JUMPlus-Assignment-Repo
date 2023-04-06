package com.exception;

public class UserDupeException extends Exception{

	private static final long serialVersionUID = -1865867689877064473L;
	
	public UserDupeException() {
		super("User Already Exists");
	}

}
