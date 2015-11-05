package org.grape.galaxy.client;

import java.io.Serializable;

public class FeedbackException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public FeedbackException() {
		super();
	}

	public FeedbackException(String message, Throwable cause) {
		super(message, cause);
	}

	public FeedbackException(String message) {
		super(message);
	}

	public FeedbackException(Throwable cause) {
		super(cause);
	}
}
