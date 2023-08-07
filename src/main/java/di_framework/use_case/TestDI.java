package di_framework.use_case;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import di_framework.framework.FrameworkDI;
import di_framework.framework.Injector;
import di_framework.framework.annotations.AutoWire;
import di_framework.framework.annotations.Component;

@Component
class MultiTest {
	static int count = 0;

	{
		System.out.println("created " + ++count + " times");
	}

	@AutoWire
	A test;

	public MultiTest() {

	}
}

interface Test {

}

@Component
class A implements Test {
	public A() {
		super();
	}

}

@Component
class B implements Test {
	public B() {
		super();
	}
}

@Component
public class TestDI {

	public static void main(String... args) throws IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		System.out.println("Hey, TestDI!");
		Injector injector = FrameworkDI.run(TestDI.class, args);

		String[] strings = {};

		injector.getBean(Arrays.class);
	}
}
