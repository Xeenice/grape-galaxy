package org.grape.galaxy.client;

import org.grape.galaxy.model.Planet;

public interface PlanetSelectionFilter {

	boolean canSelect(Planet planet);
	
	void onSelect(Planet planet);
}
