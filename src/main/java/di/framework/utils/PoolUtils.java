package di.framework.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utils for BeanPool
 *
 * @author uday.mekala
 */
public class PoolUtils {

	/**
	 * Logger for PoolUtils
	 */
	private static final Logger LOGGER = LogManager.getLogger(PoolUtils.class);

	/**
	 * Gives the {@code Constructor} for given class type
	 *
	 * @param clazz
	 * @return
	 */
	public static Constructor<?> getConstructor(final Class<?> clazz) {
		Constructor<?> constructor = null;
		try {
			Objects.requireNonNull(clazz, "can not get constructor with null class");
			constructor = clazz.getConstructor();
		} catch (NoSuchMethodException | SecurityException e) {
			LOGGER.error("Could not able to initialize the object with default constructor for {}", clazz, e);
		}
		return constructor;
	}

	/**
	 * Validates the Class type for initializing the object
	 *
	 * @param clazz
	 */
	public static void validateClazzForInitializingObject(final Class<?> clazz) {
		Objects.requireNonNull(clazz, "Class type must not be null to initialize the object");
		if (clazz.isInterface()) {
			throw new RuntimeException(clazz.getSimpleName() + ", interfaces can not be instantiated");
		}
		if (clazz.isArray()) {
			throw new RuntimeException("Instantiating type Array is not implemented yet, " + clazz.getSimpleName());
		}
	}

	/**
	 * Gives the {@code Object} by initializing the {@code Constructor}
	 *
	 * @param constructor
	 * @return
	 */
	public static Object initializeConstructor(final Constructor<?> constructor) {
		Object object = null;
		try {
			constructor.setAccessible(true);
			object = constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Error while initializing constructor {}", constructor, e);
		}
		return object;
	}

	/**
	 * Gives the List of Classes by filters the classes who has annotated with
	 * atleast one annotation in the provided annotation classes
	 *
	 * @param classes
	 * @param annotationClasses
	 * @return List of Classes
	 */
	public static List<Class<?>> getClassesAnnotatedWithAllowedClasses(final List<Class<?>> classes, final List<Class<? extends Annotation>> annotationClasses) {
		final Predicate<Class<?>> annotationPresent = clazz -> annotationClasses.stream()
				.filter(anno -> clazz.isAnnotationPresent(anno))
				.findAny()
				.isPresent();

		return classes.stream()
				.filter(annotationPresent)
				.toList();
	}

	private PoolUtils() {}
}
