package blockRendering;

import entities.MWBE;

public class ChunkEntity extends MWBE {// still the shadowRenderer does not
										// work!

//	private RawModel mod;
	private float[] vertices, texcoords, normals, lightData;
	private int[] indis;

	public ChunkEntity(float[] vertices, float[] texcoords, float[] normals, int[] indis, float[] lightData, float x,
			float y, float z) {
//		mod = Loader.loadToVAO3DTex(vertices, texcoords, normals, indis, lightData);
//		this.model = new TexturedModel(mod, SC.getTex("white"));
		this.tIndex = 0;
		this.x = x;
		this.y = y;
		this.z = z;
		this.rotX = 0;
		this.rotY = 0;
		this.rotZ = 0;
		this.scale = 1;
		this.vertices = vertices;
		this.texcoords = texcoords;
		this.normals = normals;
		this.indis = indis;
		this.lightData = lightData;
	}

	public void updateModel(float[] vertices, float[] texcoords, float[] normals, int[] indis, float[] lightData) {
//		Loader.updateVAO3DTex(mod, vertices, texcoords, normals, indis, lightData);
		this.vertices = vertices;
		this.texcoords = texcoords;
		this.normals = normals;
		this.indis = indis;
		this.lightData = lightData;
	}

	public float[] verts() {
		return vertices;
	}

	public float[] texCs() {
		return texcoords;
	}

	public float[] norms() {
		return normals;
	}

	public int[] indis() {
		return indis;
	}

	public float[] lightData() {
		return lightData;
	}

//	public RawModel getMod() {
//		return mod;
//	}

//	/*
//	 * RETURNS A UNNECESSARY TEXTURED MODEL!
//	 */
//	@Override
//	public TexturedModel getModel() {
//		return this.model;
//	}

	public void unload() {
//		Loader.unload(mod);
	}

	public boolean translucent() {
		return translucent;
	}

	public void setTranslucent(boolean b) {
		translucent = b;
	}

	private boolean translucent;

}
