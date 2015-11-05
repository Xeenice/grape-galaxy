package org.grape.galaxy.client;

public interface GalaxySceneListener {

	public void sceneLoadSuccess();

	public void sceneLoadFailure(Throwable caught);

	public void updateScene(double dt, double time);

}
