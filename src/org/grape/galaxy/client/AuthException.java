package org.grape.galaxy.client;

import java.io.Serializable;

public class AuthException extends Exception implements Serializable {

	private static final long serialVersionUID = 3472415369223300550L;

	public AuthException() {
		super();
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthException(String message) {
		super(message);
	}

	public AuthException(Throwable cause) {
		super(cause);
	}
}
