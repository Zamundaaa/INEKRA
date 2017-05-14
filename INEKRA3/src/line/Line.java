package line;

import org.joml.Vector3f;

import models.RawModel;
import renderStuff.Loader;

public class Line {

	private float x1, y1, z1, x2, y2, z2;
	private float r, g, b;

	/**
	 * creates a new line and adds it to the LineRenderer (/calls show())
	 */
	public Line(float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.r = r;
		this.g = g;
		this.b = b;
		show();
	}

	public Line() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	public void show() {
		LineRenderer.logIn(this);
	}

	public void hide() {
		LineRenderer.logOut(this);
	}

	public void cleanUp() {
		hide();
		// SC.loader.unload(model);
	}

	public float getX1() {
		return x1;
	}

	public void setX1(float x1) {
		this.x1 = x1;
	}

	public float getY1() {
		return y1;
	}

	public void setY1(float y1) {
		this.y1 = y1;
	}

	public float getZ1() {
		return z1;
	}

	public void setZ1(float z1) {
		this.z1 = z1;
	}

	public float getX2() {
		return x2;
	}

	public void setX2(float x2) {
		this.x2 = x2;
	}

	public float getY2() {
		return y2;
	}

	public void setY2(float y2) {
		this.y2 = y2;
	}

	public float getZ2() {
		return z2;
	}

	public void setZ2(float z2) {
		this.z2 = z2;
	}

	public float getR() {
		return r;
	}

	public void setR(float r) {
		this.r = r;
	}

	public float getG() {
		return g;
	}

	public void setG(float g) {
		this.g = g;
	}

	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public void set1(float x, float y, float z) {
		this.x1 = x;
		this.y1 = y;
		this.z1 = z;
	}

	public void set1(Vector3f vect) {
		set1(vect.x, vect.y, vect.z);
	}

	public void set2(float x, float y, float z) {
		this.x2 = x;
		this.y2 = y;
		this.z2 = z;
	}

	public void set2(Vector3f vect) {
		set2(vect.x, vect.y, vect.z);
	}

	private static RawModel model = Loader.loadToVAO(new float[] { 1, 1, 1, -1, -1, -1 }, new int[] { 0, 1 });

	public static RawModel getRawModel() {
		return model;
	}

	public void setColor(Vector3f color) {
		this.r = color.x;
		this.g = color.y;
		this.b = color.z;
	}

	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void set(float x, float y, float z, float x2, float y2, float z2) {
		set1(x, y, z);
		set2(x2, y2, z2);
	}

}
