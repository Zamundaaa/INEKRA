package models;

import org.lwjgl.opengl.GL30;

import objConverter.ModelData;

public class RawModel {
	
	private int vaoID;
	private int vboID, vboPos, vboTex, vboNorm, vboTan;
	private int aP = 0, aTex = 1, aN = 2, aTan = 3;
	private int vertexCount;
	private float[] vertices;
	private int[] indices;

	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}

	public RawModel(int vaoID, int vertexCount, int vboID) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vboID = vboID;
	}

	public RawModel(int vaoID, int vertexCount, int vboID, int vboPos, int vboTex, int vboNorm) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vboID = vboID;
		this.vboPos = vboPos;
		this.vboTex = vboTex;
		this.vboNorm = vboNorm;
	}

	public RawModel(int vaoID, int vertexCount, int vboID, int vboPos, int vboTex, int vboNorm, int tan) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vboID = vboID;
		this.vboPos = vboPos;
		this.vboTex = vboTex;
		this.vboNorm = vboNorm;
		this.vboTan = tan;
	}
	
	public int attributeListPositions(){
		return aP;
	}
	
	public int attributeListTextureCoords(){
		return aTex;
	}
	
	public int attributeListNormals(){
		return aN;
	}
	
	public int attributeListTangents(){
		return aTan;
	}
	
	public void setAttributeListNormals(int al){
		aN = al;
	}
	
	public int vboID() {
		return vboID;
	}

	public void saveThings(ModelData d) {
		vertices = d.getVertices();
		indices = d.getIndices();
	}

	public float[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setVBO(int vbo) {
		this.vboID = vbo;
	}

	public int vboPos() {
		return vboPos;
	}

	public int vboTex() {
		return vboTex;
	}

	public int vboNorm() {
		return vboNorm;
	}

	public int vboTan() {
		return vboTan;
	}

	public void setVertexCount(int c) {
		vertexCount = c;
	}
	
	@Override
	public String toString(){
		return "VAO: " + vaoID + " posVbo: " + vboPos + " indicesVbo: " + vboID;
	}

	public void bindVAO() {
		GL30.glBindVertexArray(vaoID);
	}
	
	public void unbindVAO(){
		GL30.glBindVertexArray(0);
	}

}
