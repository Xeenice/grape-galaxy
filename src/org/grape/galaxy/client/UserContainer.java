package org.grape.galaxy.client;

import org.grape.galaxy.client.service.GalaxyServiceProvider;
import org.grape.galaxy.model.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserContainer {

	private static User user = null;
	
	public static void prepare(final AsyncCallback<User> callback) {
		GalaxyServiceProvider.get().getUser(new AsyncCallback<User>() {
			
			@Override
			public void onSuccess(User result) {
				user = result;
				callback.onSuccess(user);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
	}
	
	public static User get() {
		return user;
	}
}
