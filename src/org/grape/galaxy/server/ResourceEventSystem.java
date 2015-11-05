package org.grape.galaxy.server;

import java.util.Map;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.ChatMessageHelper;
import org.grape.galaxy.server.utils.EventSystemUtils;

public class ResourceEventSystem implements GalaxyEventSystem<PlanetDetails> {

	private double resourceGrowProbabilityA = -0.49;
	private double resourceGrowProbabilityB = 0.5;

	private double resourceDeltaA = -10.0;
	private double resourceDeltaB = 10.0;

	@Override
	public void init(Map<String, String> params) {
		String resourceGrowProbabilityAStr = params
				.get("ResourceEventSystem.resourceGrowProbabilityA");
		if (resourceGrowProbabilityAStr != null) {
			resourceGrowProbabilityA = new Double(resourceGrowProbabilityAStr);
		}
		String resourceGrowProbabilityBStr = params
				.get("ResourceEventSystem.resourceGrowProbabilityB");
		if (resourceGrowProbabilityBStr != null) {
			resourceGrowProbabilityB = new Double(resourceGrowProbabilityBStr);
		}
		String resourceDeltaAStr = params
				.get("ResourceEventSystem.resourceDeltaA");
		if (resourceDeltaAStr != null) {
			resourceDeltaA = new Double(resourceDeltaAStr);
		}
		String resourceDeltaBStr = params
				.get("ResourceEventSystem.resourceDeltaB");
		if (resourceDeltaBStr != null) {
			resourceDeltaB = new Double(resourceDeltaBStr);
		}
	}

	@Override
	public void preProcess(PlanetDetails planetDetails, int missedCycleCount) {
		if (planetDetails.isDefenceEnabled()) {
			return;
		}
		
		double rNorm = (planetDetails.getResourceCount() / planetDetails
				.getResourceCountLimit());
		double p = (resourceGrowProbabilityA * rNorm + resourceGrowProbabilityB);
		int evCount = EventSystemUtils.getEventCount(p, missedCycleCount);
		if (evCount > 0) {
			double rDelta = (resourceDeltaA * rNorm + resourceDeltaB);
			if (rDelta > Constants.EPS) {
				planetDetails.setResourceCount(Math.min(
						planetDetails.getResourceCountLimit(),
						planetDetails.getResourceCount() + rDelta));
				CacheUtils.put(PlanetDetails.class, planetDetails.getIndex(),
						planetDetails);

				// XXX закрыто на оновании задачи GALAXY_RTE-43
//				ChatServiceBackend.get().recordHistoryMessage(
//						planetDetails.getOwnerName(),
//						ChatMessageHelper.get().createMessageText(
//								"Найден клад ресурсов на планете ",
//								planetDetails, ". Ресурс вырос на ", rDelta,
//								" ед."));
			}
		}
	}

	@Override
	public void postProcess(PlanetDetails planetDetails, int missedCycleCount) {
	}
}
