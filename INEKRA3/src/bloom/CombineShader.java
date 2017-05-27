package bloom;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import shaders.ShaderProgram;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "bloom/combineFragment.txt";

	private int location_colourTexture;
	private int location_highlightTexture;
	private int location_brightness;
	private int location_GUI;
	private int location_renderGUI;
	
	private int location_sunDir;
	private int location_sunC;
	
	private int location_invProj;
	private int location_invView;

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
		
		location_sunDir = super.getUniformLocation("sunDir");
		location_invProj = super.getUniformLocation("invertedProjMat");
		location_invView = super.getUniformLocation("invertedViewMat");
		location_sunC = super.getUniformLocation("sunColour");
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

	public void loadSunDir(Vector3f v) {
		super.loadVector(location_sunDir, v);
	}
	
	public void loadInvView(Matrix4f mat){
		super.loadMatrix(location_invView, mat);
	}
	
	public void loadInvProj(Matrix4f mat){
		super.loadMatrix(location_invProj, mat);
	}
	
	public void loadSunColour(Vector3f c){
		super.loadVector(location_sunC, c);
	}

}
