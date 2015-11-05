package org.grape.galaxy.utils;

public class VecUtils {

	public static double len(double... xs) {
		double len = 0;
		for (double x : xs) {
			len += (x * x);
		}
		return Math.sqrt(len);
	}
	
}
