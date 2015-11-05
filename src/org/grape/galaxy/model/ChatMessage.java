package org.grape.galaxy.model;

import java.io.Serializable;

public class ChatMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public static int SPECIALS_MAX_COUNT = 10;

	public static final char SPECIAL_CHAR = '#';
	public static final String PLANET_REF_PREFIX = "PREF";
	public static final String PLANET_NAME_PREFIX = "PNAME";
	public static final String HTTP_LINK_PREFIX = "http://";

	public static final String PRIVATE_MESSAGE_PREFIX = (SPECIAL_CHAR + "to");
	public static final String WARN_MESSAGE_PREFIX = (SPECIAL_CHAR + "warn");

	public static String createPrivateMessageText(String targetUserName,
			String text) {
		return (ChatMessage.PRIVATE_MESSAGE_PREFIX + " " + targetUserName + " " + text);
	}

	public static String createWarnMessageText(String targetUserName,
			String text) {
		return (ChatMessage.WARN_MESSAGE_PREFIX + " " + targetUserName + " " + text);
	}

	private long index;

	private int room;

	private long timeMillis;
	private String userName;
	private String text;

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	public long getTimeMillis() {
		return timeMillis;
	}

	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isPrivate() {
		return ((text != null) && text.startsWith(PRIVATE_MESSAGE_PREFIX));
	}

	public boolean isPrivateTo(String targetUserName) {
		return ((text != null) && (targetUserName != null) && text
				.toLowerCase().startsWith(
						(PRIVATE_MESSAGE_PREFIX + " " + targetUserName)
								.toLowerCase()));
	}

	public boolean isWarn() {
		return ((text != null) && text.startsWith(WARN_MESSAGE_PREFIX));
	}

	public boolean isWarnTo(String targetUserName) {
		return ((text != null) && (targetUserName != null) && text
				.toLowerCase().startsWith(
						(WARN_MESSAGE_PREFIX + " " + targetUserName)
								.toLowerCase()));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
