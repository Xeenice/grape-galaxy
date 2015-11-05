package org.grape.galaxy.client;

import org.grape.galaxy.model.Transportation;

import com.google.gwt.core.client.JavaScriptObject;

public class ResourceTransportation3dView extends Transportation3dView {

	private JavaScriptObject emitter;

	public ResourceTransportation3dView(TransportationView parentView,
			Transportation model) {
		super(parentView, model);
		
		createO3DObjectSpec();
	}

	private native void createO3DObjectSpec() /*-{
		var sectorRoot = this.@org.grape.galaxy.client.Transportation3dView::getRootTransform()();
		var particleSystem = this.@org.grape.galaxy.client.Transportation3dView::getParticleSystem()();
		var pack = this.@org.grape.galaxy.client.Transportation3dView::pack;

		var transform = pack.createObject("Transform");
		transform.parent = sectorRoot;
		this.@org.grape.galaxy.client.Transportation3dView::transform = transform;

		var emitter = particleSystem.createParticleEmitter();
		emitter.setState($wnd.o3djs.particles.ParticleStateIds.ADD);
		var c;
		if (this.@org.grape.galaxy.client.Transportation3dView::isEnemyTransportation()()) {
			c = [ @org.grape.galaxy.client.ViewConstants::ENEMY_COL_R,
					@org.grape.galaxy.client.ViewConstants::ENEMY_COL_G,
					@org.grape.galaxy.client.ViewConstants::ENEMY_COL_B ];
		} else {
			c = [ @org.grape.galaxy.client.ViewConstants::OWN_COL_R,
					@org.grape.galaxy.client.ViewConstants::OWN_COL_G,
					@org.grape.galaxy.client.ViewConstants::OWN_COL_B ];
		}
		emitter.setColorRamp([ //
		c[0], c[1], c[2], 0, // 0
		c[0], c[1], c[2], 1, // 1
		c[0], c[1], c[2], 1, // 2
		c[0], c[1], c[2], 1, // 3
		c[0], c[1], c[2], 0 ]); // 4
		var params = new $wnd.o3djs.particles.ParticleSpec();
		params.numParticles = 30;
		params.lifeTime = 3;
		params.timeRange = 3;
		params.startSize = 0.015;
		params.endSize = 0.015;
		params.position = [ -0.2, 0.0, 0.0 ];
		params.positionRange = [ 0.6, 0.15, 0.0 ];
		params.velocity = [ 0.1, 0.0, 0.0 ];
		emitter.setParameters(params);
		transform.addShape(emitter.shape);
		this.@org.grape.galaxy.client.ResourceTransportation3dView::emitter = emitter;
	}-*/;

	@Override
	public void destroy() {
		destroyO3DObjectSpec();
		super.destroy();
	}

	private native void destroyO3DObjectSpec() /*-{
		var emitter = this.@org.grape.galaxy.client.ResourceTransportation3dView::emitter;
		@org.grape.galaxy.client.ResourceManager::removeParticleEmitter(Lcom/google/gwt/core/client/JavaScriptObject;)(emitter);
	}-*/;

}
