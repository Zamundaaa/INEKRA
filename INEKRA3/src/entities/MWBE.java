package entities;

import models.TexturedModel;

public class MWBE {

	protected TexturedModel model;
	protected float x, y, z;
	protected float rotX, rotY, rotZ, scale;

	protected int tIndex = 0;

	public MWBE(TexturedModel model, int tIndex, float x, float y, float z, float rotX, float rotY, float rotZ,
			float scale) {
		this.model = model;
		this.tIndex = tIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
	}

	public MWBE(TexturedModel model, int tIndex, float x, float y, float z, float scale) {
		this.model = model;
		this.tIndex = tIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale = scale;
	}

	protected MWBE() {

	}

	public void settIndex(int i) {
		tIndex = i;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void rotate(float x, float y, float z) {
		rotX += x;
		rotY += y;
		rotZ += z;
	}

	public float getTextureXOffset() {
		int column = tIndex % model.getTex().getNOR();
		return (float) column / (float) model.getTex().getNOR();
	}

	public float getTextureYOffset() {
		int row = (int) ((float) tIndex / (float) model.getTex().getNOR());
		return (float) row / (float) model.getTex().getNOR();
	}

	public void increasePos(float dx, float dy, float dz) {
		x += dx;
		y += dy;
		z += dz;
	}

	public void increaseRot(float dx, float dy, float dz) {
		rotX += dx;
		rotY += dy;
		rotZ += dz;
	}

	public TexturedModel getModel() {
		return model;
	}

	public float getRotX() {
		return rotX;
	}

	public float getRotY() {
		return rotY;
	}

	public float getRotZ() {
		return rotZ;
	}

	public float getScale() {
		return scale;
	}

	public void setModel(TexturedModel model) {
		this.model = model;
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void update() {

	}

}
