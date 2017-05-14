package toolBox;

import java.awt.Color;
import java.lang.Math;
import java.util.Random;

import org.joml.*;

import entities.Camera;
import entities.Entity;
import gameStuff.Err;
import gameStuff.TM;
import hitbox.BoundingSphere;

public class Meth {

	private static Random random = new Random();

	public static final boolean water = false;
	public static final float waterHeight = -10;
	public static final boolean frozenWater = true;

	public static float GRAVITY = Tools.loadFloatPreference("gravity", -9.81f);// -9.81f
	public static float loadedGravity = GRAVITY;

	public static final float PI = (float) Math.PI;
	public static final float angToRad = 2 * PI / 360;
	public static final float radToAng = 1 / angToRad;

	static {
		Err.err.println("gravity (in m/s²): " + GRAVITY);
	}

	public static float getDistance(Vector3f one, Vector3f two) {
		return getDistance(one.x, one.y, one.z, two.x, two.y, two.z);
	}

	public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dz = z2 - z1;
		return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
	}

	public static float getDistanceSquared(float x1, float y1, float z1, float x2, float y2, float z2) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dz = z2 - z1;
		return dx * dx + dy * dy + dz * dz;
	}

	public static float gehZuWert(float start, float change, float aim) {
		float value = start;
		if (value < aim) {
			value += Math.abs(change);
			if (value > aim) {
				value = aim;
			}
		} else if (value > aim) {
			value -= Math.abs(change);
			if (value < aim) {
				value = aim;
			}
		}
		return value;
	}

	public static Color randomColor() {
		return new Color(randomFloat(0, 1), randomFloat(0, 1), randomFloat(0, 1));
	}

	public static float pow(float value, int e) {
		if (e == 0) {
			return 1;
		} else {
			return pow(value, e - 1) * value;
		}
	}

	/**
	 * @param f
	 * @return gibt das Vorzeichen des floats zurück (-1 / +1) Wenn f 0 ist, 0
	 */
	public static int vorzeichen(float f) {
		return (int) Math.signum(f);
	}

	public static void giveCoords(String an, float x, float y, float z) {
		an = " " + an;
		Err.err.println(an + "x: \t" + x + an + "y: \t" + y + an + "z: \t" + z);
	}

	/**
	 * @return returns System.currentTimeMillis
	 */
	public static long systemTime() {
		return System.currentTimeMillis();
	}

	/**
	 * @return TM.gameTimeMillis. just for convenience
	 */
	public static float time() {
		return TM.sT();
	}

	public static BoundingSphere createBoundingSphere(Entity e) {
		float r2 = 0;
		float[] vertices = e.getModel().getRawMod().getVertices();
		int[] indices = e.getModel().getRawMod().getIndices();
		int i = 0;
		while (i < indices.length) {
			Vector3f pos = new Vector3f();
			pos.x = vertices[indices[i]];
			i += 1;
			pos.y = vertices[indices[i]];
			i += 1;
			pos.z = vertices[indices[i]];
			i += 1;
			if (pos.lengthSquared() > r2) {
				r2 = pos.lengthSquared();
			}
		}
		return new BoundingSphere(e.getPosition(), (float) Math.sqrt(r2));
	}

	/**
	 * @param millis
	 *            Anzahl an Millisekunden, die der Thread warten soll
	 */
	public static void wartn(long millis) {
		try {
			Thread.currentThread();
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace(Err.err);
		}
	}

	/**
	 * @param dest
	 * @param l
	 *            die Länge, zu der der Vector gestaucht oder gestreckt werden
	 *            soll
	 * @return dest mit der Länge l
	 */
	public static Vector3f scaleToLength(Vector3f dest, float l) {
		float length = dest.length();
		float fact = l / length;
		dest.x *= fact;
		dest.y *= fact;
		dest.z *= fact;
		return dest;
	}

	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * @param f
	 * @param min
	 *            min
	 * @param max
	 *            max
	 * @return max, wenn f größer ist als max und min, wenn f kleiner ist als
	 *         min. Ansonsten f
	 */
	public static int clamp(int f, int min, int max) {
		int ret = Math.min(f, max);
		ret = Math.max(ret, min);
		return ret;
	}

	/**
	 * @param f
	 * @param min
	 *            min
	 * @param max
	 *            max
	 * @return max, wenn f größer ist als max und min, wenn f kleiner ist als
	 *         min. Ansonsten f
	 */
	public static float clamp(float f, float min, float max) {
		f = Math.min(f, max);
		f = Math.max(f, min);
		return f;
	}

	/**
	 * @param f
	 * @return (int) (f + 0.5f)
	 */
	public static int round(float f) {
		return (int) (f + 0.5f);
	}

	/**
	 * @param f
	 * @return den mit Math.ceil gerundeten Wert von f als Integer
	 */
	public static int toInt(float f) {
		return (int) Math.ceil(f);
	}

	public static int randomInt(int min, int max, long seed) {
		random.setSeed(seed);
		int ret = random.nextInt(max - min + 1) + min;
		random = new Random();
		return ret;
	}

	public static float randomFloat(float min, float max, long seed) {
		random.setSeed(seed);
		float ret = random.nextFloat() * (max - min) + min;
		random = new Random();
		return ret;
	}

	/**
	 * @param chance
	 *            float zwischen 0 und 1
	 * @return gibt mit einer Wahrscheinlichkeit von chance true aus
	 */
	public static boolean doChance(float chance, long seed) {
		boolean ret = false;
		random.setSeed(seed);
		if (random.nextFloat() > (1 - chance)) {
			ret = true;
		}
		random = new Random();
		return ret;
	}

	/**
	 * @param min
	 *            min
	 * @param max
	 *            max
	 * @return Zufallszahl im Bereich von min - max
	 */
	public static int randomInt(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	/**
	 * @param min
	 *            min
	 * @param max
	 *            max
	 * @return Zufallszahl im Bereich von min - max
	 */
	public static float randomFloat(float min, float max) {
		return random.nextFloat() * (max - min) + min;
	}

	/**
	 * @param chance
	 *            float zwischen 0 und 1
	 * @return gibt mit einer Wahrscheinlichkeit von chance true aus
	 */
	public static boolean doChance(float chance) {
		if (random.nextFloat() > (1 - chance)) {
			return true;
		}
		return false;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale,
			Matrix4f matrix) {
		if (matrix == null) {
			matrix = new Matrix4f();
		}
		matrix.identity();
		matrix.translate(translation);
		matrix.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0));
		matrix.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0));
		matrix.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1));
		matrix.scale(new Vector3f(scale, scale, scale));
		return matrix;
	}

	// private static Vector3f translationmarker = new Vector3f();

	public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz,
			float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.translate(x, y, z);
		matrix.rotate((float) Math.toRadians(rx), Vects.XP);
		matrix.rotate((float) Math.toRadians(ry), Vects.UP);
		matrix.rotate((float) Math.toRadians(rz), Vects.ZP);
		matrix.scale(scale);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz,
			float scale, Matrix4f matrix) {
		matrix.identity();
		matrix.translate(x, y, z);
		matrix.rotate((float) Math.toRadians(rx), Vects.XP);
		matrix.rotate((float) Math.toRadians(ry), Vects.UP);
		matrix.rotate((float) Math.toRadians(rz), Vects.ZP);
		matrix.scale(scale);
		return matrix;
	}

	private static Matrix4f viewMatrix = new Matrix4f();

	public static Matrix4f createViewMatrix() {
//		viewMatrix.identity();
//		viewMatrix.rotate((float) Math.toRadians(Camera.getPitch()), Vects.XP);
//		viewMatrix.rotate((float) Math.toRadians(Camera.getYaw()), Vects.UP);
//		viewMatrix.translate(-Camera.getPosition().x, -Camera.getPosition().y, -Camera.getPosition().z);
//		return viewMatrix;
		return createViewMatrix(viewMatrix, Camera.getYaw(), Camera.getPitch(), Camera.getRoll(), Camera.getPosition().x, Camera.getPosition().y, Camera.getPosition().z);
	}

	private static Matrix4f transformationMatrix2D = new Matrix4f();

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		transformationMatrix2D.identity();
		transformationMatrix2D.translate(translation.x, translation.y, 0);
		transformationMatrix2D.scale(scale.x, scale.y, 1);
		// transformationMatrix2D.translation(translation.x, translation.y, 0);
		// transformationMatrix2D.scaling(scale.x, scale.y, 1);
		return transformationMatrix2D;
	}

	public static Matrix4f createTransformationMatrix(float x, float y, float scalex, float scaley) {
		transformationMatrix2D.identity();
		transformationMatrix2D.translate(x, y, 0);
		transformationMatrix2D.scale(scalex, scaley, 1);
		return transformationMatrix2D;
	}

	public static float blend(float one, float two, float blendFactor) {
		return (1 - blendFactor) * one + blendFactor * two;
	}

	public static Matrix4f createViewMatrix(float rotY, float pitch, float x, float y, float z) {
		viewMatrix.identity();
		viewMatrix.rotate(pitch * angToRad, Vects.XP);
		viewMatrix.rotate(rotY * angToRad, Vects.UP);
		viewMatrix.translate(-x, -y, -z);
		return viewMatrix;
	}

	public static Matrix4f createViewMatrix(Matrix4f viewMatrix, float rotY, float pitch, float x, float y, float z) {
		viewMatrix.identity();
		viewMatrix.rotate(pitch * angToRad, Vects.XP);
		viewMatrix.rotate(rotY * angToRad, Vects.UP);
		viewMatrix.translate(-x, -y, -z);
		return viewMatrix;
	}
	
	public static Matrix4f createViewMatrix(Matrix4f viewMatrix, float rotY, float pitch, float roll, float x, float y, float z) {
		viewMatrix.identity();
		viewMatrix.rotate(pitch * angToRad, Vects.XP);
		viewMatrix.rotate(rotY * angToRad, Vects.UP);
		viewMatrix.rotate(roll * angToRad, Vects.ZP);
		viewMatrix.translate(-x, -y, -z);
		return viewMatrix;
	}

	/**
	 * @param one
	 * @param two
	 * @param genauigkeit
	 *            in Nachkommastellen
	 * @return
	 */
	public static boolean theSame(float one, float two, int genauigkeit) {
		return ((int) (one * genauigkeit * 10) == (int) (two * genauigkeit * 10));
	}

	/**
	 * @param f
	 * @param i Nachkommastellen
	 */
	public static String floatToString(float f, int i) {
		int p = (int)pow(10, i);
		f *= p;
		int v = (int)f;
		return "" + (v/p);
	}

	public static float sin(float f) {
		return (float)Math.sin(f);
	}
	
	public static float cos(float f){
		return (float)Math.cos(f);
	}

	public static float sinDeg(float f) {
		return sin(f*angToRad);
	}
	
	public static float cosDeg(float f){
		return cos(f*angToRad);
	}

	public static float abs(float dot) {
		if(dot < 0){
			return -dot;
		}else{
			return dot;
		}
	}

	// public static void main(String[] args){
	// System.out.println("angle: " + 90 + " rad: " + (90*Meth.angToRad) + "
	// rad/PI " + (90*Meth.angToRad/Meth.PI) + " radToAngle: "
	// + ((90*Meth.angToRad)*Meth.radToAng));
	// }

}
