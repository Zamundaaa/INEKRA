package gameStuff;

import renderStuff.DisplayManager;
import toolBox.Tools;

public class PreferenceSaver {

	public static void applyPrefs() {
		if (!DisplayManager.FULLSCREEN && !DisplayManager.searchBiggest) {
			int xPosOnScreen = (int) Tools.loadLongPreference("xPosOfDisplay", 50);
			int yPosOnScreen = (int) Tools.loadLongPreference("yPosOfDisplay", 50);
			DisplayManager.setWindowLocation(xPosOnScreen, yPosOnScreen);
		}
		int w = (int) Tools.loadLongPreference("WIDTH", 1200);
		int h = (int) Tools.loadLongPreference("HEIGHT", 720);
		DisplayManager.setWindowSize(w, h);
	}

	public static void savePrefs() {
		int xPosOnScreen = DisplayManager.getWindowX();
		int yPosOnScreen = DisplayManager.getWindowY();
		Tools.setLongPreference("xPosOfDisplay", xPosOnScreen);
		Tools.setLongPreference("yPosOfDisplay", yPosOnScreen);
		Tools.setLongPreference("WIDTH", DisplayManager.WIDTH);
		Tools.setLongPreference("HEIGHT", DisplayManager.HEIGHT);

	}

}
