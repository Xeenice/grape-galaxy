package org.grape.galaxy.client;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Transportation;

import com.google.gwt.core.client.JavaScriptObject;

public abstract class Transportation3dView extends
		AbstractView<Transportation, TransportationView> {

	protected JavaScriptObject pack;
	protected JavaScriptObject transform;
	private JavaScriptObject aabbTransform;
	private JavaScriptObject guideLineTransform;
	private JavaScriptObject guideLineMaterial;
	private double course;
	private double distance;
	protected double absX;
	protected double absY;
	protected double velocity; // [коорд.сектора/с]
	private double relZ;
	private long prevCellX;
	private long prevCellY;
	private long prevTimeMillis;

	public Transportation3dView(TransportationView parentView,
			Transportation model) {
		super(parentView, model);

		createO3DObject();
		initParams();
	}

	public JavaScriptObject getTransform() {
		return transform;
	}

	protected boolean isEnemyTransportation() {
		return !UserContainer.get().getUserId().equals(model.getOwnerId());
	}

	protected double getSize() {
		return Constants.TRANSPORTATION_LINEAR_SIZE;
	}

	private TransportationView getTransportationView() {
		return getParentView();
	}

	private SectorView getSectorView() {
		return getTransportationView().getParentView();
	}

	private JavaScriptObject getPickRootTransform() {
		return getSectorView().getPickRootTransform();
	}

	protected JavaScriptObject getRootTransform() {
		return getSectorView().getRootTransform();
	}

	protected JavaScriptObject getParticleSystem() {
		return getSectorView().getParticleSystem();
	}

	private String getTransportationIdAsString() {
		return "" + model.getId();
	}

	private native void createO3DObject() /*-{
		var sectorPickRoot = this.@org.grape.galaxy.client.Transportation3dView::getPickRootTransform()();

		var pack = $wnd.g_client.createPack();
		this.@org.grape.galaxy.client.Transportation3dView::pack = pack;

		var aabbTransform = pack.createObject("Transform");
		aabbTransform.parent = sectorPickRoot;
		var transportationIdParam = aabbTransform.createParam(
				"transportationId", "ParamString");
		transportationIdParam.value = this.@org.grape.galaxy.client.Transportation3dView::getTransportationIdAsString()();
		var identityCube = @org.grape.galaxy.client.ResourceManager::getIdentityCube()();
		aabbTransform.addShape(identityCube);
		this.@org.grape.galaxy.client.Transportation3dView::aabbTransform = aabbTransform;
	}-*/;

	public void updatePosition() {
		long currTimeMillis = System.currentTimeMillis();
		if ((prevCellX != model.getCurrentCellX())
				|| (prevCellY != model.getCurrentCellY())) {
			correctParams();
			prevCellX = model.getCurrentCellX();
			prevCellY = model.getCurrentCellY();
		} else {
			computePosition(0.001 * (currTimeMillis - prevTimeMillis));
		}
		prevTimeMillis = currTimeMillis;
	}

	public native void showGuideLine() /*-{
		var guideLineTransform = this.@org.grape.galaxy.client.Transportation3dView::guideLineTransform;
		if (guideLineTransform == null) {
			var sectorRoot = this.@org.grape.galaxy.client.Transportation3dView::getRootTransform()();
			var pack = this.@org.grape.galaxy.client.Transportation3dView::pack;

			var baseGuideLineMaterial = @org.grape.galaxy.client.ResourceManager::getMaterial(Ljava/lang/String;)("guideline");
			var guideLineMaterial = pack.createObject("Material");
			guideLineMaterial.copyParams(baseGuideLineMaterial);
			this.@org.grape.galaxy.client.Transportation3dView::guideLineMaterial = guideLineMaterial;

			guideLineTransform = @org.grape.galaxy.client.ResourceManager::cloneTransformAndShapes(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "GuideLine");
			guideLineTransform.parent = sectorRoot;
			var shapes = guideLineTransform.shapes;
			for ( var i = 0; i < shapes.length; i++) {
				shapes[i].elements[0].material = guideLineMaterial;
			}
			this.@org.grape.galaxy.client.Transportation3dView::guideLineTransform = guideLineTransform;
		}
		guideLineTransform.visible = true;
	}-*/;

	public native void hideGuideLine() /*-{
		var guideLineTransform = this.@org.grape.galaxy.client.Transportation3dView::guideLineTransform;
		if (guideLineTransform != null) {
			guideLineTransform.visible = false;
		}
	}-*/;

	protected double cellToAbs(long cell) {
		return (cell / (double) Constants.SECTOR_LINEAR_SIZE_IN_CELLS)
				* Constants.SECTOR_LINEAR_SIZE;
	}

	private void initParams() {
		prevTimeMillis = System.currentTimeMillis();
		course = Math.atan2(model.getTargetCellY() - model.getSourceCellY(),
				model.getTargetCellX() - model.getSourceCellX());
		double asx = cellToAbs(model.getSourceCellX());
		double asy = cellToAbs(model.getSourceCellY());
		double atx = cellToAbs(model.getTargetCellX());
		double aty = cellToAbs(model.getTargetCellY());
		double acx = cellToAbs(model.getCurrentCellX());
		double acy = cellToAbs(model.getCurrentCellY());
		distance = Math.sqrt((atx - asx) * (atx - asx) + (aty - asy)
				* (aty - asy));
		double currDistance = Math.sqrt((acx - asx) * (acx - asx) + (acy - asy)
				* (acy - asy));
		double k = currDistance / distance;
		absX = asx + k * (atx - asx);
		absY = asy + k * (aty - asy);
		velocity = cellToAbs(model.getVelocityInCells())
				/ Constants.ACTIVITY_PERIOD_SECONDS;
		prevCellX = model.getCurrentCellX();
		prevCellY = model.getCurrentCellY();
		relZ = SceneLayerManager.get().getFreeLayer(
				SceneLayerType.TRANSPORTATION);
	}

	private void correctParams() {
		double defaultVelocity = cellToAbs(model.getVelocityInCells())
				/ Constants.ACTIVITY_PERIOD_SECONDS;
		if ((model.getCurrentCellX() == model.getTargetCellX())
				&& (model.getCurrentCellY() == model.getTargetCellY())) {
			velocity = 100 * defaultVelocity;
		} else {
			double atx = cellToAbs(model.getTargetCellX());
			double aty = cellToAbs(model.getTargetCellY());
			double acx = cellToAbs(model.getCurrentCellX());
			double acy = cellToAbs(model.getCurrentCellY());
			double restDistance = Math.sqrt((atx - acx) * (atx - acx)
					+ (aty - acy) * (aty - acy));
			double restTime = restDistance / defaultVelocity;
			double realRestDistance = Math.sqrt((atx - absX) * (atx - absX)
					+ (aty - absY) * (aty - absY));
			velocity = realRestDistance / restTime;
		}
	}

	private void computePosition(double dt) {
		absX += Math.cos(course) * velocity * dt;
		absY += Math.sin(course) * velocity * dt;
		double asx = cellToAbs(model.getSourceCellX());
		double asy = cellToAbs(model.getSourceCellY());
		double currDistance = Math.sqrt((absX - asx) * (absX - asx)
				+ (absY - asy) * (absY - asy));
		if (currDistance > distance) {
			absX = cellToAbs(model.getTargetCellX());
			absY = cellToAbs(model.getTargetCellY());
			velocity = 0;
		}
		long sx = model.getCurrentSectorIndex()
				% Constants.GALAXY_LINEAR_SIZE_IN_SECTORS;
		long sy = model.getCurrentSectorIndex()
				/ Constants.GALAXY_LINEAR_SIZE_IN_SECTORS;
		updateO3DObject(absX - sx, absY - sy, course);
		if (isGuideLineVisible()) {
			double atx = cellToAbs(model.getTargetCellX());
			double aty = cellToAbs(model.getTargetCellY());
			updateGuideLine(absX - sx, absY - sy, atx - sx, aty - sy);
		}
	}

	private native void updateO3DObject(double relX, double relY, double course) /*-{
		var transform = this.@org.grape.galaxy.client.Transportation3dView::transform;
		var translate = [ relX, relY,
				this.@org.grape.galaxy.client.Transportation3dView::relZ ];
		var scale = this.@org.grape.galaxy.client.Transportation3dView::getSize()();
		transform.identity();
		transform.translate(translate);
		transform.rotateZ(course);
		transform.scale([ scale, scale, scale ]);

		var aabbTransform = this.@org.grape.galaxy.client.Transportation3dView::aabbTransform;
		aabbTransform.identity();
		aabbTransform.translate(translate);
		aabbTransform
				.scale(
						scale
								* @org.grape.galaxy.client.ViewConstants::TRANSPORTATION_AABB_SCALE,
						scale
								* @org.grape.galaxy.client.ViewConstants::TRANSPORTATION_AABB_SCALE,
						scale
								* @org.grape.galaxy.client.ViewConstants::TRANSPORTATION_AABB_SCALE);
	}-*/;

	private native boolean isGuideLineVisible() /*-{
		var guideLineTransform = this.@org.grape.galaxy.client.Transportation3dView::guideLineTransform;
		return (guideLineTransform != null) && guideLineTransform.visible;
	}-*/;

	private void updateGuideLine(double relX1, double relY1, double relX2,
			double relY2) {
		double dx = relX2 - relX1;
		double dy = relY2 - relY1;
		double l = Math.sqrt(dx * dx + dy * dy);
		double nx = dx / l;
		double ny = dy / l;
		double corrRelX2 = relX2;
		double corrRelY2 = relY2;
		double planes[][] = new double[][] {
				{ 0, -1, Constants.SECTOR_BOARD_MAX_COORD },
				{ 0, 1, Constants.SECTOR_BOARD_MIN_COORD },
				{ -1, 0, Constants.SECTOR_BOARD_MAX_COORD },
				{ 1, 0, Constants.SECTOR_BOARD_MIN_COORD } };
		for (int i = 0; i < planes.length; i++) {
			double p[] = planes[i];
			double d1 = p[0] * relX1 + p[1] * relY1 + p[2];
			double d2 = p[0] * relX2 + p[1] * relY2 + p[2];
			if (d1 < Constants.EPS) { // за пределами сектора
				hideGuideLine();
				return;
			}
			if (d1 * d2 >= Constants.EPS) { // отрезок пути не пересекает
											// границу сектора
				continue;
			}
			double t = -d1 / (p[0] * nx + p[1] * ny);
			double newCorrRelX2 = relX1 + nx * t;
			double newCorrRelY2 = relY1 + ny * t;
			double newCorrSqrL = (newCorrRelX2 - relX1)
					* (newCorrRelX2 - relX1) + (newCorrRelY2 - relY1)
					* (newCorrRelY2 - relY1);
			double corrSqrL = (corrRelX2 - relX1) * (corrRelX2 - relX1)
					+ (corrRelY2 - relY1) * (corrRelY2 - relY1);
			if (newCorrSqrL < corrSqrL) {
				corrRelX2 = newCorrRelX2;
				corrRelY2 = newCorrRelY2;
			}
		}
		updateO3DGuideLine(relX1, relY1, corrRelX2, corrRelY2);
	}

	private native void updateO3DGuideLine(double relX1, double relY1,
			double relX2, double relY2) /*-{
		var guideLineTransform = this.@org.grape.galaxy.client.Transportation3dView::guideLineTransform;
		var dy = relY2 - relY1;
		var dx = relX2 - relX1;
		var angle = Math.atan2(dy, dx);
		var l = Math.sqrt(dx * dx + dy * dy);
		var w = @org.grape.galaxy.client.ViewConstants::GUIDE_LINE_WIDTH;
		guideLineTransform.identity();
		guideLineTransform.translate(relX1, relY1,
				this.@org.grape.galaxy.client.Transportation3dView::relZ);
		guideLineTransform.rotateZ(angle);
		guideLineTransform.scale(l, w, 1.0);

		var color;
		if (this.@org.grape.galaxy.client.Transportation3dView::isEnemyTransportation()()) {
			color = [ @org.grape.galaxy.client.ViewConstants::ENEMY_COL_R,
					@org.grape.galaxy.client.ViewConstants::ENEMY_COL_G,
					@org.grape.galaxy.client.ViewConstants::ENEMY_COL_B, 1.0 ];
		} else {
			color = [ @org.grape.galaxy.client.ViewConstants::OWN_COL_R,
					@org.grape.galaxy.client.ViewConstants::OWN_COL_G,
					@org.grape.galaxy.client.ViewConstants::OWN_COL_B, 1.0 ];
		}
		var guideLineMaterial = this.@org.grape.galaxy.client.Transportation3dView::guideLineMaterial;
		guideLineMaterial.getParam("color").value = color;
		guideLineMaterial.getParam("lengthScale").value = l / w;
	}-*/;

	@Override
	public void destroy() {
		super.destroy();
		destroyO3DObject();
	}

	private native void destroyO3DObject() /*-{
		var transform = this.@org.grape.galaxy.client.Transportation3dView::transform;
		transform.parent = null;

		var aabbTransform = this.@org.grape.galaxy.client.Transportation3dView::aabbTransform;
		aabbTransform.parent = null;

		var pack = this.@org.grape.galaxy.client.Transportation3dView::pack;
		$wnd.g_client.destroyPack(pack);
	}-*/;

}
