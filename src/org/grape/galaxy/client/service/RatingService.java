package org.grape.galaxy.client.service;

import java.util.ArrayList;
import org.grape.galaxy.client.RatingException;
import org.grape.galaxy.model.UserRating;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ratingService")
public interface RatingService extends RemoteService {

	ArrayList<UserRating> getRatingPage(int pageIndex) throws RatingException;

}
