package com.rydlo.custom_exception;

public class UserRoleMismatchException extends RuntimeException {

	public UserRoleMismatchException() {
		
	}

	public UserRoleMismatchException(String message) {
		super(message);
		
	}

	public UserRoleMismatchException(Throwable cause) {
		super(cause);
		
	}

	public UserRoleMismatchException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UserRoleMismatchException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
