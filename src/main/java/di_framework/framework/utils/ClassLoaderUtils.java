package di_framework.framework.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class ClassLoaderUtils {

	public static Class<?>[] getClasses(String packageName) throws IOException, ClassNotFoundException {
		Objects.requireNonNull(packageName, "packageName is required");

		List<Class<?>> classes = new ArrayList<>();
		String path = packageName.replace(".", "/");
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		List<File> dirs = new ArrayList<>();
		Enumeration<URL> resources = classLoader.getResources(path);

		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}

		for (File dir : dirs) {
			classes.addAll(findClasses(dir, packageName));
		}
		return classes.toArray(Class<?>[]::new);
	}

	public static List<Class<?>> findClasses(File dir, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<>();
		if (!dir.exists())
			return classes;

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
				classes.add(Class.forName(className));
			}
		}

		return classes;
	}
}
