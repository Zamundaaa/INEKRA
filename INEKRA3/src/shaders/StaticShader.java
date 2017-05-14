package shaders;

import java.util.List;

import org.joml.*;

import entities.Light;

public class StaticShader extends ShaderProgram {

	private static final String VERTEX_FILE = "shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE = "shaders/fragmentShader.txt";
	public static final int MAX_LIGHTS = 4;

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
	private int location_SpecularMap;
	private int location_usesSpecularMap;
	private int location_modelTexture;
	private int location_highlight;
	private int location_toShadowMapSpace;
	private int location_ShadowMap;

	private int location_gradient;
	private int location_density;

	private int location_useShadows;

	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindFragOutput(0, "out_Color");
		super.bindFragOutput(1, "out_BrightColor");
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoordinates");
		super.bindAttribute(2, "normal");
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
		location_SpecularMap = super.getUniformLocation("specularMap");
		location_usesSpecularMap = super.getUniformLocation("usesSpecularMap");
		location_modelTexture = super.getUniformLocation("modelTexture");
		location_highlight = super.getUniformLocation("highlight");
		location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
		location_ShadowMap = super.getUniformLocation("shadowMap");

		location_gradient = super.getUniformLocation("gradient");
		location_density = super.getUniformLocation("density");

		location_plane = super.getUniformLocation("plane");

		location_lightPosition = new int[MAX_LIGHTS];
		location_lightColour = new int[MAX_LIGHTS];
		location_attenuation = new int[MAX_LIGHTS];
		for (int i = 0; i < MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

		location_useShadows = super.getUniformLocation("useShadows");
	}

	public void connectTextureUnits() {
		super.loadInt(location_modelTexture, 0);
		super.loadInt(location_SpecularMap, 1);
		super.loadInt(location_ShadowMap, 5);
	}

	public void loadToShadowMapSpaceMatrix(Matrix4f mat) {
		super.loadMatrix(location_toShadowMapSpace, mat);
	}

	public void loadHighlight(boolean highlight) {
		super.loadFloat(location_highlight, highlight ? 0.5f : 0);
	}

	public void loadUseSpecularMap(boolean useMap) {
		super.loadBoolean(location_usesSpecularMap, useMap);
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

	public void loadLights(List<Light> lights) {
		for (int i = 0; i < MAX_LIGHTS; i++) {
			if (i < lights.size()) {
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());
				super.loadVector(location_lightColour[i], lights.get(i).getColour());
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());
			} else {
				super.loadVector(location_lightPosition[i], new Vector3f(0, 0, 0));
				super.loadVector(location_lightColour[i], new Vector3f(0, 0, 0));
				super.loadVector(location_attenuation[i], new Vector3f(1, 0, 0));
			}
		}
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

	public void loadViewMatrix(Matrix4f viewMatrix) {
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	public void loadShadow(boolean value) {
		super.loadBoolean(location_useShadows, value);
	}

}