package org.grape.galaxy.server.utils;

import java.util.Date;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class GlobalTimeAndIndexUtils {

	private static final Logger logger = Logger
			.getLogger(GlobalTimeAndIndexUtils.class.getName());

	private static final String MC_KEY_TIME = "org.grape.galaxy.server.utils.GlobalTimeAndIndexUtils#time";

	private static final MemcacheService memcacheService = MemcacheServiceFactory
			.getMemcacheService();

	public static long currentTimeMillis() {
		long timeMillis = System.currentTimeMillis();
		try {
			Long lastTimeMillis = (Long) memcacheService.get(MC_KEY_TIME);
			if ((lastTimeMillis != null) && (lastTimeMillis >= timeMillis)) {
				timeMillis = (lastTimeMillis + 1);
			}
			memcacheService.put(MC_KEY_TIME, timeMillis);
		} catch (Exception ex) {
			logger.warning(ex.getMessage());
		}
		return timeMillis;
	}
	
	public static Date today() {
		return new Date(GlobalTimeAndIndexUtils.currentTimeMillis());
	}
}
