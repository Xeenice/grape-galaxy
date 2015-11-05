package org.grape.galaxy.client;

import org.grape.galaxy.client.service.GalaxyServiceProvider;
import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.SectorDetails;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.model.TransportationDetails;
import org.grape.galaxy.model.User;
import org.grape.galaxy.model.UserPrefs;
import org.grape.galaxy.utils.ConsoleUtils;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SectionStackSection;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
@SuppressWarnings("rawtypes")
public class GalaxyView extends AbstractView<Galaxy, AbstractView> implements
		EntryPoint {

	private GalaxyFormView galaxyFormView;
	private GlobalTransFormView globalTransFormView;
	private Galaxy3dView galaxy3dView;

	private SectorView activeSectorView;

	private Timer sectorDetailsUpdateTimer;

	private Planet transportationSourcePlanet;
	private Planet transportationTargetPlanet;

	private GalaxyMapView galaxyMapView;

	private PlanetSelectionFilter planetSelectionFilter;

	public GalaxyView() {
		super(Galaxy.get());
	}

	public SectorView getActiveSectorView() {
		return activeSectorView;
	}

	public Button getGoHomeButton() {
		return galaxyFormView.getGoHomeButton();
	}

	public Canvas getSectorCenterPanel() {
		return galaxyFormView.getSectorCenterPanel();
	}

	public SectionStackSection getPlanetsGridSection() {
		return galaxyFormView.getPlanetsGridSection();
	}

	public SectionStackSection getTransportationsGridSection() {
		return galaxyFormView.getTransportationsGridSection();
	}

	public ChatView getChatView() {
		return galaxyFormView.getChatView();
	}

	public SectionStackSection getPlanetSection() {
		return galaxyFormView.getPlanetSection();
	}

	public SectionStackSection getGlobalTransSection() {
		return galaxyFormView.getGlobalTransSection();
	}

	public JavaScriptObject getMapRoot() {
		return galaxy3dView.getMapRoot();
	}

	public JavaScriptObject getSectorsRoot() {
		return galaxy3dView.getSectorsRoot();
	}

	public GlobalTransFormView getGlobalTransFormView() {
		return globalTransFormView;
	}

	public PlanetSelectionFilter getPlanetSelectionFilter() {
		return planetSelectionFilter;
	}

	public void setPlanetSelectionFilter(
			PlanetSelectionFilter planetSelectionFilter) {
		this.planetSelectionFilter = planetSelectionFilter;
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		UserContainer.prepare(new AsyncCallback<User>() {

			@Override
			public void onSuccess(User result) {
				createView();
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.warn("Ошибка: " + caught.getLocalizedMessage());
			}
		});
	}

	public boolean refreshActiveSectorDetails() {
		if ((activeSectorView != null) && !activeSectorView.isDestroyed()) {
			activeSectorView.requestSectorDetails(true);
			resetSectorDetailsRequestTimer();
			return true;
		}
		return false;
	}

	private void createView() {
		galaxyFormView = new GalaxyFormView(this, model);
		galaxyFormView.checkEula(new EulaListener() {

			@Override
			public void accepted() {
				create3dView();
			}
		});

		globalTransFormView = new GlobalTransFormView(this, model);
	}

	private void create3dView() {
		galaxy3dView = new Galaxy3dView(this, model);
		galaxy3dView.initializeScene(new GalaxySceneListener() {

			@Override
			public void sceneLoadSuccess() {
				processSceneLoad();
			}

			@Override
			public void sceneLoadFailure(Throwable caught) {
				ConsoleUtils.error(caught);
				Window.Location.reload();
			}

			@Override
			public void updateScene(double dt, double time) {
				update(dt, time);
			}
		});
	}

	private void processSceneLoad() {
		try {
			createGalaxyMapView();

			UserPrefs userPrefs = UserContainer.get().getUserPrefs();

			Long targetPlanetIndex = null;
			String locationHash = Window.Location.getHash();
			if (!locationHash.isEmpty()) {
				targetPlanetIndex = new Long(locationHash.substring(1));
			}
			if (targetPlanetIndex != null) {
				changePlanet(targetPlanetIndex.toString());
			} else {
				changeSector(model.getSector(userPrefs.getSectorIndex()), null);
			}
			createSectorDetailsRequestTimer();
			createRenderTimer();

			onResize();

			ViewUtils.unblockUI();
		} catch (Exception ex) {
			ConsoleUtils.error(ex);
			Window.Location.reload();
		}
	}

	private void createSectorDetailsRequestTimer() {
		sectorDetailsUpdateTimer = new Timer() {

			@Override
			public void run() {
				if ((activeSectorView != null)
						&& !activeSectorView.isDestroyed()) {
					activeSectorView.requestSectorDetails(false);
				}
			}

		};
		sectorDetailsUpdateTimer
				.scheduleRepeating(Constants.ACTIVITY_PERIOD_MILLIS);
	}

	private void resetSectorDetailsRequestTimer() {
		if (sectorDetailsUpdateTimer != null) {
			try {
				sectorDetailsUpdateTimer.cancel();
				sectorDetailsUpdateTimer
						.scheduleRepeating(Constants.ACTIVITY_PERIOD_MILLIS);
			} catch (Exception nothing) {
			}
		}
	}

	private void createRenderTimer() {
		Timer renderTimer = new Timer() {

			@Override
			public void run() {
				galaxy3dView.render();
			}
		};
		renderTimer.scheduleRepeating(ViewConstants.RENDER_PERIOD_MILLIS);
	}

	public void changeSector(String sectorIndexStr) {
		changeSector(getModel().getSector(Long.valueOf(sectorIndexStr)), null);
	}

	public void changeSector(long targetPlanetIndex) {
		Planet targetPlanet = Galaxy.get().getPlanet(targetPlanetIndex);
		if (targetPlanet != null) {
			changeSector(targetPlanet.getSector(), targetPlanet);
		}
	}

	public void changeSector(Sector activeSector, Planet activePlanet) {
		if (isGalaxyMapVisible()) {
			hideGalaxyMap();
		}
		if ((activeSectorView == null)
				|| (activeSectorView.getModel().getIndex() != activeSector
						.getIndex())) {
			ViewUtils.blockUI();

			UserPrefs userPrefs = UserContainer.get().getUserPrefs();
			userPrefs.setSectorIndex(activeSector.getIndex());
			GalaxyServiceProvider.get().updateUserPrefs(userPrefs,
					GalaxyServiceProvider.<Void> createEmptyCallback());

			if ((activeSectorView != null) && !activeSectorView.isDestroyed()) {
				activeSectorView.destroy();
			}
			activeSectorView = new SectorView(this, activeSector);
			galaxyFormView.getSectorTab().setTitle(
					"Сектор #" + activeSector.getIndex());

			ViewUtils.unblockUI();
		}

		if (activePlanet != null) {
			activeSectorView.changePlanet(activePlanet.getIndexAsString());
		}

		resetSectorDetailsRequestTimer();
	}

	public void changePlanet(String planetIndexStr) {
		if (planetIndexStr == null) {
			return;
		}

		Planet planet = Galaxy.get().getPlanet(new Long(planetIndexStr));
		changeSector(planet.getSector(), planet);
	}

	public void showStatusMessage() {
		galaxyFormView.showStatusMessage();
	}

	public void hideStatusMessage() {
		galaxyFormView.hideStatusMessage();
	}

	public void setStatusMessage(String message) {
		galaxyFormView.setStatusMessage(message);
	}

	public void clearStatusMessage() {
		galaxyFormView.clearStatusMessage();
	}

	public void updateActualizationState() {
		galaxyFormView.updateActualizationState((activeSectorView != null)
				&& !activeSectorView.isDestroyed()
				&& activeSectorView.getModel().isDetailsActual());
	}

	public boolean isGalaxyMapVisible() {
		return galaxy3dView.isGalaxyMapVisible();
	}

	private void createGalaxyMapView() {
		galaxyMapView = new GalaxyMapView(this, Galaxy.get().getMap());
		galaxyMapView.setSectorSelectionListener(new SectorSelectionListener() {

			@Override
			public void sectorSelected(Sector sector) {
				hideGalaxyMap();
				if ((activeSectorView == null)
						|| (activeSectorView.isDestroyed())
						|| (activeSectorView.getModel() != sector)) {
					changeSector(sector, null);
				}
			}
		});
	}

	public void showGalaxyMap() {
		galaxyMapView.requestGalaxyMapDetails();
		galaxyFormView.getGalaxyMapButton().setTitle("Сектор");
		galaxy3dView.showO3DGalaxyMap();
		for (PlanetView planetView : activeSectorView.getPlanetsViews()) {
			planetView.hidePlanetDetailsDiv();
		}
	}

	public void hideGalaxyMap() {
		galaxyFormView.getGalaxyMapButton().setTitle("Карта");
		galaxy3dView.hideO3DGalaxyMap();
		for (PlanetView planetView : activeSectorView.getPlanetsViews()) {
			planetView.showPlanetDetailsDiv();
		}
		onResize();
	}

	void quickFleetTransportation(Planet sourcePlanet, Planet targetPlanet) {
		requestFleetTransportation(sourcePlanet, targetPlanet,
				sourcePlanet.getOrbitUnitCount(), false, null);
	}

	void requestFleetTransportation(Planet sourcePlanet, Planet targetPlanet,
			int unitCount, final boolean block, final Runnable okCallback) {
		if ((sourcePlanet == null)
				|| (targetPlanet == null)
				|| (sourcePlanet == targetPlanet)
				|| (unitCount <= 0)
				|| !UserContainer.get().getUserId()
						.equals(sourcePlanet.getOwnerId())) {
			return;
		}

		if (block)
			ViewUtils.blockUI();

		final long activeSectorIndex = activeSectorView.getModel().getIndex();
		GalaxyServiceProvider.get().startFleetTransportation(
				sourcePlanet.getIndex(), targetPlanet.getIndex(), unitCount,
				new AsyncCallback<TransportationDetails>() {

					@Override
					public void onSuccess(TransportationDetails result) {
						if (block)
							ViewUtils.unblockUI();

						Sector activeSector = activeSectorView.getModel();
						if (activeSectorIndex != activeSector.getIndex()) {
							if (okCallback != null) {
								okCallback.run();
							}
							return;
						}

						onTransportationRequestSuccess(result);

						if (okCallback != null) {
							okCallback.run();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (block)
							ViewUtils.unblockUI();

						if (activeSectorIndex != activeSectorView.getModel()
								.getIndex()) {
							return;
						}

						SC.warn("Лимит перебросок в секторе достигнут!");
					}
				});
	}

	void requestResourceTransportation(Planet sourcePlanet,
			Planet targetPlanet, double resourceCount, final boolean block,
			final Runnable okCallback) {
		if ((sourcePlanet == null)
				|| (targetPlanet == null)
				|| (sourcePlanet == targetPlanet)
				|| (resourceCount < Constants.EPS)
				|| !UserContainer.get().getUserId()
						.equals(sourcePlanet.getOwnerId())) {
			return;
		}

		if (block)
			ViewUtils.blockUI();

		final long activeSectorIndex = activeSectorView.getModel().getIndex();
		GalaxyServiceProvider.get().startResourceTransportation(
				sourcePlanet.getIndex(), targetPlanet.getIndex(),
				resourceCount, new AsyncCallback<TransportationDetails>() {

					@Override
					public void onSuccess(TransportationDetails result) {
						if (block)
							ViewUtils.unblockUI();

						Sector activeSector = activeSectorView.getModel();
						if (activeSectorIndex != activeSector.getIndex()) {
							if (okCallback != null) {
								okCallback.run();
							}
							return;
						}

						onTransportationRequestSuccess(result);

						if (okCallback != null) {
							okCallback.run();
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (block)
							ViewUtils.unblockUI();

						if (activeSectorIndex != activeSectorView.getModel()
								.getIndex()) {
							return;
						}

						SC.warn("Лимит перебросок в секторе достигнут!");
					}
				});
	}

	private void onTransportationRequestSuccess(
			TransportationDetails transportationDetails) {
		Long sourcePlanetIndex = transportationDetails.getSourcePlanetDetails()
				.getIndex();
		PlanetView sourcePlanetView = activeSectorView
				.getPlanetViewByIndex(sourcePlanetIndex);
		if (sourcePlanetView != null) {
			sourcePlanetView.getModel().bindDetails(
					transportationDetails.getSourcePlanetDetails());
			sourcePlanetView.updatePlanetDetailsRelatedElements();
		}

		Long targetPlanetIndex = transportationDetails.getTargetPlanetDetails()
				.getIndex();
		PlanetView targetPlanetView = activeSectorView
				.getPlanetViewByIndex(targetPlanetIndex);
		if (targetPlanetView != null) {
			targetPlanetView.getModel().bindDetails(
					transportationDetails.getTargetPlanetDetails());
			targetPlanetView.updatePlanetDetailsRelatedElements();
		}

		Transportation transportation = transportationDetails
				.getTransportation();
		Sector activeSector = activeSectorView.getModel();
		SectorDetails sectorDetails = activeSector.getSectorDetails();
		if ((activeSector.getIndex() == transportation.getCurrentSectorIndex())
				&& (sectorDetails != null)
				&& !sectorDetails.getTransportations().contains(transportation)) {
			sectorDetails.getTransportations().add(transportation);
		}
	}

	private void updateCursor(int x, int y) {
		Cursor cursor = Cursor.DEFAULT;
		if (transportationSourcePlanet == null) {
			if (activeSectorView.isActionUnderCursor(x, y)) {
				cursor = Cursor.HAND;
			}
		} else {
			transportationTargetPlanet = activeSectorView.getPlanetUnderCursor(
					x, y);
			if (transportationTargetPlanet == transportationSourcePlanet) {
				transportationTargetPlanet = null;
			}
			if ((transportationSourcePlanet.getPlanetDetails() == null)
					|| (transportationSourcePlanet.getOrbitUnitCount() == 0)
					|| !activeSectorView.getModel().isDetailsActual()) {
				cursor = Cursor.NOT_ALLOWED;
			} else {
				cursor = Cursor.CROSSHAIR;
			}
		}
		galaxyFormView.getSectorCenterPanel().setCursor(cursor);
	}

	public void onClick(Event e) {
	}

	public void onDoubleClick(Event e) {
	}

	public void onMouseDown(Event e) {
		if (isGalaxyMapVisible() || (planetSelectionFilter != null)) {
			return;
		}
		if ((e != null) && e.isLeftButton() && (activeSectorView != null)
				&& !activeSectorView.isDestroyed()) {
			transportationSourcePlanet = activeSectorView.getPlanetUnderCursor(
					e.getX(), e.getY());
			if ((transportationSourcePlanet != null)
					&& !UserContainer.get().getUserId()
							.equals(transportationSourcePlanet.getOwnerId())) {
				transportationSourcePlanet = null;
			}
		}
	}

	public void onMouseMove(Event e) {
		if (e != null) {
			if (isGalaxyMapVisible()) {
				if (galaxyMapView.isActionUnderCursor(e.getX(), e.getY())) {
					galaxyFormView.getSectorCenterPanel()
							.setCursor(Cursor.HAND);
				} else {
					galaxyFormView.getSectorCenterPanel().setCursor(
							Cursor.DEFAULT);
				}
			} else if ((activeSectorView != null)
					&& !activeSectorView.isDestroyed()) {
				if ((planetSelectionFilter != null)
						&& !activeSectorView.isGateUnderCursor(e.getX(),
								e.getY())) {
					Planet planet = activeSectorView.getPlanetUnderCursor(
							e.getX(), e.getY());
					if (planet != null) {
						if (planetSelectionFilter.canSelect(planet)) {
							galaxyFormView.getSectorCenterPanel().setCursor(
									Cursor.HAND);
						} else {
							galaxyFormView.getSectorCenterPanel().setCursor(
									Cursor.NOT_ALLOWED);
						}
					} else {
						if (activeSectorView.isActionUnderCursor(e.getX(),
								e.getY())) {
							galaxyFormView.getSectorCenterPanel().setCursor(
									Cursor.NOT_ALLOWED);
						} else {
							galaxyFormView.getSectorCenterPanel().setCursor(
									Cursor.DEFAULT);
						}
					}
				} else {
					updateCursor(e.getX(), e.getY());
					if (transportationSourcePlanet != null) {
						if (transportationTargetPlanet != null) {
							activeSectorView.showPathArrow(
									transportationSourcePlanet,
									transportationTargetPlanet);
							if (activeSectorView.getActivePlanetView()
									.getModel() != transportationTargetPlanet) {
								activeSectorView
										.changePlanet(""
												+ transportationTargetPlanet
														.getIndex());
							}
						} else {
							activeSectorView.showPathArrow(
									transportationSourcePlanet, e.getX(),
									e.getY());
						}
					}
				}
			}
		}
	}

	public void onMouseUp(Event e) {
		if ((e != null) && e.isLeftButton()) {
			if (isGalaxyMapVisible()) {
				galaxyMapView.performAction(e.getX(), e.getY());
			} else {
				if ((planetSelectionFilter != null)
						&& !activeSectorView.isGateUnderCursor(e.getX(),
								e.getY())) {
					Planet planet = activeSectorView.getPlanetUnderCursor(
							e.getX(), e.getY());
					if (planet != null) {
						if (planetSelectionFilter.canSelect(planet)) {
							planetSelectionFilter.onSelect(planet);
							planetSelectionFilter = null;
							activeSectorView.performAction(e.getX(), e.getY());
						}
					}
				} else {
					if ((activeSectorView != null)
							&& !activeSectorView.isDestroyed()) {
						if ((transportationSourcePlanet != null)
								&& (transportationTargetPlanet != null)
								&& (transportationSourcePlanet
										.getPlanetDetails() != null)
								&& (transportationSourcePlanet
										.getOrbitUnitCount() > 0)
								&& activeSectorView.getModel()
										.isDetailsActual()) {
							quickFleetTransportation(
									transportationSourcePlanet,
									transportationTargetPlanet);
						} else {
							activeSectorView.performAction(e.getX(), e.getY());
						}
						activeSectorView.hidePathArrow();
					}
					transportationSourcePlanet = null;
					transportationTargetPlanet = null;
					updateCursor(e.getX(), e.getY());
				}
			}
		}
	}

	public void onWheel(Event e) {
	}

	public void onKeyUp(Event e) {
		if (e.getKeyCode() == 27) {
			if (globalTransFormView != null) {
				globalTransFormView.reset();
			}
		}
	}

}
