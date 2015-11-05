package org.grape.galaxy.client.service;

import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.FeedbackException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("feedbackService")
public interface FeedbackService extends RemoteService {

	void sendMessage(String subject, String comments) throws AuthException, FeedbackException;
}
