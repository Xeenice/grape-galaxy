package org.grape.galaxy.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.UserRating;
import org.grape.galaxy.server.utils.JDOUtils;

public class RatingController extends HttpServlet {

	private static final long serialVersionUID = 4810053942493889020L;

	private static Logger logger = Logger.getLogger(RatingController.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		computeRating();
	}

	private void computeRating() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			// Удаление предыдущих результатов
			Query deleteQuery = pm.newQuery(UserRating.class);
			deleteQuery.deletePersistentAll();

			// Заполнение новых результатов
			Map<String, UserRating> userRatingsMap = new LinkedHashMap<String, UserRating>();
			for (Sector sector : Galaxy.get().getAvailableSectors()) {
				for (Planet planet : sector.getPlanets()) {
					PlanetDetails planetDetails = JDOUtils.getObjectById(pm,
							PlanetDetails.class, planet.getIndex(), true);
					if (planetDetails == null) {
						continue;
					}
					String ownerId = planetDetails.getOwnerId();
					if (ownerId == null) {
						continue;
					}
					UserRating userRating = userRatingsMap.get(ownerId);
					if (userRating == null) {
						userRating = new UserRating();
						userRating.setUserId(ownerId);
						userRating.setUserName(planetDetails.getOwnerName());
						userRatingsMap.put(ownerId, userRating);
					}
					userRating.setPlanetCount(userRating.getPlanetCount() + 1);
					userRating.setOrbitUnitCount(userRating.getOrbitUnitCount()
							+ planetDetails.getOrbitUnitCount());
					userRating.setResourceCount(userRating.getResourceCount()
							+ planetDetails.getResourceCount());
				}
			}

			// Сортировка результатов и назначение страниц (оптимизация)
			List<UserRating> userRatings = new ArrayList<UserRating>(
					userRatingsMap.values());
			Collections.sort(userRatings);
			int index = 0;
			for (ListIterator<UserRating> iterator = userRatings
					.listIterator(userRatings.size()); iterator.hasPrevious();) {
				UserRating userRating = (UserRating) iterator.previous();
				userRating
						.setPageIndex(index / Constants.USER_RATING_PAGE_SIZE);
				userRating.setLastPageIndex((userRatings.size() - 1)
						/ Constants.USER_RATING_PAGE_SIZE);
				index++;
			}

			// Сохранение новых результатов
			pm.makePersistentAll(userRatings);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		} finally {
			pm.close();
		}
	}
}
