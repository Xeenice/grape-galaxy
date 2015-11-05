package org.grape.galaxy.server.utils;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.jdo.JDOCanRetryException;
import javax.jdo.PersistenceManager;

import org.grape.galaxy.client.AuthException;
import org.grape.galaxy.model.Constants;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class JDOUtils {
	
	private static Logger logger = Logger.getLogger(JDOUtils.class.getName());

	public static User getUser() throws AuthException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if ((user == null) || (user.getUserId() == null)) {
			throw new AuthException();
		}
		return user;
	}
	
	public static <T> T getObjectById(PersistenceManager pm, Class<T> type,
			Object id) {
		return getObjectById(pm, type, id, false, false);
	}
	
	public static <T> T getObjectById(PersistenceManager pm, Class<T> type,
			Object id, boolean useCache) {
		return getObjectById(pm, type, id, useCache, false);
	}
	
	public static <T> T getObjectById(PersistenceManager pm, Class<T> type,
			Object id, boolean useCache, boolean forceRetrieve) {
		T result = null;
		try {
			if (!useCache) {
				result = pm.getObjectById(type, id);
				if (forceRetrieve && (result != null)) {
					pm.retrieve(result);
				}
			} else {
				result = CacheUtils.get(type, id);
				if (result == null) {
					result = pm.getObjectById(type, id);
					if (result != null) {
						if (forceRetrieve) {
							pm.retrieve(result);
						}
						CacheUtils.put(type, id, result);
					}
				}
			}
		} catch (Exception ex) {
			logger.fine(ex.getMessage());
		}
		return result;
	}
	
	public static <T> T runInTransaction(PersistenceManager pm,
			Callable<T> callable) throws Exception {
		return runInTransaction(pm, callable, Constants.TRANSACTION_MAX_TRY_COUNT);
	}
	
	public static <T> T runInTransaction(PersistenceManager pm, Callable<T> callable,
			int maxTryCount) throws Exception {
		if (maxTryCount <= 0) {
			maxTryCount = Constants.TRANSACTION_MAX_TRY_COUNT;
		}
		
		T result = null;
		
		for (int tryCount = 0; tryCount < maxTryCount; tryCount++) {
			pm.currentTransaction().begin();
		
			try {
				result = callable.call();
			} catch (Exception ex) {
				logger.severe(ex.getMessage());
				pm.currentTransaction().rollback();
				throw ex;
			}
			
			try {
				pm.currentTransaction().commit();
				break;
			} catch (JDOCanRetryException ex) {
				if (tryCount == (maxTryCount - 1)) {
					logger.severe(ex.getMessage());
				} else {
					Thread.yield();
				}
			}
		}
		
		return result;
	}
}
