package chatStuff;

import static gameStuff2.CommandProcessor.*;

import java.util.ArrayDeque;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import controls.Keyboard;
import dataAdvanced.*;
import entities.Player;
import entities.graphicsParts.Texes;
import fontMeshCreator.GUIText;
import fontRendering.Out;
import gameStuff.*;
import guis.GUIManager;
import guis.GuiTexture;
import toolBox.*;
import weather.WeatherController;

public class Chat {

	private static GuiTexture background = new GuiTexture(Models.getLoadedTex(Texes.button), new Vector2f(0, -0.9f),
			new Vector2f(0.5f, 0.025f), false);

	private static boolean open = false;
	private static long lastOpen = Meth.systemTime();
	private static String input = "";
	private static GUIText in;
	private static Vector2f pos = new Vector2f(0.25f, 0.93f);

	public static void update() {

		if (Meth.systemTime() > lastOpen + 500 && Keyboard.isKeyDown(GLFW.GLFW_KEY_TAB)) {
			open = !open;
			lastOpen = Meth.systemTime();
			if (open) {
				open();
			} else {
				close();
			}
		} else if (Keyboard.keyPressedThisFrame(GLFW.GLFW_KEY_ESCAPE)) {
			if (open) {
				lastOpen = Meth.systemTime();
				close();
			}
			open = false;
		}

		if (open) {
			ArrayDeque<Character> chars = Keyboard.getPressedChars();
			boolean bool = false;
			// for (int i = 0; i < chars.size(); i++) {
			while (chars.size() > 0) {
				char c = chars.pop();
				if (c != '\u0008') {
					input += c;
				} else {
					if (input.length() > 0) {
						input = input.substring(0, input.length() - 1);
					}
				}
				// c = Keyboard.getEventCharacter();
				bool = true;
			}

			if (Keyboard.isKeyDown(GLFW.GLFW_KEY_ENTER) && input.length() > 0) {
				addMessage(new Message(input));
				if (input.startsWith("/")) {
					if (input.equals(RAIN)) {
						WeatherController.makeItRAIN();
					} else if (input.equals(STOPRAIN)) {
						WeatherController.stopItNOW();
					} else if (input.equals(HELP1) || input.equals(HELP2)) {
						showAllCommands();
					} else if (input.equals(toggleBurn)) {
						Player.players.get(0).GHOSTRIDER = !Player.players.get(0).GHOSTRIDER;
						Tools.setBoolPreference("burn", Player.players.get(0).GHOSTRIDER);
						// } else if (input.startsWith(speedup)) {
						// Sapling.GROWSPEED = 5;
						// } else if (input.startsWith(slowdown)) {
						// Sapling.GROWSPEED = 1;
					} else if (input.equals(day)) {
						TM.setNextDay();
					} else if (input.equals(night)) {
						TM.setNextNight();
					} else if (input.startsWith(teleport)) {
						try {
							input = input.substring(4);
							String[] is = input.split(" ");
							if (is.length < 3) {
								Out.println("no valid input! what you probably wanted is: /tp X Y Z");
							} else {
								Player.players.get(0).getPosition().x = Integer.parseInt(is[0]);
								Player.players.get(0).getPosition().y = Integer.parseInt(is[1]);
								Player.players.get(0).getPosition().z = Integer.parseInt(is[2]);
							}
						} catch (Exception e) {
							Out.println("no valid input! what you probably wanted is: /tp X Y Z");
						}
					} else if(input.startsWith(lstrike)){
						Vector3f v = MousePicker.getNextFilledBlockCoord(100);
						if(v != null){
							WeatherController.lstrikeback(v);
						}
					} else if (input.startsWith(script)) {
						try {
							input = input.substring(script.length() + 1);
							Script s = Script.loadScript(input);
							Builder.build(Player.players.get(0).getPosition().x, Player.players.get(0).getPosition().y,
									Player.players.get(0).getPosition().z, s);
							Out.println("Order 66 ausgeführt!");
						} catch (Exception e) {
							Out.println("No such script available! " + input);
						}
					} else if (input.startsWith(saveScript)) {
						input = input.substring(saveScript.length() + 1);
						try {
							Mark.saveToFile(input);
						} catch (Exception e) {
							if (e.getMessage() != null)
								Out.println(e.getMessage());
						}
					} else {
						Out.println("no such command?");
					}
					close();
				}
				input = "";
				bool = true;
			}

			if (bool) {
				if (in != null) {
					in.setText(input);
				} else {
					in = new GUIText(input, 1.25f, SC.font, pos, 0.5f, true);
					in.setColour(1, 1, 1);
				}
				if (in.getNumberOfLines() > 2) {
					pos.y = 0.93f - (in.getNumberOfLines() - 2) * 0.04f;
				} else {
					pos.y = 0.93f;
				}
			}
		}

		updateMessages();

	}

	public static void open() {
		GUIManager.addGuiTexture(background);
		Player.setNOCONTROL(true);
		if (in != null) {
			in.show();
		}
		// CharHolder.update();
		// CharHolder.reset();
		Keyboard.resetChars();
		open = true;
	}

	public static void close() {
		GUIManager.removeGuiTexture(background);
		Player.setNOCONTROL(false);
		open = false;
		if (in != null) {
			in.hide();
		}
	}

	public static void clear() {
		input = "";
		in.cleanUp();
		for (int i = 0; i < messages.size(); i++) {
			messages.get(i).cleanUp();
		}
		messages.clear();
	}

	public static boolean isOpen() {
		return open;
	}

	public static void showAllCommands() {
		Out.println(RAIN + " || " + STOPRAIN + " || " + toggleBurn + " || " + day + " || " + night + " || " + speedup
				+ " || " + slowdown + " || " + teleport + " || " + script + " || " + saveScript);
	}

	private static final ArrayList<Message> messages = new ArrayList<Message>();

	public static void addMessage(Message m) {
		messages.add(m);
	}

	private static void updateMessages() {
		float y = 0.8f;
		for (int i = messages.size() - 1; i > -1; i--) {
			Message m = messages.get(i);
			if (m.timeSinceCreation() > 10000) {
				m.cleanUp();
				messages.remove(i);
			} else {
				m.setPos(0.2f, y);
				y -= m.getDY();
			}
		}
	}

	public static void hideCompletely() {
		for(int i = 0; i < messages.size(); i++){
			messages.get(i).hide();
		}
	}
	
	public static void showAgain(){
		for(int i = 0; i < messages.size(); i++){
			messages.get(i).show();
		}
	}

}
