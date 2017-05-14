package bloom;

import shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "bloom/combineFragment.txt";

	private int location_colourTexture;
	private int location_highlightTexture;
	private int location_brightness;
	private int location_GUI;
	private int location_renderGUI;

	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformLocation("colourTexture");
		location_highlightTexture = super.getUniformLocation("highlightTexture");
		location_brightness = super.getUniformLocation("brightness");
		location_GUI = super.getUniformLocation("GUI");
		location_renderGUI = super.getUniformLocation("renderGUI");
	}

	protected void connectTextureUnits() {
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_highlightTexture, 1);
		super.loadInt(location_GUI, 2);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadBrightness(float brightness) {
		super.loadFloat(location_brightness, brightness);
	}

	public void loadRenderGUI(boolean renderGUI) {
		super.loadBoolean(location_renderGUI, renderGUI);
	}

}
