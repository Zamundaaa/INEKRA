package gameStuff2;

import static gameStuff.MainLoop.CLEANUPNOW;
import static gameStuff.MainLoop.running;

import audio.MusicManager;
import audio.SourcesManager;
import gameStuff.*;
import renderStuff.DisplayManager;
import skybox.SkyRenderer;
import threadingStuff.ThreadManager;
import toolBox.FontColorManager;
import weather.WeatherController;

public class ServerLoop {

	public static boolean isServer = false;

	public static void run() {
		isServer = true;
		ThreadManager.goOn();
		WorldObjects.init();
		SkyRenderer.setMoonThings();
		while (running && !DisplayManager.isCloseRequested()) {
			if (CLEANUPNOW) {
				running = false;
				break;
			}
			update();
		}
		cleanUpGame();
	}

	public static void update() {
		// time shit
		TM.update();
		// color shit
		FontColorManager.update();
		// sounds/music
		SourcesManager.update();
		MusicManager.update();

		// inGameStuff
		WorldObjects.update();
		TickManager.update();
	}

	public static void cleanUpGame() {
		Err.err.println("Saving stuff...");
		ThreadManager.shutdown();
		WeatherController.save();
		Err.err.println("Weather settings saved!");
		WorldObjects.save();
		WorldObjects.cleanUp();
		Err.err.println("World saved!");
		TM.save();
		Err.err.println("Time settings saved!");
		PreferenceSaver.savePrefs();
		Err.err.println("Preferences saved!");
	}

}
