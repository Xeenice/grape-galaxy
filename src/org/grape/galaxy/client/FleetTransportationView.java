package org.grape.galaxy.client;

import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Transportation;

public class FleetTransportationView extends TransportationView {

	private FleetTransportation3dView fleetTransportation3dView;

	public FleetTransportationView(SectorView parentView, Transportation model) {
		super(parentView, model);

		fleetTransportation3dView = new FleetTransportation3dView(this, model);
		setTransportation3dView(fleetTransportation3dView);
	}

	public boolean isAttackPlanet(Planet planet) {
		return fleetTransportation3dView.isAttackPlanet(planet);
	}

}
