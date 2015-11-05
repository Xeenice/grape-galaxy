package org.grape.galaxy.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grape.galaxy.model.PlanetDetails;

@SuppressWarnings("unchecked")
public class GalaxyController extends HttpServlet {

	private static final long serialVersionUID = 5897163383073178758L;

	private static Logger logger = Logger.getLogger(GalaxyController.class
			.getName());

	public static final String RPN_DUMP = "dump";
	public static final String RPN_SECTOR_Y = "y";

	private StatefulEventSystem<PlanetDetails> planetDetailsEventSystem;

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

		GalaxyEventSystem<PlanetDetails> botEventSystem = new BotEventSystem();
		GalaxyEventSystem<PlanetDetails> resourceEventSystem = new ResourceEventSystem();
		// XXX закрыто на основании задачи GALAXY_RTE-41
//		GalaxyEventSystem<PlanetDetails> antimonopolyBotEventSystem = new AntimonopolyBotEventSystem();
		planetDetailsEventSystem = new CompositeEventSystem<PlanetDetails>(
				botEventSystem, resourceEventSystem/*, antimonopolyBotEventSystem*/);
		
		planetDetailsEventSystem.init(params);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter(RPN_DUMP) != null) {
			long sectorY = Long.valueOf(request.getParameter(RPN_SECTOR_Y));
			GalaxyBackend.get().dumpPlanets(sectorY);
		} else {
			boolean sectorRelevanceEnabled = "true".equals(getServletContext()
					.getInitParameter("sectorRelevanceEnabled"));
			GalaxyBackend.get().compute(sectorRelevanceEnabled,
					planetDetailsEventSystem);
		}
	}

}
