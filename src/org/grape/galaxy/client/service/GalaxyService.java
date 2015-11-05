package org.grape.galaxy.client.service;

import org.grape.galaxy.client.ActivityException;
import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.client.HomePlanetRegistrationException;
import org.grape.galaxy.client.PlanetRenameException;
import org.grape.galaxy.model.GalaxyMapDetails;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.SectorDetails;
import org.grape.galaxy.model.TransportationDetails;
import org.grape.galaxy.model.User;
import org.grape.galaxy.model.UserPrefs;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("galaxyService")
public interface GalaxyService extends RemoteService {

	User getUser() throws AuthException;

	void updateUserPrefs(UserPrefs userPrefs) throws AuthException;

	SectorDetails getSectorDetails(long sectorIndex) throws AuthException;

	PlanetDetails registerHomePlanet(long planetIndex) throws AuthException,
			HomePlanetRegistrationException;

	PlanetDetails renamePlanet(long planetIndex, String name)
			throws AuthException, PlanetRenameException;

	PlanetDetails startUnitProduction(long planetIndex) throws AuthException,
			ActivityException;

	PlanetDetails stopUnitProduction(long planetIndex) throws AuthException,
			ActivityException;

	PlanetDetails enableDefence(long planetIndex) throws AuthException,
			ActivityException;

	PlanetDetails disableDefence(long planetIndex) throws AuthException,
			ActivityException;

	TransportationDetails startResourceTransportation(long sourcePlanetIndex,
			long targetPlanetIndex, double resourceCount) throws AuthException,
			ActivityException;

	TransportationDetails startFleetTransportation(long sourcePlanetIndex,
			long targetPlanetIndex, int fleetCount) throws AuthException,
			ActivityException;

	GalaxyMapDetails getGalaxyMapDetails() throws AuthException;

	boolean isEulaAccepted() throws AuthException;
	
	void acceptEula() throws AuthException;
}
