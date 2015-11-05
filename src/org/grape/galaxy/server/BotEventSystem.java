package org.grape.galaxy.server;

import java.util.Map;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.server.utils.CacheUtils;
import org.grape.galaxy.server.utils.EventSystemUtils;

public class BotEventSystem implements GalaxyEventSystem<PlanetDetails> {

	private double productionEnableProbability = 0.9;
	private double productionDisableProbability = 0.1;
	private double defenceEnableProbability = 0.9;
	private double defenceDisableProbability = 0.1;

	@Override
	public void init(Map<String, String> params) {
		String productionEnableProbabilityStr = params
				.get("BotEventSystem.productionEnableProbability");
		if (productionEnableProbabilityStr != null) {
			productionEnableProbability = Math.max(0.0,
					Math.min(1.0, new Double(productionEnableProbabilityStr)));
		}
		String productionDisableProbabilityStr = params
				.get("BotEventSystem.productionDisableProbability");
		if (productionDisableProbabilityStr != null) {
			productionDisableProbability = Math.max(0.0,
					Math.min(1.0, new Double(productionDisableProbabilityStr)));
		}
		String defenceEnableProbabilityStr = params
				.get("BotEventSystem.defenceEnableProbability");
		if (defenceEnableProbabilityStr != null) {
			defenceEnableProbability = Math.max(0.0,
					Math.min(1.0, new Double(defenceEnableProbabilityStr)));
		}
		String defenceDisableProbabilityStr = params
				.get("BotEventSystem.defenceDisableProbability");
		if (defenceDisableProbabilityStr != null) {
			defenceDisableProbability = Math.max(0.0,
					Math.min(1.0, new Double(defenceDisableProbabilityStr)));
		}
	}

	@Override
	public void preProcess(PlanetDetails planetDetails, int missedCycleCount) {
		if (planetDetails.getOwnerId() == null) {
			boolean orbitFull = (planetDetails.getOrbitUnitCount() >= planetDetails
					.getOrbitUnitCountLimit());
			boolean defenceProfitable = ((planetDetails.getOrbitUnitCount() * (planetDetails
					.getDefenceK() - 1.0)) > (Constants.PLANET_DEFENCE_SWITCH_ON_PRICE / Constants.UNIT_PRICE));
			boolean resourceAvailable = (planetDetails.getResourceCount() > (Constants.PLANET_DEFENCE_SWITCH_ON_PRICE * 2));
			boolean resourceAvailableForActivitySwitchOn = (planetDetails
					.getResourceCount() > (Constants.PLANET_DEFENCE_SWITCH_ON_PRICE * 3.5));
			boolean changed = false;
			if (planetDetails.isDefenceEnabled()) {
				if (!resourceAvailable) {
					planetDetails.setDefenceEnabled(false);
					changed = true;
				} else {
					int evCount = EventSystemUtils.getEventCount(
							defenceDisableProbability, missedCycleCount);
					if (evCount > 0) {
						planetDetails.setDefenceEnabled(false);
						changed = true;
					}
				}
			} else if (defenceProfitable && resourceAvailableForActivitySwitchOn) {
				int evCount = EventSystemUtils.getEventCount(
						defenceEnableProbability, missedCycleCount);
				if (evCount > 0) {
					double resourceCount = (planetDetails.getResourceCount() - Constants.PLANET_DEFENCE_SWITCH_ON_PRICE);
					planetDetails.setResourceCount(resourceCount);
					resourceAvailable = (resourceCount > (Constants.PLANET_DEFENCE_SWITCH_ON_PRICE * 2));
					planetDetails.setDefenceEnabled(true);
					changed = true;
				}
			}
			if (planetDetails.isUnitProduction()) {
				if (orbitFull || !resourceAvailable) {
					planetDetails.setUnitProduction(false);
					changed = true;
				} else {
					int evCount = EventSystemUtils.getEventCount(
							productionDisableProbability, missedCycleCount);
					if (evCount > 0) {
						planetDetails.setUnitProduction(false);
						changed = true;
					}
				}
			} else if (!orbitFull && resourceAvailableForActivitySwitchOn) {
				int evCount = EventSystemUtils.getEventCount(
						productionEnableProbability, missedCycleCount);
				if (evCount > 0) {
					planetDetails.setUnitProduction(true);
					changed = true;
				}
			}
			if (changed) {
				CacheUtils.put(PlanetDetails.class, planetDetails.getIndex(),
						planetDetails);
			}
		}
	}

	@Override
	public void postProcess(PlanetDetails planetDetails, int missedCycleCount) {
	}
}
