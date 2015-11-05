package org.grape.galaxy.client;

import java.io.Serializable;

public class PlanetRenameException extends GalaxyException implements
		Serializable {

	private static final long serialVersionUID = 7706688669568046097L;

	public PlanetRenameException() {
		super();
	}

	public PlanetRenameException(String message) {
		super(message);
	}

	public PlanetRenameException(Throwable cause) {
		super(cause);
	}

	public PlanetRenameException(String message, Throwable cause) {
		super(message, cause);
	}
}

