package org.grape.galaxy.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.grape.galaxy.client.service.GalaxyServiceProvider;
import org.grape.galaxy.model.Gate;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.PlanetDetails;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.model.SectorDetails;
import org.grape.galaxy.model.Transportation;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class SectorView extends AbstractView<Sector, GalaxyView> {

	private Sector3dView sector3dView;
	private SectorFormView sectorFormView;

	private List<PlanetView> planetsViews;
	private PlanetView activePlanetView;

	private List<GateView> gateViews;

	private Map<Long, TransportationView> transportationViews;

	private TransportationView selectedTransportationView;

	private boolean sectorDetailsUpdated = false;

	public SectorView(GalaxyView parentView, Sector model) {
		super(parentView, model);

		sector3dView = new Sector3dView(this, model);

		planetsViews = new ArrayList<PlanetView>();
		for (Planet planet : model.getPlanets()) {
			PlanetView planetView = new PlanetView(this, planet);
			planetsViews.add(planetView);
		}

		gateViews = new ArrayList<GateView>();
		for (Gate gate : model.getGates()) {
			GateView gateView = new GateView(this, gate);
			gateViews.add(gateView);
		}

		transportationViews = new HashMap<Long, TransportationView>();

		sectorFormView = new SectorFormView(this, model,
				new SelectionChangedHandler() {

					@Override
					public void onSelectionChanged(SelectionEvent event) {
						ListGridRecord listGridRecord = event
								.getSelectedRecord();

						if (listGridRecord != null) {
							changeTransportation(null);
							long planetIndex = new Long(listGridRecord
									.getAttributeAsString("index"));
							activePlanetView = getPlanetViewByIndex(planetIndex);
							activePlanetView
									.updatePlanetDetailsRelatedElements();
							sector3dView.showCursor(activePlanetView
									.getTransform());
						} else {
							activePlanetView = null;
						}

						showActiveItemForm();

						updateGoHomeButton();
					}
				}, new SelectionChangedHandler() {

					@Override
					public void onSelectionChanged(SelectionEvent event) {
						ListGridRecord listGridRecord = event
								.getSelectedRecord();

						if (listGridRecord != null) {
							changePlanet(null);
							long transId = new Long(listGridRecord
									.getAttributeAsString("id"));
							selectedTransportationView = getTransViewById(transId);
							selectedTransportationView.showGuideLine();
							selectedTransportationView
									.updateTransportationRelatedElements();
							sector3dView.showCursor(selectedTransportationView
									.getTransform());
						} else {
							if (selectedTransportationView != null) {
								selectedTransportationView.hideGuideLine();
							}
							selectedTransportationView = null;
						}

						showActiveItemForm();

						updateGoHomeButton();
					}
				});

		requestSectorDetails(true);
	}

	public PlanetView getActivePlanetView() {
		return activePlanetView;
	}

	public TransportationView getSelectedTransportationView() {
		return selectedTransportationView;
	}

	public PlanetView getPlanetViewByIndex(long planetIndex) {
		PlanetView result = null;
		for (PlanetView planetView : planetsViews) {
			if (planetIndex == planetView.getModel().getIndex()) {
				result = planetView;
				break;
			}
		}
		return result;
	}

	public TransportationView getTransViewById(long transId) {
		TransportationView result = null;
		if (transportationViews != null) {
			for (TransportationView trasnsView : transportationViews.values()) {
				if (transId == trasnsView.getModel().getId()) {
					result = trasnsView;
					break;
				}
			}
		}
		return result;
	}

	public List<PlanetView> getPlanetsViews() {
		return planetsViews;
	}

	public ListGrid getSectorPlanetsGrid() {
		if (sectorFormView == null) {
			return null;
		}
		return sectorFormView.getSectorPlanetsGrid();
	}

	public ListGrid getSectorTransportationsGrid() {
		if (sectorFormView == null) {
			return null;
		}
		return sectorFormView.getSectorTransportationsGrid();
	}

	public Map<Long, TransportationView> getTransportationViews() {
		return transportationViews;
	}

	public JavaScriptObject getPack() {
		return sector3dView.getPack();
	}

	public JavaScriptObject getRootTransform() {
		return sector3dView.getRootTransform();
	}

	public JavaScriptObject getPickRootTransform() {
		return sector3dView.getPickRootTransform();
	}

	public JavaScriptObject getParticleSystem() {
		return sector3dView.getParticleSystem();
	}

	public Planet getPlanet(String planetIndexStr) {
		if (planetIndexStr != null) {
			return model.getPlanetByIndex(Long.valueOf(planetIndexStr));
		} else {
			return null;
		}
	}

	public void changePlanet(String planetIndexStr) {
		if (getSectorPlanetsGrid() == null) {
			return;
		}

		if (planetIndexStr != null) {
			ListGridRecord record = getPlanetViewByIndex(
					Long.valueOf(planetIndexStr)).getListGridRecord();
			getSectorPlanetsGrid().selectSingleRecord(record);
			getSectorPlanetsGrid().scrollToRow(
					getSectorPlanetsGrid().getRecordIndex(record));
		} else {
			getSectorPlanetsGrid().deselectAllRecords();
		}
	}

	public void changeTransportation(String transIdStr) {
		if (getSectorTransportationsGrid() == null) {
			return;
		}

		if (transIdStr != null) {
			ListGridRecord record = getTransViewById(Long.valueOf(transIdStr))
					.getListGridRecord();
			getSectorTransportationsGrid().selectSingleRecord(record);
			getSectorTransportationsGrid().scrollToRow(
					getSectorTransportationsGrid().getRecordIndex(record));
		} else {
			getSectorTransportationsGrid().deselectAllRecords();
		}
	}

	public void updateGoHomeButton() {
		boolean disableGoHomeButton = false;

		PlanetDetails homePlanetDetails = UserContainer.get()
				.getHomePlanetDetails();
		if (homePlanetDetails == null) {
			disableGoHomeButton = true;
		} else if (activePlanetView != null) {
			Planet activePlanet = activePlanetView.getModel();
			disableGoHomeButton = homePlanetDetails.getIndex().equals(
					activePlanet.getIndex());
		}

		parentView.getGoHomeButton().setDisabled(disableGoHomeButton);
	}

	public void requestSectorDetails(final boolean block) {
		if (destroyed)
			return;

		if (block)
			ViewUtils.blockUI();

		GalaxyServiceProvider.get().getSectorDetails(model.getIndex(),
				new AsyncCallback<SectorDetails>() {

					@Override
					public void onSuccess(SectorDetails result) {
						if (destroyed)
							return;

						model.bindDetails(result);

						for (PlanetView planetView : planetsViews) {
							planetView.updatePlanetDetailsRelatedElements();
						}

						getParentView().updateActualizationState();

						if (block)
							ViewUtils.unblockUI();

						sectorDetailsUpdated = true;
					}

					@Override
					public void onFailure(Throwable caught) {
						if (block)
							ViewUtils.unblockUI();

						SC.warn("Информация о секторе не может быть получена."
								+ " Пожалуйста, убедитесь, в наличии интернет соединения."
								+ " Если интернет соединение присутствует, вероятней всего на"
								+ " сервере закончились бесплатные квоты на сегодня."
								+ " В таком случае приносим свои извинения. Приходите завтра.");

						sectorDetailsUpdated = true;
					}
				});
	}

	private void showActiveItemForm() {
		for (PlanetView planetView : planetsViews) {
			planetView.getForm().hide();
		}
		for (TransportationView transView : transportationViews.values()) {
			transView.getForm().hide();
		}
		if (activePlanetView != null) {
			activePlanetView.getForm().show();
		} else if (selectedTransportationView != null) {
			selectedTransportationView.getForm().show();
		}
	}

	public void showPathArrow(Planet sourcePlanet, Planet targetPlanet) {
		sector3dView.showPathArrow(sourcePlanet, targetPlanet);
	}

	public void showPathArrow(Planet sourcePlanet, int targetX, int targetY) {
		sector3dView.showPathArrow(sourcePlanet, targetX, targetY);
	}

	public void hidePathArrow() {
		sector3dView.hidePathArrow();
	}

	public void performAction(int x, int y) {
		sector3dView.performAction(x, y);
	}

	public boolean isGateUnderCursor(int x, int y) {
		return sector3dView.isGateUnderCursor(x, y);
	}

	public boolean isActionUnderCursor(int x, int y) {
		return sector3dView.isActionUnderCursor(x, y);
	}

	public Planet getPlanetUnderCursor(int x, int y) {
		return getPlanet(sector3dView.getPlanetIndexUnderCursor(x, y));
	}

	@Override
	protected void update(double dt, double time) {
		updateTransportations();
		super.update(dt, time);
	}

	private void updateTransportations() {
		if (selectedTransportationView != null) {
			if (sector3dView.getSelectedTransform() != selectedTransportationView
					.getTransform()) {
				selectedTransportationView.hideGuideLine();
				selectedTransportationView = null;
			}
		}

		if (!sectorDetailsUpdated || (model.getSectorDetails() == null)) {
			return;
		}

		List<Transportation> transportations = model.getSectorDetails()
				.getTransportations();
		// Удаление отображения удаленных транспортируемых объектов
		transportationCycleLabel: for (Iterator<Long> iterator = transportationViews
				.keySet().iterator(); iterator.hasNext();) {
			Long transportationId = (Long) iterator.next();
			for (Transportation transportation : transportations) {
				if (transportationId.equals(transportation.getId())) {
					continue transportationCycleLabel;
				}
			}
			transportationViews.get(transportationId).destroy();
			iterator.remove();
		}
		// Обновление положения транспортируемых объектов
		for (Transportation transportation : transportations) {
			TransportationView transportationView = transportationViews
					.get(transportation.getId());
			if (transportationView == null) {
				if (transportation.isFleetTransportation()) {
					transportationView = new FleetTransportationView(this,
							transportation);
				} else if (transportation.isResourceTransportation()) {
					transportationView = new ResourceTransportationView(this,
							transportation);
				} else {
					continue; // неизвестный тип транспортируемого объекта
				}
				transportationViews.put(transportation.getId(),
						transportationView);
			}
			transportationView.updatePosition();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		planetsViews.clear();
		activePlanetView = null;
		gateViews.clear();
	}

}
