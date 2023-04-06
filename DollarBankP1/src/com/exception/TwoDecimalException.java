package com.exception;

public class TwoDecimalException extends Exception {

	private static final long serialVersionUID = 428195313830830676L;
	
	public TwoDecimalException() {
		super("Must be in a '00.00' format");
	}
	
}
