package org.grape.galaxy.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FeedbackServiceAsync {

	void sendMessage(String subject, String comments, AsyncCallback<Void> callback);
}
