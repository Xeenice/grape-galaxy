package org.grape.galaxy.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.JDOUtils;

public class StartupController extends HttpServlet {

	private static final long serialVersionUID = 5897163383073178758L;

	private static Logger logger = Logger.getLogger(StartupController.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		run();
	}

	private void run() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			preparePlanetsDetails(pm);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		} finally {
			pm.close();
		}
	}

	private void preparePlanetsDetails(final PersistenceManager pm)
			throws Exception {
		int planetsAmount = 0;
		for (Sector sector : Galaxy.get().getAvailableSectors()) {
			for (@SuppressWarnings("unused")
			final Planet planet : sector.getPlanets()) {
				planetsAmount++;
			}
		}
		List<String> uniquePlanetNames = PlanetNameProvider.get()
				.generateUniqueNames(planetsAmount);
		for (Sector sector : Galaxy.get().getAvailableSectors()) {
			for (final Planet planet : sector.getPlanets()) {
				long planetIndex = planet.getIndex();
				PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
						PlanetDetails.class, planetIndex, true);
				if (planetDetails == null) {
					planetDetails = GalaxyFactory.get().createBotPlanetDetails(
							planet.getIndex());
					planetDetails.setPlanetName(uniquePlanetNames.remove(0));
					CacheUtils.put(PlanetDetails.class, planetIndex,
							planetDetails);
					logger.info("Детали планеты #" + planetDetails.getIndex()
							+ " созданы.");
				}
			}
		}
	}

}
