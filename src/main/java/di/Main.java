package di;

import di.framework.BeanService;
import di.framework.annotations.Component;

@Component
public class Main {

	private final String str = "uday"; 

	public static void main(final String... args) {
		System.out.println("Hey, WhatsApp?");

		final BeanService service = BeanService.run(Main.class, args);
		final Main main = service.getBean(Main.class);
		System.out.println(main.str);

	}
}
