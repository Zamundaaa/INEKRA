package solarSystemRendering;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import shaders.ShaderProgram;

public class PlanetShader extends ShaderProgram {
	
	private static final String vertexFile = "solarSystemRendering/planetVertex.txt", fragmentFile = "solarSystemRendering/planetFragment.txt";
	
	private int proj, trans, view, drehMat;
	private int tex;
	private int sunDir, sunColor;
	
	public PlanetShader() {
		super(vertexFile, fragmentFile);
	}

	@Override
	protected void getAllUniformLocations() {
		proj = super.getUniformLocation("projectionMatrix");
		trans = super.getUniformLocation("transformationMatrix");
		view = super.getUniformLocation("viewMatrix");
		tex = super.getUniformLocation("tex");
		sunDir = super.getUniformLocation("sunDir");
		sunColor = super.getUniformLocation("sunColor");
		drehMat = super.getUniformLocation("drehMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
//		super.bindAttribute(1, "textureCoords");
//		super.bindAttribute(2, "normals");
		
		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");
	}
	
	public void loadTex(int tex){
		super.loadInt(this.tex, tex);
	}
	
	public void loadProjectionMatrix(Matrix4f mat){
		super.loadMatrix(proj, mat);
	}
	
	public void loadViewMatrix(Matrix4f mat){
		super.loadMatrix(view, mat);
	}
	
	public void loadTransformationMatrix(Matrix4f mat){
		super.loadMatrix(trans, mat);
	}

	public void loadSunDir(Vector3f dir) {
		super.loadVector(sunDir, dir);
	}
	
	public void loadSunColor(Vector3f c){
		super.loadVector(sunColor, c);
	}

	public void loadDrehMatrix(Matrix4f drehMatrix) {
		super.loadMatrix(drehMat, drehMatrix);
	}

}
