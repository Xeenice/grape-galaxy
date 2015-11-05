package org.grape.galaxy.client;

import java.util.EnumMap;
import java.util.Map;
import org.grape.galaxy.model.Constants;

public class SceneLayerManager {

	private static SceneLayerManager instance;
	private Map<SceneLayerType, Integer> layerCounterMap = new EnumMap<SceneLayerType, Integer>(
			SceneLayerType.class);

	private SceneLayerManager() {
	}

	public static SceneLayerManager get() {
		if (instance == null) {
			instance = new SceneLayerManager();
		}
		return instance;
	}

	public double getFreeLayer(SceneLayerType layerType) {
		switch (layerType) {
		case TRANSPORTATION:
			return getTransportationFreeLayer(getFreeLayerIndex(layerType));

		default:
			return 0.0;
		}
	}

	private int getFreeLayerIndex(SceneLayerType layerType) {
		Integer index = layerCounterMap.get(layerType);
		if (index == null) {
			index = 0;
		}
		layerCounterMap.put(layerType, index + 1);
		return index;
	}

	private double getTransportationFreeLayer(int index) {
		int rangeZ = (int) ((ViewConstants.TRANSPORTATION_MAX_Z - ViewConstants.TRANSPORTATION_MIN_Z) / Constants.TRANSPORTATION_LINEAR_SIZE);
		return ViewConstants.TRANSPORTATION_MIN_Z
				+ ((index % rangeZ) / (double) (rangeZ - 1))
				* (ViewConstants.TRANSPORTATION_MAX_Z - ViewConstants.TRANSPORTATION_MIN_Z);
	}

}
