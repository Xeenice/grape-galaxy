package org.grape.galaxy.client.service;

import com.google.gwt.core.client.GWT;

public class ChatServiceProvider {
	
	private static ChatServiceAsync service;
	
	public static ChatServiceAsync get() {
		if (service == null) {
			service = GWT.create(ChatService.class);
		}
		return service;
	}
}
