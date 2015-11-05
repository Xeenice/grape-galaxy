package org.grape.galaxy.client.service;

import com.google.gwt.core.client.GWT;

public class FeedbackServiceProvider {

	private static FeedbackServiceAsync service;

	public static FeedbackServiceAsync get() {
		if (service == null) {
			service = GWT.create(FeedbackService.class);
		}
		return service;
	}
}
