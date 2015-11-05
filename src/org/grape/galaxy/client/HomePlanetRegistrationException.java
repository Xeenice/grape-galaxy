package org.grape.galaxy.client;

import java.io.Serializable;

public class HomePlanetRegistrationException extends GalaxyException implements
		Serializable {

	private static final long serialVersionUID = 7706688669568046097L;

	public HomePlanetRegistrationException() {
		super();
	}

	public HomePlanetRegistrationException(String message) {
		super(message);
	}

	public HomePlanetRegistrationException(Throwable cause) {
		super(cause);
	}

	public HomePlanetRegistrationException(String message, Throwable cause) {
		super(message, cause);
	}
}
