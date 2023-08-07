package di.framework.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility class for Class types
 *
 * @author uday.mekala
 */
public class ClassLoaderUtils {

	/**
	 * Logger for ClassLoaderUtils
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClassLoaderUtils.class);

	/**
	 * Gives all classes within the {@value packageName}
	 *
	 * @param packageName
	 * @return return list of classes from the give package name.
	 */
	public static List<Class<?>> getClasses(final String packageName) {
		Objects.requireNonNull(packageName, "packageName is required");

		final List<Class<?>> classes = new ArrayList<>();
		final String path = packageName.replace(".", "/");
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		try {
			final List<File> dirs = new ArrayList<>();
			final Enumeration<URL> resources = classLoader.getResources(path);

			while (resources.hasMoreElements()) {
				final URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}

			for (final File dir : dirs) {
				classes.addAll(findClasses(dir, packageName));
			}
		} catch (IOException | ClassNotFoundException e) {
			LOGGER.error("Error while getting all classes of package '{}'", packageName, e);
		}

		return classes;
	}

	/**
	 * Finds all classes within the package(with recursive subpackage)
	 *
	 * @param dir
	 * @param packageName
	 * @return list of all classes in the package
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(final File dir, final String packageName) throws ClassNotFoundException {
		final List<Class<?>> classes = new ArrayList<>();

		final File[] files = dir.exists() ? dir.listFiles() : new File[0];
		for (final File file : files) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				final String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
				classes.add(Class.forName(className));
			}
		}

		return classes;
	}

	private ClassLoaderUtils() {}
}
