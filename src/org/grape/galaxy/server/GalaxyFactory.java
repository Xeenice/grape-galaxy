package org.grape.galaxy.server;

import java.util.UUID;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.server.utils.GlobalTimeAndIndexUtils;

public class GalaxyFactory {

	private static GalaxyFactory inst;

	public synchronized static GalaxyFactory get() {
		if (inst == null) {
			inst = new GalaxyFactory();
		}
		return inst;
	}

	public PlanetDetails createBotPlanetDetails(long planetIndex) {
		PlanetDetails result = new PlanetDetails();
		result.setIndex(planetIndex);
		result.setOwnerId(null);
		result.setOwnerName(null);
		result.setHome(false);
		Planet planet = new Planet(null, planetIndex);
		result.setResourceCount(planet.getResourceCountLimit());
		result.setResourceCountLimit(planet.getResourceCountLimit());
		result.setOrbitUnitCount(planet.getOrbitUnitCountLimit());
		result.setOrbitUnitCountLimit(planet.getOrbitUnitCountLimit());
		result.setDefenceK(planet.getDefenceK());
		result.setCaptureTimeMillis(0L);
		return result;
	}

	public PlanetDetails createHomePlanetDetails(long planetIndex,
			String ownerId, String ownerName) {
		PlanetDetails result = new PlanetDetails();
		result.setIndex(planetIndex);
		result.setOwnerId(ownerId);
		result.setOwnerName(ownerName);
		result.setHome(true);
		Planet planet = new Planet(null, planetIndex);
		result.setResourceCount(planet.getResourceCountLimit()
				* Constants.HOME_CAPTURE_RESOURCE_COUNT_PENALTY);
		result.setResourceCountLimit(planet.getResourceCountLimit());
		result.setOrbitUnitCount(planet.getOrbitUnitCountLimit());
		result.setOrbitUnitCountLimit(planet.getOrbitUnitCountLimit());
		result.setDefenceK(planet.getDefenceK());
		result.setCaptureTimeMillis(GlobalTimeAndIndexUtils.currentTimeMillis());
		return result;
	}

	public PlanetDetails convertBotPlanetDetailsToHomePlanetDetails(
			PlanetDetails planetDetails, String ownerId, String ownerName) {
		PlanetDetails result = planetDetails;
		result.setOwnerId(ownerId);
		result.setOwnerName(ownerName);
		result.setHome(true);
		result.setResourceCount(result.getResourceCount()
				* Constants.HOME_CAPTURE_RESOURCE_COUNT_PENALTY);
		result.setCaptureTimeMillis(GlobalTimeAndIndexUtils
				.currentTimeMillis());
		return result;
	}

	public PlanetDetails createTestPlanetDetails(long planetIndex,
			String testOwner) {
		PlanetDetails result = new PlanetDetails();
		result.setIndex(planetIndex);
		result.setOwnerId(testOwner);
		result.setOwnerName(testOwner);
		result.setHome(false);
		Planet planet = new Planet(null, planetIndex);
		result.setResourceCount(planet.getResourceCountLimit());
		result.setResourceCountLimit(planet.getResourceCountLimit());
		result.setOrbitUnitCount(planet.getOrbitUnitCountLimit());
		result.setOrbitUnitCountLimit(planet.getOrbitUnitCountLimit());
		result.setDefenceK(planet.getDefenceK());
		result.setCaptureTimeMillis(0L);
		return result;
	}
	
	public Transportation createResourceTransportation(PlanetDetails sourcePlanetDetails,
			long targetCellIndex, double resourceCount) {
		Transportation transportation = createEmptyTransportation(
				sourcePlanetDetails, targetCellIndex);
		transportation
				.setVelocityInCells(Constants.RESOURCE_TRANSPORTATION_VELOCITY_IN_CELLS);
		transportation.setResourceCount(resourceCount);
		return transportation;
	}

	public Transportation createResourceTransportation(long sourceCellIndex,
			long targetCellIndex, String ownerId, String ownerName,
			double resourceCount) {
		Transportation transportation = createEmptyTransportation(
				sourceCellIndex, targetCellIndex, ownerId, ownerName);
		transportation
				.setVelocityInCells(Constants.RESOURCE_TRANSPORTATION_VELOCITY_IN_CELLS);
		transportation.setResourceCount(resourceCount);
		return transportation;
	}
	
	public Transportation createFleetTransportation(PlanetDetails sourcePlanetDetails,
			long targetCellIndex, int unitCount) {
		Transportation transportation = createEmptyTransportation(
				sourcePlanetDetails, targetCellIndex);
		transportation
				.setVelocityInCells(Constants.FLEET_TRANSPORTATION_VELOCITY_IN_CELLS);
		transportation.setUnitCount(unitCount);
		return transportation;
	}

	public Transportation createFleetTransportation(long sourceCellIndex,
			long targetCellIndex, String ownerId, String ownerName,
			int unitCount) {
		Transportation transportation = createEmptyTransportation(
				sourceCellIndex, targetCellIndex, ownerId, ownerName);
		transportation
				.setVelocityInCells(Constants.FLEET_TRANSPORTATION_VELOCITY_IN_CELLS);
		transportation.setUnitCount(unitCount);
		return transportation;
	}

	public Transportation createEmptyTransportation(
			PlanetDetails sourcePlanetDetails, long targetCellIndex) {
		return createEmptyTransportation(sourcePlanetDetails.getIndex(),
				targetCellIndex, sourcePlanetDetails.getOwnerId(),
				sourcePlanetDetails.getOwnerName());
	}

	public Transportation createEmptyTransportation(long sourceCellIndex,
			long targetCellIndex, String ownerId, String ownerName) {
		Transportation transportation = new Transportation();
		transportation.setId(UUID.randomUUID().getLeastSignificantBits());
		transportation.setOwnerId(ownerId);
		transportation.setOwnerName(ownerName);

		long sourceCellX = sourceCellIndex
				% Constants.GALAXY_LINEAR_SIZE_IN_CELLS;
		long sourceCellY = sourceCellIndex
				/ Constants.GALAXY_LINEAR_SIZE_IN_CELLS;
		long targetCellX = targetCellIndex
				% Constants.GALAXY_LINEAR_SIZE_IN_CELLS;
		long targetCellY = targetCellIndex
				/ Constants.GALAXY_LINEAR_SIZE_IN_CELLS;

		transportation.setSourceCellX(sourceCellX);
		transportation.setSourceCellY(sourceCellY);
		transportation.setTargetCellX(targetCellX);
		transportation.setTargetCellY(targetCellY);
		transportation.setCurrentCellX(sourceCellX);
		transportation.setCurrentCellY(sourceCellY);

		transportation.setCompleted(false);
		transportation.setCanceled(false);
		return transportation;
	}
}
