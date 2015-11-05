package org.grape.galaxy.client;

import java.io.Serializable;

public class RatingException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public RatingException() {
		super();
	}

	public RatingException(String message, Throwable cause) {
		super(message, cause);
	}

	public RatingException(String message) {
		super(message);
	}

	public RatingException(Throwable cause) {
		super(cause);
	}
}
