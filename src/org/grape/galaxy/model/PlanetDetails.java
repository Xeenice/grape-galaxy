package org.grape.galaxy.model;

import java.io.Serializable;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class PlanetDetails implements Serializable {

	private static final long serialVersionUID = 1314889911069002314L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long index;

	@Persistent(columns = @Column(length = 64))
	private String ownerId;
	@Persistent(columns = @Column(length = 64))
	private String ownerName;
	@Persistent
	private boolean home;
	@Persistent(columns = @Column(length = 64))
	private String planetName;
	@Persistent
	private double resourceCountLimit;
	@Persistent
	private double resourceCount;
	@Persistent
	private int orbitUnitCountLimit;
	@Persistent
	private int orbitUnitCount;
	@Persistent
	private long captureTimeMillis;
	@Persistent
	private long lastRenameTimeMillis;
	@Persistent
	private boolean unitProduction;
	@Persistent
	private double defenceK;
	@Persistent
	private boolean defenceEnabled;

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
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

	public boolean isHome() {
		return home;
	}

	public void setHome(boolean home) {
		this.home = home;
	}

	public String getPlanetName() {
		return planetName;
	}

	public void setPlanetName(String planetName) {
		this.planetName = planetName;
	}

	public double getResourceCountLimit() {
		return resourceCountLimit;
	}

	public void setResourceCountLimit(double resourceCountLimit) {
		this.resourceCountLimit = resourceCountLimit;
	}

	public double getResourceCount() {
		return resourceCount;
	}

	public void setResourceCount(double resourceCount) {
		this.resourceCount = resourceCount;
	}

	public int getOrbitUnitCountLimit() {
		return orbitUnitCountLimit;
	}

	public void setOrbitUnitCountLimit(int orbitUnitCountLimit) {
		this.orbitUnitCountLimit = orbitUnitCountLimit;
	}

	public int getOrbitUnitCount() {
		return orbitUnitCount;
	}

	public void setOrbitUnitCount(int orbitUnitCount) {
		this.orbitUnitCount = orbitUnitCount;
	}

	public long getCaptureTimeMillis() {
		return captureTimeMillis;
	}

	public void setCaptureTimeMillis(long captureTimeMillis) {
		this.captureTimeMillis = captureTimeMillis;
	}

	public long getLastRenameTimeMillis() {
		return lastRenameTimeMillis;
	}

	public void setLastRenameTimeMillis(long lastRenameTimeMillis) {
		this.lastRenameTimeMillis = lastRenameTimeMillis;
	}

	public boolean isUnitProduction() {
		return unitProduction;
	}

	public void setUnitProduction(boolean unitProduction) {
		this.unitProduction = unitProduction;
	}

	public double getDefenceK() {
		return defenceK;
	}

	public void setDefenceK(double defenceK) {
		this.defenceK = defenceK;
	}
	
	public boolean isDefenceEnabled() {
		return defenceEnabled;
	}

	public void setDefenceEnabled(boolean defenceEnabled) {
		this.defenceEnabled = defenceEnabled;
	}

	public void merge(PlanetDetails other) {
		this.ownerId = other.ownerId;
		this.ownerName = other.ownerName;
		this.home = other.home;
		this.planetName = other.planetName;
		this.resourceCount = other.resourceCount;
		this.orbitUnitCount = other.orbitUnitCount;
		this.captureTimeMillis = other.captureTimeMillis;
		this.lastRenameTimeMillis = other.lastRenameTimeMillis;
		this.unitProduction = other.unitProduction;
		this.defenceEnabled = other.defenceEnabled;
	}
}
