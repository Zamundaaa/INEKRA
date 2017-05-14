package controls;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayDeque;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharModsCallback;

import gameStuff.Err;
import renderStuff.DisplayManager;

public class Keyboard {

	private static boolean[] pressed = new boolean[GLFW_KEY_LAST];
	private static boolean[] tipped = new boolean[GLFW_KEY_LAST];
	private static boolean[] t2 = new boolean[GLFW_KEY_LAST];
	private static ArrayDeque<Character> characters = new ArrayDeque<Character>();

	public static void init() {
		// Setup a key callback. It will be called every time a key is pressed,
		// repeated or released.
		glfwSetKeyCallback(DisplayManager.getWindow(), (window, key, scancode, action, mods) -> {
			// if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
			// glfwSetWindowShouldClose(window, true); // We will detect this in
			// the rendering loop
			try {
				if (key >= 0) {
					if (action == GLFW_PRESS) {
						pressed[key] = true;
						tipped[key] = true;
						t2[key] = true;
						if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
							characters.add('\u0008');
						}
					} else if (action == GLFW_RELEASE) {
						pressed[key] = false;
					}
					if (action == GLFW_REPEAT) {
						tipped[key] = true;
						if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
							characters.add('\u0008');
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException a) {
				a.printStackTrace(Err.err);
			}
		});

		glfwSetCharModsCallback(DisplayManager.getWindow(), new GLFWCharModsCallback() {
			@Override
			public void invoke(long window, int codepoint, int mods) {
				char[] chars = Character.toChars(codepoint);
				characters.add(mods == GLFW.GLFW_MOD_SHIFT ? Character.toUpperCase(chars[0]) : chars[0]);
			}
		});

		// @Override
		// public void invoke(long window, int codepoint) {
		// characters.add(Character.toChars(codepoint)[0]);
		// }

		Err.err.println("Keyboard inited!");

	}

	public static boolean isKeyDown(int keyCode) {
		return pressed[keyCode];
	}

	public static void updateSomething() {
		for (int i = 0; i < t2.length; i++) {
			t2[i] = false;
		}
	}

	// public static boolean isKeyDown(int key) {
	// return GLFW.glfwGetKey(DisplayManager.getWindow(), key) ==
	// GLFW.GLFW_PRESS;
	// }

	/**
	 * @param keyCode
	 * @return if the key has been pressed (since the last reset) !! sets
	 *         tipped-value to false !!
	 */
	public static boolean keyTipped(int keyCode) {
		boolean ret = tipped[keyCode];
		tipped[keyCode] = false;
		return ret;
	}

	public static boolean keyPressedThisFrame(int key) {
		return t2[key];
	}

	public static void resetTip(int keyCode) {
		tipped[keyCode] = false;
	}

	public static void resetTips() {
		for (int i = 0; i < tipped.length; i++) {
			tipped[i] = false;
		}
	}

	public static boolean nextCharAvailable() {
		return characters.size() > 0;
	}

	/**
	 * @return the next char if available, else Character.MAX_VALUE
	 */
	public static char getNext() {
		if (characters.size() > 0) {
			char c = characters.pop();
			return c;
		} else {
			return Character.MAX_VALUE;
		}
	}

	public static void resetChars() {
		characters.clear();
	}

	public static ArrayDeque<Character> getPressedChars() {
		return characters;
	}

}
