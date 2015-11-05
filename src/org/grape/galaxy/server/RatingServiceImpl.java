package org.grape.galaxy.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import org.grape.galaxy.client.RatingException;
import org.grape.galaxy.client.service.RatingService;
import org.grape.galaxy.model.UserRating;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RatingServiceImpl extends RemoteServiceServlet implements
		RatingService {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(RatingServiceImpl.class
			.getName());

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<UserRating> getRatingPage(int pageIndex)
			throws RatingException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(UserRating.class);
			query.setFilter("pageIndex == pageIndexParam");
			query.declareParameters("Integer pageIndexParam");
			return new ArrayList<UserRating>(
					(List<UserRating>) query.execute(pageIndex));
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
			throw new RatingException(ex);
		} finally {
			pm.close();
		}
	}

}
