package entities;

import entities.graphicsParts.ModelGraphics;
import mainInterface.Intraface;
import models.TexturedModel;

public class MWBE {

	protected short texID, modelID;
	protected float x, y, z;
	protected float rotX, rotY, rotZ, scale;

	protected int tIndex = 0;
	
	protected ModelGraphics graphics;
	
	public MWBE(short modelID, short texID, int tIndex, float x, float y, float z, float rotX, float rotY, float rotZ,
			float scale, ModelGraphics g) {
		this.texID = texID;
		this.modelID = modelID;
		this.tIndex = tIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.graphics = g;
		graphics.setParent(this);
	}
	
	public MWBE(short modelID, short texID, int tIndex, float x, float y, float z, float scale, ModelGraphics g) {
		this.texID = texID;
		this.modelID = modelID;
		this.tIndex = tIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale = scale;
		this.graphics = g;
		graphics.setParent(this);
	}
	
	public MWBE(short modelID, short texID, int tIndex, float x, float y, float z, float rotX, float rotY, float rotZ,
			float scale) {
		this.texID = texID;
		this.modelID = modelID;
		this.tIndex = tIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		graphics = Intraface.getModelGraphics(this, modelID, texID);
	}

	public MWBE(short modelID, short texID, int tIndex, float x, float y, float z, float scale) {
		this.texID = texID;
		this.modelID = modelID;
		this.tIndex = tIndex;
		this.x = x;
		this.y = y;
		this.z = z;
		this.scale = scale;
		graphics = Intraface.getModelGraphics(this, modelID, texID);
	}

	protected MWBE() {

	}
	
	public void setModelGraphics(ModelGraphics g){
		this.graphics = g;
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

	public int getModelID() {
		return modelID;
	}
	
	public int getTexID(){
		return texID;
	}
	
	public int texIndex(){
		return tIndex;
	}
	
	public ModelGraphics getModelGraphics(){
		return graphics;
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
		this.graphics.setModel(model);
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
		graphics.update();
	}
	
	public void show(){
		graphics.show();
	}
	
	public void hide(){
		graphics.hide();
	}
	
	public void cleanUp(){
		hide();
		graphics.cleanUp();
	}

}
