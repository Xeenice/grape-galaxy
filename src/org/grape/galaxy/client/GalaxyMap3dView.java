package org.grape.galaxy.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.model.GalaxyMap;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Sector;

import com.google.gwt.core.client.JavaScriptObject;

public class GalaxyMap3dView extends AbstractView<GalaxyMap, GalaxyMapView> {

	private JavaScriptObject pack;
	private JavaScriptObject pickManager;
	private JavaScriptObject transform;
	private JavaScriptObject cursorTransform;
	private JavaScriptObject aabbTransforms[][];
	private JavaScriptObject planetEmitter;

	public GalaxyMap3dView(GalaxyMapView parentView, GalaxyMap model) {
		super(parentView, model);

		aabbTransforms = new JavaScriptObject[model.getSizeInSectors()][model
				.getSizeInSectors()];
		createO3DObject();
		updateMap();
	}

	private GalaxyView getGalaxyView() {
		return getParentView().getParentView();
	}

	private native void createO3DObject() /*-{
		var pack = $wnd.g_client.createPack();
		this.@org.grape.galaxy.client.GalaxyMap3dView::pack = pack;

		var galaxyView = this.@org.grape.galaxy.client.GalaxyMap3dView::getGalaxyView()();
		var mapRoot = galaxyView.@org.grape.galaxy.client.GalaxyView::getMapRoot()();

		var pickRoot = pack.createObject("Transform");
		var pickManager = $wnd.o3djs.picking.createPickManager(pickRoot);
		this.@org.grape.galaxy.client.GalaxyMap3dView::pickManager = pickManager;

		var transform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Map");
		transform.parent = mapRoot;
		transform.scale(@org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
				@org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE, 1.0);
		this.@org.grape.galaxy.client.GalaxyMap3dView::transform = transform;

		var planetsTransform = pack.createObject("Transform");
		planetsTransform.translate(0, 0,
				@org.grape.galaxy.client.ViewConstants::MAP_PLANETS_Z);
		planetsTransform.parent = transform;

		var cursorTransform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "MapCursor");
		cursorTransform.parent = transform;
		this.@org.grape.galaxy.client.GalaxyMap3dView::cursorTransform = cursorTransform;

		var planetTexture = @org.grape.galaxy.client.ResourceManager::getMaterialTexture(Ljava/lang/String;)("mapplanet");
		var particleSystem = @org.grape.galaxy.client.ResourceManager::createStaticParticleSystem(Lcom/google/gwt/core/client/JavaScriptObject;)(pack);
		var planetEmitter = particleSystem.createParticleEmitter(planetTexture);
		planetEmitter.setState($wnd.o3djs.particles.ParticleStateIds.ADD);
		planetEmitter.setColorRamp([ 1, 1, 1, 1 ]);
		planetsTransform.addShape(planetEmitter.shape);
		this.@org.grape.galaxy.client.GalaxyMap3dView::planetEmitter = planetEmitter;

		var model = this.@org.grape.galaxy.client.GalaxyMapView::getModel()();
		var sizeInSectors = model.@org.grape.galaxy.model.GalaxyMap::getSizeInSectors()();

		var material = @org.grape.galaxy.client.ResourceManager::getMaterial(Ljava/lang/String;)("map");
		if (material != null) {
			material.getParam("sizeInSectors").value = sizeInSectors;
		}

		var sl = 1.0 / sizeInSectors;
		for ( var i = 0; i < sizeInSectors; i++) {
			for ( var j = 0; j < sizeInSectors; j++) {
				var aabbTransform = pack.createObject("Transform");
				aabbTransform.parent = pickRoot;
				var sectorIndexParam = aabbTransform.createParam("sectorIndex",
						"ParamString");
				var identityCube = @org.grape.galaxy.client.ResourceManager::getIdentityCube()();
				aabbTransform.addShape(identityCube);
				aabbTransform
						.translate(
								(sl * (i + 0.5) - 0.5)
										* @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
								(sl * (j + 0.5) - 0.5)
										* @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
								0.0);
				aabbTransform
						.scale(
								0.85
										* sl
										* @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
								0.85
										* sl
										* @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
								1.0);
				this.@org.grape.galaxy.client.GalaxyMap3dView::aabbTransforms[i][j] = aabbTransform;
			}
		}
	}-*/;

	public void updateMap() {
		Map<Long, String> planetId2OwnerIdMap = null;
		if (model.getGalaxyMapDetails() != null) {
			planetId2OwnerIdMap = model.getGalaxyMapDetails()
					.getPlanetId2OwnerIdMap();
		}

		List<Double> pos = new ArrayList<Double>();
		List<Double> size = new ArrayList<Double>();
		List<Double> col = new ArrayList<Double>();
		int numPlanets = 0;
		double sl = 1.0 / model.getSizeInSectors();
		double pl = 0.7 * sl;
		double asx = 0;
		double asy = 0;
		long nx0 = model.getStartSectorIndex()
				% Constants.GALAXY_LINEAR_SIZE_IN_SECTORS;
		long ny0 = model.getStartSectorIndex()
				/ Constants.GALAXY_LINEAR_SIZE_IN_SECTORS;

		Sector activeSector = null;
		if (getGalaxyView().getActiveSectorView() != null) {
			activeSector = getGalaxyView().getActiveSectorView().getModel();
		}
		for (int i = 0; i < model.getSizeInSectors(); i++) {
			for (int j = 0; j < model.getSizeInSectors(); j++) {
				long sectorIndex = ((ny0 + j)
						* Constants.GALAXY_LINEAR_SIZE_IN_SECTORS + (nx0 + i));
				Sector sector = Galaxy.get().getSector(sectorIndex);

				setPickSectorIndex(i, j, "" + sector.getIndex());
				if (sector == activeSector) {
					asx = (sl * (i + 0.5) - 0.5) * Constants.SECTOR_LINEAR_SIZE;
					asy = (sl * (j + 0.5) - 0.5) * Constants.SECTOR_LINEAR_SIZE;
				}
				for (Planet planet : sector.getPlanets()) {
					pos.add((sl
							* (i + 0.5)
							+ pl
							* (planet.getRelativeX()
									/ Constants.SECTOR_LINEAR_SIZE - 0.5) - 0.5)
							* Constants.SECTOR_LINEAR_SIZE);
					pos.add((sl
							* (j + 0.5)
							+ pl
							* (planet.getRelativeY()
									/ Constants.SECTOR_LINEAR_SIZE - 0.5) - 0.5)
							* Constants.SECTOR_LINEAR_SIZE);
					size.add(ViewConstants.MAP_PLANET_SCALE * pl
							* (2 * planet.getRadius()));
					String ownerId = null;
					if (planetId2OwnerIdMap != null) {
						ownerId = planetId2OwnerIdMap.get(planet.getIndex());
					}
					if (ownerId != null) {
						if (UserContainer.get().getUserId().equals(ownerId)) {
							col.add(ViewConstants.OWN_COL_R);
							col.add(ViewConstants.OWN_COL_G);
							col.add(ViewConstants.OWN_COL_B);
						} else {
							col.add(ViewConstants.ENEMY_COL_R);
							col.add(ViewConstants.ENEMY_COL_G);
							col.add(ViewConstants.ENEMY_COL_B);
						}
					} else {
						col.add(ViewConstants.NATIVE_COL_R);
						col.add(ViewConstants.NATIVE_COL_G);
						col.add(ViewConstants.NATIVE_COL_B);
					}
					numPlanets++;
				}
			}
		}

		updateO3DPlanets(numPlanets, toDoubleArray(pos), toDoubleArray(size),
				toDoubleArray(col));
		updateO3DCursor(asx, asy, sl * Constants.SECTOR_LINEAR_SIZE);
	}

	private double[] toDoubleArray(List<Double> list) {
		double array[] = new double[list.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = list.get(i).doubleValue();
		}
		return array;
	}

	private native void updateO3DPlanets(int numPlanets, double pos[],
			double size[], double col[]) /*-{
		var planetEmitter = this.@org.grape.galaxy.client.GalaxyMap3dView::planetEmitter;
		var defaultParams = new $wnd.o3djs.particles.ParticleSpec();
		defaultParams.numParticles = numPlanets;
		defaultParams.startTime = 0;
		planetEmitter.setParameters(defaultParams, function(number, params) {
			params.position = [ pos[number * 2 + 0], pos[number * 2 + 1], 0 ];
			params.startSize = size[number];
			params.colorMult = [ col[number * 3 + 0], col[number * 3 + 1],
					col[number * 3 + 2], 1.0 ];
		});
	}-*/;

	private native void updateO3DCursor(double x, double y, double size) /*-{
		var cursorTransform = this.@org.grape.galaxy.client.GalaxyMap3dView::cursorTransform;
		cursorTransform.identity();
		cursorTransform.translate(x, y,
				@org.grape.galaxy.client.ViewConstants::MAP_CURSOR_Z);
		cursorTransform.scale(size, size, 1.0);
	}-*/;

	private native void setPickSectorIndex(int i, int j, String sectorIndexStr) /*-{
		var aabbTransforms = this.@org.grape.galaxy.client.GalaxyMap3dView::aabbTransforms;
		aabbTransforms[i][j].getParam("sectorIndex").value = sectorIndexStr;
	}-*/;

	public native String getSectorIndexUnderCursor(int x, int y) /*-{
		var pickInfo = this.@org.grape.galaxy.client.GalaxyMap3dView::getPickInfo(II)(x, y);
		if (pickInfo != null) {
			var transform = pickInfo.shapeInfo.parent.transform;
			var sectorIndexParam = transform.getParam("sectorIndex");
			if (sectorIndexParam != null) {
				return sectorIndexParam.value;
			}
		}
		return null;
	}-*/;

	public native boolean isActionUnderCursor(int x, int y) /*-{
		var pickInfo = this.@org.grape.galaxy.client.GalaxyMap3dView::getPickInfo(II)(x, y);
		return (pickInfo != null);
	}-*/;

	private native JavaScriptObject getPickInfo(int x, int y) /*-{
		var worldRay = $wnd.o3djs.picking.clientPositionToWorldRay(x, y,
				$wnd.g_viewInfo.drawContext, $wnd.g_client.width,
				$wnd.g_client.height);
		var pickManager = this.@org.grape.galaxy.client.GalaxyMap3dView::pickManager;
		pickManager.update();
		return pickManager.pick(worldRay);
	}-*/;

	@Override
	public void destroy() {
		super.destroy();

		destroyO3DObject();
	}

	private native void destroyO3DObject() /*-{
		var transform = this.@org.grape.galaxy.client.GalaxyMap3dView::transform;
		transform.parent = null;

		var pack = this.@org.grape.galaxy.client.GalaxyMap3dView::pack;
		$wnd.g_client.destroyPack(pack);
	}-*/;
}
