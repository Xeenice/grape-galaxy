package org.grape.galaxy.client;

import java.io.Serializable;

public class ActivityException extends GalaxyException implements
		Serializable {

	private static final long serialVersionUID = -394282738330643157L;

	public ActivityException() {
		super();
	}

	public ActivityException(String message) {
		super(message);
	}

	public ActivityException(Throwable cause) {
		super(cause);
	}

	public ActivityException(String message, Throwable cause) {
		super(message, cause);
	}
}
