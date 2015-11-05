package org.grape.galaxy.server;

import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;

import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.FeedbackException;
import org.grape.galaxy.client.service.FeedbackService;
import org.grape.galaxy.server.utils.JDOUtils;
import com.google.appengine.api.users.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FeedbackServiceImpl extends RemoteServiceServlet implements
		FeedbackService {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(FeedbackServiceImpl.class
			.getName());

	private String recipientEmails[];

	@Override
	public void init() throws ServletException {
		super.init();
		String recipientEmailsParam = getServletContext().getInitParameter(
				"feedbackRecipientEmails");
		if (recipientEmailsParam != null) {
			recipientEmails = recipientEmailsParam.split(",");
		}
	}

	@Override
	public void sendMessage(String subject, String comments)
			throws AuthException, FeedbackException {
		if ((recipientEmails == null) || (recipientEmails.length == 0)) {
			throw new FeedbackException("Не задан получатель сообщения");
		}

		User user = JDOUtils.getUser();

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(user.getEmail()));
			for (String email : recipientEmails) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						email));
			}
			msg.setSubject(MimeUtility.encodeText("[GF] " + subject, "utf-8",
					null));
			msg.setText(comments);
			Transport.send(msg);
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
			throw new FeedbackException(ex);
		}
	}
}
