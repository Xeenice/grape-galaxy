package org.grape.galaxy.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.grape.galaxy.utils.RandomGenerator;
import org.grape.galaxy.utils.VecUtils;

public class Sector implements Serializable {

	private static final long serialVersionUID = -6999250485208793549L;

	private Galaxy galaxy;

	private long index; // глобальный идентификатор, для БД; порядковый номер
						// сектора

	private List<Planet> planets;

	private List<Gate> gates;

	private SectorDetails sectorDetails;

	public Sector() {
	}

	public Sector(Galaxy galaxy, long index) {
		this.galaxy = galaxy;
		this.index = index;
		planets = new ArrayList<Planet>();
		double random = RandomGenerator.next(index,
				Constants.GENERATION_CYCLE_COUNT);
		int planetCount = (int) Math
				.round(random
						* (Constants.SECTOR_MAX_PLANET_COUNT - Constants.SECTOR_MIN_PLANET_COUNT)
						+ Constants.SECTOR_MIN_PLANET_COUNT);
		double blindZoneSize = (Constants.SECTOR_BLIND_ZONE_SIZE + Constants.PLANET_MAX_RADIUS);
		long blindZoneSizeInCells = (long) Math.ceil(blindZoneSize
				* Constants.SECTOR_LINEAR_SIZE_IN_CELLS
				/ Constants.SECTOR_LINEAR_SIZE);
		long minPlanetXInCells = ((index % Constants.GALAXY_LINEAR_SIZE_IN_SECTORS)
				* Constants.SECTOR_LINEAR_SIZE_IN_CELLS + blindZoneSizeInCells);
		long minPlanetYInCells = ((index / Constants.GALAXY_LINEAR_SIZE_IN_SECTORS)
				* Constants.SECTOR_LINEAR_SIZE_IN_CELLS + blindZoneSizeInCells);
		long sectorActiveSizeInCells = (Constants.SECTOR_LINEAR_SIZE_IN_CELLS - 2 * blindZoneSizeInCells);
		int tryCount = 0;
		while (planetCount > 0) {
			random = RandomGenerator.next(random,
					Constants.GENERATION_CYCLE_COUNT);
			long planetXInCells = (long) (random * sectorActiveSizeInCells + minPlanetXInCells);
			random = RandomGenerator.next(random,
					Constants.GENERATION_CYCLE_COUNT);
			long planetYInCells = (long) (random * sectorActiveSizeInCells + minPlanetYInCells);
			long planetIndex = (planetYInCells
					* Constants.GALAXY_LINEAR_SIZE_IN_CELLS + planetXInCells);
			Planet newPlanet = new Planet(this, planetIndex);
			boolean ok = true;
			for (Planet planet : planets) {
				double dx = (newPlanet.getX() - planet.getX());
				double dy = (newPlanet.getY() - planet.getY());
				if (VecUtils.len(dx, dy) < 4 * Constants.PLANET_MAX_RADIUS) {
					// слишком близко к другим планетам сектора
					ok = false;
					tryCount++;
					break;
				}
			}
			if (ok) {
				planets.add(newPlanet);
				tryCount = 0;
				planetCount--;
			} else if (tryCount > Constants.GENERATION_MAX_TRY_COUNT) {
				// слишком много попыток, генерируем следующую планету
				tryCount = 0;
				planetCount--;
			}
		}
		gates = new ArrayList<Gate>();
		addGate(-1, -1);
		addGate(-1, 0);
		addGate(-1, 1);
		addGate(0, 1);
		addGate(1, 1);
		addGate(1, 0);
		addGate(1, -1);
		addGate(0, -1);
	}

	public Galaxy getGalaxy() {
		return galaxy;
	}

	public long getIndex() {
		return index;
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public List<Gate> getGates() {
		return gates;
	}

	public Planet getPlanetByIndex(long planetIndex) {
		Planet result = null;
		for (Planet planet : planets) {
			if (planetIndex == planet.getIndex()) {
				result = planet;
				break;
			}
		}
		return result;
	}

	public List<Transportation> getTransportations() {
		List<Transportation> result;
		if (sectorDetails != null) {
			result = sectorDetails.getTransportations();
		} else {
			result = new ArrayList<Transportation>(0);
		}
		return result;
	}

	public SectorDetails getSectorDetails() {
		return sectorDetails;
	}

	public boolean isDetailsActual() {
		return ((sectorDetails != null) && (sectorDetails.isActual()));
	}

	public void bindDetails(SectorDetails sectorDetails) {
		if (this.sectorDetails == null) {
			this.sectorDetails = sectorDetails;
		} else {
			this.sectorDetails.merge(sectorDetails);
		}

		if (sectorDetails.getPlanetsDetails() != null) {
			for (PlanetDetails planetDetails : sectorDetails
					.getPlanetsDetails()) {
				Planet planet = getPlanetByIndex(planetDetails.getIndex());
				if (planet != null) {
					planet.bindDetails(planetDetails);
				}
			}
		}
		if (sectorDetails.getAddititonalPlanetsDetails() != null) {
			for (PlanetDetails planetDetails : sectorDetails
					.getAddititonalPlanetsDetails()) {
				Planet planet = Galaxy.get()
						.getPlanet(planetDetails.getIndex());
				if (planet != null) {
					planet.bindDetails(planetDetails);
				}
			}
		}
	}

	private void addGate(int dx, int dy) {
		long nx = index % Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + dx;
		long ny = index / Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + dy;
		if ((nx < (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 - Constants.GALAXY_SECTORS_RANGE))
				|| (nx > (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 + Constants.GALAXY_SECTORS_RANGE))
				|| (ny < (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 - Constants.GALAXY_SECTORS_RANGE))
				|| (ny > (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 + Constants.GALAXY_SECTORS_RANGE))) {
			return;
		}
		long index = ny * Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + nx;
		double x = 0.5 * (dx + 1); // [0..1]
		double y = 0.5 * (dy + 1); // [0..1]
		gates.add(new Gate(index, x, y));
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
		Sector other = (Sector) obj;
		if (index != other.index)
			return false;
		return true;
	}
}
