<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div style="display: none">
	<textarea id="fx-atmosphere">
		uniform mat4 worldViewProjection;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = texture2D(diffuseSampler, v_texcoord);
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-selection">
		uniform mat4 worldViewProjection;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = texture2D(diffuseSampler, v_texcoord);
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-gatefield">
		uniform mat4 worldViewProjection;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		uniform vec4 color;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = color * texture2D(diffuseSampler, v_texcoord);
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-arrow">
		uniform mat4 worldViewProjection;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		uniform vec4 color;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = texture2D(diffuseSampler, v_texcoord);
			gl_FragColor *= color;
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-map">
		uniform mat4 worldViewProjection;
		uniform float sizeInSectors;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = sizeInSectors * texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = texture2D(diffuseSampler, v_texcoord);
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-mapcursor">
		uniform mat4 worldViewProjection;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = texture2D(diffuseSampler, v_texcoord);
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-guideline">
		uniform mat4 worldViewProjection;
		uniform float lengthScale;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
			v_texcoord.x *= lengthScale;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D diffuseSampler;
		uniform vec4 color;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_FragColor = texture2D(diffuseSampler, v_texcoord);
			gl_FragColor *= color;
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
	<textarea id="fx-shield">
		uniform mat4 worldViewProjection;
		
		attribute vec4 position;
		attribute vec2 texCoord0;
		
		varying vec2 v_texcoord;
		
		void main() {
			gl_Position = worldViewProjection * position;
			v_texcoord = texCoord0;
		}
		
		// #o3d SplitMarker
		
		uniform sampler2D ambientSampler;
		uniform sampler2D diffuseSampler;
		uniform float time;
		
		const vec3 baseColor = vec3(0.0, 0.0, 1.0);
		const vec3 intensiveColor = vec3(1.0, 1.0, 1.0);
		const float shiftScale = 0.07;
		const float rotVel = 0.01;
		const float rippleVel = 0.07;
		
		varying vec2 v_texcoord;
		
		void main() {
			float rot = rotVel * time;
			float ripple = rippleVel * time;
			float du = shiftScale * (texture2D(ambientSampler, vec2(v_texcoord.x + ripple, v_texcoord.y)).r - 0.5) + rot;
			float dv = shiftScale * (texture2D(ambientSampler, v_texcoord).g - 0.5);
			float a = texture2D(diffuseSampler, vec2(v_texcoord.x + du, v_texcoord.y + dv)).a;
			a = max(0.15, a);
			gl_FragColor = vec4(baseColor + a * (intensiveColor - baseColor), a);
		}
		
		// #o3d MatrixLoadOrder RowMajor
	</textarea>
</div>