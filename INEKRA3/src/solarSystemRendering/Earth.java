package solarSystemRendering;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import controls.Keyboard;
import entities.Camera;
import models.RawModel;
import renderStuff.DisplayManager;
import renderStuff.Loader;
import toolBox.Meth;
import toolBox.Vects;

public class Earth extends Planet {

//	private static final TexturedModel earth = SC.getModel("../planets/Models/icosphere6",
//			"../planets/Texes/blue planet");// remove this eventually
	private static final int texture = Loader.loadTexture("../planets/Texes/blue planet");

	private RawModel raw;

	public Earth(float x, float y, float z, float scale) {
		this(x, y, z);
		this.scale = scale;
	}

	public Earth(float x, float y, float z) {
		super(x, y, z);
		raw = Loader.loadToVAO(new float[] { 0, 0, 0, 0, 0, 1, 1, 1, 1 }, new int[] { 0, 1, 2 });
		// System.out.println(raw);
		// rotationAxis.set(0, 1, 0);
	}

	@Override
	public void update() {
		rot += 0.03f * DisplayManager.getFrameTimeSeconds();
	}

	private long lastModelUpdate;

	@Override
	public RawModel getLODModel() {
		// if(Keyboard.keyTipped(GLFW.GLFW_KEY_U))
		if (Meth.systemTime() > lastModelUpdate + 500) {
			updateModel();
			lastModelUpdate = Meth.systemTime();
		}
		// return earth.getRawMod();
		return raw;
	}

	@Override
	public int getTex() {
		return texture;
		// return 0;
	}

	@Override
	public Matrix4f buildTransformationMatrix(Matrix4f transMat) {
		transMat.identity();
		// transMat.rotate((float) Math.toRadians(ry), Vects.UP);
		// transMat.rotate((float) Math.toRadians(rz), Vects.ZP);
		transMat.translate(x, y, z);
		transMat.rotate(rot, rotationAxis);
		// transMat.rotate(90*Meth.angToRad, Vects.XP);
		transMat.scale(scale);
		return transMat;
	}

	@SuppressWarnings("unused")
	private void updateModelold() {
		VertexData d = new VertexData();
		buildTransformationMatrix(Vects.mat4);
		buildDrehMatrix(Vects.mat42);

		Vects.calcVect2.set(x, y, z).negate().add(Camera.getPosition()).normalize();
		// System.out.println(Vects.calcVect2);
		addTriangle(d, -1, -1, 1, 1, -1, -1, 1, 1, 1, 0);
		addTriangle(d, 1, 1, 1, 1, -1, -1, -1, 1, -1, 0);
		addTriangle(d, 1, 1, 1, -1, 1, -1, -1, -1, 1, 0);
		addTriangle(d, -1, -1, 1, -1, 1, -1, 1, -1, -1, 0);
		d.updateVAO(raw);
	}

	private void updateModel() {
		VertexData d = new VertexData();
		buildTransformationMatrix(Vects.mat4);
		buildDrehMatrix(Vects.mat42);
//		Vects.mat42.identity();
//		Vects.mat42.rotation(rot, rotationAxis);
		
		Vects.calcVect2.set(x, y, z).negate().add(Camera.getPosition()).normalize();
		
		maxLOD = 0;
		
		addTriangle(d, -1, -1, 1, 1, -1, -1, 1, 1, 1, 0);
		addTriangle(d, 1, 1, 1, 1, -1, -1, -1, 1, -1, 0);
		addTriangle(d, 1, 1, 1, -1, 1, -1, -1, -1, 1, 0);
		addTriangle(d, -1, -1, 1, -1, 1, -1, 1, -1, -1, 0);

		// float x1 = -1, y1 = -1, z1 = 1, x21 = 1, y21 = -1, z21 = -1, x31 = 1,
		// y31 = 1, z31 = 1;
		//
		// float x12 = 1, y12 = 1, z12 = 1, x2 = -1, y2 = 1, z2 = -1, x32 = -1,
		// y32 = 1, z32 = -1;
		//
		// float x13 = 1, y13 = 1, z13 = 1, x23 = -1, y23 = 1, z23 = -1, x3 =
		// -1, y3 = -1, z3 = 1;
		//
		// float x14 = -1, y14 = -1, z14 = 1, x24 = -1, y24 = 1, z24 = -1, x34 =
		// 1, y34 = -1, z34 = -1;
		//
		// float xc = (x1 + x21 + x31) * onethird;
		// float yc = (y1 + y21 + y31) * onethird;
		// float zc = (z1 + z21 + z31) * onethird;
		//
		// int l1 = getLOD(xc, yc, zc);
		//
		// xc = (x12 + x2 + x32) * onethird;
		// yc = (y12 + y2 + y32) * onethird;
		// zc = (z12 + z2 + z32) * onethird;
		//
		// int l2 = getLOD(xc, yc, zc);
		//
		// xc = (x13 + x23 + x3) * onethird;
		// yc = (y13 + y23 + y3) * onethird;
		// zc = (z13 + z23 + z3) * onethird;
		//
		// int l3 = getLOD(xc, yc, zc);
		//
		// xc = (x14 + x24 + x34) * onethird;
		// yc = (y14 + y24 + y34) * onethird;
		// zc = (z14 + z24 + z34) * onethird;
		//
		// int l4 = getLOD(xc, yc, zc);
		//
		// addTriangleNew(d, x1, y1, z1, x21, y21, z21, x31, y31, z31, l1, l1,
		// l4, l3, 0);
		//
		// addTriangleNew(d, x12, y12, z12, x2, y2, z2, x32, y32, z32, l2, l1,
		// l2, l4, 0);
		//
		// addTriangleNew(d, x13, y13, z13, x23, y21, z21, x3, y3, z3, l3, l4,
		// l2, l3, 0);
		//
		// addTriangleNew(d, x14, y14, z14, x24, y24, z24, x34, y34, z34, l4,
		// l1, l2, l3, 0);
		
		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_PERIOD))
			System.out.println("MLOD: " + maxLOD + d.vertexCount());
		
		d.updateVAO(raw);
	}
	
	private int maxLOD;
	
	@SuppressWarnings("unused")
	private void addTriangleNew(VertexData d, float x1, float y1, float z1, float x2, float y2, float z2, float x3,
			float y3, float z3, int LOD, int L1, int L2, int L3, int it) {

		if (L1 >= LOD) {
			float f1 = (float) Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
			x1 /= f1;
			y1 /= f1;
			z1 /= f1;
		}
		if (L2 >= LOD) {
			float f2 = (float) Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
			x2 /= f2;
			y2 /= f2;
			z2 /= f2;
		}
		if (L3 >= LOD) {
			float f3 = (float) Math.sqrt(x3 * x3 + y3 * y3 + z3 * z3);
			x3 /= f3;
			y3 /= f3;
			z3 /= f3;
		}

		if (it == LOD) {
			d.addVertexWithIndices(x1, y1, z1);
			d.addVertexWithIndices(x2, y2, z2);
			d.addVertexWithIndices(x3, y3, z3);
			return;
		}

		float x21 = (x1 + x2) * 0.5f, y21 = (y1 + y2) * 0.5f, z21 = (z1 + z2) * 0.5f, x31 = (x1 + x3) * 0.5f,
				y31 = (y1 + y3) * 0.5f, z31 = (z1 + z3) * 0.5f;

		float x12 = (x1 + x2) * 0.5f, y12 = (y1 + y2) * 0.5f, z12 = (z1 + z2) * 0.5f, x32 = (x2 + x3) * 0.5f,
				y32 = (y2 + y3) * 0.5f, z32 = (z2 + z3) * 0.5f;

		float x13 = (x1 + x3) * 0.5f, y13 = (y1 + y3) * 0.5f, z13 = (z1 + z3) * 0.5f, x23 = (x3 + x2) * 0.5f,
				y23 = (y3 + y2) * 0.5f, z23 = (z3 + z2) * 0.5f;

		float x14 = (x1 + x2) * 0.5f, y14 = (y1 + y2) * 0.5f, z14 = (z1 + z2) * 0.5f, x24 = (x2 + x3) * 0.5f,
				y24 = (y2 + y3) * 0.5f, z24 = (z2 + z3) * 0.5f, x34 = (x1 + x3) * 0.5f, y34 = (y1 + y3) * 0.5f,
				z34 = (z1 + z3) * 0.5f;

		float xc = (x1 + x21 + x31) * onethird;
		float yc = (y1 + y21 + y31) * onethird;
		float zc = (z1 + z21 + z31) * onethird;

		int l1 = getLOD(xc, yc, zc);

		xc = (x12 + x2 + x32) * onethird;
		yc = (y12 + y2 + y32) * onethird;
		zc = (z12 + z2 + z32) * onethird;

		int l2 = getLOD(xc, yc, zc);

		xc = (x13 + x23 + x3) * onethird;
		yc = (y13 + y23 + y3) * onethird;
		zc = (z13 + z23 + z3) * onethird;

		int l3 = getLOD(xc, yc, zc);

		xc = (x14 + x24 + x34) * onethird;
		yc = (y14 + y24 + y34) * onethird;
		zc = (z14 + z24 + z34) * onethird;

		int l4 = getLOD(xc, yc, zc);

		addTriangleNew(d, x1, y1, z1, x21, y21, z21, x31, y31, z31, l1, L1, l4, L3, it + 1);

		addTriangleNew(d, x12, y12, z12, x2, y2, z2, x32, y32, z32, l2, L1, L2, l4, it + 1);

		addTriangleNew(d, x13, y13, z13, x23, y21, z21, x3, y3, z3, l3, l4, L2, L3, it + 1);

		addTriangleNew(d, x14, y14, z14, x24, y24, z24, x34, y34, z34, l4, L1, L2, L3, it + 1);

		// addTriangle(d, x1, y1, z1, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 +
		// z2) * 0.5f,
		// (x1 + x3) * 0.5f, (y1 + y3) * 0.5f, (z1 + z3) * 0.5f, it + 1);
		// addTriangle(d, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f,
		// x2, y2, z2,
		// (x2 + x3) * 0.5f, (y2 + y3) * 0.5f, (z2 + z3) * 0.5f, it + 1);
		// addTriangle(d, (x1 + x3) * 0.5f, (y1 + y3) * 0.5f, (z1 + z3) * 0.5f,
		// (x3 + x2) * 0.5f,
		// (y3 + y2) * 0.5f, (z3 + z2) * 0.5f, x3, y3, z3, it + 1);
		// addTriangle(d, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f,
		// (x2 + x3) * 0.5f,
		// (y2 + y3) * 0.5f, (z2 + z3) * 0.5f, (x1 + x3) * 0.5f, (y1 + y3) *
		// 0.5f, (z1 + z3) * 0.5f,
		// it + 1);

	}

	private int getLOD(float xc, float yc, float zc) {
		float distSq = Camera.getPosition()
				.distanceSquared(Vects.mat4.transformPosition(Vects.calcVect.set(xc, yc, zc)));
		float dot = Vects.calcVect2.dot(Vects.mat42.transformPosition(Vects.calcVect.set(xc, yc, zc)).normalize());
//		System.out.println(Vects.calcVect);
//		if(Keyboard.isKeyDown(GLFW.GLFW_KEY_PERIOD))
//			System.out.println(Vects.calcVect.lengthSquared());
		return getLOD(distSq, dot);
	}

	private int getLOD(float distSq, float dot) {
		if(dot < -0.3f){
			return -1;
		}
		for (int i = 0; i < LODS.length - 1; i++) {
			if (distSq < LODdists[i] || (distSq < LODdists[i + 1] && dot < LODdots[i] && dot > 0)) {// &&dot>0
				return i;
			}
		}
		return LODS.length - 1;
	}

	private void addTriangle(VertexData d, float x1, float y1, float z1, float x2, float y2, float z2, float x3,
			float y3, float z3, int it) {

		float f1 = (float) Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
		float f2 = (float) Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
		float f3 = (float) Math.sqrt(x3 * x3 + y3 * y3 + z3 * z3);

		x1 /= f1;
		y1 /= f1;
		z1 /= f1;

		x2 /= f2;
		y2 /= f2;
		z2 /= f2;

		x3 /= f3;
		y3 /= f3;
		z3 /= f3;

		float xc = (x1 + x2 + x3) * onethird;
		float yc = (y1 + y2 + y3) * onethird;
		float zc = (z1 + z2 + z3) * onethird;

		int LOD = getLOD(xc, yc, zc);
		if(LOD == -1)
			return;
		if (it < LODS[LOD]) {
			addTriangle(d, x1, y1, z1, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f, (x1 + x3) * 0.5f,
					(y1 + y3) * 0.5f, (z1 + z3) * 0.5f, it + 1);
			addTriangle(d, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f, x2, y2, z2, (x2 + x3) * 0.5f,
					(y2 + y3) * 0.5f, (z2 + z3) * 0.5f, it + 1);
			addTriangle(d, (x1 + x3) * 0.5f, (y1 + y3) * 0.5f, (z1 + z3) * 0.5f, (x3 + x2) * 0.5f, (y3 + y2) * 0.5f,
					(z3 + z2) * 0.5f, x3, y3, z3, it + 1);
			addTriangle(d, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f, (x2 + x3) * 0.5f, (y2 + y3) * 0.5f,
					(z2 + z3) * 0.5f, (x1 + x3) * 0.5f, (y1 + y3) * 0.5f, (z1 + z3) * 0.5f, it + 1);
		} else {
			// multiply by 1-(addheight/scale)
			// change generator to work with xyz relative coords instead
			// of xz
			if(LOD > maxLOD)
				maxLOD = LOD;
			d.addVertexWithIndices(x1, y1, z1);
			d.addVertexWithIndices(x2, y2, z2);
			d.addVertexWithIndices(x3, y3, z3);
		}

		// apply transformationMatrix here. Nothing else. For rotation!
		// AND remove that edge problem!
		// float distSq = Camera.getPosition()
		// .distanceSquared(Vects.mat4.transformPosition((Vects.calcVect.set(xc,
		// yc, zc))));
		//
		// float dot =
		// Vects.calcVect2.dot(Vects.mat42.transformPosition(Vects.calcVect.set(xc,
		// yc, zc)));
		// for (int i = 0; i < LODS.length; i++) {
		// if (distSq < LODdists[i] || (distSq < LODdists[i + 1] &&
		// Meth.abs(dot) < LODdots[i])) {// &&
		// // dot
		// // >
		// // 0
		// // || (dot < LODdots[i])
		// // System.out.println(dot);
		// if (it < LODS[i]) {
		// addTriangle(d, x1, y1, z1, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 +
		// z2) * 0.5f, (x1 + x3) * 0.5f,
		// (y1 + y3) * 0.5f, (z1 + z3) * 0.5f, it + 1);
		// addTriangle(d, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f,
		// x2, y2, z2, (x2 + x3) * 0.5f,
		// (y2 + y3) * 0.5f, (z2 + z3) * 0.5f, it + 1);
		// addTriangle(d, (x1 + x3) * 0.5f, (y1 + y3) * 0.5f, (z1 + z3) * 0.5f,
		// (x3 + x2) * 0.5f,
		// (y3 + y2) * 0.5f, (z3 + z2) * 0.5f, x3, y3, z3, it + 1);
		// addTriangle(d, (x1 + x2) * 0.5f, (y1 + y2) * 0.5f, (z1 + z2) * 0.5f,
		// (x2 + x3) * 0.5f,
		// (y2 + y3) * 0.5f, (z2 + z3) * 0.5f, (x1 + x3) * 0.5f, (y1 + y3) *
		// 0.5f, (z1 + z3) * 0.5f,
		// it + 1);
		// } else {
		// // multiply by 1-(addheight/scale)
		// // change generator to work with xyz relative coords instead
		// // of xz
		// d.addVertexWithIndices(x1, y1, z1);
		// d.addVertexWithIndices(x2, y2, z2);
		// d.addVertexWithIndices(x3, y3, z3);
		// }
		// break;
		// }
		// }

	}

	private static final float onethird = 1 / 3f;

	// private static final float startLength;
	// static{
	// Vector3f one = new Vector3f(1, 1, 1).normalize();
	// Vector3f two = new Vector3f(1, -1, -1).normalize();
	// startLength = one.distance(two);
	// }

	private static final float[] LODdists = new float[] { 500 * 500, 1000 * 1000, 1500 * 1500, 3000 * 3000, 4000 * 4000,
			Float.MAX_VALUE };
	// private static final float LOD2D = 300*300;

	// private static final int LOD0 = 7;
	// private static final int LOD1 = 5;
	// private static final int LOD2 = 3;
	private static final float[] LODS = new float[] { 7, 6, 5, 4, 4, 4 };
	private static final float[] LODdots = new float[] { 80, 70, 60, 90, 90, 90 };

	static {
		for (int i = 0; i < LODdots.length; i++) {
			LODdots[i] = Meth.cosDeg(LODdots[i]);
			// System.out.println(LODdots[i]);
		}
	}

}
