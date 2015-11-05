package org.grape.galaxy.utils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;

public class ViewUtils {
	
	private static int blockCount = 0;

	public static final char[] HEX_CODES_MAP = {
		'0', '1', '2', '3',
		'4', '5', '6', '7',
		'8', '9', 'A', 'B',
		'C', 'D', 'E', 'F'};
	
	public static final String getColorStr(int color) {
		int r = ((color >> 16) & 0xFF);
		int g = ((color >> 8) & 0xFF);
		int b = (color & 0xFF);
		return ("#" + HEX_CODES_MAP[(r >> 4) & 0xF] + HEX_CODES_MAP[r & 0xF]
		        + HEX_CODES_MAP[(g >> 4) & 0xF] + HEX_CODES_MAP[g & 0xF]
		        + HEX_CODES_MAP[(b >> 4) & 0xF] + HEX_CODES_MAP[b & 0xF]);
	}
	
	public static final void blockUI() {
		if (blockCount == 0) {
			SC.showPrompt(createProcessHTML("Пожалуйста, подождите..."));
		}
		blockCount++;
	}
	
	public static final void unblockUI() {
		blockCount--;
		if (blockCount == 0) {
			SC.clearPrompt();
		}
	}
	
	public static String createProcessHTML(String processMessage) {
		return ("&nbsp;&nbsp;<img src=\"/images/process.gif\" border=\"0\" style=\"position: relative; top: 2px\" />&nbsp;&nbsp;" + processMessage);
	}
	
	public static Canvas createProcessCanvas(String processMessage) {
		return new HTMLFlow(createProcessHTML(processMessage));
	}
	
	public static native String escapeHTML(String text) /*-{
		if (!text) {
			return text;
		}
		var div = $doc.createElement("div");
		div.appendChild($doc.createTextNode(text));
		return div.innerHTML;
	}-*/;
	
	// BEER масло масленное :) Надо придумать как привлекать внимание к элементам UI
	public static void attractAttention(final Element el) {
		if (el == null) {
			return;
		}
		
		int delta = 1;
		for (int i = 0; i <= 3; i++) {
			final int step = i;
			new Timer() {
				
				@Override
				public void run() {
					if (step % 2 == 0) {
						el.addClassName("attention");
					} else {
						el.removeClassName("attention");
					}
				}
			}.schedule(delta);
			if (step % 2 == 0) {
				delta += 100;
			} else {
				delta += 70;
			}
		}
	}
	
	public static void attractAttention(final Canvas canvas) {
		if (canvas == null) {
			return;
		}
		
		int delta = 1;
		for (int i = 0; i <= 3; i++) {
			final int step = i;
			new Timer() {
				
				@Override
				public void run() {
					if (step % 2 == 0) {
						canvas.setOpacity(80);
					} else {
						canvas.setOpacity(null);
					}
				}
			}.schedule(delta);
			if (step % 2 == 0) {
				delta += 100;
			} else {
				delta += 70;
			}
		}
	}
}
