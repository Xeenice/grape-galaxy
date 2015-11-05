package org.grape.galaxy.utils;

public class RandomGenerator {
	
	private static final int A = 1664525;
	private static final int C = 1013904223;
	
	private static final double LIMIT = 100000.0;

	public static double next(double prev) {
		return ((Math.abs(A * prev + C) % LIMIT) / LIMIT);
	}
	
	public static double next(double prev, int cycles) {
		if (cycles < 1) {
			cycles = 1;
		}
		double result = prev;
		while (cycles > 0) {
			result = next(result);
			cycles--;
		}
		return result;
	}
}
