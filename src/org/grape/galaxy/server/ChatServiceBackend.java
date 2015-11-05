package org.grape.galaxy.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.ChatException;
import org.grape.galaxy.client.service.ChatService;
import org.grape.galaxy.model.ChatMessage;
import org.grape.galaxy.server.utils.GlobalTimeAndIndexUtils;
import org.grape.galaxy.server.utils.JDOUtils;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.users.User;

public class ChatServiceBackend implements ChatService {

	private static Logger logger = Logger.getLogger(ChatServiceBackend.class
			.getName());

	private static final int MESSAGE_EXPIRATION_PERIOD_MILLIS = 3 * 60 * 1000;
	private static final int HISTORY_MESSAGE_EXPIRATION_PERIOD_SECONDS = 24 * 60 * 60;
	private static final int MAX_FAIL_COUNT = 10;

	private static final String MC_KEY_LAST_INDEX = "ChatService#index#";
	private static final String MC_KEY_CHAT_MESSAGE = "ChatMessage#";

	private static final MemcacheService memcacheService = MemcacheServiceFactory
			.getMemcacheService();

	private static ChatServiceBackend inst;

	public synchronized static ChatServiceBackend get() {
		if (inst == null) {
			inst = new ChatServiceBackend();
		}
		return inst;
	}

	private ChatServiceBackend() {
	}

	@Override
	public List<ChatMessage> getMessages(int room, long index)
			throws AuthException, ChatException {
		if ((room != RU_CHAT_ROOM) && (room != EN_CHAT_ROOM)
				&& (room != HISTORY_ROOM)) {
			throw new ChatException();
		}

		User user = JDOUtils.getUser();

		return getMessages(user.getNickname(), room, index);
	}

	public List<ChatMessage> getMessages(String userName, int room, long index)
			throws ChatException {
		if ((room != RU_CHAT_ROOM) && (room != EN_CHAT_ROOM)
				&& (room != HISTORY_ROOM)) {
			throw new ChatException();
		}

		List<ChatMessage> result = new ArrayList<ChatMessage>();

		long lastIndex = 0;
		Object lastIndexObj = memcacheService.get(MC_KEY_LAST_INDEX + room);
		if (lastIndexObj instanceof Number) {
			lastIndex = ((Number) lastIndexObj).longValue();
		}

		if ((index == 0) && (lastIndex > 0)) {
			// Первое обращение
			int maxMessageCount;
			if (room != ChatService.HISTORY_ROOM) {
				maxMessageCount = ChatService.MAX_MESSAGE_PER_ROOM_COUNT;
			} else {
				maxMessageCount = ChatService.MAX_HISTORY_MESSAGE_COUNT;
			}
			for (long currentIndex = lastIndex; (currentIndex > 0)
					&& (currentIndex > (lastIndex - maxMessageCount)); currentIndex--) {
				String key = getKey(room, currentIndex);
				ChatMessage message = (ChatMessage) memcacheService.get(key);
				if ((message != null)
						&& (((userName != null) && userName
								.equalsIgnoreCase(message.getUserName()))
								|| !message.isPrivate() || message
								.isPrivateTo(userName))) {
					result.add(message);
				}
			}
		} else {
			long currentIndex = (index + 1);
			int failCount = 0;
			while (true) {
				String key = getKey(room, currentIndex);
				ChatMessage message = (ChatMessage) memcacheService.get(key);
				if (message != null) {
					if (((userName != null) && userName
							.equalsIgnoreCase(message.getUserName()))
							|| !message.isPrivate()
							|| message.isPrivateTo(userName)) {
						result.add(message);
					}
				} else {
					failCount++;
					if (failCount > MAX_FAIL_COUNT) {
						break;
					}
				}
				currentIndex++;
			}
		}

		return result;
	}

	@Override
	public List<ChatMessage> appendMessage(int room, long index, String text)
			throws AuthException, ChatException {
		if ((room != RU_CHAT_ROOM) && (room != EN_CHAT_ROOM)) {
			throw new ChatException();
		}

		User user = JDOUtils.getUser();

		appendMessage(user.getNickname(), room, text,
				Expiration.byDeltaMillis(MESSAGE_EXPIRATION_PERIOD_MILLIS));

		return getMessages(room, index);
	}

	public void recordHistoryMessage(User user, String text) {
		recordHistoryMessage(user, text, null);
	}

	public void recordHistoryMessage(User user, String text,
			String attractedUserName) {
		String userName = "";
		if (user != null) {
			userName = user.getNickname();
		}
		recordHistoryMessage(userName, text, attractedUserName);
	}

	public void recordHistoryMessage(String userName, String text) {
		recordHistoryMessage(userName, text, null);
	}

	public void recordHistoryMessage(String userName, String text,
			String attractedUserName) {
		if (userName == null) {
			userName = "";
		}
		if ((attractedUserName != null) && !attractedUserName.equals(userName)) {
			text = ChatMessage.createWarnMessageText(attractedUserName, text);
		}
		appendMessage(
				userName,
				HISTORY_ROOM,
				text,
				Expiration
						.byDeltaSeconds(HISTORY_MESSAGE_EXPIRATION_PERIOD_SECONDS));
	}
	
	/**
	 * Обновление последних MAX_HISTORY_MESSAGE_COUNT сообщений истории,
	 * для того чтобы продлить им жизнь
	 * 
	 */
	public void updateHistoryMessages() {
		long lastIndex = 0;
		Object lastIndexObj = memcacheService.get(MC_KEY_LAST_INDEX + HISTORY_ROOM);
		if (lastIndexObj instanceof Number) {
			lastIndex = ((Number) lastIndexObj).longValue();
		}

		if (lastIndex > 0) {
			int maxMessageCount = ChatService.MAX_HISTORY_MESSAGE_COUNT;
			for (long currentIndex = lastIndex; (currentIndex > 0)
					&& (currentIndex > (lastIndex - maxMessageCount)); currentIndex--) {
				String key = getKey(HISTORY_ROOM, currentIndex);
				memcacheService.put(key, memcacheService.get(key), Expiration
						.byDeltaSeconds(HISTORY_MESSAGE_EXPIRATION_PERIOD_SECONDS),
						MemcacheService.SetPolicy.SET_ALWAYS);
			}
		}
	}

	private void appendMessage(String userName, int room, String text,
			Expiration expiration) {
		long nextIndex = memcacheService.increment(MC_KEY_LAST_INDEX + room, 1L, 0L);

		ChatMessage message = new ChatMessage();
		message.setIndex(nextIndex);
		message.setRoom(room);
		message.setTimeMillis(GlobalTimeAndIndexUtils.currentTimeMillis());
		message.setUserName(userName);
		message.setText(text);

		memcacheService.put(getKey(room, nextIndex), message, expiration,
				MemcacheService.SetPolicy.SET_ALWAYS);
	}

	private String getKey(int room, long index) {
		return (MC_KEY_CHAT_MESSAGE + room + "#" + index);
	}
}
