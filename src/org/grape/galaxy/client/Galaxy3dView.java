package org.grape.galaxy.client;

import org.grape.galaxy.model.Galaxy;
import org.grape.galaxy.utils.Interpolator;

import com.google.gwt.core.client.JavaScriptObject;

public class Galaxy3dView extends AbstractView<Galaxy, GalaxyView> {

	private JavaScriptObject galaxyRoot;
	private JavaScriptObject mapRoot;
	private JavaScriptObject sectorsRoot;
	private JavaScriptObject starsRoot;

	private Interpolator gateFieldAplha;

	public Galaxy3dView(GalaxyView parentView, Galaxy model) {
		super(parentView, model);

		gateFieldAplha = new Interpolator(0.8, 0.2,
				ViewConstants.GATE_FIELD_ALPHA_FADE_TIME);
	}

	public JavaScriptObject getMapRoot() {
		return mapRoot;
	}

	public JavaScriptObject getSectorsRoot() {
		return sectorsRoot;
	}

	private GalaxyView getGalaxyView() {
		return getParentView();
	}

	public native void initializeScene(GalaxySceneListener listener) /*-{
		var self = this;
		var time = 0;
		
		var width = 0;
		var height = 0;

		var renderCallback = function(renderEvent) {
			if ((width != $wnd.g_client.width) || (height != $wnd.g_client.height)) {
				width = $wnd.g_client.width;
				height = $wnd.g_client.height;
				
				var aspect = width / height;
				
				var halfW, halfH;
				if (aspect >= 1) {
					halfW = 0.5 * @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE * aspect;
					halfH = 0.5 * @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE;
				} else {
					halfW = 0.5 * @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE;
					halfH = 0.5 * @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE / aspect;
				}
				halfW *= @org.grape.galaxy.client.ViewConstants::SECTOR_VIEW_RELATIVE_SIZE;
				halfH *= @org.grape.galaxy.client.ViewConstants::SECTOR_VIEW_RELATIVE_SIZE;
				$wnd.g_viewInfo.drawContext.projection = $wnd.g_math.matrix4
				.orthographic(-halfW, halfW, -halfH, halfH,
				@org.grape.galaxy.client.ViewConstants::CAMERA_NEAR,
				@org.grape.galaxy.client.ViewConstants::CAMERA_FAR);
		
				$wnd.g_viewInfo.drawContext.view = $wnd.g_math.matrix4.lookAt(
				[ 0, 0, @org.grape.galaxy.client.ViewConstants::CAMERA_EYE_Z ], // eye
				[ 0, 0, 0 ], // target
				[ 0, 1, 0 ]); // up
			}
	
			time += renderEvent.elapsedTime;
			listener.@org.grape.galaxy.client.GalaxySceneListener::updateScene(DD)(renderEvent.elapsedTime, time);
			$wnd.g_globalParams.getParam("clock").value = time;			
		}

		var initStep2 = function(clientElements) {
			@org.grape.galaxy.utils.ViewUtils::blockUI()();
			
			// Initializes global variables and libraries.
			var o3dElement = clientElements[0];
			$wnd.g_client = o3dElement.client;
			$wnd.g_o3d = o3dElement.o3d;
			$wnd.g_math = $wnd.o3djs.math;
	
			// Create a pack to manage the objects created.
			$wnd.g_pack = $wnd.g_client.createPack();
	
			// Create the render graph for a view.
			var backgroundColour = [ 0, 0, 0, 1 ];
			$wnd.g_viewInfo = $wnd.o3djs.rendergraph.createBasicView(
					$wnd.g_pack, $wnd.g_client.root,
					$wnd.g_client.renderGraphRoot, backgroundColour);
	
			$wnd.g_globalParams = $wnd.g_pack.createObject("ParamObject");
			var lightWorldPos = $wnd.g_globalParams.createParam(
					"lightWorldPos", "ParamFloat3");
			lightWorldPos.value = [ 0, 0, 1000 ];
			var clockParam = $wnd.g_globalParams.createParam("clock", "ParamFloat");
			$wnd.g_globalParams.createParam("zeroClock", "ParamFloat");
	
			var galaxyRoot = $wnd.g_pack.createObject("Transform");
			galaxyRoot.parent = $wnd.g_client.root;
			self.@org.grape.galaxy.client.Galaxy3dView::galaxyRoot = galaxyRoot;

			var mapRoot = $wnd.g_pack.createObject("Transform");
			mapRoot.parent = $wnd.g_client.root;
			mapRoot.visible = false;
			self.@org.grape.galaxy.client.Galaxy3dView::mapRoot = mapRoot;
	
			var sectorsRoot = $wnd.g_pack.createObject("Transform");
			sectorsRoot.translate(-0.5 * @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
					-0.5 * @org.grape.galaxy.model.Constants::SECTOR_LINEAR_SIZE,
					0.0);
			sectorsRoot.parent = galaxyRoot;
			self.@org.grape.galaxy.client.Galaxy3dView::sectorsRoot = sectorsRoot;
			
			var starsRoot = $wnd.g_pack.createObject("Transform");
			starsRoot.translate(0.0, 0.0,
					@org.grape.galaxy.client.ViewConstants::STARS_Z);
			starsRoot.parent = galaxyRoot;
			self.@org.grape.galaxy.client.Galaxy3dView::starsRoot = starsRoot;
			
			$wnd.g_client.setRenderCallback(renderCallback);
			$wnd.g_client.renderMode = $wnd.g_o3d.Client.RENDERMODE_ON_DEMAND;
	
			var assignEvents = function() {
				var proxy = function(func, obj) {
					return function() {
						try {
							return func.apply(obj, arguments);
						} catch (ex) { try { console.warn(ex); } catch(nothing) {} }
					};
				};

				var galaxyView = self.@org.grape.galaxy.client.Galaxy3dView::getGalaxyView()();
				$wnd.o3djs.event.addEventListener(o3dElement, "click",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onClick(Lorg/grape/galaxy/client/Event;), galaxyView));
				$wnd.o3djs.event.addEventListener(o3dElement, "dblclick",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onDoubleClick(Lorg/grape/galaxy/client/Event;), galaxyView));
				$wnd.o3djs.event.addEventListener(o3dElement, "mousedown",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onMouseDown(Lorg/grape/galaxy/client/Event;), galaxyView));
				$wnd.o3djs.event.addEventListener(o3dElement, "mousemove",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onMouseMove(Lorg/grape/galaxy/client/Event;), galaxyView));
				$wnd.o3djs.event.addEventListener(o3dElement, "mouseup",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onMouseUp(Lorg/grape/galaxy/client/Event;), galaxyView));
				$wnd.o3djs.event.addEventListener(o3dElement, "wheel",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onWheel(Lorg/grape/galaxy/client/Event;), galaxyView));
				$wnd.o3djs.event.addEventListener(o3dElement, "keyup",
				proxy(galaxyView.@org.grape.galaxy.client.GalaxyView::onKeyUp(Lorg/grape/galaxy/client/Event;), galaxyView));
			};

			var sceneLoadCallback = function(pack, parent, exception) {
				try {
					if (exception) {
						listener.@org.grape.galaxy.client.GalaxySceneListener::sceneLoadFailure(Ljava/lang/Throwable;)(exception);
						return;
					}
			
					$wnd.o3djs.pack.preparePack(pack, $wnd.g_viewInfo);
			
					@org.grape.galaxy.client.ResourceManager::prepareResources()();
					@org.grape.galaxy.client.ResourceManager::bindMaterialParameters(Lcom/google/gwt/core/client/JavaScriptObject;)(parent);

					self.@org.grape.galaxy.client.Galaxy3dView::createStars()();
					listener.@org.grape.galaxy.client.GalaxySceneListener::sceneLoadSuccess()();
					
					assignEvents();
				} catch (ex) {
					listener.@org.grape.galaxy.client.GalaxySceneListener::sceneLoadFailure(Ljava/lang/Throwable;)(ex);
				}
			};

			var galaxyTransform = $wnd.g_pack.createObject("Transform");
			galaxyTransform.name = "galaxy";
			$wnd.o3djs.scene.loadScene($wnd.g_client, $wnd.g_pack,
					galaxyTransform, "/res/galaxy/scene.json",
					sceneLoadCallback);
		}

		var onselectstart = $doc.onselectstart;
		var onmousedown = $doc.onmousedown;
		$wnd.o3djs.webgl.makeClients(initStep2);
		$doc.onselectstart = onselectstart;
		$doc.onmousedown = onmousedown;
	}-*/;

	private native void createStars() /*-{
		var particleSystem = @org.grape.galaxy.client.ResourceManager::createParticleSystem(Lcom/google/gwt/core/client/JavaScriptObject;)($wnd.g_pack);
		var starTexture = @org.grape.galaxy.client.ResourceManager::getMaterialTexture(Ljava/lang/String;)("star");
		var starsEmitter = particleSystem.createParticleEmitter(starTexture);
		starsEmitter.setState($wnd.o3djs.particles.ParticleStateIds.ADD);
		starsEmitter.setColorRamp([ //
		1, 1, 1, 0, // 1
		1, 1, 1, 0.3, // 2
		1, 1, 1, 0.7, // 3
		1, 1, 1, 0.8, // 4
		1, 1, 1, 0.7, // 5
		1, 1, 1, 0.3, // 6
		1, 1, 1, 0 ]); // 7
		var defaultParams = new $wnd.o3djs.particles.ParticleSpec();
		defaultParams.numParticles = @org.grape.galaxy.client.ViewConstants::STARS_AMOUNT;
		defaultParams.timeRange = @org.grape.galaxy.client.ViewConstants::STARS_LIFE_TIME;
		defaultParams.lifeTime = @org.grape.galaxy.client.ViewConstants::STARS_LIFE_TIME;
		starsEmitter
				.setParameters(
						defaultParams,
						function(number, params) {
							params.startSize = @org.grape.galaxy.client.ViewConstants::STARS_MIN_SIZE
									+ (@org.grape.galaxy.client.ViewConstants::STARS_MAX_SIZE - @org.grape.galaxy.client.ViewConstants::STARS_MIN_SIZE)
									* Math.random();
							params.endSize = params.startSize;
							var c = Math.random();
							var r = 1.0 + c * (1.0 - 1.0);
							var g = 1.0 + c * (0.8 - 1.0);
							var b = 1.0 + c * (0.4 - 1.0);
							params.colorMult = [ r, g, b, 1.0 ];
							params.position = [ 2 * (Math.random() - 0.5),
									2 * (Math.random() - 0.5), 0 ];
						});
		var starsRoot = this.@org.grape.galaxy.client.Galaxy3dView::starsRoot;
		starsRoot.addShape(starsEmitter.shape);
	}-*/;

	@Override
	protected void update(double dt, double time) {
		super.update(dt, time);

		updateStars(dt);
		gateFieldAplha.update(dt);
		updateGateField(gateFieldAplha.getValue());
		if (gateFieldAplha.isFinished()) {
			gateFieldAplha.invert();
			gateFieldAplha.reset();
		}
	}

	private native void updateStars(double dt) /*-{
		var starsRoot = this.@org.grape.galaxy.client.Galaxy3dView::starsRoot;
		starsRoot
				.rotateZ(@org.grape.galaxy.client.ViewConstants::STARS_ROTATION_VELOCITY
						* dt);
	}-*/;

	private native void updateGateField(double a) /*-{
		var material = @org.grape.galaxy.client.ResourceManager::getMaterial(Ljava/lang/String;)("gatefield");
		if (material == null) {
			return;
		}
		material.getParam("color").value = [
				@org.grape.galaxy.client.ViewConstants::GATE_COL_R,
				@org.grape.galaxy.client.ViewConstants::GATE_COL_G,
				@org.grape.galaxy.client.ViewConstants::GATE_COL_B, a ];
	}-*/;

	public native void render() /*-{
		try {
			$wnd.g_client.counter_manager_.tick();
			$wnd.g_client.render();
		} catch (nothing) {
		}
	}-*/;

	public native boolean isGalaxyMapVisible() /*-{
		return this.@org.grape.galaxy.client.Galaxy3dView::mapRoot.visible;
	}-*/;

	public native void showO3DGalaxyMap() /*-{
		this.@org.grape.galaxy.client.Galaxy3dView::galaxyRoot.visible = false;
		this.@org.grape.galaxy.client.Galaxy3dView::mapRoot.visible = true;
	}-*/;

	public native void hideO3DGalaxyMap() /*-{
		this.@org.grape.galaxy.client.Galaxy3dView::galaxyRoot.visible = true;
		this.@org.grape.galaxy.client.Galaxy3dView::mapRoot.visible = false;
	}-*/;

}
