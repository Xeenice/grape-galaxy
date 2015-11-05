package org.grape.galaxy.model;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -7151822684141646338L;
	
	private String userId;
	private String nickname;
	
	private PlanetDetails homePlanetDetails;
	
	private UserPrefs userPrefs;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public PlanetDetails getHomePlanetDetails() {
		return homePlanetDetails;
	}

	public void setHomePlanetDetails(PlanetDetails homePlanetDetails) {
		this.homePlanetDetails = homePlanetDetails;
	}

	public UserPrefs getUserPrefs() {
		return userPrefs;
	}

	public void setUserPrefs(UserPrefs userPrefs) {
		this.userPrefs = userPrefs;
	}
}
