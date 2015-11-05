package org.grape.galaxy.server.utils;

import org.grape.galaxy.model.ChatMessage;
import org.grape.galaxy.model.PlanetDetails;

public class ChatMessageHelper {

	private static ChatMessageHelper inst;

	public synchronized static ChatMessageHelper get() {
		if (inst == null) {
			inst = new ChatMessageHelper();
		}
		return inst;
	}

	private ChatMessageHelper() {
	}
	
	public String createMessageText(Object... parts) {
		StringBuilder buf = new StringBuilder();
		for (Object part : parts) {
			if (part instanceof PlanetDetails) {
				PlanetDetails planetDetails = (PlanetDetails) part;
				buf.append(ChatMessage.PLANET_REF_PREFIX);
				buf.append(planetDetails.getIndex());
				buf.append(ChatMessage.SPECIAL_CHAR);
				if (planetDetails.getPlanetName() != null) {
					buf.append(ChatMessage.PLANET_NAME_PREFIX);
					buf.append(planetDetails.getPlanetName());
					buf.append(ChatMessage.SPECIAL_CHAR);
				}
			} else if ((part instanceof Double) || (part instanceof Float)) {
				double val = ((Number) part).doubleValue();
				buf.append(Math.round(val * 100) / 100.0);
			} else {
				buf.append(part);
			}
		}
		return buf.toString();
	}
}
