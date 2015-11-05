package org.grape.galaxy.client;

import org.grape.galaxy.model.Gate;
import com.google.gwt.core.client.JavaScriptObject;

public class Gate3dView extends AbstractView<Gate, GateView> {

	public Gate3dView(GateView parentView, Gate model) {
		super(parentView, model);

		createO3DObject();
	}

	private SectorView getSectorView() {
		return getParentView().getParentView();
	}

	private JavaScriptObject getRootTransform() {
		return getSectorView().getRootTransform();
	}

	private JavaScriptObject getPickRootTransform() {
		return getSectorView().getPickRootTransform();
	}

	private JavaScriptObject getPack() {
		return getSectorView().getPack();
	}

	private native void createO3DObject() /*-{
		var sectorRoot = this.@org.grape.galaxy.client.Gate3dView::getRootTransform()();
		var sectorPickRoot = this.@org.grape.galaxy.client.Gate3dView::getPickRootTransform()();
		var pack = this.@org.grape.galaxy.client.Gate3dView::getPack()();

		var model = this.@org.grape.galaxy.client.Gate3dView::getModel()();

		var transform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Gate");
		transform.parent = sectorRoot;
		var translate = [ model.@org.grape.galaxy.model.Gate::getRelX()(),
				model.@org.grape.galaxy.model.Gate::getRelY()(),
				@org.grape.galaxy.client.ViewConstants::GATE_Z ];
		var scale = [ @org.grape.galaxy.model.Constants::GATE_LINEAR_SIZE,
				@org.grape.galaxy.model.Constants::GATE_LINEAR_SIZE,
				@org.grape.galaxy.model.Constants::GATE_LINEAR_SIZE ];
		var angleZ = this.@org.grape.galaxy.client.Gate3dView::getAngle()();
		transform.translate(translate);
		transform.rotateZ(angleZ); // ориентация ворот в центр сектора
		transform
				.rotateY(-@org.grape.galaxy.client.ViewConstants::GATE_TILT_ANGLE);
		transform.scale(scale);

		var aabbTransform = pack.createObject("Transform");
		aabbTransform.parent = sectorPickRoot;
		var sectorIndexParam = aabbTransform.createParam("sectorIndex",
				"ParamString");
		sectorIndexParam.value = model.@org.grape.galaxy.model.Gate::getSectorIndexAsString()();
		var identityCube = @org.grape.galaxy.client.ResourceManager::getIdentityCube()();
		aabbTransform.addShape(identityCube);
		aabbTransform.translate(translate);
		aabbTransform.scale(scale);
	}-*/;

	private double getAngle() {
		return -(Math.PI / 2 + getModel().getAngle());
	}
}
