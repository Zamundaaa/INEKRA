package renderStuff;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import blockRendering.BlockRenderer;
import chatStuff.Chat;
import chatStuff.Message;
import controls.*;
import gameStuff.Err;
import gameStuff.MainLoop;
import toolBox.Meth;
import toolBox.Tools;

public class DisplayManager {

	public static final boolean DEBUG = false;
	public static final float GAMMA = 2.2f;
	public static boolean VSYNC = Tools.loadBoolPreference("VSYNC", true);

	/**
	 * 16/8.8
	 */
	public static final float desiredRatioForGUI = 16.0f / 8.8f;

	public static final String TITLE = "RANDOM!";
	public static boolean FULLSCREEN = false;
	public static boolean searchBiggest = false;

	public static int WIDTH = 1200, HEIGHT = 720;
	/**
	 * WIDTH/HEIGHT
	 */
	public static float RATIO = WIDTH / (float) HEIGHT;

	// The window handle
	private static long window;

	public static void run() {
		Err.err.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
	}

	public static long getWindow() {
		return window;
	}

	public static void cleanUp() {
		// Free the window callbacks and destroy the window
		// glfwFreeCallbacks(window);
		// glfwDestroyWindow(window);
		
//		gldebug.close();
//		gldebug.free();
		
		// Terminate GLFW and free the error callback
		// glfwSetErrorCallback(null).free();
		glfwTerminate();
		
		FramePerformanceLogger.cleanUp();
		
		Tools.setBoolPreference("VSYNC", VSYNC);
		
	}
	
	private static GLDebugMessageCallback gldebug;

	public static void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(Err.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
													// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be

		// resizable
		// für OpenGL MAJOR.MINOR
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		
//		if (DEBUG) {
//			glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
//		}else{
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
//		}

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, TITLE, NULL, NULL);// FULLSCREEN
																	// ?
																	// GLFW.glfwGetPrimaryMonitor()
																	// :
		
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		try {
			Loader.setWindowIcon(window);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setFullscreen(FULLSCREEN);
		
		Keyboard.init();
		Mouse.init();

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);

		glfwSwapInterval(1);
		// Make the window visible
		glfwShowWindow(window);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
		
		if(DEBUG){
			gldebug = new GLDebugMessageCallback() {
				@Override
				public void invoke(int source, int type, int id, int severity, int length, long message, long ka) {
					Err.err.println(getMessage(length, message));
				}
			};
			gldebug = GLDebugMessageCallback.create(gldebug);
			GL11.glEnable(GL43.GL_DEBUG_OUTPUT);
//			checkForErrors("HI! ");
//			GL43.glDebugMessageInsert(0, 0, 0, 1, "Hello!");
			GL43.glDebugMessageCallback(gldebug, 0);
		}

		// Set the clear color
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

		// glfwSwapInterval(1);

		glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				WIDTH = width;
				HEIGHT = height;
				RATIO = WIDTH / (float) HEIGHT;

				setViewPort();
				MasterRenderer.reload();
				MainLoop.recreateFrameBuffers();
				// Frame.reloadMenuSizes();
			}
		});
		
		Loader.loadAllCursors(window);

	}
	
	private static long lastMillis = Meth.systemTime();
	private static long ldelta;

	private static final int TOTALDFTS = 60;
	private static float[] fts = new float[TOTALDFTS];
	private static int counter = 0;
	private static float dfts;
	
	private static final long desiredPollFTS = 1000/60;
	private static boolean vsyncEnabled = true;
	private static boolean vsyncoff = true;
	
	public static void disableVsyncMessage(){
		if(!VSYNC)
			vsyncoff = true;
	}
	
	public static void updateWindow() {
		
		if(vsyncoff && vsyncEnabled){
			glfwSwapInterval(0);
			vsyncEnabled = false;
		}else if(!vsyncoff && !vsyncEnabled){
			glfwSwapInterval(1);
			vsyncEnabled = true;
		}
		vsyncoff = false;
		
		glfwSwapBuffers(window); // swap the color buffers

		long millis = Meth.systemTime();
		ldelta = millis - lastMillis;
		lastMillis = millis;
		
		Controller.update();
		Mouse.updateSomething();
		Keyboard.updateSomething();
		
		// logs not very performant frames!
		FramePerformanceLogger.update();
		
		float timesToPollEvents = Math.min((float)ldelta/(float)desiredPollFTS, 5);
		do{
			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			timesToPollEvents--;
		}while(timesToPollEvents >= 0);
		
//		Mouse.updateControllerInputForMouse();
		
		Loader.updateCursor(window);
		
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_F2) && Meth.systemTime() > lastShot + 3000) {
			saveScreenshot();
			lastShot = Meth.systemTime();
		}

		// clear the framebuffer(s)
		MasterRenderer.clear();
		

		if (counter < TOTALDFTS - 1) {
			fts[counter] = getFrameTimeSeconds();
			counter++;
		} else {
			float all = 0;
			for (int i = 0; i < TOTALDFTS - 1; i++) {
				fts[i] = fts[i + 1];
				all += fts[i];
			}
			fts[TOTALDFTS - 1] = getFrameTimeSeconds();
			all += getFrameTimeSeconds();
			dfts = all / TOTALDFTS;
		}
		// so the BlockRenderer does FI checks and organizes its data new (if
		// necessary) in the next frame
		// wanted because of water rendering!
		BlockRenderer.doneThisFrame = false;
		
//		checkForErrors("[DisplayManager]");

	}

	public static float getFrameTimeSeconds() {
		return ldelta * 0.001f;
	}

	public static long getFrameTimeMillis() {
		return ldelta;
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	public static void setFrameTimeSeconds(float f) {
		ldelta = (long) (f * 1000);
	}

	private static boolean closeRequested = false;

	public static boolean isCloseRequested() {
		if (!closeRequested) {
			closeRequested = GLFW.glfwWindowShouldClose(window);
		}
		return closeRequested;
	}

	public static void setWindowLocation(int xPosOnScreen, int yPosOnScreen) {
		GLFW.glfwSetWindowPos(window, xPosOnScreen, yPosOnScreen);
	}

	private static int[] xpos = new int[1], ypos = new int[1];

	public static int getWindowX() {
		GLFW.glfwGetWindowPos(window, xpos, ypos);
		return xpos[0];
	}

	public static int getWindowY() {
		GLFW.glfwGetWindowPos(window, xpos, ypos);
		return ypos[0];
	}

	public static void closeDisplay() {
		cleanUp();
	}

	public static float getDFTS() {
		return dfts;
	}

	public static void setWindowSize(int w, int h) {
		GLFW.glfwSetWindowSize(window, w, h);
	}

	public static void setFullscreen(boolean full) {
		FULLSCREEN = full;
		if (FULLSCREEN) {
			long primary = glfwGetPrimaryMonitor();
			GLFWVidMode vm = GLFW.glfwGetVideoMode(primary);
			GLFW.glfwSetWindowMonitor(window, primary, 0, 0, vm.width(), vm.height(), vm.refreshRate());
			WIDTH = vm.width();
			HEIGHT = vm.height();
			// GL11.glViewport(0, 0, WIDTH, HEIGHT); not needed because... WHY
			// NOT?
		}
	}

	private static long lastShot = Meth.systemTime();// new Thread: ImageSaver!

	private static void saveScreenshot() {
		// read current buffer
		FloatBuffer imageData = BufferUtils.createFloatBuffer(WIDTH * HEIGHT * 3);
		GL11.glReadPixels(0, 0, WIDTH, HEIGHT, GL11.GL_RGB, GL11.GL_FLOAT, imageData);
		imageData.rewind();

		File f = getNextScreenFile();
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Chat.addMessage(new Message("Screenshot saved in: " + Tools.screenShotFolder + " as "
				+ (f.getPath().substring(Tools.screenShotFolder.length()))));
		ScreenshotExporter.addToQueue(imageData, f);
	}

	public static void setViewPort() {
		if (cutUnwantedResolution) {
			float fact = RATIO / desiredRatioForGUI;
			if (WIDTH < HEIGHT * desiredRatioForGUI) {
				yGUIOffset = (int) (HEIGHT * (1 - fact) * 0.5f);
				xGUIOffset = 0;
			} else {
				yGUIOffset = 0;
				xGUIOffset = (int) (WIDTH * (1 - (1 / fact)) * 0.5f);
			}
			GL11.glViewport(xGUIOffset, yGUIOffset, WIDTH - xGUIOffset * 2, HEIGHT - yGUIOffset * 2);
		} else {
			GL11.glViewport(0, 0, WIDTH, HEIGHT);
		}
	}

	public static int getXGUIOffset() {
		if (cutUnwantedResolution) {
			return xGUIOffset;
		} else {
			return 0;
		}
	}

	public static int getYGUIOffset() {
		if (cutUnwantedResolution) {
			return yGUIOffset;
		} else {
			return 0;
		}
	}

	public static boolean cutUnwantedResolution = true;

	private static int yGUIOffset = 0;
	private static int xGUIOffset = 0;

	public static float getRenderWidth() {
		return WIDTH - xGUIOffset * 2;
	}

	public static float getRenderHeight() {
		return HEIGHT - yGUIOffset * 2;
	}

	private static File getNextScreenFile() {
		// create image name
		String fileName = Tools.screenShotFolder + "screenshot_" + getSystemTime(false);
		File imageToSave = new File(fileName + ".png");

		// check for duplicates
		int duplicate = 0;
		while (imageToSave.exists()) {
			imageToSave = new File(fileName + "_" + ++duplicate + ".png");
		}

		return imageToSave;
	}

	// format the time
	public static String getSystemTime(boolean getTimeOnly) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(getTimeOnly ? "HH-mm-ss" : "yyyy-MM-dd'T'HH-mm-ss");
		return dateFormat.format(new Date());
	}

	public static void checkForErrors(String addMessage) {
		int errorCode = GL11.glGetError();
		while(errorCode != GL11.GL_NO_ERROR){
			String error;
			switch (errorCode)
	        {
	            case GL11.GL_INVALID_ENUM:                  error = " INVALID_ENUM"; break;
	            case GL11.GL_INVALID_VALUE:                 error = "INVALID_VALUE"; break;
	            case GL11.GL_INVALID_OPERATION:             error = "INVALID_OPERATION"; break;
	            case GL11.GL_STACK_OVERFLOW:                error = "STACK_OVERFLOW"; break;
	            case GL11.GL_STACK_UNDERFLOW:               error = "STACK_UNDERFLOW"; break;
	            case GL11.GL_OUT_OF_MEMORY:                 error = "OUT_OF_MEMORY"; break;
	            default:error = "don't know!";
	        }
			Err.err.println(addMessage + error);
			errorCode = GL11.glGetError();
		}
	}

	public static float getFps() {
		return 1f/getFrameTimeSeconds();
	}
	
}


//// ignore non-significant error/warning codes
//if (id == 131169 || id == 131185 || id == 131218 || id == 131204)
//	return;
//
//Err.err.println("---------------");
//Err.err.println("Debug message (" + id + "): ");
//
//switch (source) {
//case GL43.GL_DEBUG_SOURCE_API:
//	Err.err.println("Source: API");
//	break;
//case GL43.GL_DEBUG_SOURCE_WINDOW_SYSTEM:
//	Err.err.println("Source: Window System");
//	break;
//case GL43.GL_DEBUG_SOURCE_SHADER_COMPILER:
//	Err.err.println("Source: Shader Compiler");
//	break;
//case GL43.GL_DEBUG_SOURCE_THIRD_PARTY:
//	Err.err.println("Source: Third Party");
//	break;
//case GL43.GL_DEBUG_SOURCE_APPLICATION:
//	Err.err.println("Source: Application");
//	break;
//case GL43.GL_DEBUG_SOURCE_OTHER:
//	Err.err.println("Source: Other");
//	break;
//}
//
//switch (type) {
//case GL43.GL_DEBUG_TYPE_ERROR:
//	Err.err.println("Type: Error");
//	break;
//case GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR:
//	Err.err.println("Type: Deprecated Behaviour");
//	break;
//case GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR:
//	Err.err.println("Type: Undefined Behaviour");
//	break;
//case GL43.GL_DEBUG_TYPE_PORTABILITY:
//	Err.err.println("Type: Portability");
//	break;
//case GL43.GL_DEBUG_TYPE_PERFORMANCE:
//	Err.err.println("Type: Performance");
//	break;
//case GL43.GL_DEBUG_TYPE_MARKER:
//	Err.err.println("Type: Marker");
//	break;
//case GL43.GL_DEBUG_TYPE_PUSH_GROUP:
//	Err.err.println("Type: Push Group");
//	break;
//case GL43.GL_DEBUG_TYPE_POP_GROUP:
//	Err.err.println("Type: Pop Group");
//	break;
//case GL43.GL_DEBUG_TYPE_OTHER:
//	Err.err.println("Type: Other");
//	break;
//}
//
//switch (severity) {
//case GL43.GL_DEBUG_SEVERITY_HIGH:
//	Err.err.println("Severity: high");
//	break;
//case GL43.GL_DEBUG_SEVERITY_MEDIUM:
//	Err.err.println("Severity: medium");
//	break;
//case GL43.GL_DEBUG_SEVERITY_LOW:
//	Err.err.println("Severity: low");
//	break;
//case GL43.GL_DEBUG_SEVERITY_NOTIFICATION:
//	Err.err.println("Severity: notification");
//	break;
//}