package org.grape.galaxy.model;

public class Constants {

	public static final double EPS = 0.001;

	public static final int TRANSACTION_MAX_TRY_COUNT = 10;

	public static final int GENERATION_MAX_TRY_COUNT = 10;
	public static final int GENERATION_CYCLE_COUNT = 5;

	public static final int ACTIVITY_PERIOD_SECONDS = 2 * 60;
	public static final int ACTIVITY_PERIOD_MILLIS = ACTIVITY_PERIOD_SECONDS * 1000;

	public static final long GALAXY_LINEAR_SIZE_IN_SECTORS = 1000 * 1000;
	public static final long SECTOR_LINEAR_SIZE_IN_CELLS = 100;
	public static final long GALAXY_LINEAR_SIZE_IN_CELLS = GALAXY_LINEAR_SIZE_IN_SECTORS
			* SECTOR_LINEAR_SIZE_IN_CELLS;

	public static final long GALAXY_SECTORS_RANGE = 3;
	public static final long GALAXY_AVAILABLE_SECTORS_COUNT = (2 * GALAXY_SECTORS_RANGE + 1)
			* (2 * GALAXY_SECTORS_RANGE + 1);

	public static final double SECTOR_LINEAR_SIZE = 1.0;
	public static final double SECTOR_BLIND_ZONE_SIZE = 0.005;
	public static final double SECTOR_BOARD_RELATIVE_SIZE = 1.07;
	public static final double SECTOR_BOARD_MIN_COORD = SECTOR_LINEAR_SIZE
			* 0.5 * (SECTOR_BOARD_RELATIVE_SIZE - 1);
	public static final double SECTOR_BOARD_MAX_COORD = SECTOR_LINEAR_SIZE
			* 0.5 * (SECTOR_BOARD_RELATIVE_SIZE + 1);

	public static final int SECTOR_MIN_PLANET_COUNT = 10;
	public static final int SECTOR_MAX_PLANET_COUNT = 20;

	public static final int PLANET_SURFACE_TYPE_COUNT = 10;
	public static final int PLANET_ATMOSPHERE_TYPE_COUNT = 10;

	public static final double PLANET_MIN_RADIUS = SECTOR_LINEAR_SIZE / 30;
	public static final double PLANET_MAX_RADIUS = SECTOR_LINEAR_SIZE / 20;

	public static final double PLANET_MIN_RESOURCE_COUNT_LIMIT = 90;
	public static final double PLANET_MAX_RESOURCE_COUNT_LIMIT = 100;

	public static final int PLANET_ORBIT_MIN_UNIT_COUNT_LIMIT = 900;
	public static final int PLANET_ORBIT_MAX_UNIT_COUNT_LIMIT = 1000;

	public static final double PLANET_MIN_DEFENCE_K = 1.4;
	public static final double PLANET_MAX_DEFENCE_K = 1.45;

	public static final double PLANET_RESOURCE_COUNT_GROW_VELOCITY = 0.2;
	public static final int PLANET_ORBIT_UNIT_COUNT_GROW_VELOCITY = 20;
	public static final double UNIT_PRICE = 0.1;
	
	public static final double PLANET_DEFENCE_SWITCH_ON_PRICE = Math.floor(PLANET_ORBIT_MIN_UNIT_COUNT_LIMIT
			* (PLANET_MIN_DEFENCE_K - 1.0) * UNIT_PRICE * 0.5);
	public static final double PLANET_DEFENCE_PRICE = PLANET_RESOURCE_COUNT_GROW_VELOCITY;

	public static final long PLANET_RENAME_TIMEOUT_MILLIS = 60 * 60 * 1000;

	public static final double GATE_LINEAR_SIZE = Constants.SECTOR_LINEAR_SIZE / 15;

	public static final double TRANSPORTATION_LINEAR_SIZE = SECTOR_LINEAR_SIZE / 20;
	public static final double FLEET_MAX_LINEAR_SIZE = TRANSPORTATION_LINEAR_SIZE;
	public static final double FLEET_MIN_LINEAR_SIZE = 0.6 * FLEET_MAX_LINEAR_SIZE;

	public static final long RESOURCE_TRANSPORTATION_VELOCITY_IN_CELLS = SECTOR_LINEAR_SIZE_IN_CELLS / 5;
	public static final long FLEET_TRANSPORTATION_VELOCITY_IN_CELLS = SECTOR_LINEAR_SIZE_IN_CELLS / 10;

	public static final int PLANET_MAX_TRANSPORTATION_COUNT = 5;

	public static final double HOME_CAPTURE_RESOURCE_COUNT_PENALTY = 0.9;
	public static final double HOME_HAVOC_RESOURCE_COUNT_PENALTY_K = 0.5;

	public static final int USER_RATING_PAGE_SIZE = 20;
	
	public static final String BOT_AGGRESSOR_NAME = "Рейнджер";
	public static final String BOT_AGGRESSOR_SOURCE_PLANET_NAME = "Астеройд";
}
