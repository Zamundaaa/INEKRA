package toolBox;

import java.util.ArrayList;

import gameStuff.Err;

public class Logger {

	private static ArrayList<String> loggedErrors = new ArrayList<String>();
	private static ArrayList<Integer> numbers = new ArrayList<Integer>();

	public static void addLog(String error) {
		if (!loggedErrors.contains(error)) {
			loggedErrors.add(error);
			numbers.add(1);
		} else {
			int i = loggedErrors.indexOf(error);
			numbers.set(i, numbers.get(i) + 1);
		}
	}

	public static void giveLogs() {
		for (int i = 0; i < loggedErrors.size(); i++) {
			Err.err.println(loggedErrors.get(i) + " x " + numbers.get(i));
		}
	}

	public static void giveAndDeleteLogs() {
		while (loggedErrors.size() > 0) {
			System.err
					.println(loggedErrors.get(loggedErrors.size() - 1) + " x " + numbers.get(loggedErrors.size() - 1));
			numbers.remove(loggedErrors.size() - 1);
			loggedErrors.remove(loggedErrors.size() - 1);
		}
	}

	public static int giveLog(String errorCode) {
		if (loggedErrors.contains(errorCode)) {
			int i = loggedErrors.indexOf(errorCode);
			Err.err.println(loggedErrors.get(i) + " x " + numbers.get(i));
			return numbers.get(i);
		} else {
			return 0;
		}
	}

}
