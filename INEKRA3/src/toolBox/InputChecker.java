package toolBox;

import java.util.ArrayList;

public abstract class InputChecker {

	private static ArrayList<InputChecker> checks = new ArrayList<InputChecker>();

	private long lastTime = 0;
	private float timeDiff;

	public InputChecker(float timeDiff) {
		this.timeDiff = timeDiff;
		checks.add(this);
	}

	public void update() {
		if (lastTime + timeDiff >= System.currentTimeMillis()) {
			doThing();
		}
	}

	public abstract void doThing();

	public static void checkThings() {
		for (int i = 0; i < checks.size(); i++) {
			checks.get(i).update();
		}
	}

}
