package snippets;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.ws.rs.core.NoContentException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Http {

	private static final Logger LOGGER = LogManager.getLogger(Http.class);

	private static final int TIMEOUT = 60;
	private static final RequestConfig CONFIG = RequestConfig.custom().setConnectTimeout(TIMEOUT * 1000).setConnectionRequestTimeout(TIMEOUT * 1000).setSocketTimeout(TIMEOUT * 1000).build();
	private static final HttpClient CLIENT = HttpClientBuilder.create().setDefaultRequestConfig(CONFIG).build();

	public static Object post(String uri, JSONObject body, JSONObject headers, AcceptType type) {
		LOGGER.info("httpPost :: {} {} {}", uri, headers, body);

		try {
			HttpPost post = new HttpPost(uri);

			// setting headers to the post
			Iterator<String> headKeys = headers.keys();
			while (headKeys.hasNext()) {
				String headKey = headKeys.next();
				String headValue = (String) headers.get(headKey);
				post.addHeader(headKey, headValue);
			}

			if (!headers.has("content-type"))
				post.addHeader("content-type", "application/json");

			StringEntity entity = new StringEntity(body.toString(), StandardCharsets.UTF_8);
			post.setEntity(entity);

			HttpResponse httpResponse = CLIENT.execute(post);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				String content = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8).trim();
				if (content.isEmpty())
					throw new NoContentException("no content available");
				LOGGER.debug("httpPost successful :: {}", content);
				return type.equals(AcceptType.JSON_OBJECT) ? new JSONObject(content) : new JSONArray(content);
			} else {
				String error = "Error via Http with status code " + httpResponse.getStatusLine().getStatusCode();
				LOGGER.error("httpPost failed :: {}", error);
			}

		} catch (JSONException e) {
			LOGGER.error("httpPost failed :: response type is not {} {}", type, e);
		} catch (Exception e) {
			LOGGER.error("httpPost failed :: {} {} {}", uri, headers, body, e);
		}

		return type.equals(AcceptType.JSON_OBJECT) ? new JSONObject() : new JSONArray();
	}

	public static Object get(String uri, JSONObject headers, AcceptType type) {
		LOGGER.info("httpGet :: {} {}", uri, headers);

		try {
			HttpPost get = new HttpPost(uri);

			// setting headers to the post
			Iterator<String> headKeys = headers.keys();
			while (headKeys.hasNext()) {
				String headKey = headKeys.next();
				String headValue = (String) headers.get(headKey);
				get.addHeader(headKey, headValue);
			}

			HttpResponse httpResponse = CLIENT.execute(get);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				String content = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
				if (content.isEmpty())
					throw new NoContentException("no content available");
				LOGGER.info("httpGet successful :: {}", content);
				return type.equals(AcceptType.JSON_OBJECT) ? new JSONObject(content) : new JSONArray(content);
			} else {
				String error = "Error via Http with status code " + httpResponse.getStatusLine().getStatusCode();
				LOGGER.error("httpGet failed :: {}", error);
			}

		} catch (JSONException e) {
			LOGGER.error("httpGet failed :: response type is not {} {}", type, e);
		} catch (Exception e) {
			LOGGER.error("httpGet failed :: {} {}", uri, headers, e);
		}

		return type.equals(AcceptType.JSON_OBJECT) ? new JSONObject() : new JSONArray();
	}

	public enum AcceptType {
		JSON_OBJECT, JSON_ARRAY
	}

	private Http() {}
}
