package org.grape.galaxy.server.utils;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class CacheUtils {

	private static Logger logger = Logger.getLogger(CacheUtils.class.getName());
	
	private static final MemcacheService memcacheService = MemcacheServiceFactory
			.getMemcacheService();
	
	public static <T> Wrapper<T> createWrapper(final T el) {
		return new Wrapper<T>() {

			@Override
			public T get() {
				return el;
			}

			@Override
			public void update() {
			}
			
			@Override
			public void delete() {
			}
		};
	}
	
	public static interface Wrapper<T> {
		
		T get();
		
		void update();
		
		void delete();
	}
	
	@SuppressWarnings("unchecked")
	public static class FixedCollection<T> extends AbstractCollection<Wrapper<T>> implements Collection<Wrapper<T>> {

		private String prefix;
		private int size;
		private Expiration expiration;
		
		public FixedCollection(String prefix, int size, Expiration expiration) {
			this.prefix = prefix;
			this.size = size;
			this.expiration = expiration;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public Iterator<Wrapper<T>> iterator() {
			return new Iterator<Wrapper<T>>() {

				private int index = 0;
				
				@Override
				public boolean hasNext() {
					boolean ok = false;
					while (index < size) {
						String key = getKey(index);
						try {
							if (memcacheService.contains(key)) {
								ok = true;
								break;
							}
						} catch (Exception ex) {
							logger.fine(ex.getMessage());
						}
						index++;
					}
					return ok;
				}

				@Override
				public Wrapper<T> next() {
					final String key = getKey(index);
					index++;
					final T el = (T) memcacheService.get(key);
					return new Wrapper<T>() {

						@Override
						public T get() {
							return el;
						}

						@Override
						public void update() {
							try {
								memcacheService.put(key, el, expiration,
										MemcacheService.SetPolicy.SET_ALWAYS);
							} catch (Exception ex) {
								logger.fine(ex.getMessage());
							}
						}
						
						@Override
						public void delete() {
							try {
								memcacheService.delete(key);
							} catch (Exception ex) {
								logger.fine(ex.getMessage());
							}
						}
					};
				}

				@Override
				public void remove() {
					delete(getKey(index - 1));
				}
			};
		}

		@Override
		public boolean add(Wrapper<T> wrapper) {
			boolean ok = false;
			int tryCount = 0;
			while (!ok && (tryCount < size)) {
				long counter = memcacheService.increment(prefix + "#counter", 1L, 0L);
				if (counter < 0) {
					counter -= Long.MIN_VALUE;
				}
				int index = (int) (counter % size);
				String key = getKey(index);
				if (!memcacheService.contains(key)) {
					memcacheService.put(key, wrapper.get(), expiration,
							MemcacheService.SetPolicy.SET_ALWAYS);
					ok = true;
				} else {
					tryCount++;
				}
			}
			return ok;
		}
		
		private String getKey(int index) {
			return (prefix + "#" + index);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> type, Object id) {
		String key = getKey(type, id);
		return (T) memcacheService.get(key);
	}
	
	public static void put(Class<?> type, Object id, Object obj) {
		String key = getKey(type, id);
		try {
			memcacheService.put(key, obj);
		} catch (Exception ex) {
			logger.fine(ex.getMessage());
		}
	}
	
	public static Object delete(Class<?> type, Object id) {
		String key = getKey(type, id);
		return delete(key);
	}
	
	public static Object delete(Object key) {
		Object old = null;
		try {
			old = memcacheService.get(key);
			memcacheService.delete(key);
		} catch (Exception ex) {
			logger.fine(ex.getMessage());
		}
		return old;
	}
	
	public static String getKey(Class<?> type, Object id) {
		return (type.getSimpleName() + "#" + id);
	}
}
