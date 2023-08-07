package di_framework.framework;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.reflections.Reflections;

import di_framework.framework.annotations.Component;
import di_framework.framework.utils.ClassLoaderUtils;
import di_framework.framework.utils.InjectorUtils;

public class Injector {

	private Map<Class<?>, Set<Class<?>>> diMap;
	private Map<Class<?>, Object> applicationScope;

	public Injector() {
		super();
		diMap = new HashMap<>();
		applicationScope = new HashMap<>();
	}

	public void initFramework(Class<?> mainClass) throws ClassNotFoundException, IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String path = mainClass.getPackage().getName();
		Reflections reflections = new Reflections(path);
		Set<Class<?>> componentClasses = reflections.getTypesAnnotatedWith(Component.class);

		for (Class<?> componentClazz : componentClasses) {
			Set<Class<?>> interfaces = Arrays.stream(componentClazz.getInterfaces()).collect(Collectors.toSet());
			System.out.println(componentClazz.getSimpleName() + " ::: " + interfaces.stream().map(e -> e.getSimpleName()).collect(Collectors.joining(", ")));
			diMap.put(componentClazz, interfaces);
			diMap.get(componentClazz).add(componentClazz);
		}

		JSONObject json = new JSONObject();
		diMap.entrySet().stream().forEach(e -> json.put(e.getKey().getSimpleName(), e.getValue().stream().map(Class::getSimpleName).collect(Collectors.joining(", "))));
		FrameworkDI.getLogger(Injector.class).info(json.toString(4));

		Class<?>[] allClasses = ClassLoaderUtils.getClasses(path);
		for (Class<?> clazz : allClasses) {
			if (clazz.isAnnotationPresent(Component.class)) {
				Constructor<?> constructor = clazz.getConstructor();
				constructor.setAccessible(true);
				applicationScope.put(clazz, constructor.newInstance());
				InjectorUtils.autowire(this, clazz, applicationScope.get(clazz));
			}
		}
	}

	public <T> T getBean(Class<T> classType) {
		try {
			return getBeanInstance(classType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <T> T getBeanInstance(Class<T> classType) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return (T) getBeanInstance(classType, null, null);
	}

	public <T> Object getBeanInstance(Class<T> classType, String fieldName, String qualifier) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		validateClassTypeForInstance(classType);

		Class<?> implementionClazz = getImplementationClass(classType, fieldName, qualifier);
		System.out.println(classType.getName() + " impltd by " + implementionClazz.getName());

		if (applicationScope.containsKey(implementionClazz)) {
			return applicationScope.get(implementionClazz);
		}

		synchronized (applicationScope) {
			Constructor<?> constructor = implementionClazz.getConstructor();
			constructor.setAccessible(true);
			Object service = constructor.newInstance();
			applicationScope.put(implementionClazz, service);
			return service;
		}
	}

	public static void validateClassTypeForInstance(Class<?> clazz) {
		Objects.requireNonNull(clazz, "can not create instance for " + clazz.getSimpleName());

		if (clazz.isInterface())
			throw new RuntimeException("can not create instance for the interface " + clazz.getName());
		if (clazz.isArray())
			throw new RuntimeException("can not create instance for the Array type of " + clazz.getName());

	}

	private Class<?> getImplementationClass(Class<?> clazz, String fieldName, String qualifier) {
		String errorMessage = "";

		Set<Entry<Class<?>, Set<Class<?>>>> implementationClasses = diMap.entrySet().stream().filter(e -> e.getValue().contains(clazz)).collect(Collectors.toSet());

		System.out.println(implementationClasses + " " + clazz);

		if (implementationClasses == null || implementationClasses.isEmpty()) {
			errorMessage = "no implementation found for " + clazz.getName();
		} else if (implementationClasses.size() == 1) {
			Optional<Entry<Class<?>, Set<Class<?>>>> implementationClazz = implementationClasses.stream().findFirst();
			if (implementationClazz.isPresent()) {
				return implementationClazz.get().getKey();
			}
		} else if (implementationClasses.size() > 1) {
			String findBy = (qualifier == null || qualifier.isEmpty()) ? fieldName : qualifier;

			Optional<Entry<Class<?>, Set<Class<?>>>> implementationClazz = implementationClasses.stream().filter(e -> e.getKey().getSimpleName().equalsIgnoreCase(findBy)).findAny();

			if (implementationClazz.isPresent())
				return implementationClazz.get().getKey();
			else {
				errorMessage = "There are " + implementationClasses.size() + " of " + implementationClazz + " Expected single implementation or make use of @Qualifier to resolve conflict";
			}
		}

		throw new RuntimeException(errorMessage);
	}
}
