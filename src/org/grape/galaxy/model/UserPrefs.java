package org.grape.galaxy.model;

import java.io.Serializable;

public class UserPrefs implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private long sectorIndex;
	
	public UserPrefs() {
		// Центральный сектор
		sectorIndex = (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + 1)
				* (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2);
	}

	public long getSectorIndex() {
		return sectorIndex;
	}

	public void setSectorIndex(long sectorIndex) {
		this.sectorIndex = sectorIndex;
	}
}
