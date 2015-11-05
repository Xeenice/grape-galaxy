package org.grape.galaxy.model;

import java.io.Serializable;

public class TransportationDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private Transportation transportation;
	private PlanetDetails sourcePlanetDetails;
	private PlanetDetails targetPlanetDetails;

	public Transportation getTransportation() {
		return transportation;
	}

	public void setTransportation(Transportation transportation) {
		this.transportation = transportation;
	}

	public PlanetDetails getSourcePlanetDetails() {
		return sourcePlanetDetails;
	}

	public void setSourcePlanetDetails(PlanetDetails sourcePlanetDetails) {
		this.sourcePlanetDetails = sourcePlanetDetails;
	}

	public PlanetDetails getTargetPlanetDetails() {
		return targetPlanetDetails;
	}

	public void setTargetPlanetDetails(PlanetDetails targetPlanetDetails) {
		this.targetPlanetDetails = targetPlanetDetails;
	}
}
