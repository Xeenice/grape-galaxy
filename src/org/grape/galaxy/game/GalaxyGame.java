package org.grape.galaxy.game;

public class GalaxyGame {

	private String id;
	private String name;
	private String description;
	private String keywords;
	private String controls;

	public GalaxyGame() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getControls() {
		return controls;
	}

	public void setControls(String controls) {
		this.controls = controls;
	}
}
