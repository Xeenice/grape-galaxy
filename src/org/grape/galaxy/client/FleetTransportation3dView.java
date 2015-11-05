package org.grape.galaxy.client;

import org.grape.galaxy.model.Constants;
import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Transportation;
import com.google.gwt.core.client.JavaScriptObject;

public class FleetTransportation3dView extends Transportation3dView {

	private JavaScriptObject flameEmitter;
	private boolean prevMoved;

	public FleetTransportation3dView(TransportationView parentView,
			Transportation model) {
		super(parentView, model);
		
		createO3DObjectSpec();
	}

	private native void createO3DObjectSpec() /*-{
		var sectorRoot = this.@org.grape.galaxy.client.Transportation3dView::getRootTransform()();
		var particleSystem = this.@org.grape.galaxy.client.Transportation3dView::getParticleSystem()();
		var pack = this.@org.grape.galaxy.client.Transportation3dView::pack;

		var shipTransformName;
		if (this.@org.grape.galaxy.client.Transportation3dView::isEnemyTransportation()()) {
			shipTransformName = "EnemyShip";
		} else {
			shipTransformName = "OwnShip";
		}
		var transform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, shipTransformName);
		transform.parent = sectorRoot;
		this.@org.grape.galaxy.client.Transportation3dView::transform = transform;

		var flameEmitter = particleSystem.createParticleEmitter();
		flameEmitter.setState($wnd.o3djs.particles.ParticleStateIds.ADD);
		var colorRamp = [ //
		1, 1, 0, 1, // 1
		1, 0, 0, 1, // 2
		0, 0, 0, 1, // 3
		0, 0, 0, 0.5, // 4
		0, 0, 0, 0 ]; // 5
		flameEmitter.setColorRamp(colorRamp);
		transform.addShape(flameEmitter.shape);
		this.@org.grape.galaxy.client.FleetTransportation3dView::flameEmitter = flameEmitter;

		this.@org.grape.galaxy.client.FleetTransportation3dView::updateFlame(Z)(true);
	}-*/;

	public boolean isAttackPlanet(Planet planet) {
		if (model.getOwnerId().equals(planet.getOwnerId())
				|| (planet.getIndex() != model.getTargetCellIndex())) {
			return false;
		}
		return (Math.abs(cellToAbs(model.getTargetCellX()) - absX) < Constants.EPS)
				&& (Math.abs(cellToAbs(model.getTargetCellY()) - absY) < Constants.EPS);
	}

	@Override
	public void updatePosition() {
		super.updatePosition();
		updateFlame(velocity > (1e-10 * Constants.SECTOR_LINEAR_SIZE));
	}

	@Override
	protected double getSize() {
		double k = model.getUnitCount()
				/ (0.8 * Constants.PLANET_ORBIT_MAX_UNIT_COUNT_LIMIT);
		k = Math.max(0, Math.min(1, k));
		return Constants.FLEET_MIN_LINEAR_SIZE
				+ k
				* (Constants.FLEET_MAX_LINEAR_SIZE - Constants.FLEET_MIN_LINEAR_SIZE);
	}

	private native void updateFlame(boolean moved) /*-{
		if (moved == this.@org.grape.galaxy.client.FleetTransportation3dView::prevMoved) {
			return;
		}
		var flameEmitter = this.@org.grape.galaxy.client.FleetTransportation3dView::flameEmitter;
		var params = new $wnd.o3djs.particles.ParticleSpec();
		var scale = this.@org.grape.galaxy.client.FleetTransportation3dView::getFlameScale()();
		if (moved) {
			params.numParticles = 15;
			params.lifeTime = 2;
			params.timeRange = 2;
			params.startSize = 0.035 * scale;
			params.endSize = 0.015 * scale;
			params.position = [ -0.47, 0.0, 0.0 ];
			params.velocity = [ -0.7, 0, 0 ];
			params.acceleration = [ 0.15, 0, 0 ];
			params.spinSpeedRange = 2;
		} else {
			params.numParticles = 5;
			params.lifeTime = 2;
			params.timeRange = 2;
			params.startSize = 0.025 * scale;
			params.endSize = 0.005 * scale;
			params.position = [ -0.47, 0.0, 0.0 ];
			params.velocity = [ -0.3, 0, 0 ];
			params.acceleration = [ 0.1, 0, 0 ];
			params.spinSpeedRange = 2;
		}
		flameEmitter.setParameters(params);
		this.@org.grape.galaxy.client.FleetTransportation3dView::prevMoved = moved;
	}-*/;

	private double getFlameScale() {
		return getSize() / Constants.FLEET_MAX_LINEAR_SIZE;
	}

	@Override
	public void destroy() {
		destroyO3DObjectSpec();
		super.destroy();
	}

	private native void destroyO3DObjectSpec() /*-{
		var flameEmitter = this.@org.grape.galaxy.client.FleetTransportation3dView::flameEmitter;
		@org.grape.galaxy.client.ResourceManager::removeParticleEmitter(Lcom/google/gwt/core/client/JavaScriptObject;)(flameEmitter);
	}-*/;

}
