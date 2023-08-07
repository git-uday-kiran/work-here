package di.framework;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import di.framework.annotations.Component;
import di.framework.utils.ClassLoaderUtils;
import di.framework.utils.PoolUtils;

/**
 * Provides Service for Beans
 *
 * @author uday.mekala
 */
public class BeanService {

	/**
	 * Holds pool to provide service
	 */
	private final BeanPool pool;

	/**
	 * Holds implementations for the classes
	 */
	public Map<Class<?>, List<Class<?>>> diMap; // key: interface, value: List[ implementation class ]

	/**
	 * Initalizes the {@code BeanService} with given {@code BeanPool}
	 * @param pool
	 */
	public BeanService(final BeanPool pool) {
		this.pool = pool;
	}

	/**
	 * Gives the Bean from the maintaining pool
	 * @param <T>
	 * @param clazz
	 * @return return the {@code T} Object if it is in pool, Otherwise null
	 */
	public <T> T getBean(final Class<T> clazz) {
		return pool.getBean(clazz);
	}


	/**
	 * Initializes the {@code BeanService} and {@code BeanPool} with given parameters
	 * @param mainClass
	 * @param args
	 * @return {@code BeanService} instance
	 */
	public static BeanService run(final Class<?> mainClass, String... args) {
		final List<Class<?>> allClasses = ClassLoaderUtils.getClasses(mainClass.getPackageName());
		final List<Class<? extends Annotation>> allowedClasses = List.of(Component.class);
		final List<Class<?>> permitedClasses = PoolUtils.getClassesAnnotatedWithAllowedClasses(allClasses, allowedClasses);

		final BeanPool pool = new BeanPool(permitedClasses);
		return new BeanService(pool);
	}

}
