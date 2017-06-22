package gameStuff2;

import java.io.*;

import audio.AudioMaster;
import audio.SourcesManager;
import data.chunkLoading.ChunkSaver;
import gameStuff.*;
import mainInterface.Intraface;
import network.Client;
import network.Server;
import threadingStuff.ThreadManager;
import toolBox.Meth;
import toolBox.configStuff.Config;
import weather.WeatherController;

public class ServerLoop {

	public static boolean alive = true;
	public static boolean isServer = false;
	public static boolean CLEANUPNOW;
	public static boolean running = true;
	
	public static Config config;

	public static void main(String[] args) {
		new Thread("commandListener") {// REPLACE WITH UPDATE METHOD IN e.g.
			// DISPLAYMANAGER.UPDATE ?
			@Override
			public void run() {
				try {
					BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
					while (running) {// !!!
						while (!r.ready()) {
							try {
								sleep(500);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						String input = r.readLine();
						if (input.equalsIgnoreCase("stop")) {
							running = false;
							CLEANUPNOW = true;
						} else if (input.equalsIgnoreCase("sc")){
							Server.init();
							Client.init();
						} else if (CommandProcessor.addCommand(input)) {
							Err.err.println("User input: \"" + input + "\"");
						} else {
							Err.err.println("HÄ? \"" + input + "\"");
						}
					}
//					System.out.println("commandListener stopped!");
				} catch (IOException i) {
					i.printStackTrace();
				}
			}
		}.start();
		
		config = new Config("Server/config.txt");
		
		ChunkSaver.worldName = "../Server/" + config.getConfig("worldName");
		if(ChunkSaver.worldName == null){
			Err.err.println("No World Specified!!! " + config.entryCount());
			Err.err.println(config.getConfigString());
			System.exit(-1);
		}
		
		isServer = true;
		Intraface.isServer = true;
		Intraface.singlePlayer = false;
		AudioMaster.soundEnabled = false;
		
		ThreadManager.goOn();

		WorldObjects.init();
		
//		System.out.println("inited WorldObjects");
		// SkyRenderer.setMoonThings();
		while (running) {
			if (CLEANUPNOW) {
				running = false;
				break;
			}
			update();
		}
		cleanUpGame();
		
		ThreadManager.shutdown();
		
		config.save();
		
		alive = false;
		
		for(int i = 0; i < 10 && Thread.activeCount() > 1; i++){
			System.out.println("waiting " + (10-i) + " seconds more for threads to finish bevore forcing exit");
			Meth.wartn(1000);
		}
		if(Thread.activeCount() > 1){
			System.out.println("forcing exit with " + (Thread.activeCount()-1) + " thread(s) too much open!");
			System.exit(0);
		}else{
			System.out.println("Exited gracefully");
		}
		// 2 Threads open (3 and Server stops a bit later!)
		
	}

	public static void update() {
		
		// time shit
		TM.update();
		// sounds
		SourcesManager.update();
		
		Intraface.update();
		
		// inGameStuff
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
//		ScreenPreferencesSaver.savePrefs();
//		Err.err.println("Preferences saved!");
	}

}
