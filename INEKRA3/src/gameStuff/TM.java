package gameStuff;

import mainInterface.Intraface;
import renderStuff.DisplayManager;
import renderStuff.MasterRenderer;
import skybox.SkyRenderer;
import toolBox.Tools;
import weather.WeatherController;

public class TM {

	private static long GTS = Tools.loadLongPreference("GTS", 0);
	private static float stretchedTime = Tools.loadFloatPreference("stretchedTime", 0);
	private static float timebuffer;

	public static final int FRÜHLING = 1, SOMMER = 2, HERBST = 3, WINTER = 4;

	public static final String F = "spring", S = "sommer", H = "autumn", W = "winter";

	public static final float morningstart = 5, morning = 7, eveningstart = 17.5f, night = 19;
	public static float TIMEFACT = Tools.loadFloatPreference("TIMEFACT", 1);

	// private static int SEASON = 1;

	private static double time;// = Tools.loadFloatPreference("time", 15);
	private static double ingameDays;// = Tools.loadFloatPreference("gameTimeMillis", 1 / 15f);
	private static long fromStartMillis = 0;
	private static int JAHRESZEIT;

	public static String season() {
		switch (jahresZeit()) {
		case FRÜHLING:
			return F;
		case SOMMER:
			return S;
		case HERBST:
			return H;
		case WINTER:
			return W;
		}
		return "ERROR " + (ingameDays / 168) + " Ergebnis " + ((ingameDays / 168) % 4);
	}

	public static void save() {
		Tools.setFloatPreference("time", (float) time);
		Tools.setFloatPreference("gameTimeMillis", (float) ingameDays);
		Tools.setLongPreference("GTS", GTS);
		Tools.setFloatPreference("stretchedTime", stretchedTime);
		Tools.setFloatPreference("TIMEFACT", TIMEFACT);
	}

	public static int jahresZeit() {
		return JAHRESZEIT;
	}

	public static double getDayTime() {
		return time;
	}

	/**
	 * @return GTMs with really great precision!
	 */
	public static double inGameDays() {
		return ingameDays;
	}

	public static long GTS() {
		return GTS;
	}

	/**
	 * @return just a value for calculation (more accurate GTS; 100 times
	 *         smaller!)
	 */
	public static float sT() {
		return stretchedTime;
	}

	private static final double mul = 1 / 24.0;

	public static void update() {
		// for (int i = 0; i <= 9; i++) {
		// if (Keyboard.keyTipped(GLFW.GLFW_KEY_0 + i)) {
		// TIMEFACT = i;
		// }
		// }
		if (!MainLoop.MENUOPEN) {
			timebuffer += TIMEFACT * DisplayManager.getFrameTimeSeconds();
			stretchedTime += TIMEFACT * DisplayManager.getFrameTimeSeconds() / 100;
			while (timebuffer >= 1) {
				GTS++;
				timebuffer--;
			}
			time += TIMEFACT * DisplayManager.getFrameTimeSeconds() * 0.1f;
			time %= 24;
			ingameDays += TIMEFACT * DisplayManager.getFrameTimeSeconds() * 0.1 * mul;
			updateSkyboxColorUSW();
			JAHRESZEIT = (int) (((ingameDays / 91.25f) % 4)) + 1;
		}
		fromStartMillis += DisplayManager.getFrameTimeMillis();
	}

	public static long fromStartMillis() {
		return fromStartMillis;
	}

	public static boolean isNight() {
		return (time >= eveningstart || time < morningstart);
	}

	public static boolean isDay() {
		return (time < eveningstart && time > morningstart);
	}

	public static void setNextDay() {
		while (!isDay()) {
			ingameDays += 0.5f * mul;
			time = (ingameDays%1)*24;
		}
	}

	public static void setNextNight() {
		while (!isNight()) {
			ingameDays += 0.5f * mul;
			time = (ingameDays%1)*24;
		}
	}

	private static float dayr = 0.4444f, dayg = 0.52f, dayb = 0.59f;
	private static float nightr = 0.03f, nightg = 0.03f, nightb = 0.03f;

	private static void updateSkyboxColorUSW() {
		// MainLoop.time((float) time);
		if(!Intraface.isServer)
			WorldObjects.timeUpdate();
//		if (!SkyRenderer.SKYBOXPIC) {
			if ((time >= night || time >= 0) && time < morningstart) {
				MasterRenderer.setFogColor(nightr, nightg, nightb);
			} else if (time >= morningstart && time < morning) {
				float fact = (float) ((time - morningstart) / (morning - morningstart));
				// WorldObjects.sun.setColour(new Vector3f((fact * 0.8f >
				// nightColor) ? fact * 0.8f : nightColor,
				// (fact * 0.8f > nightColor) ? fact * 0.8f : nightColor,
				// (fact * 0.8f > nightColor) ? fact * 0.8f : nightColor));
				MasterRenderer.setFogColor((fact * dayr > nightr) ? fact * dayr : nightr,
						(fact * dayg > nightg) ? fact * dayg : nightg, (fact * dayb > nightb) ? fact * dayb : nightb);
			} else if (time >= morning && time < eveningstart) {
				// WorldObjects.sun.setColour(new Vector3f(0.8f, 0.8f, 0.8f));
				// MasterRenderer.setFogColor(0.5444f, 0.62f, 0.69f);
				MasterRenderer.setFogColor(dayr, dayg, dayb);
			} else {
				// float fact = (time - 21) / (24 - 21);
				float fact = (float) ((time - eveningstart) / (night - eveningstart));
				fact = 1 - fact;
				if (fact < 1)
					MasterRenderer.setFogColor(((fact * dayr) > nightr) ? (fact * dayr) : nightr,
							((fact * dayg) > nightg) ? (fact * dayg) : nightg,
							((fact * dayb) > nightb) ? (fact * dayb) : nightb);
			}
			float b = WeatherController.blendFactor();
			MasterRenderer.setFogColor((MasterRenderer.r * (1 - b) + WeatherController.getWeatherColor().x * b),
					(MasterRenderer.g * (1 - b) + WeatherController.getWeatherColor().y * b),
					(MasterRenderer.b * (1 - b) + WeatherController.getWeatherColor().z * b));
//		}
		SkyRenderer.updateTime((float) time);
	}

	public static float particleColorMult() {
		return SkyRenderer.getTimeB();
	}

	public static void setDayTime(int t) {
		while (time < t) {
			time += 0.1f;
			time %= 24;
			ingameDays += 0.1f * mul;
		}
	}

	public static void setIngameDays(double d) {
		ingameDays = d;
		time = (ingameDays%1)*24;
	}

}
