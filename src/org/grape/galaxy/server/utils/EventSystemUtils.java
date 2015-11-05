package org.grape.galaxy.server.utils;

public final class EventSystemUtils {

	public static int getEventCount(double probability,
			int missedCycleCount) {
		int count = 0;
		for (int i = 0; i <= missedCycleCount; i++) {
			if (checkProbability(probability)) {
				count++;
			}
		}
		return count;
	}
	
	public static boolean checkProbability(double probability) {
		double rand = rand();
		double shift = ((1.0 - probability) / 2.0);
		return ((shift <= rand) && (rand <= (1.0 - shift)));
	}
	
	public static double rand() {
		double result = Math.random();
		for (int i = 0; i < 3; i++) {
			result = Math.random();
		}
		return result;
	}
}
