package snippets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class EntityTable {
	public static Logger logger = LogManager.getLogger();

	public static void createSessionIfNotExist(String sessionId) {
		if (sessionId.isEmpty())
			return;
		String query = "SELECT session_id from entitytable where session_id = '" + sessionId + "'";
		logger.info("createSessionIfNotExist Query :: " + query);

		try (Connection con = Jdbc.getConnection()) {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if (rs.next())
				return;

			Timestamp timestamp = Timestamp.from(Instant.now());
			query = " INSERT INTO entitytable (`session_id`, `created_at`) VALUES ('" + sessionId + "', '" + timestamp + "') ";
			logger.info("sessionNotExist, Query {}", query);

			int rows = stmt.executeUpdate(query);
			logger.info("{} rows effected.", rows);
		} catch (SQLException e) {
			logger.error("SQLException while createSessionIfNotExist Query {}, Error {}", query, e);
		}
	}

	public static void setData(JSONObject session, String entity_name) {
		String query = "UPDATE entitytable SET `" + entity_name + "` = '" + session.optString(entity_name) + "' WHERE (`session_id` = '" + session.optString("session_id") + "');";
		logger.info("setData Query :: " + query);

		try (Connection con = Jdbc.getConnection()) {
			Statement stmt = con.createStatement();
			int rows = stmt.executeUpdate(query);
			logger.info("{} rows effected.", rows);
		} catch (SQLException e) {
			logger.error("SQLException while setData Query {}, Error {}", query, e);
		}
	}

	public static final Set<String> entities = Set.of("first_entry", "second_entry");

	public static void update(JSONObject json) {
		JSONObject session = json.getJSONObject("rezoSession").getJSONObject("sessionObj");
		createSessionIfNotExist(session.optString("session_id"));

		String entity_name = session.optString("funnel_from");
		if (entities.contains(entity_name) && session.has(entity_name))
			setData(session, entity_name);
		else
			logger.warn("no entity found, entity_name {}", entity_name);
	}
}
