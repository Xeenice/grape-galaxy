package org.grape.galaxy.client.service;

import java.util.ArrayList;
import org.grape.galaxy.model.UserRating;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RatingServiceAsync {

	void getRatingPage(int pageIndex,
			AsyncCallback<ArrayList<UserRating>> callback);

}
