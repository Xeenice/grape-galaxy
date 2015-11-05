package org.grape.galaxy.server;

import java.util.List;

import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.ChatException;
import org.grape.galaxy.client.service.ChatService;
import org.grape.galaxy.model.ChatMessage;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ChatServiceImpl extends RemoteServiceServlet implements
		ChatService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<ChatMessage> getMessages(int room, long index)
			throws AuthException, ChatException {
		return ChatServiceBackend.get().getMessages(room, index);
	}

	@Override
	public List<ChatMessage> appendMessage(int room, long index, String text)
			throws AuthException, ChatException {
		return ChatServiceBackend.get().appendMessage(room, index, text);
	}
}
