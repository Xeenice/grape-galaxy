package org.grape.galaxy.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grape.galaxy.model.LastAccessInfo;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.GlobalTimeAndIndexUtils;
import org.grape.galaxy.server.utils.JDOUtils;

import com.google.appengine.api.users.User;

public class AdminController extends HttpServlet {

	private static final long serialVersionUID = 4810053942493889020L;

	private static Logger logger = Logger.getLogger(AdminController.class
			.getName());

	private int daysPriorToKickNotification = 60;
	private int daysPriorToKick = 90;

	@Override
	public void init() throws ServletException {
		super.init();
		ServletContext context = getServletContext();
		try {
			daysPriorToKickNotification = new Integer(
					context.getInitParameter("daysPriorToKickNotification"));
		} catch (Exception nothing) {
		}
		try {
			daysPriorToKick = new Integer(
					context.getInitParameter("daysPriorToKick"));
		} catch (Exception nothing) {
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (request.getParameter("autokick") != null) {
			autokick();
		} else if (request.getParameter("kick") != null) {
			String userId = request.getParameter("kick");
			kick(userId);
		}
	}

	private void autokick() {
		Calendar today = Calendar.getInstance();
		today.setTime(GlobalTimeAndIndexUtils.today());

		List<String> kickList = new ArrayList<String>();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Extent<LastAccessInfo> extent = pm.getExtent(LastAccessInfo.class);
			for (LastAccessInfo lastAccessInfo : extent) {
				if (lastAccessInfo.isKicked()) {
					continue;
				}
				
				Calendar lastAccess = Calendar.getInstance();
				lastAccess.setTime(lastAccessInfo.getLastAccessTime());
				lastAccess.add(Calendar.DAY_OF_YEAR, daysPriorToKick);
				if (today.after(lastAccess)) {
					kickList.add(lastAccessInfo.getUserId());
					lastAccessInfo.setKicked(true);
				} else {
					lastAccess.setTime(lastAccessInfo.getLastAccessTime());
					lastAccess.add(Calendar.DAY_OF_YEAR,
							daysPriorToKickNotification);
					if (today.after(lastAccess)) {
						sendKickNotification(lastAccessInfo.getEmail());
					}
				}
			}
			extent.closeAll();
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		} finally {
			pm.close();
		}
		
		for (String userId : kickList) {
			kick(userId);
		}
	}

	private void sendKickNotification(String email) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			User user = JDOUtils.getUser();
			
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(user.getEmail()));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));
			msg.setSubject(MimeUtility.encodeText("[GA] Уведомление об освобождении занимаемых Вами планет", "utf-8", null));
			msg.setText("Здравстуйте!\n\nВы не заходили в игру \"Галактика\" (www.grape-galaxy.org) более 2-х месяцев. По правилам, игрок, отсутствующий более 3-х месяцев, считается выбывшим из игры. При этом все Ваши данные будут удалены.\n\nС уважением, администрация Grape");
			Transport.send(msg);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}
	}

	private void kick(String userId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Query query = pm.newQuery(PlanetDetails.class);
			query.setFilter("ownerId == userId");
			query.declareParameters("String userId");
			@SuppressWarnings("unchecked")
			List<PlanetDetails> userPlanets = (List<PlanetDetails>) query
					.execute(userId);
			if (userPlanets != null) {
				for (PlanetDetails userPlanet : userPlanets) {
					userPlanet.setHome(false);
					userPlanet.setOwnerId(null);
					userPlanet.setOwnerName(null);
					userPlanet.setDefenceEnabled(false);
					userPlanet.setUnitProduction(false);
					CacheUtils.put(PlanetDetails.class, userPlanet.getIndex(),
							userPlanet);
				}
			}
			query.closeAll();
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		} finally {
			pm.close();
		}
	}
}
