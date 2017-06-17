package controls;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.*;

import gameStuff.Err;
import network.SensorData;
import renderStuff.DisplayManager;
import toolBox.Tools;

public class Mouse {

	private static boolean useSensorData = false;

	public static float sensitivity = Tools.loadFloatPreference("MouseSensitivity", 10f);

	private static boolean grabbed = false;

	private static boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST],
			buttonsTipped = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

	private static double x, y, lastX, lastY, dscrolly, dscrollyFrame;

	public static void init() {
		glfwSetMouseButtonCallback(DisplayManager.getWindow(), new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW.GLFW_PRESS) {
					buttons[button] = true;
					buttonsTipped[button] = true;
				} else if (action == GLFW.GLFW_RELEASE) {
					buttons[button] = false;
				}
			}
		});
		glfwSetCursorPosCallback(DisplayManager.getWindow(), new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				x = xpos;
				y = ypos;
			}
		});
		glfwSetScrollCallback(DisplayManager.getWindow(), new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				dscrolly = yoffset;
				dscrollyFrame = yoffset;
			}
		});
		glfwSetCursorEnterCallback(DisplayManager.getWindow(), new GLFWCursorEnterCallback() {
			@Override
			public void invoke(long window, boolean entered) {
				if (entered) {
					setGrabbed(!grabbed);
					setGrabbed(!grabbed);
				}
				mouseInWindow = entered;
			}
		});

		Err.err.println("Mouse inited!");
	}
	
	private static boolean mouseInWindow;

	public static float getDX() {
		float ret = (float) (x - lastX);
		lastX = x;
		return ret;
	}

	public static float getDY() {
		float ret = (float) (y - lastY);
		lastY = y;
		return ret;
	}

	public static int getX() {
		return (int) x;
	}

	public static int getY() {
		return (int) y;
	}

	/**
	 * @return the x coordinate in percent
	 */
	public static float getAX() {
		return (float) ((x - DisplayManager.getXGUIOffset())
				/ (DisplayManager.getWidth() - DisplayManager.getXGUIOffset() * 2));
	}

	/**
	 * @return the y coordinate in percent; shifted so it fits for the GUI
	 */
	public static float getAY() {
		return (float) ((y - DisplayManager.getYGUIOffset())
				/ (DisplayManager.getHeight() - DisplayManager.getYGUIOffset() * 2));
	}

	public static boolean isButtonDown(int button) {
		if(Controller.USECONTROLLER){
			if(button == 0)
				return buttons[0] || Controller.isButtonDown(Controller.RIGHT_SHOULDER);
			else if(button == 1)
				return buttons[1] || Controller.isButtonDown(Controller.LEFT_SHOULDER);
		}
		return buttons[button];
	}

	public static boolean isGrabbed() {
		return grabbed;
	}

	public static void setGrabbed(boolean b) {
		grabbed = b;
		if (grabbed) {
//			 glfwSetInputMode(DisplayManager.getWindow(),
//			 GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			hideCursor();
		} else {
			showCursor();
//			 glfwSetInputMode(DisplayManager.getWindow(),
//			 GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		}
		Tools.mouseGrabbed = grabbed;
	}

	public static void hideCursor() {
		glfwSetInputMode(DisplayManager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}

	public static void showCursor() {
		glfwSetInputMode(DisplayManager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}

	public static void setCursorPosition(double newx, double newy) {
		glfwSetCursorPos(DisplayManager.getWindow(), newx, newy);
		x = newx;
		y = newy;
		lastX = x;
		lastY = y;
	}

	/**
	 * @return a float representing how much the mouse wheel has been scrolled
	 *         since the last call
	 */
	public static float getDWheel() {
		float ret = (float) dscrolly;
		dscrolly = 0;
		return ret;
	}

	/**
	 * @return a float representing how much the mouse wheel has been scrolled
	 *         this frame
	 */
	public static float getDWheelFrame() {
		return (float) dscrollyFrame;
	}

	public static boolean buttonClickedThisFrame(int button) {
		if(!buttonsTipped[button] && Controller.USECONTROLLER){
			if(button == 0){
				return Controller.buttonTippedThisFrame(Controller.RIGHT_SHOULDER);
			}else if(button == 1){
				return Controller.buttonTippedThisFrame(Controller.LEFT_SHOULDER);
			}
			return false;
		}
		return buttonsTipped[button];
	}

	/**
	 * shall be called after every frame is drawn and before
	 * {@code glfwPollEvents()}
	 */
	public static void updateSomething() {
		for (int i = 0; i < buttonsTipped.length; i++) {
			buttonsTipped[i] = false;
		}
		dscrollyFrame = 0;
		if (useSensorData) {
			if (SensorData.proximity == 0) {// !!!
				buttons[0] = true;
				sensorB = true;
			} else if (sensorB) {
				buttons[0] = false;
				sensorB = false;
			}
		}
		
//		if(Controller.USECONTROLLER && MainLoop.ANYMENUOPEN && (Controller.getAxis(Controller.LR_RIGHT_STICKER) != 0
//				|| Controller.getAxis(Controller.UD_RIGHT_STICKER) != 0)){
//			setCursorPosition(x+400*Controller.getAxis(Controller.LR_RIGHT_STICKER)*DisplayManager.getFrameTimeSeconds(), 
//					y+400*Controller.getAxis(Controller.UD_RIGHT_STICKER)*DisplayManager.getFrameTimeSeconds());
//		}
		
	}
	
	public static void updateControllerInputForMouse(){
		if(Controller.USECONTROLLER && mouseInWindow){
			setCursorPosition(x+400*Controller.getAxis(Controller.LR_RIGHT_STICKER)*DisplayManager.getFrameTimeSeconds(), 
					y+400*Controller.getAxis(Controller.UD_RIGHT_STICKER)*DisplayManager.getFrameTimeSeconds());
//			glfwPollEvents();
//			x += 400*Controller.getAxis(Controller.LR_RIGHT_STICKER)*DisplayManager.getFrameTimeSeconds();
//			y += 400*Controller.getAxis(Controller.UD_RIGHT_STICKER)*DisplayManager.getFrameTimeSeconds();
		}
	}
	
	public static boolean mouseInWindow(){
		return mouseInWindow;
	}

	private static boolean sensorB;

	public static void save() {
		Tools.setFloatPreference("MouseSensitivity", sensitivity);
	}

}
