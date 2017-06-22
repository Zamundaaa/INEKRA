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
				TM.setDayTime(Integer.parseInt(setTime.substring(10)));
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

	public static final String RAIN = "/rain";
	public static final String STOPRAIN = "/stopweather";
	public static final String HELP1 = "/help";
	public static final String HELP2 = "/?";
	public static final String toggleBurn = "/toggleburn";
	public static final String day = "/day";
	public static final String night = "/night";
	public static final String speedup = "/speedup";
	public static final String slowdown = "/slowdown";
	public static final String teleport = "/tp";
	public static final String script = "/order66";
	public static final String setTime = "/time set";
	public static final String setServer = "/setServer";
	public static final String setClient = "/setClient";
	public static final String saveScript = "/saveS";
	public static final String lstrike = "/lstrike";

	public static boolean isServer = false, isClient = false;

}
