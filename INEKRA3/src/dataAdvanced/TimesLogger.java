package dataAdvanced;

import java.util.*;

public class TimesLogger {

	private static final Map<String, ArrayList<Long>> map = new HashMap<String, ArrayList<Long>>();

	public static void log(String code, long millis) {
		ArrayList<Long> l = map.get(code);
		if (l == null) {
			l = new ArrayList<Long>();
			map.put(code, l);
		}
		l.add(millis);
	}

	public static long d(String code) {
		ArrayList<Long> l = map.get(code);
		if (l != null) {
			long all = 0;
			for (int i = 0; i < l.size(); i++) {
				all += l.get(i);
			}
			return (long) ((double) all / l.size());
		} else {
			System.out.println("No logs found for code '" + code + "'");
			return Long.MAX_VALUE;
		}
	}

}
