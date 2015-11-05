package org.grape.galaxy.client;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Transportation;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.LinkItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.form.fields.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class TransportationFormView extends
		AbstractView<Transportation, TransportationView> {

	private ListGridRecord listGridRecord;
	private DynamicForm form;

	public TransportationFormView(TransportationView parentView,
			Transportation model) {
		super(parentView, model);

		listGridRecord = new ListGridRecord();
		createForm();

		updateTransportationRelatedElements();
		getSectorView().getSectorTransportationsGrid().addData(listGridRecord);
	}

	public ListGridRecord getListGridRecord() {
		return listGridRecord;
	}

	public DynamicForm getForm() {
		return form;
	}

	private TransportationView getTransportationView() {
		return getParentView();
	}

	private SectorView getSectorView() {
		return getTransportationView().getParentView();
	}

	private GalaxyView getGalaxyView() {
		return getSectorView().getParentView();
	}

	private void createForm() {
		form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setIsGroup(false);
		form.setNumCols(4);

		form.setItemHoverWidth(190);
		form.setItemHoverDelay(1000);
		form.setItemHoverOpacity(80);
		form.setItemHoverStyle("prompt");

		LinkItem sourceNameField = new LinkItem("sourceName");
		sourceNameField.setTarget("javascript");
		sourceNameField.setTitle("Планета А");
		sourceNameField.setPrompt("<p>Имя исходной планеты</p>");
		sourceNameField.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getGalaxyView().changeSector(model.getSourceCellIndex());
			}
		});
		sourceNameField.setColSpan("*");

		LinkItem targetNameField = new LinkItem("targetName");
		targetNameField.setTarget("javascript");
		targetNameField.setTitle("Планета Б");
		targetNameField.setPrompt("<p>Имя целевой планеты</p>");
		targetNameField.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				getGalaxyView().changeSector(model.getTargetCellIndex());
			}
		});
		targetNameField.setColSpan("*");

		LinkItem ownerField = new LinkItem("ownerName");
		ownerField.setTitle("Владелец");
		ownerField.setShowTitle(true);
		ownerField.setColSpan("*");
		ownerField.setPrompt("<p>Владелец переброски</p>");
		ownerField.setTarget("javascript");
		ownerField.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String ownerName = model.getOwnerName();
				if ((ownerName != null) && (ownerName.length() > 0)) {
					getGalaxyView().getChatView().sendPrivateMessage(ownerName);
				}
			}
		});

		StaticTextItem resourceCountField = new StaticTextItem("resourceCount",
				"Ресурс");
		resourceCountField.setColSpan("*");
		resourceCountField
				.setPrompt("<p>Количество перебрасываемых ресурсов</p>");
		StaticTextItem orbitUnitCountField = new StaticTextItem("unitCount",
				"Корабли");
		orbitUnitCountField.setColSpan("*");
		orbitUnitCountField
				.setPrompt("<p>Количество перебрасываемых кораблей</p>");

		form.setFields(sourceNameField, targetNameField, ownerField,
				resourceCountField, orbitUnitCountField);

		getGalaxyView().getPlanetSection().getItems()[0].addChild(form);

		form.hide();
	}

	public void updateTransportationRelatedElements() {
		listGridRecord.setAttribute("model", model);
		listGridRecord.setAttribute("id", "" + model.getId());

		Planet sourcePlanet = Galaxy.get()
				.getPlanet(model.getSourceCellIndex());
		String sourceName = null;
		if (sourcePlanet != null) {
			sourceName = sourcePlanet.getText();
		} else if (model.getOwnerId() == null) {
			sourceName = Constants.BOT_AGGRESSOR_SOURCE_PLANET_NAME;
		}
		listGridRecord.setAttribute("sourceName", sourceName);

		Planet targetPlanet = Galaxy.get()
				.getPlanet(model.getTargetCellIndex());
		listGridRecord.setAttribute("targetName",
				((targetPlanet != null) ? targetPlanet.getText() : null));

		listGridRecord.setAttribute("ownerId", model.getOwnerId());
		listGridRecord.setAttribute("ownerName", model.getOwnerName());

		listGridRecord.setAttribute("resourceCount",
				Math.round(model.getResourceCount() * 100) / 100.0);
		listGridRecord.setAttribute("unitCount", model.getUnitCount());

		if (getSectorView().getSectorTransportationsGrid() != null) {
			getSectorView().getSectorTransportationsGrid().markForRedraw();
		}
		getSectorView().updateGoHomeButton();
		if ((getSectorView().getSelectedTransportationView() == getTransportationView())
				&& (getGalaxyView().getPlanetSection() != null)) {
			String sectionTitle = ("Переброска с "
					+ listGridRecord.getAttributeAsString("sourceName")
					+ " на " + listGridRecord
					.getAttributeAsString("targetName"));
			getGalaxyView().getPlanetSection().setTitle(sectionTitle);
		}

		boolean transportationOwned = UserContainer.get().getUserId()
				.equals(model.getOwnerId());

		form.getField("ownerName").setDisabled(transportationOwned);

		form.reset();
		form.editRecord(listGridRecord);
	}

	@Override
	public void destroy() {
		super.destroy();
		getGalaxyView().getPlanetSection().getItems()[0].removeChild(form);
		if (listGridRecord != null) {
			getSectorView().getSectorTransportationsGrid().removeData(
					listGridRecord);
		}
	}

}
