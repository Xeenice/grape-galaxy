package org.grape.galaxy.server;

import java.util.Map;

public interface GalaxyEventSystem<T> {

	void init(Map<String, String> params);
	
	void preProcess(T target, int missedCycleCount);
	
	void postProcess(T target, int missedCycleCount);
}
