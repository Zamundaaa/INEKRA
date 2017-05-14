package weather;

import java.util.ArrayList;

import gameStuff.TM;
import renderStuff.DisplayManager;

public class WeatherMap {

	private static ArrayList<Cloud> clouds = new ArrayList<Cloud>();

	private static int MIN = 5, MAX = 30;

	public static float getSpawnChance() {
		if (NOC() < MIN) {
			return DisplayManager.getFrameTimeSeconds() * TM.TIMEFACT;
		} else if (NOC() >= MIN && NOC() <= MAX) {
			return DisplayManager.getFrameTimeSeconds() * ((MAX - NOC()) / (float) MAX) * TM.TIMEFACT;
		} else {
			return 0;
		}
	}

	public static float getChanceForThunderCloud() {
		return 0.05f;
	}

	public static int NOC() {
		return clouds.size();
	}

	public static void add(Cloud c) {
		clouds.add(c);
	}

	public static void remove(Cloud c) {
		clouds.remove(c);
	}

	public static void update() {
		for (int i = 0; i < clouds.size(); i++) {
			clouds.get(i).update();
		}
		// if(Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_7)){
		// Out.println("Nr of clouds: " + clouds.size());
		// }
	}

}
