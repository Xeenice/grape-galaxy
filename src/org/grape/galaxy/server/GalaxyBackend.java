package org.grape.galaxy.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.GalaxyMapDetails;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.ChatMessageHelper;
import org.grape.galaxy.server.utils.GlobalTimeAndIndexUtils;
import org.grape.galaxy.server.utils.JDOUtils;

public class GalaxyBackend {

	private static Logger logger = Logger.getLogger(GalaxyBackend.class
			.getName());

	private static GalaxyBackend instance;

	private GalaxyBackend() {
	}

	public static GalaxyBackend get() {
		if (instance == null) {
			instance = new GalaxyBackend();
		}
		return instance;
	}

	public void compute(boolean sectorRelevanceEnabled,
			StatefulEventSystem<PlanetDetails> planetDetailsEventSystem) {
		GalaxyMapDetails galaxyMapDetails = CacheUtils.get(
				GalaxyMapDetails.class, "");
		if (galaxyMapDetails == null) {
			galaxyMapDetails = new GalaxyMapDetails();
		}
		Map<Long, String> planetId2OwnerIdMap = galaxyMapDetails
				.getPlanetId2OwnerIdMap();

		planetDetailsEventSystem.restoreState();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			for (Sector sector : Galaxy.get().getAvailableSectors()) {
				if (sectorRelevanceEnabled
						&& GalaxyServiceBackend
								.get()
								.incMissedCycleCountForSector(sector.getIndex())) {
					continue;
				}

				int missedCycleCount = GalaxyServiceBackend.get()
						.getMissedCycleCountForSector(sector.getIndex());
				GalaxyServiceBackend.get().clearMissedCycleCountForSector(
						sector.getIndex());

				preProcessPlanets(pm, sector, missedCycleCount,
						planetDetailsEventSystem);

				processPlanets(pm, sector, missedCycleCount,
						planetId2OwnerIdMap);
				processTransportations(sector, missedCycleCount,
						planetId2OwnerIdMap);

				postProcessPlanets(pm, sector, missedCycleCount,
						planetDetailsEventSystem);
			}
		} finally {
			pm.close();
		}

		planetDetailsEventSystem.storeState();

		galaxyMapDetails.setPlanetId2OwnerIdMap(planetId2OwnerIdMap);
		CacheUtils.put(GalaxyMapDetails.class, "", galaxyMapDetails);
	}

	private void preProcessPlanets(PersistenceManager pm, Sector sector,
			int missedCycleCount,
			StatefulEventSystem<PlanetDetails> planetDetailsEventSystem) {
		for (Planet planet : sector.getPlanets()) {
			PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, planet.getIndex(), true);
			if (planetDetails != null) {
				planetDetailsEventSystem.preProcess(planetDetails,
						missedCycleCount);
			}
		}
	}

	private void processPlanets(PersistenceManager pm, Sector sector,
			int missedCycleCount, Map<Long, String> planetId2OwnerIdMap) {
		for (Planet planet : sector.getPlanets()) {
			PlanetDetails planetDetails = processPlanet(pm, planet,
					missedCycleCount);
			if (planetDetails != null) {
				planetId2OwnerIdMap.put(planet.getIndex(),
						planetDetails.getOwnerId());
			} else {
				planetId2OwnerIdMap.remove(planet.getIndex());
			}
		}
	}

	private List<List<CacheUtils.Wrapper<Transportation>>> processTransportations(
			Sector sector, int missedCycleCount,
			Map<Long, String> planetId2OwnerIdMap) {
		Collection<CacheUtils.Wrapper<Transportation>> transportations = GalaxyServiceBackend
				.get().getSectorTransportations(sector.getIndex());

		List<List<CacheUtils.Wrapper<Transportation>>> highPriorityTransportations = new ArrayList<List<CacheUtils.Wrapper<Transportation>>>();
		for (int i = 0; i <= Transportation.MAX_PRIORITY; i++) {
			highPriorityTransportations
					.add(new ArrayList<CacheUtils.Wrapper<Transportation>>());
		}

		for (CacheUtils.Wrapper<Transportation> wrapper : transportations) {
			Transportation transportation = wrapper.get();
			if ((transportation != null) && transportation.isHighPriority()) {
				highPriorityTransportations.get(transportation.getPriority())
						.add(wrapper);
				continue;
			}

			List<PlanetDetails> captured = new ArrayList<PlanetDetails>(1);
			processTransportation(sector.getIndex(), wrapper, missedCycleCount,
					captured);
			for (PlanetDetails capturedPlanetDetails : captured) {
				planetId2OwnerIdMap.put(capturedPlanetDetails.getIndex(),
						capturedPlanetDetails.getOwnerId());
			}
		}

		for (List<CacheUtils.Wrapper<Transportation>> additionalTransportations : highPriorityTransportations) {
			for (CacheUtils.Wrapper<Transportation> wrapper : additionalTransportations) {
				List<PlanetDetails> captured = new ArrayList<PlanetDetails>(1);
				processTransportation(sector.getIndex(), wrapper,
						missedCycleCount, captured);
				for (PlanetDetails capturedPlanetDetails : captured) {
					planetId2OwnerIdMap.put(capturedPlanetDetails.getIndex(),
							capturedPlanetDetails.getOwnerId());
				}
			}
		}
		return highPriorityTransportations;
	}

	private void postProcessPlanets(PersistenceManager pm, Sector sector,
			int missedCycleCount,
			StatefulEventSystem<PlanetDetails> planetDetailsEventSystem) {
		for (Planet planet : sector.getPlanets()) {
			PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, planet.getIndex(), true);
			if (planetDetails != null) {
				planetDetailsEventSystem.postProcess(planetDetails,
						missedCycleCount);
			}
		}
	}

	private PlanetDetails processPlanet(PersistenceManager pm, Planet planet,
			int missedCycleCount) {
		PlanetDetails result = null;

		try {
			result = JDOUtils.getObjectById(pm, PlanetDetails.class,
					planet.getIndex(), true);
			if (result == null) {
				return result;
			}

			int cycleCount = (1 + missedCycleCount);

			double resourceCount = result.getResourceCount();
			int orbitUnitCount = result.getOrbitUnitCount();

			// Прибавить ресурс
			resourceCount = Math.min(result.getResourceCountLimit(),
					resourceCount + cycleCount
							* Constants.PLANET_RESOURCE_COUNT_GROW_VELOCITY);

			if (result.isDefenceEnabled()) {
				resourceCount = Math.max(0.0, resourceCount - cycleCount
						* Constants.PLANET_DEFENCE_PRICE);
			}

			if (result.isUnitProduction()) {
				// Пересчитать кол-во юнитов на орбите в
				// результате производства
				int orbitUnitCountDelta = cycleCount
						* Constants.PLANET_ORBIT_UNIT_COUNT_GROW_VELOCITY;
				double resourceDelta = Math.min(resourceCount,
						orbitUnitCountDelta * Constants.UNIT_PRICE);
				orbitUnitCountDelta = (int) (resourceDelta / Constants.UNIT_PRICE);
				if (orbitUnitCountDelta > 0) {
					int newOrbitUnitCount = Math.min(
							result.getOrbitUnitCountLimit(), orbitUnitCount
									+ orbitUnitCountDelta);
					orbitUnitCountDelta = (newOrbitUnitCount - orbitUnitCount);
					if (orbitUnitCountDelta > 0) {
						resourceCount -= orbitUnitCountDelta
								* Constants.UNIT_PRICE;
						orbitUnitCount = newOrbitUnitCount;
					}
				}
			}

			result.setResourceCount(resourceCount);
			result.setOrbitUnitCount(orbitUnitCount);

			CacheUtils.put(PlanetDetails.class, planet.getIndex(), result);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}

		return result;
	}

	private void processTransportation(long sectotIndex,
			CacheUtils.Wrapper<Transportation> wrapper, int missedCycleCount,
			List<PlanetDetails> captured) {
		Transportation transportation = wrapper.get();
		if ((transportation != null)
				&& (sectotIndex == transportation.getCurrentSectorIndex())
				&& transportation.isCompleted() && !transportation.isCanceled()
				&& transportation.isFleetTransportation()) {
			processTransportationTarget(transportation, captured);
		}
		if ((transportation == null) || transportation.isCompleted()
				|| transportation.isCanceled()) {
			wrapper.delete();
			return;
		}

		transportation.update(missedCycleCount);
		transportation.setLastUpdateTimeMillis(GlobalTimeAndIndexUtils
				.currentTimeMillis());

		if ((sectotIndex == transportation.getCurrentSectorIndex())
				&& transportation.isCompleted()) {
			PlanetDetails sourcePlanetDetails = null;
			PlanetDetails targetPlanetDetails = null;
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				sourcePlanetDetails = JDOUtils.getObjectById(pm,
						PlanetDetails.class,
						transportation.getSourceCellIndex(), true);
				targetPlanetDetails = JDOUtils.getObjectById(pm,
						PlanetDetails.class,
						transportation.getTargetCellIndex(), true);
				Object sourcePlanetDetailsForHistory;
				if (sourcePlanetDetails == null) {
					sourcePlanetDetailsForHistory = Constants.BOT_AGGRESSOR_SOURCE_PLANET_NAME;
				} else {
					sourcePlanetDetailsForHistory = sourcePlanetDetails;
				}
				if (transportation.isFleetTransportation()) {
					ChatServiceBackend.get().recordHistoryMessage(
							transportation.getOwnerName(),
							ChatMessageHelper.get().createMessageText(
									"Завершена переброска ",
									transportation.getUnitCount(),
									" ед. кораблей с планеты ",
									sourcePlanetDetailsForHistory,
									" на планету ", targetPlanetDetails, "."),
							targetPlanetDetails.getOwnerName());
				} else if (transportation.isResourceTransportation()) {
					ChatServiceBackend.get().recordHistoryMessage(
							transportation.getOwnerName(),
							ChatMessageHelper.get().createMessageText(
									"Завершена переброска ",
									transportation.getResourceCount(),
									" ед. ресурса с планеты ",
									sourcePlanetDetailsForHistory,
									" на планету ", targetPlanetDetails, "."),
							targetPlanetDetails.getOwnerName());
				} else {
					ChatServiceBackend.get().recordHistoryMessage(
							transportation.getOwnerName(),
							ChatMessageHelper.get().createMessageText(
									"Завершена переброска ",
									transportation.getUnitCount(),
									" ед. кораблей и ",
									transportation.getResourceCount(),
									" ед. ресурса с планеты ",
									sourcePlanetDetailsForHistory,
									" на планету ", targetPlanetDetails, "."),
							targetPlanetDetails.getOwnerName());
				}
			} catch (Exception ex) {
				logger.severe(ex.getMessage());
			} finally {
				pm.close();
			}

			if (transportation.isResourceTransportation() || (transportation.isFleetTransportation()
					&& (targetPlanetDetails != null)
					&& (((transportation.getOwnerId() == null) && (targetPlanetDetails
							.getOwnerId() == null)) || ((transportation
							.getOwnerId() != null) && transportation
							.getOwnerId().equals(
									targetPlanetDetails.getOwnerId()))))) {
				processTransportationTarget(transportation, captured);
				wrapper.delete();
				return;
			}
		}

		wrapper.update();
	}

	private void processTransportationTarget(Transportation transportation,
			List<PlanetDetails> captured) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			PlanetDetails targetPlanetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, transportation.getTargetCellIndex(),
					true);

			double resourceCount = targetPlanetDetails.getResourceCount();
			int orbitUnitCount = targetPlanetDetails.getOrbitUnitCount();

			if (((transportation.getOwnerId() == null) && (targetPlanetDetails
					.getOwnerId() == null))
					|| ((transportation.getOwnerId() != null) && transportation
							.getOwnerId().equals(
									targetPlanetDetails.getOwnerId()))) {
				resourceCount = Math.min(
						targetPlanetDetails.getResourceCountLimit(),
						resourceCount + transportation.getResourceCount());
				orbitUnitCount = Math.min(
						targetPlanetDetails.getOrbitUnitCountLimit(),
						orbitUnitCount + transportation.getUnitCount());
			} else {
				double attackersK;
				double defenceK = 1.0;
				if (targetPlanetDetails.isDefenceEnabled()) {
					defenceK = targetPlanetDetails.getDefenceK();
				}
				if (orbitUnitCount > 0) {
					attackersK = ((double) transportation.getUnitCount())
							/ (double) (orbitUnitCount * defenceK);
				} else {
					attackersK = 10.0;
				}
				if (attackersK < 1.0) {
					// Флот разбит, ресурсы теряются
					orbitUnitCount = (int) Math.ceil(orbitUnitCount
							* (1.0 - attackersK));

					ChatServiceBackend.get().recordHistoryMessage(
							transportation.getOwnerName(),
							ChatMessageHelper.get().createMessageText(
									"Разбит флот при попытке захвата планеты ",
									targetPlanetDetails, "."),
							targetPlanetDetails.getOwnerName());
				} else if (targetPlanetDetails.isHome()) {
					// Захватить "родную" планету нельзя,
					// однако, её можно опустошить
					orbitUnitCount = 0;
					double remainingAttackersFleetCost = (Math
							.floor(transportation.getUnitCount()
									* (1.0 - Math.min(1.0, 1.0 / attackersK))) * Constants.UNIT_PRICE);
					resourceCount = Math.max(0, resourceCount
							- remainingAttackersFleetCost
							* Constants.HOME_HAVOC_RESOURCE_COUNT_PENALTY_K);

					targetPlanetDetails.setDefenceEnabled(false);
					
					ChatServiceBackend.get().recordHistoryMessage(
							transportation.getOwnerName(),
							ChatMessageHelper.get().createMessageText(
									"Опустошена базовая планета ",
									targetPlanetDetails, "."),
							targetPlanetDetails.getOwnerName());
				} else {
					targetPlanetDetails.setOwnerId(transportation.getOwnerId());
					String oldOwnerName = targetPlanetDetails.getOwnerName();
					targetPlanetDetails.setOwnerName(transportation
							.getOwnerName());
					targetPlanetDetails
							.setCaptureTimeMillis(GlobalTimeAndIndexUtils
									.currentTimeMillis());
					resourceCount = Math.min(
							targetPlanetDetails.getResourceCountLimit(),
							resourceCount + transportation.getResourceCount());
					orbitUnitCount = Math.min(
							targetPlanetDetails.getOrbitUnitCountLimit(),
							(int) Math.floor(transportation.getUnitCount()
									* (1.0 - Math.min(1.0, 1.0 / attackersK))));

					targetPlanetDetails.setDefenceEnabled(false);
					
					captured.add(targetPlanetDetails);

					ChatServiceBackend.get().recordHistoryMessage(
							transportation.getOwnerName(),
							ChatMessageHelper.get().createMessageText(
									"Захвачена планета ", targetPlanetDetails,
									"."), oldOwnerName);
				}
			}

			targetPlanetDetails.setResourceCount(resourceCount);
			targetPlanetDetails.setOrbitUnitCount(orbitUnitCount);
			CacheUtils.put(PlanetDetails.class, targetPlanetDetails.getIndex(),
					targetPlanetDetails);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		} finally {
			pm.close();
		}
	}

	public void dumpPlanets(long sectorY) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			for (Sector sector : Galaxy.get().getAvailableSectors()) {
				long sectorIndex = sector.getIndex();
				if (sectorY == (sectorIndex / Constants.GALAXY_LINEAR_SIZE_IN_SECTORS)) {
					for (Planet planet : sector.getPlanets()) {
						dumpPlanet(pm, planet);
					}
				}
			}
		} finally {
			pm.close();
		}
	}

	private void dumpPlanet(PersistenceManager pm, Planet planet) {
		try {
			PlanetDetails cachedPlanetDetails = CacheUtils.get(
					PlanetDetails.class, planet.getIndex());
			if (cachedPlanetDetails == null) {
				return;
			}

			PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, planet.getIndex());
			if (planetDetails == null) {
				planetDetails = cachedPlanetDetails;
			} else {
				planetDetails.merge(cachedPlanetDetails);
			}
			pm.makePersistent(planetDetails);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}
	}
}
