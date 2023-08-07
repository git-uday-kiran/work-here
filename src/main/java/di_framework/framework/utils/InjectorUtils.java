package di_framework.framework.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import di_framework.framework.Injector;
import di_framework.framework.annotations.AutoWire;
import di_framework.framework.annotations.Qualifier;

public class InjectorUtils {

	public static void autowire(Injector injector, Class<?> clazz, Object clazzInstance) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Set<Field> fields = getFields(clazz);
		System.out.println("Here.....");
		System.out.println(fields + " :: " + clazz);
		for (Field field : fields) {
			String qualifier = field.isAnnotationPresent(Qualifier.class) ? field.getAnnotation(Qualifier.class).value() : null;
			Object fieldInstance = injector.getBeanInstance(field.getType(), field.getName(), qualifier);
			field.set(clazzInstance, fieldInstance);
			autowire(injector, field.getType(), fieldInstance);
		}
		System.out.println("--------------------------------");
	}

	public static Set<Field> getFields(Class<?> clazz) {
		Set<Field> fields = new HashSet<>();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(AutoWire.class)) {
					field.setAccessible(true);
					fields.add(field);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return fields;
	}
}
