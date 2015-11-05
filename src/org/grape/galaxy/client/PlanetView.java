package org.grape.galaxy.client;

import org.grape.galaxy.model.Planet;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class PlanetView extends AbstractView<Planet, SectorView> {

	private PlanetFormView planetFormView;
	private Planet3dView planet3dView;

	public PlanetView(SectorView parentView, Planet model) {
		super(parentView, model);

		planetFormView = new PlanetFormView(this, model);
		planet3dView = new Planet3dView(this, model);
	}

	public ListGridRecord getListGridRecord() {
		return planetFormView.getListGridRecord();
	}

	public DynamicForm getForm() {
		return planetFormView.getForm();
	}

	public void updatePlanetDetailsRelatedElements() {
		planetFormView.updatePlanetDetailsRelatedElements();
	}

	public void showPlanetDetailsDiv() {
		planetFormView.showPlanetDetailsDiv();
	}

	public void hidePlanetDetailsDiv() {
		planetFormView.hidePlanetDetailsDiv();
	}

	public JavaScriptObject getTransform() {
		return planet3dView.getTransform();
	}
}
