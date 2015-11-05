package org.grape.galaxy.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.grape.galaxy.client.ChatException;
import org.grape.galaxy.client.service.ChatService;
import org.grape.galaxy.model.ChatMessage;

public class HistoryGadgetController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger
			.getLogger(HistoryGadgetController.class.getName());

	private static final String MESSAGE_SEPARATOR = "|";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String indexStr = request.getParameter("i");
		if (indexStr == null) {
			throw new IllegalArgumentException();
		}
		long index;
		try {
			index = new Long(indexStr);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException();
		}
		String userName = request.getParameter("u");
		if ((userName != null) && userName.trim().equals("")) {
			userName = null;
		}

		try {
			List<ChatMessage> messages = ChatServiceBackend.get().getMessages(
					userName, ChatService.HISTORY_ROOM, index);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/plain");

			PrintWriter out = response.getWriter();

			if (messages != null) {
				StringBuilder buf = new StringBuilder();
				for (ChatMessage message : messages) {
					if ((userName == null)
							|| userName.equalsIgnoreCase(message.getUserName())
							|| message.isWarnTo(userName)) {
						buf.append(message.getIndex());
						buf.append(MESSAGE_SEPARATOR);
						buf.append(message.getTimeMillis());
						buf.append(MESSAGE_SEPARATOR);
						buf.append(escape(message.getUserName()));
						buf.append(MESSAGE_SEPARATOR);
						buf.append(escape(message.getText()));
						buf.append(MESSAGE_SEPARATOR);
						out.write(buf.toString());
						buf.setLength(0);
					}
				}
			}
		} catch (ChatException ex) {
			logger.severe(ex.getMessage());
			throw new ServletException(ex);
		}
	}

	private String escape(String text) {
		String result;
		if (text != null) {
			result = text.replace(MESSAGE_SEPARATOR, MESSAGE_SEPARATOR
					+ MESSAGE_SEPARATOR);
		} else {
			result = " ";
		}
		return ((result.length() > 0) ? result : " ");
	}
}
