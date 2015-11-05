package org.grape.galaxy.client;

import java.util.Map;

import org.grape.galaxy.model.Planet;
import org.grape.galaxy.model.Transportation;
import com.google.gwt.core.client.JavaScriptObject;

public class Planet3dView extends AbstractView<Planet, PlanetView> {

	private JavaScriptObject transform;
	private JavaScriptObject planetTransform;
	private JavaScriptObject explosionTransform;
	private JavaScriptObject shieldTransform;

	public Planet3dView(PlanetView parentView, Planet model) {
		super(parentView, model);

		createO3DObject();
	}

	public JavaScriptObject getTransform() {
		return transform;
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

	private JavaScriptObject getParticleSystem() {
		return getSectorView().getParticleSystem();
	}
	
	private native void createO3DObject() /*-{
		var sectorRoot = this.@org.grape.galaxy.client.Planet3dView::getRootTransform()();
		var sectorPickRoot = this.@org.grape.galaxy.client.Planet3dView::getPickRootTransform()();
		var pack = this.@org.grape.galaxy.client.Planet3dView::getPack()();

		var model = this.@org.grape.galaxy.client.Planet3dView::getModel()();
		
		var transform = pack.createObject("Transform");
		transform.parent = sectorRoot;
		var surfaceType = model.@org.grape.galaxy.model.Planet::getSurfaceType()();
		var planetTransform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Planet" + surfaceType);
		planetTransform.rotateY(Math.random() * (2 * Math.PI));
		planetTransform.parent = transform;
		var atmosphereType = model.@org.grape.galaxy.model.Planet::getAtmosphereType()();
		if (atmosphereType > 4) {
			var transformAtmosphere = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Atmosphere");
			transformAtmosphere
					.scale(
							@org.grape.galaxy.client.ViewConstants::ATMOSPHERE_RELATIVE_DIAMETER,
							@org.grape.galaxy.client.ViewConstants::ATMOSPHERE_RELATIVE_DIAMETER,
							@org.grape.galaxy.client.ViewConstants::ATMOSPHERE_RELATIVE_DIAMETER);
			transformAtmosphere.parent = transform;
		}
		var translate = [
				model.@org.grape.galaxy.model.Planet::getRelativeX()(),
				model.@org.grape.galaxy.model.Planet::getRelativeY()(),
				@org.grape.galaxy.client.ViewConstants::PLANET_Z ];
		var scale = [ 2 * model.@org.grape.galaxy.model.Planet::radius,
				2 * model.@org.grape.galaxy.model.Planet::radius,
				2 * model.@org.grape.galaxy.model.Planet::radius ];
		transform.translate(translate);
		transform.scale(scale);
		this.@org.grape.galaxy.client.Planet3dView::transform = transform;
		this.@org.grape.galaxy.client.Planet3dView::planetTransform = planetTransform;

		var aabbTransform = pack.createObject("Transform");
		aabbTransform.parent = sectorPickRoot;
		var planetIndexParam = aabbTransform.createParam("planetIndex",
				"ParamString");
		planetIndexParam.value = model.@org.grape.galaxy.model.Planet::getIndexAsString()();
		var identityCube = @org.grape.galaxy.client.ResourceManager::getIdentityCube()();
		aabbTransform.addShape(identityCube);
		aabbTransform.translate(translate);
		aabbTransform
				.scale(
						scale[0]
								* @org.grape.galaxy.client.ViewConstants::PLANET_AABB_SCALE,
						scale[1]
								* @org.grape.galaxy.client.ViewConstants::PLANET_AABB_SCALE,
						scale[2]
								* @org.grape.galaxy.client.ViewConstants::PLANET_AABB_SCALE);
	}-*/;

	private native void showO3DExplosions() /*-{
		var explosionTransform = this.@org.grape.galaxy.client.Planet3dView::explosionTransform;
		if (explosionTransform == null) {
			var particleSystem = this.@org.grape.galaxy.client.Planet3dView::getParticleSystem()();
			var pack = this.@org.grape.galaxy.client.Planet3dView::getPack()();

			var texture = @org.grape.galaxy.client.ResourceManager::getMaterialTexture(Ljava/lang/String;)("explosion");
			var explosionEmitter = particleSystem
					.createParticleEmitter(texture);
			explosionEmitter
					.setState($wnd.o3djs.particles.ParticleStateIds.ADD);
			var colorRamp = [ //
			1, 1, 0, 0, // 1
			1, 1, 0, 1, // 2
			1, 0, 0, 1, // 3
			0, 0, 0, 0 ]; // 4
			explosionEmitter.setColorRamp(colorRamp);
			var defaultParams = new $wnd.o3djs.particles.ParticleSpec();
			var numIter = 10;
			var particlesPerIter = 8;
			var iterTime = 4;
			defaultParams.numParticles = numIter * particlesPerIter;
			defaultParams.timeRange = numIter * iterTime;
			defaultParams.lifeTime = 0.5 * iterTime;
			defaultParams.startSize = 0.2;
			defaultParams.endSize = 0.6;
			defaultParams.billboard = false;

			explosionEmitter.setParameters(defaultParams,
					function(number, params) {
						var matrix = $wnd.g_math.matrix4.rotationZ(2 * Math.PI
								* Math.random());
						$wnd.g_math.matrix4.rotateX(matrix, 0.5 * Math.PI
								* (0.2 + 0.5 * Math.random()));
						var position = $wnd.g_math.matrix4.transformDirection(
								matrix, [ 0, 0.5, 0 ]);
						params.position = position;
						params.orientation = $wnd.o3djs.quaternions
								.rotationToQuaternion(matrix);
						var iter = Math.floor(number / particlesPerIter);
						params.startTime = iter * iterTime + 0.5 * iterTime
								* (number - iter * particlesPerIter)
								/ particlesPerIter;
					});

			var transform = this.@org.grape.galaxy.client.Planet3dView::transform;
			var explosionTransform = pack.createObject("Transform");
			explosionTransform.parent = transform;
			explosionTransform.addShape(explosionEmitter.shape);
			this.@org.grape.galaxy.client.Planet3dView::explosionTransform = explosionTransform;
		}
		explosionTransform.visible = true;
	}-*/;

	private native void hideO3DExplosions() /*-{
		var explosionTransform = this.@org.grape.galaxy.client.Planet3dView::explosionTransform;
		if (explosionTransform != null) {
			explosionTransform.visible = false;
		}
	}-*/;

	private void updateTransportationsViews() {
		if (getSectorView().getTransportationViews() != null) {
			for (TransportationView transportationView : getSectorView()
					.getTransportationViews().values()) {
				Transportation transportation = transportationView.getModel();
				if ((model.getIndex() == transportation.getSourceCellIndex())
						|| (model.getIndex() == transportation
								.getTargetCellIndex())) {
					transportationView.updateTransportationRelatedElements();
				}
			}
		}
	}

	@Override
	protected void update(double dt, double time) {
		super.update(dt, time);
		updateO3DObject(dt);
		updateExplosions();
		updateShield(dt);
	}

	private native void updateO3DObject(double dt) /*-{
		var planetTransform = this.@org.grape.galaxy.client.Planet3dView::planetTransform;
		if (planetTransform != null) {
			planetTransform.rotateY(@org.grape.galaxy.client.ViewConstants::PLANET_ROTATION_VELOCITY * dt);
		}
	}-*/;

	private void updateExplosions() {
		Map<Long, TransportationView> transportationViews = getSectorView()
				.getTransportationViews();
		for (TransportationView transportationView : transportationViews
				.values()) {
			if ((transportationView instanceof FleetTransportationView)
					&& ((FleetTransportationView) transportationView)
							.isAttackPlanet(model)) {
				showO3DExplosions();
				return;
			}
		}
		hideO3DExplosions();
	}

	private void updateShield(double dt) {
		if (model.isDefenceEnabled() != isO3DShieldVisible()) {
			if (model.isDefenceEnabled()) {
				showO3DShield();
			} else {
				hideO3DShield();
			}
		}
	}

	private native boolean isO3DShieldVisible() /*-{
		var shieldTransform = this.@org.grape.galaxy.client.Planet3dView::shieldTransform;
		return (shieldTransform != null) && shieldTransform.visible;
	}-*/;

	private native void showO3DShield() /*-{
		var shieldTransform = this.@org.grape.galaxy.client.Planet3dView::shieldTransform;
		if (shieldTransform == null) {
			var pack = this.@org.grape.galaxy.client.Planet3dView::getPack()();

			var transform = this.@org.grape.galaxy.client.Planet3dView::transform;
			shieldTransform = @org.grape.galaxy.client.ResourceManager::cloneTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(pack, "Shield");
			shieldTransform.rotateY(Math.random() * (2 * Math.PI));
			shieldTransform
					.scale(
							@org.grape.galaxy.client.ViewConstants::SHIELD_RELATIVE_DIAMETER,
							@org.grape.galaxy.client.ViewConstants::SHIELD_RELATIVE_DIAMETER,
							@org.grape.galaxy.client.ViewConstants::SHIELD_RELATIVE_DIAMETER);
			this.@org.grape.galaxy.client.Planet3dView::shieldTransform = shieldTransform;
			shieldTransform.parent = transform;
		}
		shieldTransform.visible = true;
	}-*/;

	private native void hideO3DShield() /*-{
		var shieldTransform = this.@org.grape.galaxy.client.Planet3dView::shieldTransform;
		if (shieldTransform != null) {
			shieldTransform.visible = false;
		}
	}-*/;

}
