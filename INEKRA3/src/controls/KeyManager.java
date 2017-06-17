package controls;

import org.lwjgl.glfw.GLFW;

/**
 * this class is meant to contain all Key-Constants (for easy configuration).
 *  It may also contain key-combinations (planned as int[] arrays!).
 *  Some methods that provide fast access to those combinations are in here as well (to shorten code in other places)
 */
public class KeyManager {

	public static int SWITCH_DEBUGPANEL = GLFW.GLFW_KEY_L;
	
	public static boolean switchDebugPanel(){
		return Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && Keyboard.keyPressedThisFrame(SWITCH_DEBUGPANEL);
	}
	
	public static boolean escapeEquivalentPressed(){
		if(Controller.USECONTROLLER && Controller.buttonTippedThisFrame(Controller.X)){
			return true;
		}
		return Keyboard.isKeyDown(GLFW.GLFW_KEY_ESCAPE);
	}

}
