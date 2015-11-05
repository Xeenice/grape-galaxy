package org.grape.galaxy.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.grape.galaxy.client.ActivityException;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.server.utils.ChatMessageHelper;
import org.grape.galaxy.server.utils.EventSystemUtils;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class AntimonopolyBotEventSystem implements
		StatefulEventSystem<PlanetDetails> {

	private static Logger logger = Logger
			.getLogger(AntimonopolyBotEventSystem.class.getName());

	private static final String MC_KEY_PLANET_IDS_UNDER_MASSIVE_ATTACK = "AntimonopolyBotEventSystem#planetIdsUnderMassiveAttack";

	private int[] planetCountPoints = new int[] { 2, 12, 15, 20 };
	private double[] massiveAttackProbabilityPoints = new double[] {
			0.000013888888889, 0.000138888888889, 0.001388888888889,
			0.006944444444444 };
	private int massiveAttackUnitCountBonus = 100;

	private static final MemcacheService memcacheService = MemcacheServiceFactory
			.getMemcacheService();

	private Map<String, Set<Long>> userId2PlanetIdsMap = new HashMap<String, Set<Long>>();
	private Map<Long, String> planetId2userIdMap = new HashMap<Long, String>();

	private Set<Long> planetIdsUnderMassiveAttack = new HashSet<Long>();

	@Override
	public void init(Map<String, String> params) {
		String planetCountPointsStr = params
				.get("AntimonopolyBotEventSystem.planetCountPoints");
		if (planetCountPointsStr != null) {
			String[] parts = planetCountPointsStr.split(",");
			planetCountPoints = new int[parts.length];
			for (int i = 0; i < parts.length; i++) {
				planetCountPoints[i] = new Integer(parts[i]);
			}
		}
		String massiveAttackProbabilityPointsStr = params
				.get("AntimonopolyBotEventSystem.massiveAttackProbabilityPoints");
		if (massiveAttackProbabilityPointsStr != null) {
			String[] parts = massiveAttackProbabilityPointsStr.split(",");
			massiveAttackProbabilityPoints = new double[parts.length];
			for (int i = 0; i < parts.length; i++) {
				massiveAttackProbabilityPoints[i] = new Double(parts[i]);
			}
		}
		String massiveAttackUnitCountBonusStr = params
				.get("AntimonopolyBotEventSystem.massiveAttackUnitCountBonus");
		if (massiveAttackUnitCountBonusStr != null) {
			massiveAttackUnitCountBonus = new Integer(
					massiveAttackUnitCountBonusStr);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void restoreState() {
		try {
			planetIdsUnderMassiveAttack = (HashSet<Long>) memcacheService
					.get(MC_KEY_PLANET_IDS_UNDER_MASSIVE_ATTACK);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}
		if (planetIdsUnderMassiveAttack == null) {
			planetIdsUnderMassiveAttack = new HashSet<Long>();
		}
	}

	@Override
	public void storeState() {
		try {
			memcacheService.put(MC_KEY_PLANET_IDS_UNDER_MASSIVE_ATTACK,
					planetIdsUnderMassiveAttack);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}
	}

	@Override
	public void preProcess(PlanetDetails target, int missedCycleCount) {
	}

	@Override
	public void postProcess(PlanetDetails target, int missedCycleCount) {
		updateMap(target);
		if (target.getOwnerId() != null) {
			int planetCount = getUserPlanetCount(target.getOwnerId());
			double massiveAttackProbability = 0;
			for (int i = 0; i < planetCountPoints.length; i++) {
				if (planetCount <= planetCountPoints[i]) {
					if (i == 0) {
						massiveAttackProbability = massiveAttackProbabilityPoints[0];
					} else {
						massiveAttackProbability = (massiveAttackProbabilityPoints[i - 1]
								+ (massiveAttackProbabilityPoints[i] - massiveAttackProbabilityPoints[i - 1])
								* (planetCount - planetCountPoints[i - 1])
								/ (planetCountPoints[i] - planetCountPoints[i - 1]));
					}
					break;
				} else if (i == (planetCountPoints.length - 1)) {
					massiveAttackProbability = massiveAttackProbabilityPoints[planetCountPoints.length - 1];
				}
			}
			if (massiveAttackProbability > 0) {
				int evCount = EventSystemUtils.getEventCount(
						massiveAttackProbability, missedCycleCount);
				if (evCount > 0) {
					beginMassiveAttackOnTarget(target);
				}
			}
		}
	}

	private void beginMassiveAttackOnTarget(PlanetDetails target) {
		if (!target.isHome()
				&& !planetIdsUnderMassiveAttack.contains(target.getIndex())) {
			long sectorIndex = Galaxy.getSectorIndexForCellIndex(target
					.getIndex());
			Transportation transportation = GalaxyFactory.get()
					.createFleetTransportation(
							Galaxy.getSectorBoundRandomCellIndex(sectorIndex),
							target.getIndex(),
							null,
							Constants.BOT_AGGRESSOR_NAME,
							(int) (target.getOrbitUnitCountLimit() * target
									.getDefenceK())
									+ massiveAttackUnitCountBonus);
			transportation.setPriority(Transportation.MAX_PRIORITY);
			try {
				GalaxyServiceBackend.get().registerTransportation(
						transportation);
				planetIdsUnderMassiveAttack.add(target.getIndex());

				ChatServiceBackend.get().recordHistoryMessage(
						Constants.BOT_AGGRESSOR_NAME,
						ChatMessageHelper.get().createMessageText(
								"Началась переброска ",
								transportation.getUnitCount(),
								" ед. кораблей на планету ", target, "."),
						target.getOwnerName());
			} catch (ActivityException ex) {
				logger.severe(ex.getMessage());
			}
		}
	}

	private int getUserPlanetCount(String userId) {
		int result = 0;
		Set<Long> planetIds = userId2PlanetIdsMap.get(userId);
		for (Long planetId : planetIds) {
			if (!planetIdsUnderMassiveAttack.contains(planetId)) {
				result++;
			}
		}
		return result;
	}

	private void updateMap(PlanetDetails target) {
		String lastOwnerId = planetId2userIdMap.get(target.getIndex());
		if ((lastOwnerId != null) && !lastOwnerId.equals(target.getOwnerId())) {
			planetId2userIdMap.remove(target.getIndex());
			Set<Long> planetIds = userId2PlanetIdsMap.get(lastOwnerId);
			if (planetIds != null) {
				planetIds.remove(target.getIndex());
			}
		}

		if (target.getOwnerId() == null) {
			planetIdsUnderMassiveAttack.remove(target.getIndex());
		} else if ((lastOwnerId == null)
				|| !lastOwnerId.equals(target.getOwnerId())) {
			planetId2userIdMap.put(target.getIndex(), target.getOwnerId());
			Set<Long> planetIds = userId2PlanetIdsMap.get(target.getOwnerId());
			if (planetIds == null) {
				planetIds = new LinkedHashSet<Long>();
				userId2PlanetIdsMap.put(target.getOwnerId(), planetIds);
			}
			planetIds.add(target.getIndex());
		}
	}
}
