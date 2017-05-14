package toolBox;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import controls.Keyboard;
import renderStuff.DisplayManager;

public class FontColorManager {

	public static Vector3f one = new Vector3f();
	public static Vector3f two = new Vector3f();
	public static float CV = Meth.randomFloat(0, 1);
	private static final float maxCV = 1.5f, minCV = -0.5f, SWITCHSPEED = 0.25f;
	private static boolean cup = true;
	//
	// private static float rf1 = Meth.randomFloat(-1, 1);
	// private static float gf1 = Meth.randomFloat(-1, 1);
	// private static float bf1 = Meth.randomFloat(-1, 1);
	// private static float cf1 = Meth.randomFloat(-1, 1);
	//
	// private static float rf2 = Meth.randomFloat(-1, 1);
	// private static float gf2 = Meth.randomFloat(-1, 1);
	// private static float bf2 = Meth.randomFloat(-1, 1);
	// private static float cf2 = Meth.randomFloat(-1, 1);

	private static float TIME = Meth.randomFloat(0, 1000);

	public static void update() {
		// float d = DisplayManager.getFrameTimeSeconds();
		// one.x += rf1*cf1*d;
		// one.y += gf1*cf1*d;
		// one.z += bf1*cf1*d;
		//
		// one.x += rf2*cf2*d;
		// one.y += gf2*cf2*d;
		// one.z += bf2*cf2*d;

		// TIME += DisplayManager.getFrameTimeSeconds()*100;
		if (Keyboard.isKeyDown(GLFW.GLFW_KEY_B)) {
			TIME += 0.1f;
		} else {
			TIME += 0.001f;
		}
		// one.x = Generator.getG().genThing(TIME)*0.5f+1;
		// one.y = Generator.getG().genThing(TIME*1.421f)*0.5f+1;
		// one.z = Generator.getG().genThing(TIME*0.894f)*0.5f+1;
		one.x = (float) Math.sin(TIME) * 0.5f + 1;
		one.y = (float) Math.cos(TIME * 2.46f - 42180) * 0.5f + 1;
		one.z = (float) Math.tan(TIME * 0.249 + 432) * 0.5f + 1;

		// two.x = Generator.getG().genThing(TIME * 48.32f) * 0.5f + 1;
		// two.y = Generator.getG().genThing(TIME * 9.99999f) * 0.5f + 1;
		// two.z = Generator.getG().genThing(TIME * 3.180423f) * 0.5f + 1;

		if (cup) {
			CV += DisplayManager.getFrameTimeSeconds() * SWITCHSPEED;
			if (CV > maxCV) {
				cup = false;
			}
		} else {
			CV -= DisplayManager.getFrameTimeSeconds() * SWITCHSPEED;
			if (CV < minCV) {
				cup = true;
			}
		}

	}

}
