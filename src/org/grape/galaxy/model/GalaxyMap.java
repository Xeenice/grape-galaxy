package org.grape.galaxy.model;

import java.io.Serializable;

public class GalaxyMap implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private GalaxyMapDetails galaxyMapDetails;
	private long startSectorIndex;
	private int sizeInSectors;

	public GalaxyMap(long startSectorIndex, int sizeInSectors) {
		this.startSectorIndex = startSectorIndex;
		this.sizeInSectors = sizeInSectors;
	}

	public void bindDetails(GalaxyMapDetails galaxyMapDetails) {
		this.galaxyMapDetails = galaxyMapDetails;
	}

	public GalaxyMapDetails getGalaxyMapDetails() {
		return galaxyMapDetails;
	}

	public long getStartSectorIndex() {
		return startSectorIndex;
	}

	public int getSizeInSectors() {
		return sizeInSectors;
	}

	public double getRelWidth() {
		return Constants.SECTOR_LINEAR_SIZE;
	}

	public double getRelHeight() {
		return Constants.SECTOR_LINEAR_SIZE;
	}
}
