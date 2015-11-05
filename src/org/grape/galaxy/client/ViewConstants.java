package org.grape.galaxy.client;

import org.grape.galaxy.model.Constants;

public class ViewConstants {

	public static final int RENDER_FRAMES_PER_SECOND = 25;
	public static final int RENDER_PERIOD_MILLIS = 1000 / RENDER_FRAMES_PER_SECOND;

	public static final double CAMERA_EYE_Z = 10.0;
	public static final double CAMERA_NEAR = 0.1;
	public static final double CAMERA_FAR = 100.0;

	public static final double SECTOR_VIEW_RELATIVE_SIZE = Constants.SECTOR_BOARD_RELATIVE_SIZE * 1.07;

	public static final double STARS_Z = -1.0;
	public static final double PLANET_Z = 0.0;
	public static final double GATE_Z = 0.0;
	public static final double TRANSPORTATION_MIN_Z = 1.0;
	public static final double TRANSPORTATION_MAX_Z = 6.0;
	public static final double PATH_ARROW_Z = 7.0;

	public static final double MAP_CURSOR_Z = 1.0;
	public static final double MAP_PLANETS_Z = 2.0;

	public static final int STARS_AMOUNT = 200;
	public static final double STARS_ROTATION_VELOCITY = Math.toRadians(0.3); // [рад/с]
	public static final double STARS_MIN_SIZE = Constants.SECTOR_LINEAR_SIZE / 25;
	public static final double STARS_MAX_SIZE = Constants.SECTOR_LINEAR_SIZE / 11;
	public static final double STARS_LIFE_TIME = 15.0;

	public static final double CURSOR_SCALE_TIME = 0.4; // [с]

	public static final double ATMOSPHERE_RELATIVE_DIAMETER = 1.25;

	public static final double GATE_TILT_ANGLE = Math.PI / 6; // [рад]
	public static final double GATE_FIELD_ALPHA_FADE_TIME = 2.0; // [с]

	public static final int STATUS_MESSAGE_BLINK_TIME_MILLIS = 1000; // [мс]

	public static final double PATH_ARROW_WIDTH = Constants.SECTOR_LINEAR_SIZE / 40;

	public static final double GUIDE_LINE_WIDTH = Constants.SECTOR_LINEAR_SIZE / 50;

	public static final double MAP_PLANET_SCALE = 2.0;

	public static final double PLANET_ROTATION_VELOCITY = Math.toRadians(8.0); // [рад/с]
	public static final double PLANET_AABB_SCALE = 1.2;

	public static final double TRANSPORTATION_AABB_SCALE = 0.8;

	public static final double SHIELD_RELATIVE_DIAMETER = ATMOSPHERE_RELATIVE_DIAMETER + 0.1;

	public static final int NATIVE_CSS_COL_R = 128;
	public static final int NATIVE_CSS_COL_G = 128;
	public static final int NATIVE_CSS_COL_B = 128;
	public static final double NATIVE_COL_R = NATIVE_CSS_COL_R / 255.0;
	public static final double NATIVE_COL_G = NATIVE_CSS_COL_G / 255.0;
	public static final double NATIVE_COL_B = NATIVE_CSS_COL_B / 255.0;
	public static final int OWN_CSS_COL_R = 255;
	public static final int OWN_CSS_COL_G = 255;
	public static final int OWN_CSS_COL_B = 128;
	public static final double OWN_COL_R = OWN_CSS_COL_R / 255.0;
	public static final double OWN_COL_G = OWN_CSS_COL_G / 255.0;
	public static final double OWN_COL_B = OWN_CSS_COL_B / 255.0;
	public static final int ENEMY_CSS_COL_R = 255;
	public static final int ENEMY_CSS_COL_G = 128;
	public static final int ENEMY_CSS_COL_B = 128;
	public static final double ENEMY_COL_R = ENEMY_CSS_COL_R / 255.0;
	public static final double ENEMY_COL_G = ENEMY_CSS_COL_G / 255.0;
	public static final double ENEMY_COL_B = ENEMY_CSS_COL_B / 255.0;

	public static final double GATE_COL_R = 185 / 255.0;
	public static final double GATE_COL_G = 109 / 255.0;
	public static final double GATE_COL_B = 28 / 255.0;
}
