package controls;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Controller {
	
	public static final int LR_LEFT_STICKER = 0,
							UD_LEFT_STICKER = 1,
							LR_RIGHT_STICKER = 2,
							UD_RIGHT_STICKER = 3,
							LR_BACK_RIGHT_TRIGGER = 4,
							LR_BACK_LEFT_TRIGGER = 5;
	public static final int A = 0, B = 1, X = 3, Y = 4,
							LEFT_SHOULDER = 6, RIGHT_SHOULDER = 7,
							LEFT_BACK = 8, RIGHT_BACK = 9,
							SELECT = 10,
							START = 11,
							LEFT_STICKER = 13,
							DPAD_LEFT = 15,
							DPAD_RIGHT = 16,
							DPAD_UP = 17,
							DPAD_DOWN = 18;
	
	public static boolean USECONTROLLER = true;
	
	private static boolean[] buttons = new boolean[30];
	private static boolean[] buttonsTippedThisFrame = new boolean[30];
	private static float[] axes = new float[8];
	
//	private static Button lrr = new Button("HI!", new Rectangle(200, 750, 200, 50), SC.getTex("white").getID(), false);
	
	public static void update(){
		String name = glfwGetJoystickName(GLFW_JOYSTICK_1);
		if(name == null){
			USECONTROLLER = false;
			return;
		}else{
			USECONTROLLER = true;
		}
//		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_Z))
//			System.out.println(name);
		
		for(int i = 0; i < buttonsTippedThisFrame.length; i++)
			buttonsTippedThisFrame[i] = false;
		ByteBuffer b = glfwGetJoystickButtons(GLFW_JOYSTICK_1);
		for(int i = 0; i < buttons.length && i < b.capacity(); i++){
			boolean pressed = b.get() == 1;
			if(pressed && !buttons[i]){
				buttonsTippedThisFrame[i] = true;
//				System.out.println("Button " + i + " down!");
			}
			buttons[i] = pressed;
		}
		
		FloatBuffer axisdata = glfwGetJoystickAxes(GLFW_JOYSTICK_1);
		for(int i = 0; i < 6; i++){
//			float a = axisdata.get();
//			if(axes[i] != a){
//				axes[i] = a;
//			}
			axes[i] = axisdata.get();
		}
		float dpad = axisdata.get();
		if(dpad == 1){
			buttons[DPAD_LEFT] = false;
			buttons[DPAD_RIGHT] = true;
		}else if(dpad == -1){
			buttons[DPAD_LEFT] = true;
			buttons[DPAD_RIGHT] = false;
		}else{
			buttons[DPAD_LEFT] = false;
			buttons[DPAD_RIGHT] = false;
		}
		
		dpad = axisdata.get();
		if(dpad == 1){
			buttons[DPAD_DOWN] = false;
			buttons[DPAD_UP] = true;
		}else if(dpad == -1){
			buttons[DPAD_DOWN] = true;
			buttons[DPAD_UP] = false;
		}else{
			buttons[DPAD_DOWN] = false;
			buttons[DPAD_UP] = false;
		}
		
//		Mouse.updateControllerInputForMouse();
		
	}
	
	public static boolean isButtonDown(int button){
		return buttons[button];
	}
	
	public static float getAxis(int axis){
		return axes[axis];
	}

	public static boolean buttonTippedThisFrame(int button) {
		return buttonsTippedThisFrame[button];
	}
	
}
