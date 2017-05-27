package guis;

import org.joml.Matrix4f;
import org.joml.Vector2f;

import shaders.ShaderProgram;

public class GuiShader extends ShaderProgram {

	private static final String VERTEX_FILE = "guis/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "guis/guiFragmentShader.txt";

	private int location_transformationMatrix;
	private int location_pos;
	private int location_highlight;
	private int location_displayLevel;
	private int location_alphaHighLight;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	public void loadTransformation(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_pos = super.getUniformLocation("pos");
		location_highlight = super.getUniformLocation("highlight");
		location_displayLevel = super.getUniformLocation("displayLevel");
		location_alphaHighLight = super.getUniformLocation("alphaHighlight");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadTranslation(Vector2f pos) {
		super.loadVector(location_pos, pos);
	}

	public void loadHighLight(float highlight) {
		super.loadFloat(location_highlight, highlight + 1);
	}

	public void loadDisplayLevel(int displayLevel) {
		super.loadInt(location_displayLevel, displayLevel);
	}

	public void loadAlphaHighlight(float alphaHighLight) {
		super.loadFloat(location_alphaHighLight, alphaHighLight);
	}

}
