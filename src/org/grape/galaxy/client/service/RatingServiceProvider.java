package org.grape.galaxy.client.service;

import com.google.gwt.core.client.GWT;

public class RatingServiceProvider {

	private static RatingServiceAsync service;

	public static RatingServiceAsync get() {
		if (service == null) {
			service = GWT.create(RatingService.class);
		}
		return service;
	}
}
