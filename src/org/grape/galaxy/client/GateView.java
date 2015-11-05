package org.grape.galaxy.client;

import org.grape.galaxy.model.Gate;

public class GateView extends AbstractView<Gate, SectorView> {

	public GateView(SectorView parentView, Gate model) {
		super(parentView, model);

		new Gate3dView(this, model);
	}
}
