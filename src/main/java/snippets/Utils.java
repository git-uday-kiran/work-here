package snippets;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.json.JSONArray;

public class Utils {

	public static void countryCity() throws SQLException {
		Connection con = Jdbc.getConnection();
		batchStatement = con.createStatement();
		try {
			query();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Completed, executing queries...");
		try {
			batchStatement.executeBatch();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(getHindi("Uday Kiran,Mekala"));
		System.out.println("Done Successfully");
	}

	public static void query() {
		String query = "SELECT * FROM country_city_state.country_city";
		try (Connection con = Jdbc.getConnection(); Statement statement = con.createStatement()) {
			ResultSet rs = statement.executeQuery(query);
			StringJoiner sj = new StringJoiner(",");
			while (rs.next()) {
				final String city = rs.getString("City");
				final String country = rs.getString("Country");
				sj.add(city).add(country);
			}
			System.out.println("readed result set");

			List<String> chunks = getChunks(sj.toString());
			System.out.println("got chunks, size: " + chunks.size());

			chunks.parallelStream().map(text -> getHindi(text)).forEach(list -> write(list));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<String> getChunks(String text) {
		List<String> rs = new ArrayList<>();
		final int mod = 20;
		String[] strings = text.split(",");
		StringJoiner sj = new StringJoiner(",");
		for (int i = 0; i < strings.length; i = i + 2) {
			if (i % mod == 0) {
				rs.add(sj.toString());
				sj = new StringJoiner(",");
			}
			sj.add(strings[i]).add(strings[i + 1]);
		}
		if (!sj.toString().isEmpty())
			rs.add(sj.toString());
		return rs;
	}

	private static Statement batchStatement;

	public static void write(List<String> words) {
		try {
//			System.out.println(count.addAndGet(words.size() / 2));
			for (int i = 0; i < words.size(); i = i + 2) {
				final String city = words.get(i);
				final String country = (i + 1) < words.size() ? words.get(i + 1) : null;
				batchStatement.addBatch("insert into country_city_state.country_city_hindi values ('" + city + "', '" + country + "');");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public static List<String> getHindi(String text) {
		List<String> words = new ArrayList<>();
		try {
			URL url = URI.create("https://inputtools.google.com/request?text=" + text.replace(" ", "_") + "&itc=hi-t-i0-und&num=13&cp=0&cs=1&ie=utf-8&oe=utf-8&app=demopage").toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			text = new String((connection.getInputStream().readAllBytes()));
			JSONArray arr = new JSONArray(text);

			arr = arr.getJSONArray(1);
			for (int i = 0; i < arr.length(); i++)
				words.add(arr.getJSONArray(i).getJSONArray(1).getString(0));
		} catch (Exception ex) {
			System.out.println(text + ", " + ex);
		}
		return words;
	}
}
