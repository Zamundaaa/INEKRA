package gameStuff2;

import java.util.ArrayList;

import gameStuff.TM;
import network.Client;
import network.Server;
import toolBox.Meth;
import weather.WeatherController;

public class CommandProcessor {

	private static ArrayList<String> cmds = new ArrayList<String>();
	private static volatile boolean working = false;

	public static boolean addCommand(String command) {
		if (!command.startsWith("/")) {
			return false;
		} else {
			if (command.startsWith(RAIN) || command.startsWith(STOPRAIN) || command.startsWith(day)
					|| command.startsWith(night) || command.startsWith(setTime)
					|| (!isServer && command.startsWith(setServer)) || (!isClient && command.startsWith(setClient))) {
				addCmd(command);
				return true;
			} else {
				return false;
			}
		}
	}

	private static void addCmd(String cmd) {
		while (working) {
			Meth.wartn(3);
		}
		cmds.add(cmd);
	}

	public static void update() {
		working = true;
		for (int i = 0; i < cmds.size(); i++) {
			execute(cmds.get(i));
		}
		cmds.clear();
		working = false;
	}

	private static void execute(String cmd) {
		if (cmd.startsWith(RAIN)) {
			WeatherController.makeItRAIN();
		} else if (cmd.startsWith(STOPRAIN)) {
			WeatherController.stopItNOW();
		} else if (cmd.startsWith(day)) {
			TM.setNextDay();
		} else if (cmd.startsWith(night)) {
			TM.setNextNight();
		} else if (cmd.startsWith(setTime)) {
			try {
				TM.setDayTime(Integer.parseInt(setTime.substring(11)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (cmd.startsWith(setServer)) {
			isServer = true;
			Server.init();
		} else if (cmd.startsWith(setClient)) {
			isClient = true;
			Client.init();
		}
	}

	public static String RAIN = "/rain";
	public static String STOPRAIN = "/stopweather";
	public static String HELP1 = "/help";
	public static String HELP2 = "/?";
	public static String toggleBurn = "/toggleburn";
	public static String day = "/day";
	public static String night = "/night";
	public static String speedup = "/speedup";
	public static String slowdown = "/slowdown";
	public static String teleport = "/tp";
	public static String script = "/order66";
	public static String setTime = "/time set";
	public static String setServer = "/setServer";
	public static String setClient = "/setClient";
	public static String saveScript = "/saveS";

	public static boolean isServer = false, isClient = false;

}
