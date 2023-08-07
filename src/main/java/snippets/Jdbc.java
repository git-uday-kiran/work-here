package snippets;

import java.sql.Connection;

import com.mysql.cj.conf.ConnectionUrl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Jdbc {

	public static String db_name;
	public static String user_name, password;

	public static int port;
	public static String host_name;
	public static String jdbc_url, jdbc_driver;

	private static HikariConfig config;
	private static HikariDataSource dataSource;

	static {
		setDefaultProperties();
		try {

			boolean autoCommit = true;
			int connectionTimeout = 30 * 1000; // Default: 30000 (30 seconds

			int maximumPoolSize = 10; // Default: 10
			int maxLifetime = 1800 * 1000; // The minimum allowed value is 30000ms (30 seconds). Default: 1800000 (30 minutes)

			int minimumIdle = 10; //  Default: same as maximumPoolSize
			int idleTimeout = 600 * 1000; // The minimum allowed value is 10000ms (10 seconds). Default: 600000 (10 minutes)

			// int keepaliveTime = 30 * 1000; // The minimum allowed value is 30000ms (30 seconds), Default: 0 (disabled)

			config = new HikariConfig();
			config.setPoolName("mysql hikari pool");

			config.setAutoCommit(autoCommit);
			config.setConnectionTimeout(connectionTimeout);

			config.setMaximumPoolSize(maximumPoolSize);
			config.setMaxLifetime(maxLifetime);

			config.setMinimumIdle(minimumIdle);
			config.setIdleTimeout(idleTimeout);

			config.setLeakDetectionThreshold(30000);
			config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
			config.addDataSourceProperty("useSSL", "false");
			config.addDataSourceProperty("characterEncoding", "UTF-8");
			config.addDataSourceProperty("useUnicode", "true");
			config.addDataSourceProperty("useJDBCCompliantTimezoneShift", "true");
			config.addDataSourceProperty("useLegacyDatetimeCode", "false");
			config.addDataSourceProperty("serverTimezone", "UTC");

			config.setUsername(user_name);
			config.setPassword(password);
			config.setSchema(db_name);
			config.setJdbcUrl(jdbc_url);
			config.setDriverClassName("com.mysql.cj.jdbc.Driver");

			dataSource = new HikariDataSource(config);
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("Hikari Configuration Loaded...");
	}

	private static void setDefaultProperties() {
		user_name = "root";
		password = "root";
		db_name = "ushaknowledge";
		host_name = ConnectionUrl.DEFAULT_HOST;
		port = ConnectionUrl.DEFAULT_PORT;
		jdbc_driver = "com.mysql.cj.jdbc.Driver";
		jdbc_url = "jdbc:mysql://" + host_name + ":" + port + "/" + db_name;
	}

	public static Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
