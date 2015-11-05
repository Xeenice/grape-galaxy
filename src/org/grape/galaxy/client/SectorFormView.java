package org.grape.galaxy.client;

import org.grape.galaxy.model.Sector;

import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;

public class SectorFormView extends AbstractView<Sector, SectorView> {

	private ListGrid sectorPlanetsGrid;
	private ListGrid sectorTransportationsGrid;

	public SectorFormView(SectorView parentView, Sector model,
			SelectionChangedHandler sectorChangedHandler,
			SelectionChangedHandler transportationChangedHandler) {
		super(parentView, model);

		createSectorPlanetsGrid(sectorChangedHandler);
		createSectorTransportationsGrid(transportationChangedHandler);
	}

	public ListGrid getSectorPlanetsGrid() {
		return sectorPlanetsGrid;
	}

	public ListGrid getSectorTransportationsGrid() {
		return sectorTransportationsGrid;
	}

	private SectorView getSectorView() {
		return getParentView();
	}

	private GalaxyView getGalaxyView() {
		return getSectorView().getParentView();
	}

	private void createSectorPlanetsGrid(
			SelectionChangedHandler sectorChangedHandler) {
		sectorPlanetsGrid = new ListGrid();
		sectorPlanetsGrid.setTitle("Планеты сектора #" + model.getIndex());
		sectorPlanetsGrid.setWidth100();
		sectorPlanetsGrid.setHeight100();
		sectorPlanetsGrid.setShowAllRecords(true);
		sectorPlanetsGrid.setCanResizeFields(true);
		sectorPlanetsGrid.setShowHover(false);

		ListGridField textField = new ListGridField("text", "Имя планеты");
		textField.setWidth("*");
		ListGridField ownerField = new ListGridField("ownerName", "Владелец",
				100);
		ListGridField resourceCountField = new ListGridField("resourceCount",
				"Ресурс", 40);
		resourceCountField.setType(ListGridFieldType.FLOAT);
		ListGridField orbitUnitCountField = new ListGridField("orbitUnitCount",
				"Корабли", 40);
		orbitUnitCountField.setType(ListGridFieldType.INTEGER);

		sectorPlanetsGrid.setSelectionType(SelectionStyle.SINGLE);
		sectorPlanetsGrid.addSelectionChangedHandler(sectorChangedHandler);

		sectorPlanetsGrid.setFields(textField, ownerField, resourceCountField,
				orbitUnitCountField);
		ListGridRecord[] data = new ListGridRecord[getSectorView()
				.getPlanetsViews().size()];
		int i = 0;
		for (PlanetView planetView : getSectorView().getPlanetsViews()) {
			data[i++] = planetView.getListGridRecord();
		}
		sectorPlanetsGrid.setData(data);

		getGalaxyView().getPlanetsGridSection().getItems()[0]
				.addChild(sectorPlanetsGrid);

		sectorPlanetsGrid.selectRecord(0);
	}

	private void createSectorTransportationsGrid(
			SelectionChangedHandler transportationChangedHandler) {
		sectorTransportationsGrid = new ListGrid();
		sectorTransportationsGrid.setTitle("Переброски сектора #"
				+ model.getIndex());
		sectorTransportationsGrid.setWidth100();
		sectorTransportationsGrid.setHeight100();
		sectorTransportationsGrid.setShowAllRecords(true);
		sectorTransportationsGrid.setCanResizeFields(true);
		sectorTransportationsGrid.setShowHover(false);

		ListGridField sourcePlanetField = new ListGridField("sourceName",
				"Планета А");
		sourcePlanetField.setWidth("*");
		ListGridField targetPlanetField = new ListGridField("targetName",
				"Планета Б", 70);
		ListGridField ownerField = new ListGridField("ownerName", "Владелец",
				70);
		ListGridField resourceCountField = new ListGridField("resourceCount",
				"Ресурс", 40);
		resourceCountField.setType(ListGridFieldType.FLOAT);
		ListGridField orbitUnitCountField = new ListGridField("unitCount",
				"Корабли", 40);
		orbitUnitCountField.setType(ListGridFieldType.INTEGER);

		sectorTransportationsGrid.setSelectionType(SelectionStyle.SINGLE);

		sectorTransportationsGrid.setFields(sourcePlanetField,
				targetPlanetField, ownerField, resourceCountField,
				orbitUnitCountField);

		getGalaxyView().getTransportationsGridSection().getItems()[0]
				.addChild(sectorTransportationsGrid);

		sectorTransportationsGrid
				.addSelectionChangedHandler(transportationChangedHandler);
	}

	@Override
	public void destroy() {
		super.destroy();

		getGalaxyView().getPlanetsGridSection().getItems()[0]
				.removeChild(sectorPlanetsGrid);
	}

}
