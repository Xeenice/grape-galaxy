package org.grape.galaxy.client;

import com.google.gwt.core.client.JavaScriptObject;

public final class ResourceManager {

	private static boolean prepared;
	private static JavaScriptObject identityCube;

	private ResourceManager() {
	}

	public static void prepareResources() {
		if (prepared) {
			return;
		}
		prepareMaterials();
		prepareObjects();
		prepared = true;
	}

	public static native JavaScriptObject getMaterial(String name) /*-{
		var material = $wnd.g_pack.getObjects(name, "o3d.Material")[0];
		if (material === undefined) {
			return null;
		}
		return material;
	}-*/;

	public static native void bindMaterialParameters(JavaScriptObject transform) /*-{
		var children = transform.children;
		for ( var i = 0; i < children.length; i++) {
			@org.grape.galaxy.client.ResourceManager::bindMaterialParameters(Lcom/google/gwt/core/client/JavaScriptObject;)(children[i]);
		}
		var shapes = transform.shapes;
		for ( var i = 0; i < shapes.length; i++) {
			var elements = shapes[i].elements;
			for ( var i = 0; i < elements.length; i++) {
				var lightWorldPos = elements[i].material
						.getParam("lightWorldPos");
				if (lightWorldPos !== undefined) {
					lightWorldPos.bind($wnd.g_globalParams
							.getParam("lightWorldPos"));
				}
			}
		}
	}-*/;

	public static native JavaScriptObject cloneTransform(JavaScriptObject pack,
			String name) /*-{
		var foundTransform = @org.grape.galaxy.client.ResourceManager::findTransform(Ljava/lang/String;)(name);
		if (foundTransform == null) {
			return null;
		}
		var clonedTransform = pack.createObject("Transform");
		clonedTransform.identity();
		var shapes = foundTransform.shapes;
		for ( var i = 0; i < shapes.length; i++) {
			clonedTransform.addShape(shapes[i]);
		}
		// TODO в дальнейшем может понадобиться реализация клонирования с учетом иерархии
		return clonedTransform;
	}-*/;

	public static native JavaScriptObject cloneTransformAndShapes(
			JavaScriptObject pack, String name) /*-{
		var foundTransform = @org.grape.galaxy.client.ResourceManager::findTransform(Ljava/lang/String;)(name);
		if (foundTransform == null) {
			return null;
		}
		var clonedTransform = pack.createObject("Transform");
		clonedTransform.identity();
		var shapes = foundTransform.shapes;
		for ( var i = 0; i < shapes.length; i++) {
			clonedTransform.addShape($wnd.o3djs.shape.duplicateShape(pack,
					shapes[i]));
		}
		// TODO в дальнейшем может понадобиться реализация клонирования с учетом иерархии
		return clonedTransform;
	}-*/;

	public static native JavaScriptObject getIdentityCube() /*-{
		var identityCube = @org.grape.galaxy.client.ResourceManager::identityCube;
		if (identityCube == null) {
			identityCube = $wnd.o3djs.primitives.createCube($wnd.g_pack,
					$wnd.o3djs.material.createConstantMaterial($wnd.g_pack,
							$wnd.g_viewInfo, [ 1, 1, 1, 1 ]), 1);
			@org.grape.galaxy.client.ResourceManager::identityCube = identityCube;
		}
		return identityCube;
	}-*/;

	public static native JavaScriptObject createParticleSystem(
			JavaScriptObject pack) /*-{
		var clockParam = $wnd.g_globalParams.getParam("clock");
		return $wnd.o3djs.particles.createParticleSystem(pack, $wnd.g_viewInfo,
				clockParam);
	}-*/;

	public static native JavaScriptObject createStaticParticleSystem(
			JavaScriptObject pack) /*-{
		var zeroClockParam = $wnd.g_globalParams.getParam("zeroClock");
		return $wnd.o3djs.particles.createParticleSystem(pack, $wnd.g_viewInfo,
				zeroClockParam);
	}-*/;

	public static native void removeParticleEmitter(JavaScriptObject emitter) /*-{
		var pack = emitter.particleSystem.pack;
		pack.removeObject(emitter.material);
		pack.removeObject(emitter.rampSampler_);
		pack.removeObject(emitter.colorSampler_);
		pack.removeObject(emitter.vertexBuffer_);
		pack.removeObject(emitter.indexBuffer_);
		pack.removeObject(emitter.streamBank_);
		pack.removeObject(emitter.shape);
		var drawElements = emitter.primitive_.drawElements;
		for ( var i = 0; i < drawElements.length; i++) {
			pack.removeObject(drawElements[i]);
		}
		pack.removeObject(emitter.primitive_);
		if (emitter.rampTexture_ != emitter.particleSystem.defaultRampTexture) {
			pack.removeObject(emitter.rampTexture_);
		}
	}-*/;

	public static native JavaScriptObject getMaterialTexture(String materialName) /*-{
		var material = $wnd.g_pack.getObjects(materialName, "o3d.Material")[0];
		if (material == null) {
			return null;
		}
		var samplerParam = material.getParam('diffuseSampler');
		if (samplerParam == null) {
			return null;
		}
		return samplerParam.value.texture;
	}-*/;

	private static native JavaScriptObject findTransform(String name) /*-{
		var galaxyTransform = $wnd.g_pack.getObjects("galaxy", "o3d.Transform")[0];
		if (galaxyTransform === undefined) {
			return null;
		}
		var foundTransform = @org.grape.galaxy.client.ResourceManager::findTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(galaxyTransform, name);
		if (foundTransform == null) {
			return null;
		}
		return foundTransform;
	}-*/;

	private static native JavaScriptObject findTransform(
			JavaScriptObject transform, String name) /*-{
		var children = transform.children;
		for ( var i = 0; i < children.length; i++) { // поиск нужного объект на текущем уровне иерархии
			var child = children[i];
			if (child.name == name) {
				return child;
			}
		}
		for ( var i = 0; i < children.length; i++) { // спуск ниже по иерархии
			var child = children[i];
			var foundTransform = @org.grape.galaxy.client.ResourceManager::findTransform(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(child, name);
			if (foundTransform != null) {
				return foundTransform;
			}
		}
		return null;
	}-*/;

	private static native void prepareMaterials() /*-{
		@org.grape.galaxy.client.ResourceManager::prepareAtmosphereMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareSelectionMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareGateFieldMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareArrowMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareMapMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareMapCursorMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareGuideLineMaterial()();
		@org.grape.galaxy.client.ResourceManager::prepareShieldMaterial()();
	}-*/;

	private static native void prepareObjects() /*-{
		@org.grape.galaxy.client.ResourceManager::preparePlanets()();
		@org.grape.galaxy.client.ResourceManager::prepareShips()();
	}-*/;

	private static native JavaScriptObject createAlphaBlendState() /*-{
		var state = $wnd.g_pack.createObject("State");
		state.getStateParam("AlphaBlendEnable").value = true;
		state.getStateParam("SourceBlendFunction").value = $wnd.g_o3d.State.BLENDFUNC_SOURCE_ALPHA;
		state.getStateParam("DestinationBlendFunction").value = $wnd.g_o3d.State.BLENDFUNC_INVERSE_SOURCE_ALPHA;
		state.getStateParam("ZWriteEnable").value = false;
		return state;
	}-*/;

	private static native JavaScriptObject setAlphaStateEffect(
			String materialName, String effectName) /*-{
		var material = $wnd.g_pack.getObjects(materialName, "o3d.Material")[0];
		if (material === undefined) {
			return null;
		}
		$wnd.g_pack.removeObject(material.state);
		$wnd.g_pack.removeObject(material.effect);

		material.drawList = $wnd.g_viewInfo.zOrderedDrawList;
		var effect = $wnd.g_pack.createObject("Effect");
		effect.loadFromFXString($doc.getElementById(effectName).value);
		material.effect = effect;
		effect.createUniformParameters(material);
		material.state = @org.grape.galaxy.client.ResourceManager::createAlphaBlendState()();
		return material;
	}-*/;

	private static native JavaScriptObject setEffect(String materialName,
			String effectName) /*-{
		var material = $wnd.g_pack.getObjects(materialName, "o3d.Material")[0];
		if (material === undefined) {
			return null;
		}
		$wnd.g_pack.removeObject(material.effect);

		var effect = $wnd.g_pack.createObject("Effect");
		effect.loadFromFXString($doc.getElementById(effectName).value);
		material.effect = effect;
		effect.createUniformParameters(material);
		return material;
	}-*/;

	private static native void prepareAtmosphereMaterial() /*-{
		@org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("atmosphere", "fx-atmosphere");
	}-*/;

	private static native void prepareSelectionMaterial() /*-{
		@org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("selection", "fx-selection");
	}-*/;

	private static native void prepareGateFieldMaterial() /*-{
		var material = @org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("gatefield", "fx-gatefield");
		material.getParam("color").value = [ 1, 1, 1, 1 ];
	}-*/;

	private static native void prepareArrowMaterial() /*-{
		var material = @org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("arrow", "fx-arrow");
		material.getParam("color").value = [ 1, 1, 1, 1 ];
	}-*/;

	private static native void prepareMapMaterial() /*-{
		var material = @org.grape.galaxy.client.ResourceManager::setEffect(Ljava/lang/String;Ljava/lang/String;)("map", "fx-map");
		material.getParam("sizeInSectors").value = 5;
	}-*/;

	private static native void prepareMapCursorMaterial() /*-{
		@org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("mapcursor", "fx-mapcursor");
	}-*/;

	private static native void prepareGuideLineMaterial() /*-{
		var material = @org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("guideline", "fx-guideline");
		material.getParam("lengthScale").value = 1;
		material.getParam("color").value = [ 1, 1, 1, 1 ];
	}-*/;

	private static native void prepareShieldMaterial() /*-{
		var material = @org.grape.galaxy.client.ResourceManager::setAlphaStateEffect(Ljava/lang/String;Ljava/lang/String;)("shield", "fx-shield");
		material.getParam("time").bind($wnd.g_globalParams.getParam("clock"));
	}-*/;

	private static native void preparePlanets() /*-{
		var planetTransform = @org.grape.galaxy.client.ResourceManager::findTransform(Ljava/lang/String;)("Planet");
		planetTransform.name = "Planet0";

		for ( var i = 1; i < 10; i++) {
			var material = $wnd.g_pack.getObjects("planet" + i, "o3d.Material")[0];

			var clonedTransform = $wnd.g_pack.createObject("Transform");
			clonedTransform.name = "Planet" + i;
			clonedTransform.copyParams(planetTransform);
			clonedTransform.parent = planetTransform.parent;
			var shapes = planetTransform.shapes;
			for ( var j = 0; j < shapes.length; j++) {
				var shape = $wnd.o3djs.shape.duplicateShape($wnd.g_pack,
						shapes[j]);
				clonedTransform.addShape(shape);
				shape.elements[0].material = material;
			}
		}
	}-*/;

	private static native void prepareShips() /*-{
		var shipMaterial = $wnd.g_pack.getObjects("ship", "o3d.Material")[0];
		var shipTransform = @org.grape.galaxy.client.ResourceManager::findTransform(Ljava/lang/String;)("Ship");

		var enemyShipMaterial = $wnd.g_pack.createObject("Material");
		enemyShipMaterial.name = "enemyShip";
		enemyShipMaterial.copyParams(shipMaterial);
		enemyShipMaterial.getParam("lightColor").value = [
				@org.grape.galaxy.client.ViewConstants::ENEMY_COL_R,
				@org.grape.galaxy.client.ViewConstants::ENEMY_COL_G,
				@org.grape.galaxy.client.ViewConstants::ENEMY_COL_B, 1.0 ];
		var enemyShipTransform = $wnd.g_pack.createObject("Transform");
		enemyShipTransform.name = "EnemyShip";
		enemyShipTransform.copyParams(shipTransform);
		enemyShipTransform.parent = shipTransform.parent;
		var shapes = shipTransform.shapes;
		for ( var j = 0; j < shapes.length; j++) {
			var shape = $wnd.o3djs.shape.duplicateShape($wnd.g_pack, shapes[j]);
			enemyShipTransform.addShape(shape);
			shape.elements[0].material = enemyShipMaterial;
		}

		shipTransform.name = "OwnShip";
		shipMaterial.name = "ownShip";
		shipMaterial.getParam("lightColor").value = [
				@org.grape.galaxy.client.ViewConstants::OWN_COL_R,
				@org.grape.galaxy.client.ViewConstants::OWN_COL_G,
				@org.grape.galaxy.client.ViewConstants::OWN_COL_B, 1.0 ];
	}-*/;
}
