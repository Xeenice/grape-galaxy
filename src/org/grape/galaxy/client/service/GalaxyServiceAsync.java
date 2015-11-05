package org.grape.galaxy.client.service;

import org.grape.galaxy.model.GalaxyMapDetails;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.SectorDetails;
import org.grape.galaxy.model.TransportationDetails;
import org.grape.galaxy.model.User;
import org.grape.galaxy.model.UserPrefs;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GalaxyServiceAsync {

	void getUser(AsyncCallback<User> callback);

	void updateUserPrefs(UserPrefs userPrefs, AsyncCallback<Void> callback);

	void getSectorDetails(long sectorIndex,
			AsyncCallback<SectorDetails> callback);

	void registerHomePlanet(long planetIndex,
			AsyncCallback<PlanetDetails> callback);

	void renamePlanet(long planetIndex, String name,
			AsyncCallback<PlanetDetails> callback);

	void startUnitProduction(long planetIndex,
			AsyncCallback<PlanetDetails> callback);

	void stopUnitProduction(long planetIndex,
			AsyncCallback<PlanetDetails> callback);

	void enableDefence(long planetIndex, AsyncCallback<PlanetDetails> callback);

	void disableDefence(long planetIndex, AsyncCallback<PlanetDetails> callback);

	void startResourceTransportation(long sourcePlanetIndex,
			long targetPlanetIndex, double resourceCount,
			AsyncCallback<TransportationDetails> callback);

	void startFleetTransportation(long sourcePlanetIndex,
			long targetPlanetIndex, int fleetCount,
			AsyncCallback<TransportationDetails> callback);

	void getGalaxyMapDetails(AsyncCallback<GalaxyMapDetails> callback);

	void isEulaAccepted(AsyncCallback<Boolean> callback);

	void acceptEula(AsyncCallback<Void> callback);
}
