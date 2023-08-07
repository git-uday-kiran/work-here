package snippets;

public class Timer {
	public static void sleep(Float seconds) {
		int time = (int) (seconds * 1000);
		try {
			Thread.sleep(time);
		} catch (Exception e) {}
	}
}
