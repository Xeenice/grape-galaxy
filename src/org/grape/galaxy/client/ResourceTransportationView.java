package org.grape.galaxy.client;

import org.grape.galaxy.model.Transportation;

public class ResourceTransportationView extends TransportationView {

	public ResourceTransportationView(SectorView parentView,
			Transportation model) {
		super(parentView, model);

		ResourceTransportation3dView resourceTransportation3dView = new ResourceTransportation3dView(
				this, model);
		setTransportation3dView(resourceTransportation3dView);
	}

}
