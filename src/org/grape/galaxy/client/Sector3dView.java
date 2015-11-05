package org.grape.galaxy.client;

import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Sector;
import org.grape.galaxy.utils.Interpolator;

import com.google.gwt.core.client.JavaScriptObject;

public class Sector3dView extends AbstractView<Sector, SectorView> {

	private JavaScriptObject pack;
	private JavaScriptObject rootTransform;
	private JavaScriptObject pickRootTransform;
	private JavaScriptObject pickManager;
	private JavaScriptObject particleSystem;
	private JavaScriptObject cursorTransform;
	private JavaScriptObject selectedTransform;
	private JavaScriptObject pathArrowTransform;

	private Interpolator cursorScaleInterpolator;

	public Sector3dView(SectorView parentView, Sector model) {
		super(parentView, model);

		cursorScaleInterpolator = new Interpolator(1.0, 1.5,
				ViewConstants.CURSOR_SCALE_TIME);

		createO3DObject();
	}

	public JavaScriptObject getPack() {
		return pack;
	}

	public JavaScriptObject getRootTransform() {
		return rootTransform;
	}

	public JavaScriptObject getPickRootTransform() {
		return pickRootTransform;
	}

	public JavaScriptObject getParticleSystem() {
		return particleSystem;
	}

	public JavaScriptObject getSelectedTransform() {
		return selectedTransform;
	}

	private SectorView getSectorView() {
		return getParentView();
	}

	private GalaxyView getGalaxyView() {
		return getSectorView().getParentView();
	}

	private native void createO3DObject() /*-{
		var pack = $wnd.g_client.createPack();
		this.@org.grape.galaxy.client.Sector3dView::pack = pack;

		var galaxyView = this.@org.grape.galaxy.client.Sector3dView::getGalaxyView()();
		var sectorsRoot = galaxyView.@org.grape.galaxy.client.GalaxyView::getSectorsRoot()();

		var rootTransform = pack.createObject("Transform");
		rootTransform.parent = sectorsRoot;
		this.@org.grape.galaxy.client.Sector3dView::rootTransform = rootTransform;

		var srm = sectorsRoot.localMatrix;
		var srt = $wnd.o3djs.math.matrix4.getTranslation(srm);
		var pickRootTransform = pack.createObject("Transform");
		pickRootTransform.translate(srt[0], srt[1], srt[2]);
		this.@org.grape.galaxy.client.Sector3dView::pickRootTransform = pickRootTransform;

		var pickManager = $wnd.o3djs.picking
				.createPickManager(pickRootTransform);
		this.@org.grape.galaxy.client.Sector3dView::pickManager = pickManager;

		var particleSystem = @org.grape.galaxy.client.ResourceManager::createParticleSystem(Lcom/google/gwt/core/client/JavaScriptObject;)(pack);
		this.@org.grape.galaxy.client.Sector3dView::particleSystem = particleSystem;
	}-*/;

	public void showCursor(JavaScriptObject selectedTransform) {
		this.selectedTransform = selectedTransform;
		cursorScaleInterpolator.reset();
		showO3DCursor();
	}

	private native void showO3DCursor() /*-{
		var cursorTransform = this.@org.grape.galaxy.client.Sector3dView::cursorTransform;
		if (cursorTransform == null) {
			var pack = this.@org.grape.galaxy.client.Sector3dView::pack;
			cursorTransform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Selection");
			var rootTransform = this.@org.grape.galaxy.client.Sector3dView::rootTransform;
			cursorTransform.parent = rootTransform;
			this.@org.grape.galaxy.client.Sector3dView::cursorTransform = cursorTransform;
		}
		cursorTransform.visible = true;
	}-*/;

	public void hideCursor() {
		this.selectedTransform = null;
		hideO3DCursor();
	}

	private native void hideO3DCursor() /*-{
		var cursorTransform = this.@org.grape.galaxy.client.Sector3dView::cursorTransform;
		if (cursorTransform != null) {
			cursorTransform.visible = false;
		}
	}-*/;

	private boolean isCursorVisible() {
		return (selectedTransform != null);
	}

	public void showPathArrow(Planet sourcePlanet, Planet targetPlanet) {
		double r, g, b;
		if (UserContainer.get().getUserId().equals(targetPlanet.getOwnerId())) {
			r = ViewConstants.OWN_COL_R;
			g = ViewConstants.OWN_COL_G;
			b = ViewConstants.OWN_COL_B;
		} else {
			r = ViewConstants.ENEMY_COL_R;
			g = ViewConstants.ENEMY_COL_G;
			b = ViewConstants.ENEMY_COL_B;
		}
		showO3DPathArrow(sourcePlanet.getRelativeX(),
				sourcePlanet.getRelativeY(), targetPlanet.getRelativeX(),
				targetPlanet.getRelativeY(), r, g, b, 1.0);
	}

	public void showPathArrow(Planet sourcePlanet, int targetX, int targetY) {
		showO3DPathArrow(sourcePlanet.getRelativeX(),
				sourcePlanet.getRelativeY(), targetX, targetY,
				ViewConstants.OWN_COL_R, ViewConstants.OWN_COL_G,
				ViewConstants.OWN_COL_B, 0.5);
	}

	private native void showO3DPathArrow(double relX1, double relY1, int x2,
			int y2, double r, double g, double b, double a) /*-{
		var worldRay = $wnd.o3djs.picking.clientPositionToWorldRay(x2, y2,
				$wnd.g_viewInfo.drawContext, $wnd.g_client.width,
				$wnd.g_client.height);
		var rootTransform = this.@org.grape.galaxy.client.Sector3dView::rootTransform;
		var inverseMatrix = $wnd.g_math.inverse(rootTransform.worldMatrix);
		var pos = $wnd.g_math.matrix4.transformPoint(inverseMatrix,
				worldRay.near);
		this.@org.grape.galaxy.client.Sector3dView::showO3DPathArrow(DDDDDDDD)(relX1, relY1, pos[0], pos[1], r, g, b, a);
	}-*/;

	private native void showO3DPathArrow(double relX1, double relY1,
			double relX2, double relY2, double r, double g, double b, double a) /*-{
		var pathArrowTransform = this.@org.grape.galaxy.client.Sector3dView::pathArrowTransform;
		if (pathArrowTransform == null) {
			var pack = this.@org.grape.galaxy.client.Sector3dView::pack;
			pathArrowTransform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Arrow");
			var rootTransform = this.@org.grape.galaxy.client.Sector3dView::rootTransform;
			pathArrowTransform.parent = rootTransform;
			this.@org.grape.galaxy.client.Sector3dView::pathArrowTransform = pathArrowTransform;
		}
		pathArrowTransform.identity();
		pathArrowTransform.translate(relX1, relY1,
				@org.grape.galaxy.client.ViewConstants::PATH_ARROW_Z);
		var dy = relY2 - relY1;
		var dx = relX2 - relX1;
		var angle = Math.atan2(dy, dx);
		var l = Math.sqrt(dx * dx + dy * dy);
		var w = @org.grape.galaxy.client.ViewConstants::PATH_ARROW_WIDTH;
		pathArrowTransform.rotateZ(angle);
		pathArrowTransform.scale(l, w, 1.0);
		pathArrowTransform.visible = true;
		var material = @org.grape.galaxy.client.ResourceManager::getMaterial(Ljava/lang/String;)("arrow");
		if (material != null) {
			material.getParam("color").value = [ r, g, b, a ];
		}
	}-*/;

	public void hidePathArrow() {
		hideO3DPathArrow();
	}

	private native void hideO3DPathArrow() /*-{
		var pathArrowTransform = this.@org.grape.galaxy.client.Sector3dView::pathArrowTransform;
		if (pathArrowTransform != null) {
			pathArrowTransform.visible = false;
		}
	}-*/;

	public native JavaScriptObject getPickInfo(int x, int y) /*-{
		var worldRay = $wnd.o3djs.picking.clientPositionToWorldRay(x, y,
				$wnd.g_viewInfo.drawContext, $wnd.g_client.width,
				$wnd.g_client.height);
		var pickManager = this.@org.grape.galaxy.client.Sector3dView::pickManager;
		pickManager.update();
		return pickManager.pick(worldRay);
	}-*/;

	public native void performAction(int x, int y) /*-{
		var pickInfo = this.@org.grape.galaxy.client.Sector3dView::getPickInfo(II)(x, y);
		if (pickInfo != null) {
			var transform = pickInfo.shapeInfo.parent.transform;
			var sectorIndexParam = transform.getParam("sectorIndex");
			if (sectorIndexParam != null) {
				var galaxyView = this.@org.grape.galaxy.client.Sector3dView::getGalaxyView()();
				galaxyView.@org.grape.galaxy.client.GalaxyView::changeSector(Ljava/lang/String;)(sectorIndexParam.value);
				return;
			}
			var planetIndexParam = transform.getParam("planetIndex");
			if (planetIndexParam != null) {
				var sectorView = this.@org.grape.galaxy.client.Sector3dView::getSectorView()();
				sectorView.@org.grape.galaxy.client.SectorView::changePlanet(Ljava/lang/String;)(planetIndexParam.value);
				return;
			}
			var transportationIdParam = transform.getParam("transportationId");
			if (transportationIdParam != null) {
				var sectorView = this.@org.grape.galaxy.client.Sector3dView::getSectorView()();
				sectorView.@org.grape.galaxy.client.SectorView::changeTransportation(Ljava/lang/String;)(transportationIdParam.value);
				return;
			}
		}
	}-*/;

	public native boolean isGateUnderCursor(int x, int y) /*-{
		var pickInfo = this.@org.grape.galaxy.client.Sector3dView::getPickInfo(II)(x, y);
		if (pickInfo != null) {
			var transform = pickInfo.shapeInfo.parent.transform;
			var sectorIndexParam = transform.getParam("sectorIndex");
			if (sectorIndexParam != null) {
				return true;
			}
		}
		return false;
	}-*/;

	public native boolean isActionUnderCursor(int x, int y) /*-{
		var pickInfo = this.@org.grape.galaxy.client.Sector3dView::getPickInfo(II)(x, y);
		return (pickInfo != null);
	}-*/;

	public native String getPlanetIndexUnderCursor(int x, int y) /*-{
		var pickInfo = this.@org.grape.galaxy.client.Sector3dView::getPickInfo(II)(x, y);
		if (pickInfo != null) {
			var transform = pickInfo.shapeInfo.parent.transform;
			var planetIndexParam = transform.getParam("planetIndex");
			if (planetIndexParam != null) {
				return planetIndexParam.value;
			}
		}
		return null;
	}-*/;

	@Override
	protected void update(double dt, double time) {
		super.update(dt, time);
		updateCursor(dt);
	}

	private void updateCursor(double dt) {
		if (isCursorVisible()) {
			if (isSelectedObjectExists()) {
				cursorScaleInterpolator.update(dt);
				updateO3DCursor(cursorScaleInterpolator.getValue());
			} else {
				hideCursor();
			}
		}
	}

	private native boolean isSelectedObjectExists() /*-{
		var selectedTransform = this.@org.grape.galaxy.client.Sector3dView::selectedTransform;
		return (selectedTransform.parent != null)
	}-*/;

	private native void updateO3DCursor(double cursorScale) /*-{
		var selectedTransform = this.@org.grape.galaxy.client.Sector3dView::selectedTransform;
		var m = selectedTransform.localMatrix;
		var translation = $wnd.o3djs.math.matrix4.getTranslation(m);
		var scale = Math.sqrt(m[0][0] * m[0][0] + m[0][1] * m[0][1] + m[0][2]
				* m[0][2])
				* cursorScale;
		var cursorTransform = this.@org.grape.galaxy.client.Sector3dView::cursorTransform;
		cursorTransform.identity();
		cursorTransform.translate(translation[0], translation[1],
				translation[2]);
		cursorTransform.scale(scale, scale, scale);
	}-*/;

	@Override
	public void destroy() {
		super.destroy();

		destroyO3DObject();
	}

	private native void destroyO3DObject() /*-{
		var rootTransform = this.@org.grape.galaxy.client.Sector3dView::rootTransform;
		rootTransform.parent = null;

		var pack = this.@org.grape.galaxy.client.Sector3dView::pack;
		$wnd.g_client.destroyPack(pack);
	}-*/;

}
