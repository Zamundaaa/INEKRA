package gameStuff;

import java.util.ArrayList;
import java.util.List;

import entities.TickingThing;

public class TickManager {

	private static List<TickingThing> ticks = new ArrayList<TickingThing>();
	public static volatile boolean updating = false;

	public static void addTickingThing(TickingThing t) {
		if (!ticks.contains(t))
			ticks.add(t);

	}

	public static void removeTickingThing(Object t) {
		ticks.remove(t);
	}

	public static void update() {
		updating = true;
		for (int i = 0; i < ticks.size(); i++) {
			TickingThing t = ticks.get(i);
			if (t.update()) {
				ticks.remove(t);
				i--;
			}
		}
		updating = false;
	}

}
