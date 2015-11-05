package org.grape.galaxy.model;

public class Gate {

	private long sectorIndex;
	private double x;
	private double y;

	public Gate(long sectorIndex, double x, double y) {
		this.sectorIndex = sectorIndex;
		this.x = x;
		this.y = y;
	}

	public long getSectorIndex() {
		return sectorIndex;
	}

	public String getSectorIndexAsString() {
		return "" + sectorIndex;
	}

	public double getRelX() {
		double x2 = Constants.SECTOR_BOARD_RELATIVE_SIZE * (x - 0.5) + 0.5;
		return x2 * Constants.SECTOR_LINEAR_SIZE;
	}

	public double getRelY() {
		double y2 = Constants.SECTOR_BOARD_RELATIVE_SIZE * (y - 0.5) + 0.5;
		return y2 * Constants.SECTOR_LINEAR_SIZE;
	}

	public double getAngle() {
		return Math.atan2(x - 0.5, y - 0.5);
	}
}
