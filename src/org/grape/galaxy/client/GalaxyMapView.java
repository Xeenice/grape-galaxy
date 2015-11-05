package org.grape.galaxy.client;

import org.grape.galaxy.client.service.GalaxyServiceProvider;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.GalaxyMap;
import org.grape.galaxy.model.GalaxyMapDetails;
import org.grape.galaxy.utils.ViewUtils;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GalaxyMapView extends AbstractView<GalaxyMap, GalaxyView> {

	private GalaxyMap3dView galaxyMap3dView;

	private SectorSelectionListener sectorSelectionListener;

	public GalaxyMapView(GalaxyView parentView, GalaxyMap model) {
		super(parentView, model);

		galaxyMap3dView = new GalaxyMap3dView(this, model);
	}

	public void setSectorSelectionListener(
			SectorSelectionListener sectorSelectionListener) {
		this.sectorSelectionListener = sectorSelectionListener;
	}

	public void requestGalaxyMapDetails() {
		ViewUtils.blockUI();
		GalaxyServiceProvider.get().getGalaxyMapDetails(
				new AsyncCallback<GalaxyMapDetails>() {

					@Override
					public void onSuccess(GalaxyMapDetails galaxyMapDetails) {
						model.bindDetails(galaxyMapDetails);
						galaxyMap3dView.updateMap();
						ViewUtils.unblockUI();
					}

					@Override
					public void onFailure(Throwable caught) {
						ViewUtils.unblockUI();
					}
				});
	}

	public void performAction(int x, int y) {
		String sectorIndexStr = galaxyMap3dView.getSectorIndexUnderCursor(x, y);
		if (sectorIndexStr != null) {
			if (sectorSelectionListener != null) {
				sectorSelectionListener.sectorSelected(Galaxy.get().getSector(
						Long.valueOf(sectorIndexStr)));
			}
		}
	}

	public boolean isActionUnderCursor(int x, int y) {
		return galaxyMap3dView.isActionUnderCursor(x, y);
	}

}
