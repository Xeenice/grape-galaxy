package org.grape.galaxy.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.grape.galaxy.client.ActivityException;
import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.HomePlanetRegistrationException;
import org.grape.galaxy.client.PlanetRenameException;
import org.grape.galaxy.client.service.GalaxyService;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.EULA;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.GalaxyMapDetails;
import org.grape.galaxy.model.LastAccessInfo;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.SectorDetails;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.model.TransportationDetails;
import org.grape.galaxy.model.UserPrefs;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.ChatMessageHelper;
import org.grape.galaxy.server.utils.GlobalTimeAndIndexUtils;
import org.grape.galaxy.server.utils.JDOUtils;

import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GalaxyServiceImpl extends RemoteServiceServlet implements
		GalaxyService {

	private static final long serialVersionUID = -1264417930668878271L;

	private static Logger logger = Logger.getLogger(GalaxyServiceImpl.class
			.getName());

	@Override
	public org.grape.galaxy.model.User getUser() throws AuthException {
		User user = JDOUtils.getUser();

		org.grape.galaxy.model.User result = new org.grape.galaxy.model.User();
		result.setUserId(user.getUserId());
		result.setNickname(user.getNickname());

		UserPrefs userPrefs = CacheUtils.get(UserPrefs.class, user.getUserId());
		if (userPrefs != null) {
			result.setUserPrefs(userPrefs);
		} else {
			userPrefs = new UserPrefs();
			CacheUtils.put(UserPrefs.class, user.getUserId(), userPrefs);
			result.setUserPrefs(userPrefs);
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result.setHomePlanetDetails(getHomePlanetDetails(user, pm));
		} finally {
			pm.close();
		}

		pm = PMF.get().getPersistenceManager();
		try {
			LastAccessInfo lastAccessInfo = JDOUtils.getObjectById(pm,
					LastAccessInfo.class, user.getUserId());
			if (lastAccessInfo == null) {
				lastAccessInfo = new LastAccessInfo();
				lastAccessInfo.setUserId(user.getUserId());
				lastAccessInfo.setEmail(user.getEmail());
				lastAccessInfo.setLastAccessTime(GlobalTimeAndIndexUtils
						.today());
				pm.makePersistent(lastAccessInfo);
			} else {
				lastAccessInfo.setKicked(false);
				lastAccessInfo.setLastAccessTime(GlobalTimeAndIndexUtils
						.today());
			}
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		} finally {
			pm.close();
		}

		return result;
	}

	@Override
	public void updateUserPrefs(UserPrefs userPrefs) throws AuthException {
		User user = JDOUtils.getUser();
		CacheUtils.put(UserPrefs.class, user.getUserId(), userPrefs);
	}

	@Override
	public SectorDetails getSectorDetails(long sectorIndex)
			throws AuthException {
		JDOUtils.getUser();

		GalaxyServiceBackend.get().enableRelevanceForSector(sectorIndex);

		SectorDetails result = new SectorDetails();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Sector sector = Galaxy.get().getSector(sectorIndex);

			int missedCycleCount = GalaxyServiceBackend.get()
					.getMissedCycleCountForSector(sector.getIndex());
			result.setActual(missedCycleCount <= 0);

			List<PlanetDetails> planetsDetails = new ArrayList<PlanetDetails>();
			for (Planet planet : sector.getPlanets()) {
				try {
					PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
							PlanetDetails.class, planet.getIndex(), true);
					if (planetDetails != null) {
						planetsDetails.add(planetDetails);
					}
				} catch (Exception ex) {
					logger.severe(ex.getMessage());
				}
			}
			result.setPlanetsDetails(planetsDetails);

			Collection<CacheUtils.Wrapper<Transportation>> wrappers = GalaxyServiceBackend
					.get().getSectorTransportations(sectorIndex);
			List<Transportation> transportations = new ArrayList<Transportation>();
			List<PlanetDetails> addititonalPlanetsDetails = new ArrayList<PlanetDetails>();
			for (CacheUtils.Wrapper<Transportation> wrapper : wrappers) {
				Transportation transportation = wrapper.get();
				if (sectorIndex == transportation.getCurrentSectorIndex()) {
					transportations.add(transportation);
				}
				if (sectorIndex != transportation.getSourceSectorIndex()) {
					try {
						PlanetDetails planetDetails = JDOUtils.getObjectById(
								pm, PlanetDetails.class,
								transportation.getSourceCellIndex(), true);
						if (planetDetails != null) {
							addititonalPlanetsDetails.add(planetDetails);
						}
					} catch (Exception ex) {
						logger.severe(ex.getMessage());
					}
				}
				if (sectorIndex != transportation.getTargetSectorIndex()) {
					try {
						PlanetDetails planetDetails = JDOUtils.getObjectById(
								pm, PlanetDetails.class,
								transportation.getTargetCellIndex(), true);
						if (planetDetails != null) {
							addititonalPlanetsDetails.add(planetDetails);
						}
					} catch (Exception ex) {
						logger.severe(ex.getMessage());
					}
				}
			}
			result.setTransportations(transportations);
			result.setAddititonalPlanetsDetails(addititonalPlanetsDetails);
		} finally {
			pm.close();
		}

		return result;
	}

	@Override
	public PlanetDetails registerHomePlanet(final long planetIndex)
			throws AuthException, HomePlanetRegistrationException {
		final User user = JDOUtils.getUser();

		PlanetDetails result = null;

		final PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result = getHomePlanetDetails(user, pm);
			if (result != null) {
				throw new HomePlanetRegistrationException();
			}

			result = GalaxyServiceBackend.get().createAndDumpHomePlanetDetails(
					pm, planetIndex, user.getUserId(), user.getNickname());
		} finally {
			pm.close();
		}

		if (result != null) {
			ChatServiceBackend.get().recordHistoryMessage(
					user,
					ChatMessageHelper.get().createMessageText(
							"База развернута на планете ", result, "."));
		}

		return result;
	}

	private PlanetDetails getHomePlanetDetails(User user, PersistenceManager pm)
			throws AuthException {
		PlanetDetails result = null;

		Query query = pm.newQuery(PlanetDetails.class);
		query.setUnique(true);
		query.setFilter("home == true && ownerId == userId");
		query.declareParameters("String userId");
		try {
			result = (PlanetDetails) query.execute(user.getUserId());
			if (result != null) {
				PlanetDetails cached = CacheUtils.get(PlanetDetails.class,
						result.getIndex());
				if (cached != null) {
					result = cached;
				} else {
					CacheUtils.put(PlanetDetails.class, result.getIndex(),
							result);
				}
			}
		} catch (JDOException ex) {
			logger.warning(ex.getMessage());
		}

		return result;
	}

	@Override
	public PlanetDetails renamePlanet(long planetIndex, String planetName)
			throws AuthException, PlanetRenameException {
		User user = JDOUtils.getUser();

		PlanetDetails result = null;
		String oldPlanetName = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result = JDOUtils.getObjectById(pm, PlanetDetails.class,
					planetIndex, true);
			if ((result != null)
					&& (user.getUserId().equals(result.getOwnerId()))) {
				oldPlanetName = result.getPlanetName();
				result.setPlanetName(planetName);
				result.setLastRenameTimeMillis(GlobalTimeAndIndexUtils
						.currentTimeMillis());
				CacheUtils.put(PlanetDetails.class, planetIndex, result);
			} else {
				throw new PlanetRenameException();
			}
		} finally {
			pm.close();
		}

		if (result != null) {
			if ((oldPlanetName != null) && (oldPlanetName.length() > 0)) {
				ChatServiceBackend.get().recordHistoryMessage(
						user,
						ChatMessageHelper.get().createMessageText(
								"Планета переименована с \"", oldPlanetName,
								"\" на \"", result, "\"."));
			} else {
				ChatServiceBackend.get().recordHistoryMessage(
						user,
						ChatMessageHelper.get().createMessageText(
								"Планете дано имя \"", result, "\"."));
			}
		}

		return result;
	}

	@Override
	public PlanetDetails startUnitProduction(long planetIndex)
			throws AuthException, ActivityException {
		User user = JDOUtils.getUser();

		PlanetDetails result = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result = JDOUtils.getObjectById(pm, PlanetDetails.class,
					planetIndex, true);
			if ((result != null)
					&& (user.getUserId().equals(result.getOwnerId()))) {
				result.setUnitProduction(true);
				CacheUtils.put(PlanetDetails.class, planetIndex, result);
			} else {
				throw new ActivityException();
			}
		} finally {
			pm.close();
		}

		return result;
	}

	@Override
	public PlanetDetails stopUnitProduction(long planetIndex)
			throws AuthException, ActivityException {
		User user = JDOUtils.getUser();

		PlanetDetails result = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result = JDOUtils.getObjectById(pm, PlanetDetails.class,
					planetIndex, true);
			if ((result != null)
					&& (user.getUserId().equals(result.getOwnerId()))) {
				result.setUnitProduction(false);
				CacheUtils.put(PlanetDetails.class, planetIndex, result);
			} else {
				throw new ActivityException();
			}
		} finally {
			pm.close();
		}

		return result;
	}

	@Override
	public PlanetDetails enableDefence(long planetIndex) throws AuthException,
			ActivityException {
		User user = JDOUtils.getUser();

		PlanetDetails result = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result = JDOUtils.getObjectById(pm, PlanetDetails.class,
					planetIndex, true);
			if ((result != null)
					&& (user.getUserId().equals(result.getOwnerId()))
					&& !result.isDefenceEnabled()
					&& (result.getResourceCount() > Constants.PLANET_DEFENCE_SWITCH_ON_PRICE)) {
				result.setResourceCount(Math.max(0.0, result.getResourceCount()
						- Constants.PLANET_DEFENCE_SWITCH_ON_PRICE));
				result.setDefenceEnabled(true);
				CacheUtils.put(PlanetDetails.class, planetIndex, result);
			} else {
				throw new ActivityException();
			}
		} finally {
			pm.close();
		}

		return result;
	}

	@Override
	public PlanetDetails disableDefence(long planetIndex) throws AuthException,
			ActivityException {
		User user = JDOUtils.getUser();

		PlanetDetails result = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			result = JDOUtils.getObjectById(pm, PlanetDetails.class,
					planetIndex, true);
			if ((result != null)
					&& (user.getUserId().equals(result.getOwnerId()))) {
				result.setDefenceEnabled(false);
				CacheUtils.put(PlanetDetails.class, planetIndex, result);
			} else {
				throw new ActivityException();
			}
		} finally {
			pm.close();
		}

		return result;
	}

	@Override
	public TransportationDetails startResourceTransportation(
			long sourcePlanetIndex, long targetPlanetIndex,
			double resourceCountDelta) throws AuthException, ActivityException {
		if ((sourcePlanetIndex == targetPlanetIndex)
				|| (resourceCountDelta <= Constants.EPS)) {
			throw new ActivityException();
		}

		User user = JDOUtils.getUser();

		Transportation transportation = null;
		PlanetDetails sourcePlanetDetails = null;
		PlanetDetails targetPlanetDetails = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			sourcePlanetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, sourcePlanetIndex, true);

			if ((sourcePlanetDetails == null)
					|| !user.getUserId().equals(
							sourcePlanetDetails.getOwnerId())) {
				throw new ActivityException();
			}

			double realResourceCountDelta = Math.min(resourceCountDelta,
					sourcePlanetDetails.getResourceCount());
			if (realResourceCountDelta < Constants.EPS) {
				throw new ActivityException();
			}

			targetPlanetDetails = GalaxyServiceBackend.get()
					.createAndDumpBotPlanetDetails(pm, targetPlanetIndex);

			transportation = GalaxyFactory.get().createResourceTransportation(
					sourcePlanetDetails, targetPlanetIndex,
					realResourceCountDelta);

			GalaxyServiceBackend.get().registerTransportation(transportation);

			sourcePlanetDetails.setResourceCount(sourcePlanetDetails
					.getResourceCount() - realResourceCountDelta);
			CacheUtils.put(PlanetDetails.class, sourcePlanetIndex,
					sourcePlanetDetails);
		} finally {
			pm.close();
		}

		TransportationDetails result = new TransportationDetails();
		result.setTransportation(transportation);
		result.setSourcePlanetDetails(sourcePlanetDetails);
		result.setTargetPlanetDetails(targetPlanetDetails);

		ChatServiceBackend.get().recordHistoryMessage(
				user,
				ChatMessageHelper.get().createMessageText(
						"Началась переброска ",
						transportation.getResourceCount(),
						" ед. ресурса с планеты ", sourcePlanetDetails,
						" на планету ", targetPlanetDetails, "."));

		return result;
	}

	@Override
	public TransportationDetails startFleetTransportation(
			long sourcePlanetIndex, long targetPlanetIndex, int unitCountDelta)
			throws AuthException, ActivityException {
		if ((sourcePlanetIndex == targetPlanetIndex) || (unitCountDelta <= 0)) {
			throw new ActivityException();
		}

		User user = JDOUtils.getUser();

		Transportation transportation = null;
		PlanetDetails sourcePlanetDetails = null;
		PlanetDetails targetPlanetDetails = null;

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			sourcePlanetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, sourcePlanetIndex, true);

			if ((sourcePlanetDetails == null)
					|| !user.getUserId().equals(
							sourcePlanetDetails.getOwnerId())) {
				throw new ActivityException();
			}

			int realUnitCountDelta = Math.min(unitCountDelta,
					sourcePlanetDetails.getOrbitUnitCount());
			if (realUnitCountDelta <= 0) {
				throw new ActivityException();
			}

			targetPlanetDetails = GalaxyServiceBackend.get()
					.createAndDumpBotPlanetDetails(pm, targetPlanetIndex);

			transportation = GalaxyFactory.get().createFleetTransportation(
					sourcePlanetDetails, targetPlanetIndex, realUnitCountDelta);

			GalaxyServiceBackend.get().registerTransportation(transportation);

			sourcePlanetDetails.setOrbitUnitCount(sourcePlanetDetails
					.getOrbitUnitCount() - realUnitCountDelta);
			CacheUtils.put(PlanetDetails.class, sourcePlanetIndex,
					sourcePlanetDetails);
		} finally {
			pm.close();
		}

		TransportationDetails result = new TransportationDetails();
		result.setTransportation(transportation);
		result.setSourcePlanetDetails(sourcePlanetDetails);
		result.setTargetPlanetDetails(targetPlanetDetails);

		ChatServiceBackend.get().recordHistoryMessage(
				user,
				ChatMessageHelper.get().createMessageText(
						"Началась переброска ", transportation.getUnitCount(),
						" ед. кораблей с планеты ", sourcePlanetDetails,
						" на планету ", targetPlanetDetails, "."),
				targetPlanetDetails.getOwnerName());

		return result;
	}

	@Override
	public GalaxyMapDetails getGalaxyMapDetails() throws AuthException {
		return GalaxyServiceBackend.get().getGalaxyMapDetails();
	}

	@Override
	public boolean isEulaAccepted() throws AuthException {
		User user = JDOUtils.getUser();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			EULA eula = getEula(pm, user);
			if ((eula != null) && eula.isAccepted()) {
				return true;
			}
		} finally {
			pm.close();
		}

		return false;
	}

	@Override
	public void acceptEula() throws AuthException {
		User user = JDOUtils.getUser();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			EULA eula = getEula(pm, user);
			if (eula == null) {
				eula = new EULA();
				eula.setUserId(user.getUserId());
				eula.setEmail(user.getEmail());
				eula.setAccepted(false);
				pm.makePersistent(eula);
			}
			eula.setAccepted(true);
		} finally {
			pm.close();
		}
	}

	private EULA getEula(PersistenceManager pm, User user) {
		try {
			return pm.getObjectById(EULA.class, user.getUserId());
		} catch (JDOObjectNotFoundException nothing) {
		}
		return null;
	}
}
