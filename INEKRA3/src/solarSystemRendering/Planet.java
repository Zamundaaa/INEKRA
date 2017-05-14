package solarSystemRendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gameStuff.SC;
import models.RawModel;
import models.TexturedModel;
import renderStuff.DisplayManager;

public class Planet {
	
	protected static final TexturedModel four = SC.getModel("../planets/Models/icosphere4", "../planets/Texes/first");
//	private static final RawModel five = SC.getModelExtraLoad("../planets/Models/icosphere5");
	
	protected Vector3f rotationAxis = new Vector3f(0.1f, 0.7f, 0).normalize();
	protected float scale = 10;

	protected float rot;

	protected float rotSpeed = 0.025f;
	protected float x, y, z;
	protected int tex;
	
	public Planet(float x, float y, float z, float scale) {
		this(x, y, z);
		this.scale = scale;
	}
	
	public Planet(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void update(){
		rot += rotSpeed*DisplayManager.getFrameTimeSeconds();
	}
	
	public RawModel getLODModel(){
//		float distsq = Camera.getPosition().distanceSquared(x, y, z);
//		if(distsq > 10*10){
			return four.getRawMod();
//		}else{
//			return five;
//		}
	}
	
	public int getTex(){
		if(tex == 0)
			return four.getTex().getID();
		else
			return tex;
	}

	public Matrix4f buildTransformationMatrix(Matrix4f transMat) {
		transMat.identity();
//		transMat.rotate((float) Math.toRadians(rx), Vects.XP);
//		transMat.rotate((float) Math.toRadians(ry), Vects.UP);
//		transMat.rotate((float) Math.toRadians(rz), Vects.ZP);
		transMat.translate(x, y, z);
		transMat.rotate(rot, rotationAxis);
		transMat.scale(scale);
		return transMat;
	}
	
	public String toString(){
		return "X: " + x + " Y: " + y + " Z: " + z + " rot: " + rot + " axis: " + rotationAxis;
	}

	public Matrix4f buildDrehMatrix(Matrix4f mat) {
		mat.identity();
		mat.rotate(rot, rotationAxis);
		return mat;
	}

}
