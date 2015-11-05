package org.grape.galaxy.server;

public interface StatefulEventSystem<T> extends GalaxyEventSystem<T> {

	void restoreState();
	
	void storeState();
}
