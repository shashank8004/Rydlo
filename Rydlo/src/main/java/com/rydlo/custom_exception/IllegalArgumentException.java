package com.rydlo.custom_exception;

public class IllegalArgumentException extends RuntimeException {

	public IllegalArgumentException() {
	}

	public IllegalArgumentException(String message) {
		super(message);
		
	}

	public IllegalArgumentException(Throwable cause) {
		super(cause);
		
	}

	public IllegalArgumentException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public IllegalArgumentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

}
