package org.grape.galaxy.model;

import java.io.Serializable;

public class Transportation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int MIN_PRIORITY = 0;
	public static final int MAX_PRIORITY = 1;

	private long id;
	
	private int priority = MIN_PRIORITY;
	
	private String ownerId;
	private String ownerName;

	private long sourceCellX;
	private long sourceCellY;
	private long targetCellX;
	private long targetCellY;

	private long currentCellX;
	private long currentCellY;

	private double resourceCount;
	private int unitCount;
	private long velocityInCells;

	private boolean completed;
	private boolean canceled;

	private long lastUpdateTimeMillis;
	
	public Transportation() {
	}

	public Transportation(Transportation other) {
		this.id = other.id;
		this.priority = other.priority;
		this.ownerId = other.ownerId;
		this.ownerName = other.ownerName;
		this.sourceCellX = other.sourceCellX;
		this.sourceCellY = other.sourceCellY;
		this.targetCellX = other.targetCellX;
		this.targetCellY = other.targetCellY;
		this.currentCellX = other.currentCellX;
		this.currentCellY = other.currentCellY;
		this.resourceCount = other.resourceCount;
		this.unitCount = other.unitCount;
		this.velocityInCells = other.velocityInCells;
		this.completed = other.completed;
		this.canceled = other.canceled;
		this.lastUpdateTimeMillis = other.lastUpdateTimeMillis;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isHighPriority() {
		return (priority > MIN_PRIORITY);
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public long getSourceCellX() {
		return sourceCellX;
	}

	public void setSourceCellX(long sourceCellX) {
		this.sourceCellX = sourceCellX;
	}

	public long getSourceCellY() {
		return sourceCellY;
	}

	public void setSourceCellY(long sourceCellY) {
		this.sourceCellY = sourceCellY;
	}

	public long getSourceCellIndex() {
		return sourceCellY * Constants.GALAXY_LINEAR_SIZE_IN_CELLS
				+ sourceCellX;
	}

	public long getSourceSectorIndex() {
		return Galaxy.getSectorIndexForCell(sourceCellX, sourceCellY);
	}

	public long getTargetCellX() {
		return targetCellX;
	}

	public void setTargetCellX(long targetCellX) {
		this.targetCellX = targetCellX;
	}

	public long getTargetCellY() {
		return targetCellY;
	}

	public void setTargetCellY(long targetCellY) {
		this.targetCellY = targetCellY;
	}

	public long getTargetCellIndex() {
		return targetCellY * Constants.GALAXY_LINEAR_SIZE_IN_CELLS
				+ targetCellX;
	}

	public long getTargetSectorIndex() {
		return Galaxy.getSectorIndexForCell(targetCellX, targetCellY);
	}

	public long getCurrentCellX() {
		return currentCellX;
	}

	public void setCurrentCellX(long currentCellX) {
		this.currentCellX = currentCellX;
	}

	public long getCurrentCellY() {
		return currentCellY;
	}

	public void setCurrentCellY(long currentCellY) {
		this.currentCellY = currentCellY;
	}

	public long getCurrentCellIndex() {
		return currentCellX * Constants.GALAXY_LINEAR_SIZE_IN_CELLS
				+ currentCellY;
	}

	public long getCurrentSectorIndex() {
		return Galaxy.getSectorIndexForCell(currentCellX, currentCellY);
	}

	public double getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(double resourceCount) {
		this.resourceCount = resourceCount;
	}

	public int getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(int unitCount) {
		this.unitCount = unitCount;
	}

	public long getVelocityInCells() {
		return velocityInCells;
	}

	public void setVelocityInCells(long velocityInCells) {
		this.velocityInCells = velocityInCells;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public long getLastUpdateTimeMillis() {
		return lastUpdateTimeMillis;
	}

	public void setLastUpdateTimeMillis(long lastUpdateTimeMillis) {
		this.lastUpdateTimeMillis = lastUpdateTimeMillis;
	}

	public boolean isFleetTransportation() {
		return ((unitCount > 0) && (resourceCount < Constants.EPS));
	}

	public boolean isResourceTransportation() {
		return ((resourceCount > Constants.EPS) && (unitCount <= 0));
	}

	public void update(int missedCycleCount) {
		if (completed) {
			return;
		}
		
		double s = (targetCellX - currentCellX) * (targetCellX - currentCellX)
				+ (targetCellY - currentCellY) * (targetCellY - currentCellY);
		long v = ((1 + missedCycleCount) * velocityInCells);
		double k = Math.sqrt(v * v / s);
		if (k >= (1.0 - Constants.EPS)) {
			currentCellX = targetCellX;
			currentCellY = targetCellY;
			completed = true;
		} else {
			currentCellX = (long) (currentCellX + (targetCellX - currentCellX)
					* k);
			currentCellY = (long) (currentCellY + (targetCellY - currentCellY)
					* k);
		}
	}
	
	public void merge(Transportation other) {
		if (id == other.id) {
			ownerId = other.ownerId;
			ownerName = other.ownerName;
			priority = other.priority;
			currentCellX = other.currentCellX;
			currentCellY = other.currentCellY;
			resourceCount = other.resourceCount;
			unitCount = other.unitCount;
			velocityInCells = other.velocityInCells;
			completed = other.completed;
			canceled = other.canceled;
			lastUpdateTimeMillis = other.lastUpdateTimeMillis;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transportation other = (Transportation) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
