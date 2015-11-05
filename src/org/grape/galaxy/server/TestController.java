package org.grape.galaxy.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.JDOUtils;

@SuppressWarnings("unchecked")
public class TestController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(TestController.class
			.getName());

	private static StatefulEventSystem<PlanetDetails> planetDetailsEventSystem;

	static {
		GalaxyEventSystem<PlanetDetails> botProductionEventSystem = new BotEventSystem();
		GalaxyEventSystem<PlanetDetails> antimonopolyBotEventSystem = new AntimonopolyBotEventSystem();
		planetDetailsEventSystem = new CompositeEventSystem<PlanetDetails>(
				botProductionEventSystem, antimonopolyBotEventSystem);
	}

	@Override
	public void init() throws ServletException {
		super.init();

		Map<String, String> params = new HashMap<String, String>();
		ServletContext context = getServletContext();
		Enumeration<String> names = context.getInitParameterNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			params.put(name, context.getInitParameter(name));
		}

		planetDetailsEventSystem.init(params);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String testIndex = request.getParameter("i");
		if ("0".equals(testIndex)) {
			test0(request, response);
		} else if ("1".equals(testIndex)) {
			test1(request, response);
		} else if ("c".equals(testIndex)) {
			if (request.getParameter("p") != null) {
				clear(Long.parseLong(request.getParameter("p")));
			} else {
				clear();
			}
		} else if ("a".equals(testIndex)) {
			activateSectors(request, response);
		} else if ("g".equals(testIndex)) {
			computeGalaxy(request, response);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void test0(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			for (Sector sector : Galaxy.get().getAvailableSectors()) {
				for (Planet planet : sector.getPlanets()) {
					long planetIndex = planet.getIndex();
					PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
							PlanetDetails.class, planetIndex, true);
					if ((planetDetails == null)
							|| (planetDetails.getOwnerId() == null)) {
						int rand = (int) Math.round(Math.random() * 50);
						String planetName;
						if (planetDetails != null) {
							planetName = planetDetails.getPlanetName();
						} else {
							planetName = "User " + rand;
						}
						GalaxyServiceBackend.get()
								.createAndCacheTestPlanetDetails(planetIndex,
										"user" + rand + "@example.com",
										planetName);
					}
				}
			}
		} finally {
			pm.close();
		}
	}

	private void test1(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			for (Sector sector : Galaxy.get().getAvailableSectors()) {
				for (Planet planet : sector.getPlanets()) {
					try {
						PlanetDetails planetDetails = JDOUtils.getObjectById(
								pm, PlanetDetails.class, planet.getIndex(),
								true);
						if (planetDetails == null) {
							continue;
						}

						Planet targetPlanet = null;
						do {
							long rnx = Constants.GALAXY_LINEAR_SIZE_IN_SECTORS
									/ 2
									+ Math.round((Math.random() - 0.5)
											* (2 * Constants.GALAXY_SECTORS_RANGE));
							long rny = Constants.GALAXY_LINEAR_SIZE_IN_SECTORS
									/ 2
									+ Math.round((Math.random() - 0.5)
											* (2 * Constants.GALAXY_SECTORS_RANGE));
							long randomSectorIndex = rny
									* Constants.GALAXY_LINEAR_SIZE_IN_SECTORS
									+ rnx;
							Sector randomSector = Galaxy.get().getSector(
									randomSectorIndex);
							List<Planet> randomSectorPlanets = randomSector
									.getPlanets();
							targetPlanet = randomSectorPlanets
									.get((int) Math.round(Math.random()
											* (randomSectorPlanets.size() - 1)));
						} while (targetPlanet == planet);

						Transportation transportation = GalaxyFactory.get()
								.createEmptyTransportation(planetDetails,
										targetPlanet.getIndex());

						if ((Math.random() > 0.5)
								&& (planetDetails.getResourceCount() > 1)) { // ресурсы
							double transResCount = Math.random()
									* planetDetails.getResourceCount();
							transportation.setResourceCount(transResCount);
							transportation
									.setVelocityInCells(Constants.RESOURCE_TRANSPORTATION_VELOCITY_IN_CELLS);
							planetDetails.setResourceCount(planetDetails
									.getResourceCount() - transResCount);
						} else if (planetDetails.getOrbitUnitCount() > 2) { // флот
							int transUnitCount = (int) (Math.random() * planetDetails
									.getOrbitUnitCount());
							if (transUnitCount == 0) {
								continue;
							}
							transportation.setUnitCount(transUnitCount);
							transportation
									.setVelocityInCells(Constants.FLEET_TRANSPORTATION_VELOCITY_IN_CELLS);
							planetDetails.setOrbitUnitCount(planetDetails
									.getOrbitUnitCount() - transUnitCount);
						} else {
							continue; // нечего перебрасывать
						}

						CacheUtils.put(PlanetDetails.class,
								planetDetails.getIndex(), planetDetails);

						GalaxyServiceBackend.get().registerTransportation(
								transportation);
					} catch (Exception e) {
						logger.severe(e.getMessage());
					}
				}
			}
		} finally {
			pm.close();
		}
	}

	private void clear() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			for (Sector sector : Galaxy.get().getAvailableSectors()) {
				for (Planet planet : sector.getPlanets()) {
					clear(pm, planet.getIndex());
				}
			}
		} finally {
			pm.close();
		}
	}

	private void clear(long planetIndex) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			clear(pm, planetIndex);
		} finally {
			pm.close();
		}
	}

	private void clear(PersistenceManager pm, long planetIndex) {
		try {
			CacheUtils.delete(PlanetDetails.class, planetIndex);
			PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
					PlanetDetails.class, planetIndex);
			if (planetDetails != null) {
				pm.deletePersistent(planetDetails);
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}

	private void activateSectors(HttpServletRequest request,
			HttpServletResponse response) {
		for (Sector sector : Galaxy.get().getAvailableSectors()) {
			GalaxyServiceBackend.get().enableRelevanceForSector(
					sector.getIndex());
		}
	}

	private void computeGalaxy(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String timeInHoursParam = request.getParameter("h");
		double timeInHours = (timeInHoursParam == null) ? 1 : Double
				.parseDouble(timeInHoursParam);
		double timeInSeconds = 60 * 60 * timeInHours;
		double time = 0;
		while (time < timeInSeconds) {
			GalaxyBackend.get().compute(false, planetDetailsEventSystem);
			time += Constants.ACTIVITY_PERIOD_SECONDS;
		}
	}
}
