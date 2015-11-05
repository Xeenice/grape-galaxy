package org.grape.galaxy.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GalaxyServiceProvider {

	private static GalaxyServiceAsync service;
	
	public static GalaxyServiceAsync get() {
		if (service == null) {
			service = GWT.create(GalaxyService.class);
		}
		return service;
	}
	
	public static <T> AsyncCallback<T> createEmptyCallback() {
		return new AsyncCallback<T>() {

			@Override
			public void onSuccess(T result) {
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}

		};
	}
}
