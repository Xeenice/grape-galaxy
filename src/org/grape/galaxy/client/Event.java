package org.grape.galaxy.client;

import com.google.gwt.core.client.JavaScriptObject;

public class Event extends JavaScriptObject {
	protected Event() {
	}
	
	public final native int getKeyCode() /*-{
		return this.keyCode;
	}-*/;

	/**
	 * The x-coordinate in pixels from the left side of the display region.
	 */
	public final native int getX() /*-{
		return this.x;
	}-*/;

	/**
	 * The y-coordinate in pixels from the top of the display region.
	 */
	public final native int getY() /*-{
		return this.y;
	}-*/;

	/**
	 * The horizontal scroll offset for wheel events, in arbitrary units.
	 * Positive values mean right; negative mean left.
	 */
	public final native int getDeltaX() /*-{
		return this.deltaX;
	}-*/;

	/**
	 * The vertical scroll offset for wheel events, in arbitrary units. Positive
	 * values mean up or away from the user; negative mean down or toward the
	 * user.
	 */
	public final native int getDeltaY() /*-{
		return this.deltaY;
	}-*/;

	/**
	 * Left mouse button caused the event, in the case of mousedown, mouseup,
	 * click, and dblclick events.
	 */
	public final native boolean isLeftButton() /*-{
		return (this.button == $wnd.g_o3d.Event.BUTTON_LEFT);
	}-*/;

	/**
	 * Right mouse button caused the event, in the case of mousedown, mouseup,
	 * click, and dblclick events.
	 */
	public final native boolean isRightButton() /*-{
		return (this.button == $wnd.g_o3d.Event.BUTTON_RIGHT);
	}-*/;

	/**
	 * Middle mouse button caused the event, in the case of mousedown, mouseup,
	 * click, and dblclick events.
	 */
	public final native boolean isMiddleButton() /*-{
		return (this.button == $wnd.g_o3d.Event.BUTTON_MIDDLE);
	}-*/;
}
