package org.grape.galaxy.client.service;

import java.util.List;

import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.ChatException;
import org.grape.galaxy.model.ChatMessage;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("chatService")
public interface ChatService extends RemoteService {

	int RU_CHAT_ROOM = 0;
	int EN_CHAT_ROOM = 1;
	int HISTORY_ROOM = 2;
	
	int CHAT_ROOM_COUNT = 3;
	
	int MAX_MESSAGE_PER_ROOM_COUNT = 32;
	int MAX_HISTORY_MESSAGE_COUNT = 256;
	int MAX_MESSAGE_LENGTH = 64;
	
	List<ChatMessage> getMessages(int room, long index)
			throws AuthException, ChatException;
	
	List<ChatMessage> appendMessage(int room, long index, String text)
			throws AuthException, ChatException;
}
