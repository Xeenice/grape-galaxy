package org.grape.galaxy.utils;

public class Interpolator {

	private double startValue;
	private double endValue;
	private double time;
	private double timer;

	public Interpolator(double startValue, double endValue, double time) {
		this.startValue = startValue;
		this.endValue = endValue;
		this.time = time;
	}

	public double getStartValue() {
		return startValue;
	}

	public double getEndValue() {
		return endValue;
	}

	public boolean isFinished() {
		return (timer >= time);
	}

	public double getValue() {
		if (isFinished()) {
			return endValue;
		}
		return startValue + (endValue - startValue) * timer / time;
	}

	public void reset() {
		timer = 0;
	}
	
	public void invert() {
		double val = startValue;
		startValue = endValue;
		endValue = val;
	}

	public void update(double dt) {
		if (!isFinished()) {
			timer += dt;
		}
	}
}
