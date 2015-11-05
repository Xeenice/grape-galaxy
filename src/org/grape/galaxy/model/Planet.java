package org.grape.galaxy.model;

import java.io.Serializable;

import org.grape.galaxy.client.UserContainer;
import org.grape.galaxy.utils.RandomGenerator;

public class Planet implements Serializable {

	private static final long serialVersionUID = -8226861190095875150L;

	private Sector sector;

	private long index; // глобальный идентификатор, для БД; порядковый номер
						// ячейки

	private double radius;

	private int surfaceType;
	private int atmosphereType;

	private double resourceCountLimit;
	private int orbitUnitCountLimit;
	private double defenceK;

	private PlanetDetails planetDetails;

	public Planet() {
	}

	public Planet(Sector sector, long index) {
		this.sector = sector;
		this.index = index;
		double random = RandomGenerator.next(index,
				Constants.GENERATION_CYCLE_COUNT);
		this.radius = (random
				* (Constants.PLANET_MAX_RADIUS - Constants.PLANET_MIN_RADIUS) + Constants.PLANET_MIN_RADIUS);
		random = RandomGenerator.next(random, Constants.GENERATION_CYCLE_COUNT);
		this.surfaceType = (int) Math.round(random
				* (Constants.PLANET_SURFACE_TYPE_COUNT - 1));
		random = RandomGenerator.next(random, Constants.GENERATION_CYCLE_COUNT);
		this.atmosphereType = (int) Math.round(random
				* Constants.PLANET_ATMOSPHERE_TYPE_COUNT);
		random = RandomGenerator.next(random, Constants.GENERATION_CYCLE_COUNT);
		this.resourceCountLimit = (random
				* (Constants.PLANET_MAX_RESOURCE_COUNT_LIMIT - Constants.PLANET_MIN_RESOURCE_COUNT_LIMIT) + Constants.PLANET_MIN_RESOURCE_COUNT_LIMIT);
		random = RandomGenerator.next(random, Constants.GENERATION_CYCLE_COUNT);
		this.orbitUnitCountLimit = (int) (Constants.PLANET_ORBIT_MIN_UNIT_COUNT_LIMIT
				+ (Constants.PLANET_ORBIT_MAX_UNIT_COUNT_LIMIT - Constants.PLANET_ORBIT_MIN_UNIT_COUNT_LIMIT)
				* (this.radius - Constants.PLANET_MIN_RADIUS)
				/ (Constants.PLANET_MAX_RADIUS - Constants.PLANET_MIN_RADIUS));
		this.defenceK = (Constants.PLANET_MAX_DEFENCE_K
				- (Constants.PLANET_MAX_DEFENCE_K - Constants.PLANET_MIN_DEFENCE_K)
				* (this.radius - Constants.PLANET_MIN_RADIUS)
				/ (Constants.PLANET_MAX_RADIUS - Constants.PLANET_MIN_RADIUS));
		random = RandomGenerator.next(random, Constants.GENERATION_CYCLE_COUNT);
	}

	public Sector getSector() {
		return sector;
	}

	public long getIndex() {
		return index;
	}

	public String getIndexAsString() {
		return "" + index;
	}

	public String getPlanetName() {
		return ((planetDetails != null) ? planetDetails.getPlanetName() : null);
	}

	public String getText() {
		String result = ("#" + index);
		if ((planetDetails != null) && (planetDetails.getPlanetName() != null)) {
			result = planetDetails.getPlanetName();
		}
		return result;
	}
	
	public boolean isHome() {
		boolean result = false;
		if (planetDetails != null) {
			result = planetDetails.isHome();
		}
		return result;
	}

	public String getOwnerId() {
		String result = null;
		if ((planetDetails != null) && (planetDetails.getOwnerId() != null)) {
			result = planetDetails.getOwnerId();
		}
		return result;
	}

	public String getOwnerName() {
		String result = "";
		if ((planetDetails != null) && (planetDetails.getOwnerName() != null)) {
			result = planetDetails.getOwnerName();
		}
		return result;
	}

	public long getCellX() {
		return (index % Constants.GALAXY_LINEAR_SIZE_IN_CELLS);
	}

	public long getCellY() {
		return (index / Constants.GALAXY_LINEAR_SIZE_IN_CELLS);
	}

	public double getX() {
		return (((double) (index % Constants.GALAXY_LINEAR_SIZE_IN_CELLS))
				* Constants.SECTOR_LINEAR_SIZE / Constants.SECTOR_LINEAR_SIZE_IN_CELLS);
	}

	public double getY() {
		return (((double) (index / Constants.GALAXY_LINEAR_SIZE_IN_CELLS))
				* Constants.SECTOR_LINEAR_SIZE / Constants.SECTOR_LINEAR_SIZE_IN_CELLS);
	}

	public double getRelativeX() {
		return (getX() % Constants.SECTOR_LINEAR_SIZE);
	}

	public double getRelativeY() {
		return (getY() % Constants.SECTOR_LINEAR_SIZE);
	}

	public double getRadius() {
		return radius;
	}

	public int getSurfaceType() {
		return surfaceType;
	}

	public int getAtmosphereType() {
		return atmosphereType;
	}

	public double getResourceCountLimit() {
		return resourceCountLimit;
	}

	public double getResourceCount() {
		double result = resourceCountLimit;
		if (planetDetails != null) {
			result = planetDetails.getResourceCount();
		}
		return result;
	}

	public int getOrbitUnitCountLimit() {
		return orbitUnitCountLimit;
	}

	public int getOrbitUnitCount() {
		int result = orbitUnitCountLimit;
		if (planetDetails != null) {
			result = planetDetails.getOrbitUnitCount();
		}
		return result;
	}

	public double getDefenceK() {
		return defenceK;
	}

	public boolean isUnitProduction() {
		boolean result = false;
		if (planetDetails != null) {
			result = planetDetails.isUnitProduction();
		}
		return result;
	}
	
	public boolean isDefenceEnabled() {
		boolean result = false;
		if (planetDetails != null) {
			result = planetDetails.isDefenceEnabled();
		}
		return result;
	}

	public PlanetDetails getPlanetDetails() {
		return planetDetails;
	}

	public void bindDetails(PlanetDetails planetDetails) {
		this.planetDetails = planetDetails;
		if (planetDetails.isHome()
				&& UserContainer.get().getUserId()
						.equals(planetDetails.getOwnerId())) {
			UserContainer.get().setHomePlanetDetails(planetDetails);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (index ^ (index >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Planet other = (Planet) obj;
		if (index != other.index)
			return false;
		return true;
	}
}
