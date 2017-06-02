package cubyWaterNew;

import org.joml.Matrix4f;

import shaders.ShaderProgram;

public class NewWaterShader extends ShaderProgram{
	
	private static final String vertex = "cubyWaterNew/water.vs", fragment = "cubyWaterNew/water.fs";
	
	private int view, proj;
	private int time;
	private int reflect;
	private int refract;
	private int refractDepth;
	private int reflections;
	
	public NewWaterShader() {
		super(vertex, fragment);
	}

	@Override
	protected void getAllUniformLocations() {
		view = super.getUniformLocation("viewMatrix");
		proj = super.getUniformLocation("projectionMatrix");
		time = super.getUniformLocation("time");
		reflect = super.getUniformLocation("reflectionTexture");
		refract = super.getUniformLocation("refractionTexture");
		refractDepth = super.getUniformLocation("refractDepth");
		reflections = super.getUniformLocation("reflections");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "pos");
		super.bindAttribute(1, "norm");
		
		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(reflect, 2);
		super.loadInt(refract, 3);
		super.loadInt(refractDepth, 4);
	}
	
	public void loadReflections(boolean reflect){
		super.loadBoolean(reflections, reflect);
	}
	
	public void loadViewMat(Matrix4f mat){
		super.loadMatrix(view, mat);
	}
	
	public void loadProjMat(Matrix4f mat){
		super.loadMatrix(proj, mat);
	}
	
	public void loadTime(float t){
		super.loadFloat(time, t);
	}

}
