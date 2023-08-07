package di.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import di.framework.utils.PoolUtils;

/**
 *
 * @author uday.mekala
 * Initializes the BeanPool
 */
public class BeanPool {

	/**
	 * Holds Class type and their Objects
	 */
	private final Map<Class<?>, Object> pool;

	/**
	 * Logger for BeanPool Class
	 */
	private static final Logger LOGGER = LogManager.getLogger(BeanPool.class);

	/**
	 * Initializes the BeanPool with given classes
	 * @param classes
	 */
	public BeanPool(final List<Class<?>> classes) {
		pool = new HashMap<>();
		LOGGER.info("BeanPool is initializing...");
		classes.forEach(clazz -> pool.put(clazz, initializeObject(clazz)));
		LOGGER.info("BeanPool has been initialized");
		view();
	}

	private Object initializeObject(final Class<?> clazz) {
		PoolUtils.validateClazzForInitializingObject(clazz);
		return PoolUtils.initializeConstructor(PoolUtils.getConstructor(clazz));
	}

	private void view() {
		final StringJoiner joiner = new StringJoiner("\n  ", "{\n  ", "\n}");
		pool.forEach((k, v) -> joiner.add(k + " : " + v));
		LOGGER.info("Beans :: {}", joiner);
	}

	public Map<Class<?>, Object> getPool() {
		return pool;
	}

	/**
	 * Gives the living bean from the pool
	 * @param <T>
	 * @param clazz
	 * @return returns bean instance if it is there in pool, otherwise null
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBean(final Class<T> clazz) {
		return (T) pool.getOrDefault(clazz, null);
	}
}