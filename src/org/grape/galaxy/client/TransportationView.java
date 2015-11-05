package org.grape.galaxy.client;

import org.grape.galaxy.model.Transportation;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public abstract class TransportationView extends
		AbstractView<Transportation, SectorView> {

	private TransportationFormView transportationFormView;
	private Transportation3dView transportation3dView;

	public TransportationView(SectorView parentView, Transportation model) {
		super(parentView, model);

		transportationFormView = new TransportationFormView(this, model);
	}

	public ListGridRecord getListGridRecord() {
		return transportationFormView.getListGridRecord();
	}

	public DynamicForm getForm() {
		return transportationFormView.getForm();
	}

	public void updateTransportationRelatedElements() {
		transportationFormView.updateTransportationRelatedElements();
	}

	public JavaScriptObject getTransform() {
		return transportation3dView.getTransform();
	}

	public void updatePosition() {
		transportation3dView.updatePosition();
	}

	public void showGuideLine() {
		transportation3dView.showGuideLine();
	}

	public void hideGuideLine() {
		transportation3dView.hideGuideLine();
	}

	protected void setTransportation3dView(Transportation3dView transportation3dView) {
		this.transportation3dView = transportation3dView;
	}

}
