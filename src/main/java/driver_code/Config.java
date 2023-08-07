package driver_code;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import providers.Filter;
import providers.JsonIO;

@ApplicationPath("run")
public class Config extends ResourceConfig {

	private Set<Class<?>> classes = new HashSet<>();

	public Config() {

		packages(true, "driver_code", "providers", "snippets");

		registerProviders();
		registerClasses(classes);

		System.out.println("loaded...");
	}

	public void registerProviders() {
		classes.add(Filter.class);
		classes.add(MultiPartFeature.class);
		classes.add(JsonIO.class);
	}
}
