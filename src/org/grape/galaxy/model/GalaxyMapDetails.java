package org.grape.galaxy.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GalaxyMapDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private Map<Long, String> planetId2OwnerIdMap = new HashMap<Long, String>();

	public Map<Long, String> getPlanetId2OwnerIdMap() {
		return planetId2OwnerIdMap;
	}

	public void setPlanetId2OwnerIdMap(Map<Long, String> planetId2OwnerIdMap) {
		this.planetId2OwnerIdMap = planetId2OwnerIdMap;
	}
}
