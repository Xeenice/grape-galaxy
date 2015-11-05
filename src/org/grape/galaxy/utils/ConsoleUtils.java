package org.grape.galaxy.utils;

public class ConsoleUtils {

	private ConsoleUtils() {
	}

	public static native void debug(Object obj) /*-{
		try {
			console.debug(obj);
		} catch (nothing) {
		}
	}-*/;

	public static native void log(Object obj) /*-{
		try {
			console.log(obj);
		} catch (nothing) {
		}
	}-*/;

	public static native void error(Object obj) /*-{
		try {
			console.error(obj);
		} catch (nothing) {
		}
	}-*/;
}
