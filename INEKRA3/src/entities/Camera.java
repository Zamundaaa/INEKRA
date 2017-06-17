package entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

import blockRendering.BlockRenderer;
import controls.Controller;
import controls.Mouse;
import data.Block;
import data.ChunkManager;
import gameStuff.WorldObjects;
import renderStuff.DisplayManager;
import toolBox.Meth;
import toolBox.Tools;

@SuppressWarnings("unused")
public abstract class Camera {

	private static float distanceFromPlayer = 5, angleAroundPlayer = 0;

	private static Vector3f position = new Vector3f();
	private static float pitch = 20, yaw, roll;
	private static Vector2f rotation = new Vector2f();
	private static Entity current;
	private static boolean following = false;
	private static int cam = 1;

	private static final float cap = 5;

	public static void setCurrent(Entity e) {
		current = e;
	}

	public static int cam() {
		return cam;
	}

	public static void invertPitch() {
		pitch *= -1;
	}

	// private long lastTime;

	public static void move() {
		// if(!following){
		// if (Meth.time() > lastTime + 1000 &&
		// Keyboard.isKeyDown(Keyboard.KEY_F5)) {
		// if (cam == 3) {
		// setCam(1);
		// } else {
		// setCam(3);
		// }
		// }
		// if (cam == 3) {
		// calcZoom();
		// calcAAP();
		// calcPitch();
		// float HD = calcHD();
		// float VD = calcVD();
		// calcCamPos(HD, VD);
		// this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
		// } else if (cam == 1) {

		// calcZoom();
		// angleAroundPlayer = 0;
		if (!Player.NOCONTROL) {
			float pC;
			if(!Controller.USECONTROLLER)
				pC = Mouse.getDY() * Mouse.sensitivity * 0.017f;
			else
				pC = DisplayManager.getFrameTimeSeconds()*100*Controller.getAxis(Controller.UD_RIGHT_STICKER);
			
			pitch += pC;
			
		}
		
//		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_K)){
//			roll += 30*DisplayManager.getFrameTimeSeconds();
//		}
//		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_J)){
//			roll -= 30*DisplayManager.getFrameTimeSeconds();
//		}
		
		// pitch = Meth.clamp(pitch, cap-90, 90);
		pitch = Meth.clamp(pitch, -90, 90);
		// if(WorldObjects.player != null){
		position = WorldObjects.player.getHeadPosition();
		yaw = (180 - WorldObjects.player.getRotY());
		

		BlockRenderer.sonarRadius += DisplayManager.getFrameTimeSeconds()*BlockRenderer.sonarSpeed;
		BlockRenderer.sonarRadius %= BlockRenderer.sonarSpeed*5;
		
		// }else{
		//
		// }

		if (Tools.mouseGrabbed) {
			Mouse.setCursorPosition(DisplayManager.WIDTH / 2, DisplayManager.HEIGHT / 2);
		}
		// }
		// }
		// else{
		// calcZoom();
		// calcAAP();
		// calcPitch();
		// float HD = calcHD();
		// float VD = calcVD();
		// calcCamPos(HD, VD);
		// this.yaw = 180 - angleAroundPlayer;
		// }
	}

	public static void setCam(int pos) {
		cam = pos;
		if (cam == 1) {
			distanceFromPlayer = -0.5f;
			if (Tools.mouseGrabbed) {
				Mouse.setCursorPosition(DisplayManager.WIDTH / 2, DisplayManager.HEIGHT / 2);
				Mouse.setGrabbed(true);
			}

		} else if (cam == 3) {
			Mouse.setGrabbed(false);
//			 Mouse.setCursorPosition(DisplayManager.width/2,
//			 DisplayManager.height/2);
			distanceFromPlayer = 5;
		} else {
			cam = 3;
		}
	}

	private static void calcCamPos(float HD, float VD) {

		float offsetX = (float) (HD * Math.sin(Math.toRadians(angleAroundPlayer)));
		float offsetZ = (float) (HD * Math.cos(Math.toRadians(angleAroundPlayer)));
		position.x = current.getPosition().x - offsetX;
		position.z = current.getPosition().z - offsetZ;

		position.y = current.getPosition().y + 1f + VD;
	}

	private static float calcHD() {
		return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
	}

	private static float calcVD() {
		return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
	}

	private static void calcZoom() {
		float zoom = Mouse.getDWheel() * 0.02f;
		distanceFromPlayer -= zoom;
	}

	private static void calcPitch() {
		if (Mouse.isButtonDown(1)) {
			float pC = Mouse.getDY() * 0.1f;
			pitch -= pC;
		}
	}

	private static void calcAAP() {
		if (Mouse.isButtonDown(0)) {
			float aC = Mouse.getDX() * 0.3f;
			angleAroundPlayer -= aC;
		}
	}

	public static void setPosition(float x, float y, float z) {
		position = new Vector3f(x, y, z);
	}

	public static Vector3f getPosition() {
		return position;
	}

	public static Vector2f getRot() {
		return rotation;
	}

	public static float getPitch() {
		return pitch;
	}

	public static float getYaw() {
		return yaw;
	}

	public static float getRoll() {
		return roll;
	}

	public static boolean isFollowing() {
		return following;
	}

	public static void setPosition(Vector3f vect) {
		position.set(vect);
	}

	public static boolean underWater() {
		short b = ChunkManager.getBlockID(position);
		if (!Block.isWater(b)) {
			return false;
		} else {
			float x = (b - 1000) * 0.01f;
			return position.y <= Math.floor(position.y) + x;
		}
	}

	public static void setYaw(float menuRot) {
		yaw = menuRot;
	}

	public static void setPitch(float f) {
		pitch = f;
	}

	public static void setRoll(float f) {
		roll = f;
	}

	public static boolean inSpace() {
		return false;//TODO
	}

}
