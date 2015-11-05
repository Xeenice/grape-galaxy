package org.grape.galaxy.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import org.grape.galaxy.client.ActivityException;
import org.grape.galaxy.client.HomePlanetRegistrationException;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.GalaxyMapDetails;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.JDOUtils;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class GalaxyServiceBackend {

	private static Logger logger = Logger.getLogger(GalaxyServiceBackend.class
			.getName());

	public static final String MC_KEY_TRANSPORTATION = "Transportation#";
	public static final int MC_TRANSPORTATION_PER_SECTOR_LIMIT = 100;
	public static final int MC_TRANSPORTATION_EXPIRATION_SECS = 2
			* Constants.ACTIVITY_PERIOD_SECONDS
			* (int) (Constants.SECTOR_LINEAR_SIZE_IN_CELLS / Constants.FLEET_TRANSPORTATION_VELOCITY_IN_CELLS);

	public static final String MC_KEY_SECTOR_RELEVANCE_PREFIX = "GalaxyController#relevance#";
	public static final String MC_KEY_SECTOR_MISSED_CYCLE_COUNT = "GalaxyController#missed#";

	public static final int SECTOR_RELEVANCE_EXPIRATION_MILLIS = 60 * 60 * 1000;

	private static final MemcacheService memcacheService = MemcacheServiceFactory
			.getMemcacheService();

	private static GalaxyServiceBackend inst;

	public synchronized static GalaxyServiceBackend get() {
		if (inst == null) {
			inst = new GalaxyServiceBackend();
		}
		return inst;
	}

	private GalaxyServiceBackend() {
	}

	public GalaxyMapDetails getGalaxyMapDetails() {
		GalaxyMapDetails result = CacheUtils.get(GalaxyMapDetails.class, "");
		if (result == null) {
			result = new GalaxyMapDetails();
		}
		return result;
	}

	public PlanetDetails createAndDumpBotPlanetDetails(
			final PersistenceManager pm, final long planetIndex) {
		PlanetDetails result = null;

		try {
			result = JDOUtils.runInTransaction(pm,
					new Callable<PlanetDetails>() {
						@Override
						public PlanetDetails call() throws Exception {
							PlanetDetails result = JDOUtils.getObjectById(pm,
									PlanetDetails.class, planetIndex, true);
							if (result == null) {
								result = GalaxyFactory.get()
										.createBotPlanetDetails(planetIndex);
								CacheUtils.put(PlanetDetails.class,
										planetIndex, result);
								pm.makePersistent(result);
							}
							return result;
						}
					});
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}

		return result;
	}

	public PlanetDetails createAndDumpHomePlanetDetails(
			final PersistenceManager pm, final long planetIndex,
			final String ownerId, final String ownerName)
			throws HomePlanetRegistrationException {
		PlanetDetails result = null;

		try {
			result = JDOUtils.runInTransaction(pm,
					new Callable<PlanetDetails>() {
						@Override
						public PlanetDetails call() throws Exception {
							PlanetDetails cachedPlanetDetails = CacheUtils.get(
									PlanetDetails.class, planetIndex);
							PlanetDetails result = JDOUtils.getObjectById(pm,
									PlanetDetails.class, planetIndex);
							if (result == null) {
								if (cachedPlanetDetails != null) {
									result = GalaxyFactory
											.get()
											.convertBotPlanetDetailsToHomePlanetDetails(
													cachedPlanetDetails,
													ownerId, ownerName);
								} else {
									result = GalaxyFactory.get()
											.createHomePlanetDetails(
													planetIndex, ownerId,
													ownerName);
								}
								CacheUtils.put(PlanetDetails.class,
										planetIndex, result);
								pm.makePersistent(result);
							} else if (result.getOwnerId() == null) {
								if (cachedPlanetDetails != null) {
									result.merge(GalaxyFactory
											.get()
											.convertBotPlanetDetailsToHomePlanetDetails(
													cachedPlanetDetails,
													ownerId, ownerName));
								} else {
									result.merge(GalaxyFactory
											.get()
											.convertBotPlanetDetailsToHomePlanetDetails(
													result, ownerId, ownerName));
								}
								CacheUtils.put(PlanetDetails.class,
										planetIndex, result);
							} else {
								throw new HomePlanetRegistrationException();
							}
							return result;
						}
					});
		} catch (HomePlanetRegistrationException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}

		return result;
	}

	public PlanetDetails createAndCacheTestPlanetDetails(long planetIndex,
			String testOwner, String planetName) {
		PlanetDetails result = GalaxyFactory.get().createTestPlanetDetails(
				planetIndex, testOwner);
		result.setPlanetName(planetName);
		CacheUtils.put(PlanetDetails.class, planetIndex, result);
		return result;
	}

	public Collection<CacheUtils.Wrapper<Transportation>> getSectorTransportations(
			long sectorIndex) {
		return new CacheUtils.FixedCollection<Transportation>(
				MC_KEY_TRANSPORTATION + sectorIndex,
				MC_TRANSPORTATION_PER_SECTOR_LIMIT,
				Expiration.byDeltaSeconds(MC_TRANSPORTATION_EXPIRATION_SECS));
	}

	public void registerTransportation(Transportation transportation)
			throws ActivityException {
		Transportation clonedTrans = new Transportation(transportation);
		Set<Long> sectorIndieces = new LinkedHashSet<Long>();
		sectorIndieces.add(clonedTrans.getCurrentSectorIndex());
		while (!clonedTrans.isCompleted()) {
			clonedTrans.update(0);
			sectorIndieces.add(clonedTrans.getCurrentSectorIndex());
		}

		List<CacheUtils.Wrapper<Transportation>> clonedTranses = new ArrayList<CacheUtils.Wrapper<Transportation>>();
		for (Long sectorIndex : sectorIndieces) {
			Collection<CacheUtils.Wrapper<Transportation>> wrappers = getSectorTransportations(sectorIndex);
			CacheUtils.Wrapper<Transportation> transWrapper = CacheUtils
					.createWrapper(new Transportation(transportation));
			if (!wrappers.add(transWrapper)) {
				for (CacheUtils.Wrapper<Transportation> wrapper : clonedTranses) {
					wrapper.delete();
				}
				throw new ActivityException();
			}
			clonedTranses.add(transWrapper);
		}
	}

	public void enableRelevanceForSector(long sectorIndex) {
		memcacheService.put(MC_KEY_SECTOR_RELEVANCE_PREFIX + sectorIndex, true,
				Expiration.byDeltaMillis(SECTOR_RELEVANCE_EXPIRATION_MILLIS),
				MemcacheService.SetPolicy.SET_ALWAYS);
	}

	public boolean incMissedCycleCountForSector(long sectorIndex) {
		Boolean relevance = (Boolean) memcacheService
				.get(MC_KEY_SECTOR_RELEVANCE_PREFIX + sectorIndex);
		boolean result = ((relevance == null) || !relevance);
		if (result) {
			memcacheService.increment(MC_KEY_SECTOR_MISSED_CYCLE_COUNT
					+ sectorIndex, 1L, 0L);
		}
		return result;
	}

	public int getMissedCycleCountForSector(long sectorIndex) {
		Number missedCycleCountNum = (Number) memcacheService
				.get(MC_KEY_SECTOR_MISSED_CYCLE_COUNT + sectorIndex);
		int missedCycleCount = 0;
		if (missedCycleCountNum != null) {
			missedCycleCount = ((Number) missedCycleCountNum).intValue();
		}
		return missedCycleCount;
	}

	public void clearMissedCycleCountForSector(long sectorIndex) {
		memcacheService.delete(MC_KEY_SECTOR_MISSED_CYCLE_COUNT + sectorIndex);
	}
}
