package org.grape.galaxy.client.service;

import java.util.List;

import org.grape.galaxy.model.ChatMessage;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ChatServiceAsync {

	void getMessages(int room, long index,
			AsyncCallback<List<ChatMessage>> callback);

	void appendMessage(int room, long index, String text,
			AsyncCallback<List<ChatMessage>> callback);
}
