package menuThings;

public class Action {

	private long lastTimeUsed;
	private Button b;

	public Action() {

	}

	public Action(Button b) {
		this.b = b;
	}

	public Button getButton() {
		return b;
	}

	/**
	 * DO NOT OVERRIDE! This is the method that is performed if needed.
	 */
	public void performAction() {
		lastTimeUsed = System.currentTimeMillis();
		actionPerformed();
	}

	/**
	 * Override this method to set a action!
	 */
	public void actionPerformed() {

	}

	public long timeSinceLastUsage() {
		return System.currentTimeMillis() - lastTimeUsed;
	}

	public void setButton(Button b2) {
		this.b = b2;
	}
}
