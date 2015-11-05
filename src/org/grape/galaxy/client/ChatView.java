package org.grape.galaxy.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.grape.galaxy.client.service.ChatService;
import org.grape.galaxy.client.service.ChatServiceProvider;
import org.grape.galaxy.model.ChatMessage;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

public class ChatView {

	public static final int MESSAGES_UPDATE_PERIOD_MILLIS = 15 * 1000;
	public static final String[] ROOM_NAMES = new String[] {
			"Канал связи [RU]", "Канал связи [EN]", "История событий" };
	
	private SectionStackSection chatSection;
	private Img chatRuButton;
	private Img chatGbButton;
	private Img historyButton;
	private Img[] chatRoomButtons;
	private HTMLPane chatMessagesHTML;
	private DynamicForm chatForm;

	private int room = ChatService.RU_CHAT_ROOM;
	private Map<Integer, List<ChatMessage>> messagesMap = new HashMap<Integer, List<ChatMessage>>();

	private Timer messagesUpdateTimer;
	
	private Timer[] newMessageSignalTimers = new Timer[ChatService.CHAT_ROOM_COUNT];
	
	private DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("HH:mm:ss");
	private DateTimeFormat historyDateTimeFormat = DateTimeFormat.getFormat("MMMM/d HH:mm:ss");

	public ChatView(final SectionStackSection chatSection) {
		this.chatSection = chatSection;

		chatSection.setTitle(ROOM_NAMES[room]);

		chatRuButton = new Img("/images/ru.png", 16, 11);
		chatRuButton.setCursor(Cursor.HAND);
		chatRuButton.setShowHover(false);
		chatRuButton.setShowFocused(false);
		chatRuButton.setShowDown(false);
		chatRuButton.setShowRollOver(false);
		chatGbButton = new Img("/images/gb.png", 16, 11);
		chatGbButton.setCursor(Cursor.HAND);
		chatGbButton.setShowHover(false);
		chatGbButton.setShowFocused(false);
		chatGbButton.setShowDown(false);
		chatGbButton.setShowRollOver(false);
		historyButton = new Img("/images/history.png", 16, 11);
		historyButton.setCursor(Cursor.HAND);
		historyButton.setShowHover(false);
		historyButton.setShowFocused(false);
		historyButton.setShowDown(false);
		historyButton.setShowRollOver(false);
		
		chatRuButton.setOpacity(100);
		chatGbButton.setOpacity(50);
		historyButton.setOpacity(50);
		
		chatRuButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showRoomSelection(ChatService.RU_CHAT_ROOM);
				changeRoom(ChatService.RU_CHAT_ROOM);
			}
		});
		chatGbButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				showRoomSelection(ChatService.EN_CHAT_ROOM);
				changeRoom(ChatService.EN_CHAT_ROOM);
			}
		});
		historyButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				showRoomSelection(ChatService.HISTORY_ROOM);
				changeRoom(ChatService.HISTORY_ROOM);
			}
		});
		
		chatSection.setControls(chatRuButton, chatGbButton, historyButton);
		
		chatRoomButtons = new Img[ChatService.CHAT_ROOM_COUNT];
		chatRoomButtons[ChatService.RU_CHAT_ROOM] = chatRuButton;
		chatRoomButtons[ChatService.EN_CHAT_ROOM] = chatGbButton;
		chatRoomButtons[ChatService.HISTORY_ROOM] = historyButton;
		
		for (int i = 0; i < newMessageSignalTimers.length; i++) {
			newMessageSignalTimers[i] = null;
		}

		VLayout chatLayout = new VLayout();
		chatLayout.setWidth100();
		chatLayout.setHeight100();

		chatMessagesHTML = new HTMLPane();
		chatMessagesHTML.setWidth100();
		chatMessagesHTML.setHeight("*");
		chatMessagesHTML.setContents("");
		chatMessagesHTML.addResizedHandler(new ResizedHandler() {
			
			@Override
			public void onResized(ResizedEvent event) {
				scrollToBottom();
			}
		});
		chatMessagesHTML.addDrawHandler(new DrawHandler() {
			
			@Override
			public void onDraw(DrawEvent event) {
				scrollToBottom();
			}
		});
		chatMessagesHTML.addVisibilityChangedHandler(new VisibilityChangedHandler() {
			
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				scrollToBottom();
			}
		});

		chatLayout.addMember(chatMessagesHTML);

		chatForm = new DynamicForm();
		chatForm.setWidth100();
		chatForm.setHeight(18);
		chatForm.setIsGroup(false);
		chatForm.setNumCols(1);
		final TextItem chatMessageItem = new TextItem("chatMessageText");
		chatMessageItem.setWidth("*");
		chatMessageItem.setShowTitle(false);
		chatMessageItem.setLength(ChatService.MAX_MESSAGE_LENGTH);
		FormItemIcon chatMessageIcon = new FormItemIcon();
		chatMessageIcon.setSrc("[SKIN]/headerIcons/double_arrow_right.png");
		chatMessageIcon.setShowOver(true);
		chatMessageIcon.setWidth(15);
		chatMessageIcon.setHeight(15);
		chatMessageItem.setIcons(chatMessageIcon);
		chatMessageItem.addIconClickHandler(new IconClickHandler() {

			@Override
			public void onIconClick(IconClickEvent event) {
				String text = chatMessageItem.getValueAsString();
				if ((text != null) && (text.trim().length() > 0)) {
					chatMessageItem.setValue("");
					requestMessageAppend(text);
				}
			}
		});
		chatMessageItem.addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				String text = chatMessageItem.getValueAsString();
				if ("Enter".equals(event.getKeyName()) && (text != null)
						&& (text.trim().length() > 0)) {
					chatMessageItem.setValue("");
					requestMessageAppend(text);
				}
			}
		});
		chatForm.setFields(chatMessageItem);

		chatLayout.addMember(chatForm);
		chatSection.addItem(chatLayout);
		
		createGlobalFuncs();

		messagesUpdateTimer = new Timer() {

			@Override
			public void run() {
				requestMessages();
			}
		};
		messagesUpdateTimer.scheduleRepeating(MESSAGES_UPDATE_PERIOD_MILLIS);
		requestMessages();
	}
	
	private native void createGlobalFuncs() /*-{
		var self = this;
		$wnd.sendPrivateMessage = function(userName) {
			if (!userName && this && this.innerHTML) {
				userName = this.innerHTML;
			}
			if (userName) {
				self.@org.grape.galaxy.client.ChatView::sendPrivateMessage(Ljava/lang/String;)(userName);
			}
		};
	}-*/;
	
	void sendPrivateMessage(String userName) {
		if ((userName == null) || (userName.length() == 0)) {
			return;
		}
		
		FormItem chatMessageItem = chatForm.getField("chatMessageText");
		chatMessageItem.setValue(ChatMessage.createPrivateMessageText(userName, ""));
		
		if (room == ChatService.HISTORY_ROOM) {
			showRoomSelection(ChatService.RU_CHAT_ROOM);
			changeRoom(ChatService.RU_CHAT_ROOM);
		}
		chatMessageItem.focusInItem();
	}

	private void changeRoom(int targetRoom) {
		this.room = targetRoom;
		chatSection.setTitle(ROOM_NAMES[targetRoom]);
		chatMessagesHTML.setContents(formatChatMessages(
				getMessages(targetRoom), targetRoom == ChatService.HISTORY_ROOM));
		chatMessagesHTML.markForRedraw();
		scrollToBottom();
		disableNewMessageSignal(targetRoom);
		requestMessages();
	}

	private void requestMessages() {
		for (int i = 0; i < ChatService.CHAT_ROOM_COUNT; i++) {
			final int currentRoom = i;
			List<ChatMessage> messages = getMessages(currentRoom);
			long index = 0;
			if (!messages.isEmpty()) {
				index = messages.get(messages.size() - 1).getIndex();
			}
			ChatServiceProvider.get().getMessages(currentRoom, index,
					new AsyncCallback<List<ChatMessage>>() {

						@Override
						public void onSuccess(List<ChatMessage> newMessages) {
							if (currentRoom == room) {
								updateMeesagesPanelWithNewMessages(newMessages);
								disableNewMessageSignal(currentRoom);
							} else {
								if (appendNewMessages(currentRoom, newMessages)) {
									enableNewMessageSignal(currentRoom);
								}
							}
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});
		}
	}

	private void requestMessageAppend(String text) {
		text = text.trim();
		if (text.length() > ChatService.MAX_MESSAGE_LENGTH) {
			text = text.substring(0, ChatService.MAX_MESSAGE_LENGTH);
		}
		
		final int currentRoom = room;
		List<ChatMessage> messages = getMessages(currentRoom);
		long index = 0;
		if (!messages.isEmpty()) {
			index = messages.get(messages.size() - 1).getIndex();
		}
		ChatServiceProvider.get().appendMessage(currentRoom, index, text,
				new AsyncCallback<List<ChatMessage>>() {

					@Override
					public void onSuccess(List<ChatMessage> newMessages) {
						if (currentRoom == room) {
							updateMeesagesPanelWithNewMessages(newMessages);
							disableNewMessageSignal(currentRoom);
						} else {
							if (appendNewMessages(currentRoom, newMessages)) {
								enableNewMessageSignal(currentRoom);
							}
						}
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
	}

	private void updateMeesagesPanelWithNewMessages(List<ChatMessage> newMessages) {
		if (appendNewMessages(room, newMessages)) {
			chatMessagesHTML.setContents(formatChatMessages(getMessages(room),
					room == ChatService.HISTORY_ROOM));
			chatMessagesHTML.markForRedraw();
			scrollToBottom();
		}
	}
	
	private List<ChatMessage> getMessages(int targetRoom) {
		List<ChatMessage> messages = messagesMap.get(targetRoom);
		if (messages == null) {
			messages = new ArrayList<ChatMessage>();
			messagesMap.put(targetRoom, messages);
		}
		return messages;
	}
	
	private boolean appendNewMessages(int targetRoom, List<ChatMessage> newMessages) {
		boolean ok = false;
		if ((newMessages != null) && !newMessages.isEmpty()) {
			Collections.sort(newMessages, new Comparator<ChatMessage>() {

				@Override
				public int compare(ChatMessage m1, ChatMessage m2) {
					return (int) (m1.getIndex() - m2.getIndex());
				}

			});
			List<ChatMessage> messages = getMessages(targetRoom);
			if (messages.isEmpty()) {
				messages.addAll(newMessages);
				ok = true;
			} else {
				ChatMessage lastMessage = messages.get(messages.size() - 1);
				for (ChatMessage newMessage : newMessages) {
					if (lastMessage.getIndex() < newMessage.getIndex()) {
						messages.add(newMessage);
						ok = true;
					}
				}
			}
			int maxMessageCount;
			if (targetRoom != ChatService.HISTORY_ROOM) {
				maxMessageCount = ChatService.MAX_MESSAGE_PER_ROOM_COUNT;
			} else {
				maxMessageCount = ChatService.MAX_HISTORY_MESSAGE_COUNT;
			}
			if (messages.size() > maxMessageCount) {
				int delta = (messages.size() - maxMessageCount);
				for (int i = 0; i < delta; i++) {
					messages.remove(0);
					ok = true;
				}
			}
		}
		return ok;
	}
	
	private void scrollToBottom() {
		new Timer() {

			@Override
			public void run() {
				chatMessagesHTML.scrollToBottom();
			}
			
		}.schedule(200);
	}
	
	private String formatChatMessages(List<ChatMessage> messages, boolean history) {
		String html = "";
		if (messages != null) {
			for (ChatMessage message : messages) {
				html += formatChatMessage(message, history);
			}
		}
		return html;
	}

	private String formatChatMessage(ChatMessage message, boolean history) {
		boolean privateMessageToMe = message.isPrivateTo(
				UserContainer.get().getNickname());
		boolean warnMessageToMe = message.isWarnTo(
				UserContainer.get().getNickname());
		
		Date date = new Date(message.getTimeMillis());
		String dateStr;
		if (history) {
			if (new Date().getDate() != date.getDate()) {
				dateStr = historyDateTimeFormat.format(date);
			} else {
				dateStr = dateTimeFormat.format(date);
			}
		} else {
			dateStr = dateTimeFormat.format(date);
		}
		String datePart = ("<span"
				+ ((privateMessageToMe || warnMessageToMe) ? " class=\"private\">" : ">")
				+ dateStr + "</span>");
		
		String userName = ViewUtils.escapeHTML(message.getUserName());
		String userNamePart;
		if (!UserContainer.get().getNickname().equals(message.getUserName())) {
			userNamePart = ("&nbsp;<a href=\"javascript: void(0)\" onclick=\"sendPrivateMessage.call(this)\">"
					+ userName + "</a>");
		} else {
			userNamePart = ("&nbsp<span class=\"nick\">" + userName + "</span>");
		}
		
		String result = "";
		if (warnMessageToMe) {
			result += "<img src=\"/images/jet.png\" width=\"10\" height=\"10\"/>&nbsp;";
		}
		result += (datePart + userNamePart + "&nbsp;<span"
				+ ((privateMessageToMe || warnMessageToMe) ? " class=\"private\">" : ">")
				+ prepareMessageText(message.getText()) + "</span><br/>");
		return result;
	}
	
	private String prepareMessageText(String text) {
		String result = ViewUtils.escapeHTML(text);
		
		int i;
		int count = 0;
		// Ссылки на планеты
		while (((i = result.indexOf(ChatMessage.PLANET_REF_PREFIX)) != -1)
				&& (count < ChatMessage.SPECIALS_MAX_COUNT)) {
			int j = result.indexOf(ChatMessage.SPECIAL_CHAR, i + ChatMessage
					.PLANET_REF_PREFIX.length());
			boolean fail = false;
			if (j != -1) {
				String planetIndexStr = result.substring(i + ChatMessage
						.PLANET_REF_PREFIX.length(), j);
				try {
					long planetIndex = new Long(planetIndexStr);
					Planet planet = Galaxy.get().getPlanet(planetIndex);
					if (planet != null) {
						String planetName = ("#" + planet.getIndex());
						int k = result.indexOf(ChatMessage.PLANET_NAME_PREFIX,
								j + 1);
						if (k == (j + 1)) {
							int m = result.indexOf(ChatMessage.SPECIAL_CHAR,
									k + ChatMessage.PLANET_NAME_PREFIX.length());
							if (m != -1) {
								planetName = result.substring(k + ChatMessage
										.PLANET_NAME_PREFIX.length(), m);
								j = m;
							}
						}
						result = (result.substring(0, i)
								+ "<a href=\"javascript: void(0)\" onclick=\"selectPlanet("
								+ planetIndex + ")\">" + ViewUtils
								.escapeHTML(planetName) + "</a>"
								+ result.substring(j + 1));
					} else {
						fail = true;
					}
				} catch (Exception nothing) {
					fail = true;
				}
			} else {
				fail = true;
			}
			if (fail) {
				result = (result.substring(0, i) + result.substring(i + ChatMessage
						.PLANET_REF_PREFIX.length()));
			} else {
				count++;
			}
		}
		
		// Внешние ссылки
		i = 0;
		while (((i = result.indexOf(ChatMessage.HTTP_LINK_PREFIX, i)) != -1)
				&& (count < ChatMessage.SPECIALS_MAX_COUNT)) {
			int j = result.indexOf(' ', i + ChatMessage
					.HTTP_LINK_PREFIX.length());
			String href;
			if (j != -1) {
				href = result.substring(i, j);
			} else {
				href = result.substring(i);
			}
			String a = ("<a href=\"" + href + "\">" + href + "</a>");
			result = (result.substring(0, i) + a + ((j != -1) ? result.substring(j) : ""));
			i += a.length();
			count++;
		}
		return result;
	}
	
	private void showRoomSelection(int targetRoom) {
		if (targetRoom == ChatService.HISTORY_ROOM) {
			chatForm.hide();
		} else {
			chatForm.show();
		}
		for (int currentRoom = 0; currentRoom < ChatService.CHAT_ROOM_COUNT; currentRoom++) {
			if (currentRoom == targetRoom) {
				chatRoomButtons[currentRoom].setOpacity(100);
			} else {
				chatRoomButtons[currentRoom].setOpacity(50);
			}
		}
	}
	
	private void enableNewMessageSignal(final int targetRoom) {
		if (newMessageSignalTimers[targetRoom] == null) {
			newMessageSignalTimers[targetRoom] = new Timer() {

				private int animCycle = 0;
				
				@Override
				public void run() {
					if ((animCycle % 2) == 0) {
						chatRoomButtons[targetRoom].setOpacity(30);
					} else {
						chatRoomButtons[targetRoom].setOpacity(80);
					}
					animCycle++;
				}
			};
			newMessageSignalTimers[targetRoom].scheduleRepeating(500);
		}
	}
	
	private void disableNewMessageSignal(int targetRoom) {
		if (newMessageSignalTimers[targetRoom] != null) {
			newMessageSignalTimers[targetRoom].cancel();
			newMessageSignalTimers[targetRoom] = null;
			showRoomSelection(room);
		}
	}
}
