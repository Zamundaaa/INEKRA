package fontRendering;

import static gameStuff.SC.font;

import java.awt.Color;
import java.util.ArrayList;

import org.joml.Vector2f;

import chatStuff.Chat;
import chatStuff.Message;
import fontMeshCreator.GUIText;

public class Out {

	private static ArrayList<GUIText> texts = new ArrayList<GUIText>();

	public static void update() {
		// for (int i = 0; i < texts.size(); i++) {
		// texts.get(i).shiftPosition(0, DisplayManager.getFrameTimeSeconds() /
		// 10);
		// if (texts.get(i).getPosition().y > 1.3f) {
		// // texts.get(i).remove();
		// texts.get(i).cleanUp();
		// texts.remove(i);
		// }
		// }
	}

	public static void println(char x, Color c) {
		println("" + x, c);
	}

	public static void println(int x, Color c) {
		println("" + x, c);
	}

	public static void println(long x, Color c) {
		println("" + x, c);
	}

	public static void println(float x, Color c) {
		println("" + x, c);
	}

	public static void println(double x, Color c) {
		println("" + x, c);
	}

	public static void println(String x, Color c) {
		texts.add(new GUIText(x, 1.5f, font, new Vector2f(0.3f, 0.3f), 0.175f, true));
		texts.get(texts.size() - 1).setColour(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
	}

	public static void println(char x) {
		println("" + x);
	}

	public static void println(int x) {
		println("" + x);
	}

	public static void println(long x) {
		println("" + x);
	}

	public static void println(float x) {
		println("" + x);
	}

	public static void println(double x) {
		println("" + x);
	}

	public static void println(String x) {
		Chat.addMessage(new Message(x));
		// texts.add(new GUIText(x, 1.5f, font, new Vector2f(0.8f, 0.3f), 0.2f,
		// true));
		// texts.get(texts.size() - 1).setColour(Meth.randomFloat(0, 1),
		// Meth.randomFloat(0, 1), Meth.randomFloat(0, 1));
	}

	public static void println(Object o) {
		println(o.toString());
	}

}
