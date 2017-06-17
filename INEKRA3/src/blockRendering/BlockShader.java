package blockRendering;

import org.joml.*;

import data.LightMaster;
import entities.Light;
import shaders.ShaderProgram;
import toolBox.Vects;

public class BlockShader extends ShaderProgram {

	public static final String VERTEX_FILE = "blockRendering/vertexShader.txt";
	public static final String FRAGMENT_FILE = "blockRendering/fragmentShader.txt";
	public static final String VERTEXFILE_SHADOWS = "blockRendering/vertexShaderSHADOWS.txt";
	public static final String FRAGMENTFILE_SHADOWS = "blockRendering/fragmentShaderSHADOWS.txt";
	
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition[];
	private int location_lightColour[];
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_useFakeLighting;
	private int location_skyColor;
	private int location_numberOfRows;
	private int location_offSet;
	private int location_attenuation[];
	private int location_plane;
	// private int location_modelTexture;
	private int location_highlight;
	private int location_toShadowMapSpace;
	private int location_ShadowMap;
	private int location_useShadows;
	private int location_Lighting;

	private int location_gradient;
	private int location_density;

	private int location_TEX;

	private int location_TIME;
	private int location_TMODE;
	private int location_DIST;

	private int location_timesin;
	private int location_sunDirection;
	
	private int location_sonar;
	private int location_sonarRadius;

	public BlockShader(String vertexFile, String fragmentFile) {
		super(vertexFile, fragmentFile);
	}

	@Override
	protected void bindAttributes() {
		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "lightStuff");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");
		location_useFakeLighting = super.getUniformLocation("useFakeLightning");
		location_skyColor = super.getUniformLocation("skyColor");
		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offSet = super.getUniformLocation("offSet");
		location_TEX = super.getUniformLocation("TEX");
		location_highlight = super.getUniformLocation("highlight");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_ShadowMap = super.getUniformLocation("shadowMap");

		location_gradient = super.getUniformLocation("gradient");
		location_density = super.getUniformLocation("density");

		location_plane = super.getUniformLocation("plane");

		location_lightPosition = new int[LightMaster.MAX_LIGHTS];
		location_lightColour = new int[LightMaster.MAX_LIGHTS];
		location_attenuation = new int[LightMaster.MAX_LIGHTS];
		
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

		location_useShadows = super.getUniformLocation("useShadows");
		location_Lighting = super.getUniformLocation("useLightingCalc");
		location_TIME = super.getUniformLocation("TIME");

		location_TMODE = super.getUniformLocation("MODE");
		location_DIST = super.getUniformLocation("DIST");

		location_timesin = super.getUniformLocation("timesin");
		
		location_sunDirection = super.getUniformLocation("sunDirection");
		
		location_sonar = super.getUniformLocation("sonar");
		location_sonarRadius = super.getUniformLocation("sonarRadius");
		
	}

	public void connectTextureUnits() {
		super.loadInt(location_TEX, 0);
		super.loadInt(location_ShadowMap, 5);
	}

	public void loadTimeSin(float ts) {
		super.loadFloat(location_timesin, ts);
	}

	public void loadDIST(float DIST) {
		super.loadFloat(location_DIST, DIST);
	}

	public void loadToShadowMapSpaceMatrix(Matrix4f mat) {
		super.loadMatrix(location_toShadowMapSpace, mat);
	}

	public void loadTMODE(int i) {
		super.loadInt(location_TMODE, i);
	}

	public void loadLighting(boolean useLighting) {
		super.loadBoolean(location_Lighting, useLighting);
	}

	public void loadHighlight(boolean highlight) {
		super.loadFloat(location_highlight, highlight ? 0.5f : 0);
	}

	public void loadClipPlane(Vector4f clipPlane) {
		super.loadVector(location_plane, clipPlane);
	}

	public void loadOffSet(float x, float y) {
		super.loadVector(location_offSet, new Vector2f(x, y));
	}

	public void loadNOR(int NOR) {
		super.loadFloat(location_numberOfRows, NOR);
	}

	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}

	public void loadFakeLightningVariable(boolean fake) {
		super.loadBoolean(location_useFakeLighting, fake);
	}

	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadSun(Vector3f sunDirection, Vector3f color){
		super.loadVector(location_sunDirection, sunDirection);
		super.loadVector(location_lightColour[0], color);
		super.loadVector(location_attenuation[0], Vects.XP);
	}

	public void loadLights(Light[] lights) {
		for (int i = 1; i < MAX_LIGHTS; i++) {
			if (i < lights.length && lights[i] != null) {
				super.loadVector(location_lightPosition[i], lights[i].getPosition());
				super.loadVector(location_lightColour[i], lights[i].getColour());
				super.loadVector(location_attenuation[i], lights[i].getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], Vects.NULL);
				super.loadVector(location_lightColour[i], Vects.NULL);
				super.loadVector(location_attenuation[i], Vects.XP);
			}
		}
	}
	
	protected static final int MAX_LIGHTS = 1;

	public void loadViewMatrix(Matrix4f viewMatrix) {
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}

	public void loadGradient(float g) {
		super.loadFloat(location_gradient, g);
	}

	public void loadDensity(float d) {
		super.loadFloat(location_density, d);
	}

	public void loadShadow(boolean value) {
		super.loadBoolean(location_useShadows, value);
	}

	public void loadTime(float TIME) {
		super.loadFloat(location_TIME, TIME);
	}
	
	public void loadSonar(boolean sonar){
		super.loadBoolean(location_sonar, sonar);
	}
	
	public void loadSonarRadius(float r){
		super.loadFloat(location_sonarRadius, r);
	}

	public static BlockShader getNewBlockShaderWithShadows() {
		return new BlockShader(VERTEXFILE_SHADOWS, FRAGMENTFILE_SHADOWS);
	}
	
	public static BlockShader getNewBlockShaderWithoutShadows(){
		return new BlockShader(VERTEX_FILE, FRAGMENT_FILE);
	}

}