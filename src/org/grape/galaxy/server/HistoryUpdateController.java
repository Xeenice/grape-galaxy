package org.grape.galaxy.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HistoryUpdateController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger
			.getLogger(HistoryUpdateController.class.getName());

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		ChatServiceBackend.get().updateHistoryMessages();
	}
}