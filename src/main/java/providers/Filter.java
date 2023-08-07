package providers;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;

@PreMatching
public class Filter implements ContainerResponseFilter, ContainerRequestFilter {

	public static int count = 0;

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		//		System.out.println(request.getHeaders());
		//		System.out.println(response.getStringHeaders());
		System.out.println("responded request... " + response.getEntity() + "\n\n");
		//		System.out.println();
	}

	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		System.out.println("request received to server... " + ++count);
	}
}
