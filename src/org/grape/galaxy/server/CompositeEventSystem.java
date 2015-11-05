package org.grape.galaxy.server;

import java.util.Map;

public class CompositeEventSystem<T> implements StatefulEventSystem<T> {

	private GalaxyEventSystem<T>[] systems;

	public CompositeEventSystem(GalaxyEventSystem<T>... systems) {
		this.systems = systems;
	}

	@Override
	public void init(Map<String, String> params) {
		for (GalaxyEventSystem<T> system : systems) {
			system.init(params);
		}
	}

	@Override
	public void restoreState() {
		for (GalaxyEventSystem<T> system : systems) {
			if (system instanceof StatefulEventSystem) {
				((StatefulEventSystem<T>) system).restoreState();
			}
		}
	}

	@Override
	public void storeState() {
		for (GalaxyEventSystem<T> system : systems) {
			if (system instanceof StatefulEventSystem) {
				((StatefulEventSystem<T>) system).storeState();
			}
		}
	}

	@Override
	public void preProcess(T target, int missedCycleCount) {
		for (GalaxyEventSystem<T> system : systems) {
			system.preProcess(target, missedCycleCount);
		}
	}

	@Override
	public void postProcess(T target, int missedCycleCount) {
		for (GalaxyEventSystem<T> system : systems) {
			system.postProcess(target, missedCycleCount);
		}
	}
}
