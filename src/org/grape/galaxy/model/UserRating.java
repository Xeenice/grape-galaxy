package org.grape.galaxy.model;

import java.io.Serializable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserRating implements Serializable, Comparable<UserRating> {

	private static final long serialVersionUID = 782768580934425720L;

	private static final int MAX_RATING = 1000;
	private static final double PLANET_WEIGHT = 0.5;
	private static final double ORBIT_UNIT_WEIGHT = 0.3;
	private static final double RESOURCE_WEIGHT = 0.2;
	private static final double PLANET_NORM_FACTOR = PLANET_WEIGHT
			/ (Constants.GALAXY_AVAILABLE_SECTORS_COUNT * Constants.SECTOR_MAX_PLANET_COUNT);
	private static final double ORBIT_UNIT_NORM_FACTOR = ORBIT_UNIT_WEIGHT
			/ (Constants.GALAXY_AVAILABLE_SECTORS_COUNT
					* Constants.SECTOR_MAX_PLANET_COUNT * Constants.PLANET_MAX_RESOURCE_COUNT_LIMIT);
	private static final double RESOURCE_NORM_FACTOR = RESOURCE_WEIGHT
			/ (Constants.GALAXY_AVAILABLE_SECTORS_COUNT
					* Constants.SECTOR_MAX_PLANET_COUNT * Constants.PLANET_ORBIT_MAX_UNIT_COUNT_LIMIT);

	@SuppressWarnings("unused")
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent(columns = @Column(length = 64))
	private String userId;
	@Persistent(columns = @Column(length = 64))
	private String userName;
	@Persistent
	private int pageIndex;
	@Persistent
	private int lastPageIndex;
	@Persistent
	private int planetCount;
	@Persistent
	private int orbitUnitCount;
	@Persistent
	private double resourceCount;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setLastPageIndex(int lastPageIndex) {
		this.lastPageIndex = lastPageIndex;
	}

	public int getLastPageIndex() {
		return lastPageIndex;
	}

	public void setPlanetCount(int planetCount) {
		this.planetCount = planetCount;
	}

	public int getPlanetCount() {
		return planetCount;
	}

	public void setOrbitUnitCount(int orbitUnitCount) {
		this.orbitUnitCount = orbitUnitCount;
	}

	public int getOrbitUnitCount() {
		return orbitUnitCount;
	}

	public void setResourceCount(double resourceCount) {
		this.resourceCount = resourceCount;
	}

	public double getResourceCount() {
		return resourceCount;
	}

	public int getRating() {
		return (int) Math.round(MAX_RATING
				* (planetCount * PLANET_NORM_FACTOR + resourceCount
						* RESOURCE_NORM_FACTOR + orbitUnitCount
						* ORBIT_UNIT_NORM_FACTOR));
	}

	@Override
	public int compareTo(UserRating o) {
		return getRating() - o.getRating();
	}
}
