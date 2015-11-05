package org.grape.galaxy.client;

import java.io.Serializable;

public class GalaxyException extends Exception implements
		Serializable {

	private static final long serialVersionUID = -2392698723357456010L;

	public GalaxyException() {
		super();
	}

	public GalaxyException(String message) {
		super(message);
	}

	public GalaxyException(Throwable cause) {
		super(cause);
	}

	public GalaxyException(String message, Throwable cause) {
		super(message, cause);
	}
}
