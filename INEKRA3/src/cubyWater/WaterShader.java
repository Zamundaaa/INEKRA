package cubyWater;

import org.joml.*;

import shaders.ShaderProgram;

public class WaterShader extends ShaderProgram {

	private static final String vertexFile = "cubyWater/waterShader.vs";
	private static final String fragmentFile = "cubyWater/waterShader.fs";

	private int location_random;
	private int location_viewMatrix;
	private int location_ProjectionMatrix;
	private int location_transmat;
	private int location_tex;
	private int location_blink;
	private int location_sunlight;
	private int location_facts;
	private int location_waveheight;
	private int location_holePlattn;
	private int location_funnyColors;

	private int location_gradient;
	private int location_density;
	private int location_SkyColor;

	private int location_sunDir;
	private int location_TIME;
	private int location_TMODE;
	private int location_DIST;

	private int location_reflect;
	private int location_refract;
	private int location_refractDepth;
	private int location_reflective;

	public WaterShader() {
		super(vertexFile, fragmentFile);
	}

	public void loadRandom(float h) {
		super.loadFloat(location_random, h);
	}

	public void loadLightValue(Vector3f vect) {
		super.loadVector(location_sunlight, vect);
	}

	public void loadProjectionMatrix(Matrix4f m) {
		super.loadMatrix(location_ProjectionMatrix, m);
	}

	public void loadViewMatrix(Matrix4f m) {
		super.loadMatrix(location_viewMatrix, m);
	}

	@Override
	protected void getAllUniformLocations() {
		location_random = super.getUniformLocation("RANDOM");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_ProjectionMatrix = super.getUniformLocation("projectionMatrix");

		location_gradient = super.getUniformLocation("gradient");
		location_density = super.getUniformLocation("density");
		location_SkyColor = super.getUniformLocation("skyColor");

		location_tex = super.getUniformLocation("tex");
		location_blink = super.getUniformLocation("blink");
		location_sunlight = super.getUniformLocation("sunlight");
		location_facts = super.getUniformLocation("facts");
		location_waveheight = super.getUniformLocation("waveheight");
		location_holePlattn = super.getUniformLocation("holePlattn");
		location_funnyColors = super.getUniformLocation("funnyColors");

		location_TIME = super.getUniformLocation("TIME");
		location_TMODE = super.getUniformLocation("MODE");
		location_DIST = super.getUniformLocation("DIST");

		location_sunDir = super.getUniformLocation("sunDir");

		location_reflect = super.getUniformLocation("reflectionTexture");
		location_refract = super.getUniformLocation("refractionTexture");
		location_refractDepth = super.getUniformLocation("refractDepth");
		location_reflective = super.getUniformLocation("reflective");
		
		location_transmat = super.getUniformLocation("transmat");
		
	}

	public void loadReflective(boolean reflect) {
		super.loadBoolean(location_reflective, reflect);
	}

	public void loadDIST(float dist) {
		super.loadFloat(location_DIST, dist);
	}

	public void loadTMODE(int mode) {
		super.loadInt(location_TMODE, mode);
	}

	public void loadTIME(float TIME) {
		super.loadFloat(location_TIME, TIME);
	}

	public void loadFunnyColors(int color) {
		super.loadInt(location_funnyColors, color);
	}

	private Vector3f loadVect = new Vector3f();

	public void loadSkyColor(float r, float g, float b) {
		loadVect.x = r;
		loadVect.y = g;
		loadVect.z = g;
		super.loadVector(location_SkyColor, loadVect);
	}

	public void loadGradient(float g) {
		super.loadFloat(location_gradient, g);
	}

	public void loadDensity(float d) {
		super.loadFloat(location_density, d);
	}

	protected void connectTextureUnits() {
		super.loadInt(location_tex, 0);
		super.loadInt(location_blink, 1);
		super.loadInt(location_reflect, 2);
		super.loadInt(location_refract, 3);
		super.loadInt(location_refractDepth, 4);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "texCoords");
		super.bindAttribute(2, "translation");

		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");
	}

	public void loadFacts(float xfact, float zfact) {
		placeholder.x = xfact;
		placeholder.y = zfact;
		super.loadVector(location_facts, placeholder);
	}

	private static Vector2f placeholder = new Vector2f();

	public void loadWaveHeight(float f) {
		super.loadFloat(location_waveheight, f);
	}

	public void loadPlattn(boolean b) {
		super.loadBoolean(location_holePlattn, b);
	}

	public void loadSunDir(Vector3f dir) {
		super.loadVector(location_sunDir, dir);
	}

	public void loadTransformationMatrix(Matrix4f mat) {
		super.loadMatrix(location_transmat, mat);
	}

}
