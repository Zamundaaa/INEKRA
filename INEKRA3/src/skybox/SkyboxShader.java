package skybox;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import shaders.ShaderProgram;
import toolBox.Vects;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = "skybox/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "skybox/skyboxFragmentShader.txt";
	// private static final float ROTATESPEED = 0;// 0.02f;

	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_FogColor;
	private int location_cubeMap;
	private int location_cubeMap2;
	private int location_blendFactor;
	private int location_bF;
	private int location_WC;
	private int location_PIC;
	private int location_TIME;
	private int location_SUN;
	private int location_moonTex;
	private int location_timeColor;
//	private int location_STARS;
	private int location_StarPic;
	private int location_StarCoordTransform;
	private int location_moonStuff;
	
	private int location_showMoon;

	// private float rotation = 0f;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	private Matrix4f VM = new Matrix4f();

	public void loadViewMatrix(Matrix4f viewMat) {
		// Matrix4f matrix = Meth.createViewMatrix(viewMat);
		VM.set(viewMat);
		VM.m30(0);
		VM.m31(0);
		VM.m32(0);
		// rotation += ROTATESPEED * DisplayManager.getFrameTimeSeconds();
		// VM.rotate(rotation, Vects.UP);
		super.loadMatrix(location_viewMatrix, VM);
	}

	public void loadFogColor(float r, float g, float b) {
		super.loadVector(location_FogColor, new Vector3f(r, g, b));
	}

	public void loadBlendFactor(float bf) {
		super.loadFloat(location_blendFactor, bf);
	}

	public void connectTextureUnits() {
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
		super.loadInt(location_moonTex, 2);
		super.loadInt(location_StarPic, 3);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_FogColor = super.getUniformLocation("fogColor");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
		location_bF = super.getUniformLocation("bF");
		location_WC = super.getUniformLocation("WC");
		location_PIC = super.getUniformLocation("PIC");
		location_TIME = super.getUniformLocation("TIME");
		location_SUN = super.getUniformLocation("sunDirection");
		location_moonTex = super.getUniformLocation("moonTex");
		location_timeColor = super.getUniformLocation("timeColor");
//		location_STARS = super.getUniformLocation("STARS");
		location_StarPic = super.getUniformLocation("startex");
		location_StarCoordTransform = super.getUniformLocation("starCoordTransform");
		location_moonStuff = super.getUniformLocation("moonStuff");
		
		location_showMoon = super.getUniformLocation("showMoon");
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");

		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");
	}

	public void loadMoonStuff(Vector3f ms) {
		super.loadVector(location_moonStuff, ms);
	}

	public void loadStarCoordTransform(Matrix4f mat) {
		super.loadMatrix(location_StarCoordTransform, mat);
	}

//	public void loadSTARS(boolean STARS) {
//		super.loadBoolean(location_STARS, STARS);
//	}

	public void loadTimeColor(Vector3f color) {
		super.loadVector(location_timeColor, color);
	}

	public void loadWeatherFactor(float bF) {
		super.loadFloat(location_bF, bF);
	}

	public void loadWeatherColor(Vector3f c) {
		super.loadVector(location_WC, c);
	}

	public void loadSkyPic(boolean PIC) {
		super.loadBoolean(location_PIC, PIC);
	}

	public void loadTIME(float time) {
		super.loadFloat(location_TIME, time);
	}

	public void loadSunDirection(float x, float y, float z) {
		Vects.calcVect.x = x;
		Vects.calcVect.y = y;
		Vects.calcVect.z = z;
		super.loadVector(location_SUN, Vects.calcVect);
	}

	public void loadSunDirection(Vector3f dir) {
		super.loadVector(location_SUN, dir);
	}

	public void loadShowMoon(boolean b) {
		super.loadBoolean(location_showMoon, b);
	}

}
