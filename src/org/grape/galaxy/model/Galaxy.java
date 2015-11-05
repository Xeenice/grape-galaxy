package org.grape.galaxy.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Galaxy implements Serializable {

	private static final long serialVersionUID = 8065845802056645198L;

	private static Galaxy inst;

	public static synchronized Galaxy get() {
		if (inst == null) {
			inst = new Galaxy();
		}
		return inst;
	}

	public static long getSectorIndexForCellIndex(long cellIndex) {
		long cellX = (cellIndex % Constants.GALAXY_LINEAR_SIZE_IN_CELLS);
		long cellY = (cellIndex / Constants.GALAXY_LINEAR_SIZE_IN_CELLS);
		return getSectorIndexForCell(cellX, cellY);
	}

	public static long getSectorIndexForCell(long cellX, long cellY) {
		return ((long) (cellY / Constants.SECTOR_LINEAR_SIZE_IN_CELLS)
				* Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + (long) (cellX / Constants.SECTOR_LINEAR_SIZE_IN_CELLS));
	}

	public static long getSectorRandomCellIndex(long sectorIndex) {
		long minXInCells = ((sectorIndex % Constants.GALAXY_LINEAR_SIZE_IN_SECTORS) * Constants.SECTOR_LINEAR_SIZE_IN_CELLS);
		long minYInCells = ((sectorIndex / Constants.GALAXY_LINEAR_SIZE_IN_SECTORS) * Constants.SECTOR_LINEAR_SIZE_IN_CELLS);
		long deltaX = (long) ((Constants.SECTOR_LINEAR_SIZE_IN_CELLS - 1) * Math
				.random());
		long deltaY = (long) ((Constants.SECTOR_LINEAR_SIZE_IN_CELLS - 1) * Math
				.random());
		return ((minYInCells + deltaY) * Constants.GALAXY_LINEAR_SIZE_IN_CELLS + (minXInCells + deltaX));
	}

	public static long getSectorBoundRandomCellIndex(long sectorIndex) {
		double deltaX;
		double deltaY;
		double q = Math.random();
		if (q < 0.25) {
			deltaX = Math.random();
			deltaY = 0;
		} else if (q < 0.5) {
			deltaX = Math.random();
			deltaY = 1.0;
		} else if (q < 0.75) {
			deltaX = 0;
			deltaY = Math.random();
		} else {
			deltaX = 1.0;
			deltaY = Math.random();
		}
		long xInCells = ((sectorIndex % Constants.GALAXY_LINEAR_SIZE_IN_SECTORS) * Constants.SECTOR_LINEAR_SIZE_IN_CELLS)
				+ (long) ((Constants.SECTOR_LINEAR_SIZE_IN_CELLS - 1) * deltaX);
		long yInCells = ((sectorIndex / Constants.GALAXY_LINEAR_SIZE_IN_SECTORS) * Constants.SECTOR_LINEAR_SIZE_IN_CELLS)
				+ (long) ((Constants.SECTOR_LINEAR_SIZE_IN_CELLS - 1) * deltaY);
		return yInCells * Constants.GALAXY_LINEAR_SIZE_IN_CELLS + xInCells;
	}

	private Map<Long, Sector> sectorsMap = new HashMap<Long, Sector>();
	private List<Sector> availableSectors = new LinkedList<Sector>();

	private GalaxyMap map;

	private Galaxy() {
		// Загрузить все сектора в память
		long startE = (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 - Constants.GALAXY_SECTORS_RANGE);
		long endE = (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 + Constants.GALAXY_SECTORS_RANGE);
		for (long sectorX = startE; sectorX <= endE; sectorX++) {
			for (long sectorY = startE; sectorY <= endE; sectorY++) {
				long sectorIndex = (sectorY
						* Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + sectorX);
				getSector(sectorIndex);
			}
		}
		long startSectorIndex = ((Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 - Constants.GALAXY_SECTORS_RANGE)
				* Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + (Constants.GALAXY_LINEAR_SIZE_IN_SECTORS / 2 - Constants.GALAXY_SECTORS_RANGE));
		map = new GalaxyMap(startSectorIndex,
				(int) (2 * Constants.GALAXY_SECTORS_RANGE + 1));
	}

	public synchronized List<Sector> getAvailableSectors() {
		return availableSectors;
	}

	public synchronized Sector getSector(long sectorIndex) {
		Sector result = sectorsMap.get(sectorIndex);
		if (result == null) {
			result = new Sector(this, sectorIndex);
			sectorsMap.put(sectorIndex, result);
			availableSectors.add(result);
		}
		return result;
	}

	public Planet getPlanet(long planetIndex) {
		Sector sector = getSector(getSectorIndexForCellIndex(planetIndex));
		return sector.getPlanetByIndex(planetIndex);
	}

	public GalaxyMap getMap() {
		return map;
	}
}
