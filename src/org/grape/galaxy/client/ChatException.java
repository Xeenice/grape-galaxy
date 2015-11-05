package org.grape.galaxy.client;

import java.io.Serializable;

public class ChatException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public ChatException() {
		super();
	}

	public ChatException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChatException(String message) {
		super(message);
	}

	public ChatException(Throwable cause) {
		super(cause);
	}
}