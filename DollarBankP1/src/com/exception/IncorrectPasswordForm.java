package com.exception;

public class IncorrectPasswordForm extends Exception {

	private static final long serialVersionUID = 5307164218071794694L;
	
	public IncorrectPasswordForm() {
		super("Incorrect Password Format");
	}
	
}
